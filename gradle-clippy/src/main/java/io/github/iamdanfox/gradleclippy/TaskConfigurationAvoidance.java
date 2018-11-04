/*
 * (c) Copyright 2018 Dan Fox. All rights reserved.
 */

package io.github.iamdanfox.gradleclippy;

import com.google.auto.service.AutoService;
import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.fixes.SuggestedFix;
import com.google.errorprone.matchers.Description;
import com.google.errorprone.matchers.method.MethodMatchers;
import com.google.errorprone.util.ASTHelpers;
import com.sun.source.tree.MethodInvocationTree;

@AutoService(BugChecker.class)
@BugPattern(
        name = "TaskConfigurationAvoidance",
        link = "https://github.com/iamdanfox/gradle-clippy",
        linkType = BugPattern.LinkType.CUSTOM,
        category = BugPattern.Category.ONE_OFF,
        severity = BugPattern.SeverityLevel.SUGGESTION,
        summary = "Avoid unnecessarily configurating tasks")
public final class TaskConfigurationAvoidance extends BugChecker implements BugChecker.MethodInvocationTreeMatcher {

    // https://docs.gradle.org/current/userguide/task_configuration_avoidance.html
    private static final MethodMatchers.MethodNameMatcher CREATE_MATCHER =
            MethodMatchers.instanceMethod()
                    .onExactClass("org.gradle.api.tasks.TaskContainer")
                    .named("create");

    @Override
    public Description matchMethodInvocation(MethodInvocationTree tree, VisitorState state) {
        if (CREATE_MATCHER.withParameters("java.util.Map").matches(tree, state)) {
            return buildDescription(tree)
                    .setMessage("Use .register(...) to avoid eagerly configuring this task")
                    .build();
        }

        if (CREATE_MATCHER.withParameters("java.lang.String").matches(tree, state)) {
            return buildDescription(tree)
                    .setMessage("Use .register(java.lang.String) to avoid eagerly configuring this task")
                    .addFix(SuggestedFix.replace(
                            state.getEndPosition(ASTHelpers.getReceiver(tree)),
                            state.getEndPosition(tree.getMethodSelect()),
                            ".register"))
                    .build();
        }

        if (CREATE_MATCHER.withParameters("java.lang.String", "groovy.lang.Closure").matches(tree, state)) {
            return buildDescription(tree)
                    .setMessage("Use .register(java.lang.String, org.gradle.api.Action) "
                            + "to avoid eagerly configuring this task")
                    .addFix(SuggestedFix.replace(
                            state.getEndPosition(ASTHelpers.getReceiver(tree)),
                            state.getEndPosition(tree.getMethodSelect()),
                            ".register"))
                    .build();
        }

        return Description.NO_MATCH;
    }
}
