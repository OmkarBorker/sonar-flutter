/*
 * SonarQube Flutter Plugin
 * Copyright (C) 2020 inside|app
 * contact@insideapp.fr
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
package fr.insideapp.sonarqube.dart.lang.issues.dartanalyzer;

import fr.insideapp.sonarqube.dart.lang.Dart;
import fr.insideapp.sonarqube.dart.lang.issues.RepositoryRule;
import fr.insideapp.sonarqube.dart.lang.issues.RepositoryRuleParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RulesDefinition;

import java.io.IOException;
import java.util.List;

public class DartAnalyzerRulesDefinition implements RulesDefinition {
    private static final Logger LOGGER = LoggerFactory.getLogger(DartAnalyzerRulesDefinition.class);
    public static final String REPOSITORY_KEY = "dartanalyzer";
    public static final String REPOSITORY_NAME = REPOSITORY_KEY;
    public static final String RULES_FILE = "/dartanalyzer/rules.json";
    
    @Override
    public void define(Context context) {
        NewRepository repository = context.createRepository(REPOSITORY_KEY, Dart.KEY).setName(REPOSITORY_NAME);
        RepositoryRuleParser repositoryRuleParser = new RepositoryRuleParser();

        try {
            List<RepositoryRule> rules = repositoryRuleParser.parse(RULES_FILE);
            for (RepositoryRule rule : rules) {

                RulesDefinition.NewRule newRule = repository.createRule(rule.key)
                        .setName(rule.name)
                        .setSeverity(rule.severity.name())
                        .setType(RuleType.valueOf(rule.type.name()))
                        .setHtmlDescription(rule.description);
                newRule.setDebtRemediationFunction(newRule.debtRemediationFunctions().constantPerIssue(rule.debt));
            }
        } catch (IOException e) {
            LOGGER.error(String.format("Failed to load dartanalyzer rules"), e);
        }

        repository.done();
    }
}
