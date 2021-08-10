/*
 * Example Plugin for SonarQube
 * Copyright (C) 2009-2020 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package io.sentry.integrations.sonar.rules;

import io.sentry.api.ApiClient;
import io.sentry.api.ApiToken;
import io.sentry.api.InvalidApiToken;
import io.sentry.api.RequestException;
import io.sentry.api.schema.SentryIssue;
import io.sentry.api.schema.StackTraceHit;
import io.sentry.integrations.sonar.settings.SentryProperties;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.rule.Severity;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.issue.NewExternalIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.config.Configuration;
import org.sonar.api.rules.RuleType;
import org.sonar.api.scanner.sensor.ProjectSensor;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import java.util.List;

/**
 * Generates issues on all java files at line 1.
 */
public class CreateSentryIssuesSensor implements ProjectSensor {

    private static final Logger LOGGER = Loggers.get(CreateSentryIssuesSensor.class);
    private static final double ARBITRARY_GAP = 2.0;

    private final Configuration config;

    public CreateSentryIssuesSensor(final Configuration config) {
        this.config = config;
    }

    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor
                .name("Annotate Sentry issues on line 1 of all Java files")
                .onlyWhenConfiguration(this::hasAllConfigs);
    }

    private boolean hasAllConfigs(Configuration configuration) {
        return configuration.hasKey(SentryProperties.TOKEN_KEY)
                && configuration.hasKey(SentryProperties.ORGANIZATION_KEY)
                && configuration.hasKey(SentryProperties.PROJECT_KEY);
    }

    private ApiToken getSentryToken() throws InvalidApiToken {
        String token = config.get(SentryProperties.TOKEN_KEY).orElse("");
        return new ApiToken(token);
    }

    private String getOrganizationSlug() {
        return config.get(SentryProperties.ORGANIZATION_KEY).orElse("");
    }

    private int getProjectId() {
        int projectId = config.getInt(SentryProperties.PROJECT_KEY).orElse(0);
        return Math.max(projectId, 0);
    }

    private String getPathPrefix() {
        return config.get(SentryProperties.PATH_PREFIX_KEY)
                .orElse(SentryProperties.DEFAULT_PATH_PREFIX);
    }

    private String stripPrefix(String path) {
        String prefix = getPathPrefix();
        if (path != null && path.startsWith(prefix)) {
            return path.substring(prefix.length());
        }

        return null;
    }

    private void saveError(SensorContext context, String message) {
        context.newAnalysisError().message(message).save();
    }

    @Override
    public void execute(SensorContext context) {
        LOGGER.info("Running Sentry analyzer");

        ApiToken token;
        try {
            token = getSentryToken();
        } catch (InvalidApiToken e) {
            saveError(context, "The Sentry token is missing or not valid");
            return;
        }

        String organization = getOrganizationSlug();
        int project = getProjectId();

        if (organization.isEmpty() || project == 0) {
            saveError(context, "Missing valid organization or project info");
            return;
        }

        ApiClient client = new ApiClient(token);

        List<StackTraceHit> stackTraceHits;
        try {
            stackTraceHits = client.countStackTraces(organization, project);
        } catch (RequestException e) {
            saveError(context, "Failed to fetch information from Sentry: " + e);
            return;
        }

        FileSystem fs = context.fileSystem();

        for (StackTraceHit hit : stackTraceHits) {
            List<String> absPaths = hit.getAbsPaths();
            List<Integer> lineNumbers = hit.getLineNumbers();
            if (absPaths.isEmpty()) {
                continue;
            }

            SentryIssue sentryIssue;
            try {
                sentryIssue = client.getIssue(hit.getIssueId());
            } catch (RequestException e) {
                saveError(context, String.format("Failed to fetch issue %s: %s", hit.getIssueShortId(), e));
                return;
            }

            if (!sentryIssue.isUnresolved()) {
                LOGGER.debug(String.format("Skipping %s because it has status `%s`", hit.getIssueShortId(), sentryIssue.getStatus()));
                continue;
            }

            NewExternalIssue sonarIssue = context.newExternalIssue()
                    .engineId("sentry")
                    .ruleId("sentry_issue")
                    .type(RuleType.BUG)
                    .severity(Severity.MAJOR);

            int lastIndex = absPaths.size() - 1;
            for (int index = 0; index < absPaths.size(); index++) {
                String relativePath = stripPrefix(absPaths.get(index));
                if (relativePath == null) {
                    // This file is not considered app code.
                    continue;
                }

                FilePredicate predicate = fs.predicates().hasRelativePath(relativePath);
                InputFile inputFile = fs.inputFile(predicate);
                if (inputFile == null) {
                    continue;
                }

                NewIssueLocation sonarLocation = sonarIssue.newLocation()
                        .on(inputFile)
                        .message(sentryIssue.getTitle());

                try {
                    int lineNumber = lineNumbers.get(index);
                    sonarLocation.at(inputFile.selectLine(lineNumber));
                } catch (IndexOutOfBoundsException e) {
                    // ignore
                }

                if (index == lastIndex) {
                    sonarIssue.at(sonarLocation);
                } else {
                    sonarIssue.addLocation(sonarLocation);
                }
            }

            sonarIssue.save();
        }
    }
}
