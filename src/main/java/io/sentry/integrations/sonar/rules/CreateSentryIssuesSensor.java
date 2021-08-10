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

import io.sentry.api.ApiToken;
import io.sentry.api.InvalidApiToken;
import io.sentry.integrations.sonar.settings.SentryProperties;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.rule.Severity;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.issue.NewExternalIssue;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.config.Configuration;
import org.sonar.api.rules.RuleType;
import org.sonar.api.scanner.sensor.ProjectSensor;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import java.util.Optional;

/**
 * Generates issues on all java files at line 1.
 */
public class CreateSentryIssuesSensor implements ProjectSensor {

    private static final Logger LOGGER = Loggers.get(CreateSentryIssuesSensor.class);
    private static final double ARBITRARY_GAP = 2.0;
    private static final int LINE_1 = 1;

    private final Configuration config;

    public CreateSentryIssuesSensor(final Configuration config) {
        this.config = config;
    }

    private ApiToken getSentryToken() throws InvalidApiToken {
        String token = config.get(SentryProperties.TOKEN_KEY).orElse("");
        return new ApiToken(token);
    }

    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor
                .name("Annotate Sentry issues on line 1 of all Java files")
                .onlyWhenConfiguration(configuration -> configuration.hasKey(SentryProperties.TOKEN_KEY));
    }

    @Override
    public void execute(SensorContext context) {
        LOGGER.info("Running Sentry analyzer");

        ApiToken token;
        try {
            token = getSentryToken();
        } catch (InvalidApiToken e) {
            context.newAnalysisError().message("The Sentry token is missing or not valid").save();
            return;
        }

        LOGGER.info("Using integration token for Sentry: " + token);

        // TODO: Instead of iterating locations, iterate the bugs and then annotate locations and add secondaryLocations

        FileSystem fs = context.fileSystem();
        Iterable<InputFile> allFiles = fs.inputFiles(fs.predicates().all());

        for (InputFile file : allFiles) {
            NewIssue newIssue = context.newIssue()
                    .forRule(SentryRulesDefinition.ISSUES_RULE)
                    .gap(ARBITRARY_GAP);

            NewIssueLocation primaryLocation = newIssue.newLocation()
                    .on(file)
                    .at(file.selectLine(LINE_1))
                    .message("This is an internal rule violation");

            newIssue.at(primaryLocation);
            newIssue.save();

            NewExternalIssue extIssue = context.newExternalIssue()
                    .engineId("sentry")
                    .ruleId("sentry_issue")
                    .type(RuleType.BUG)
                    .severity(Severity.MAJOR);

            NewIssueLocation extLocation = extIssue.newLocation()
                    .on(file)
                    .at(file.selectLine(LINE_1))
                    .message("This is an external issue");

            extIssue.at(extLocation);
            extIssue.save();
        }
    }
}
