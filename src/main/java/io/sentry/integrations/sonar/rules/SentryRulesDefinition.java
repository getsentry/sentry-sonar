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

import org.sonar.api.rule.RuleKey;
import org.sonar.api.rule.RuleStatus;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RulesDefinition;

public class SentryRulesDefinition implements RulesDefinition {

    public static final String REPOSITORY = "sentry";
    public static final String LANGUAGE = "python";
    public static final RuleKey ISSUES_RULE = RuleKey.of(REPOSITORY, "issues");
    public static final String ISSUES_DESCRIPTION = "Annotate with Sentry Issues";

    @Override
    public void define(Context context) {
        NewRepository repository = context.createRepository(REPOSITORY, LANGUAGE).setName("Sentry");

        NewRule issuesRule = repository.createRule(ISSUES_RULE.rule())
                .setName("Issues")
                .setHtmlDescription(ISSUES_DESCRIPTION)
                .setStatus(RuleStatus.BETA)
                .setSeverity(Severity.MAJOR)
                .setType(RuleType.BUG);

        // TODO: Configure a more realistic debt remediation function
        issuesRule.setDebtRemediationFunction(issuesRule.debtRemediationFunctions().linearWithOffset("1h", "30min"));

        repository.done();
    }
}
