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
package io.sentry.integrations.sonar.settings;

import java.util.List;

import org.sonar.api.PropertyType;
import org.sonar.api.config.PropertyDefinition;

import static java.util.Arrays.asList;

public class SentryProperties {

    public static final String TOKEN_KEY = "io.sentry.integrations.sonar.token";
    public static final String ORGANIZATION_KEY = "io.sentry.integrations.sonar.organization";
    public static final String PROJECT_KEY = "io.sentry.integrations.sonar.project_id";
    public static final String SENTRY_CATEGORY = "Sentry";

    private SentryProperties() {
        // only statics
    }

    public static List<PropertyDefinition> getProperties() {
        return asList(
                // https://docs.sentry.io/product/integrations/integration-platform/
                PropertyDefinition.builder(TOKEN_KEY)
                        .name("Integration Token")
                        .description("Access token of the internal integration in Sentry")
                        .defaultValue("")
                        .category(SENTRY_CATEGORY)
                        .build(),
                PropertyDefinition.builder(ORGANIZATION_KEY)
                        .name("Organization Slug")
                        .description("The Sentry organization slug where the project resides")
                        .defaultValue("")
                        .category(SENTRY_CATEGORY)
                        .build(),
                PropertyDefinition.builder(PROJECT_KEY)
                        .name("Project ID")
                        .description("Numeric ID of the Sentry project")
                        .defaultValue("")
                        .type(PropertyType.INTEGER)
                        .category(SENTRY_CATEGORY)
                        .build()
                );
    }

}
