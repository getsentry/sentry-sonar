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
package io.sentry.integrations.sonar;

import io.sentry.integrations.sonar.rules.*;
import org.sonar.api.Plugin;
import io.sentry.integrations.sonar.hooks.PostJobInScanner;
import io.sentry.integrations.sonar.hooks.DisplayQualityGateStatus;
import io.sentry.integrations.sonar.languages.FooLanguage;
import io.sentry.integrations.sonar.languages.FooQualityProfile;
import io.sentry.integrations.sonar.measures.ComputeSizeAverage;
import io.sentry.integrations.sonar.measures.ComputeSizeRating;
import io.sentry.integrations.sonar.measures.ExampleMetrics;
import io.sentry.integrations.sonar.measures.SetSizeOnFilesSensor;
import io.sentry.integrations.sonar.settings.FooLanguageProperties;
import io.sentry.integrations.sonar.settings.SentryProperties;
import io.sentry.integrations.sonar.settings.SayHelloFromScanner;

/**
 * This class is the entry point for all extensions. It is referenced in pom.xml.
 */
public class SentryPlugin implements Plugin {

    @Override
    public void define(Context context) {
        // tutorial on hooks
        // http://docs.sonarqube.org/display/DEV/Adding+Hooks
        // context.addExtensions(PostJobInScanner.class, DisplayQualityGateStatus.class);

        // tutorial on languages
        // context.addExtensions(FooLanguage.class, FooQualityProfile.class);
        // context.addExtensions(FooLanguageProperties.getProperties());

        // tutorial on measures
        // context.addExtensions(ExampleMetrics.class, SetSizeOnFilesSensor.class, ComputeSizeAverage.class, ComputeSizeRating.class);

        // tutorial on rules
        // context.addExtensions(JavaRulesDefinition.class, CreateIssuesOnJavaFilesSensor.class);
        // context.addExtensions(FooLintRulesDefinition.class, FooLintIssuesLoaderSensor.class);

        context.addExtensions(SentryRulesDefinition.class, CreateSentryIssuesSensor.class);
        context.addExtensions(SentryProperties.getProperties());

        // tutorial on settings
        //   .addExtension(SayHelloFromScanner.class);
    }
}
