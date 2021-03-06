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
import com.google.errorprone.matchers.Matcher;
import com.google.errorprone.matchers.Matchers;
import com.google.errorprone.matchers.method.MethodMatchers;
import com.google.errorprone.util.ASTHelpers;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodInvocationTree;

@AutoService(BugChecker.class)
@BugPattern(
        name = "TaskConfigurationAvoidance",
        link = "https://github.com/iamdanfox/gradle-clippy#TaskConfigurationAvoidance",
        linkType = BugPattern.LinkType.CUSTOM,
        category = BugPattern.Category.ONE_OFF,
        severity = BugPattern.SeverityLevel.WARNING,
        summary = "Avoid configuring tasks unless absolutely necessary")
public final class TaskConfigurationAvoidance extends BugChecker implements BugChecker.MethodInvocationTreeMatcher {

    // https://docs.gradle.org/current/userguide/task_configuration_avoidance.html
    private static final MethodMatchers.MethodNameMatcher CREATE_MATCHER =
            MethodMatchers.instanceMethod()
                    .onExactClass("org.gradle.api.tasks.TaskContainer")
                    .named("create");

    private static final MethodMatchers.MethodNameMatcher GET_BY_NAME =
            MethodMatchers.instanceMethod()
                    .onDescendantOf("org.gradle.api.tasks.TaskCollection")
                    .named("getByName");

    private static final MethodMatchers.ParameterMatcher CROSS_PROJECT_LOOKUP =
            MethodMatchers.instanceMethod()
                    .onExactClass("org.gradle.api.tasks.TaskContainer")
                    .namedAnyOf("getByPath", "findByPath")
                    .withParameters("java.lang.String");

    @Override
    public Description matchMethodInvocation(MethodInvocationTree tree, VisitorState state) {
        Matcher<ExpressionTree> deprecatedCreateUsage =
                Matchers.anyOf(
                        CREATE_MATCHER.withParameters("java.util.Map"),
                        CREATE_MATCHER.withParameters("java.util.Map", "groovy.lang.Closure"));
        if (deprecatedCreateUsage.matches(tree, state)) {
            return buildDescription(tree)
                    .setMessage("Use .register(...) to avoid eagerly configuring this task")
                    .build();
        }

        if (CREATE_MATCHER.withParameters("java.lang.String").matches(tree, state)) {
            return replaceWithRegister(tree, state, "java.lang.String");
        }

        if (CREATE_MATCHER.withParameters("java.lang.String", "java.lang.Class").matches(tree, state)) {
            return replaceWithRegister(tree, state, "java.lang.String, java.lang.Class");
        }

        if (CREATE_MATCHER.withParameters("java.lang.String", "java.lang.Class", "org.gradle.api.Action")
                .matches(tree, state)) {
            return replaceWithRegister(tree, state, "java.lang.String, java.lang.Class, org.gradle.api.Action");
        }

        if (CREATE_MATCHER.withParameters("java.lang.String", "groovy.lang.Closure").matches(tree, state)) {
            return replaceWithRegister(tree, state, "java.lang.String, org.gradle.api.Action");
        }

        if (GET_BY_NAME.withParameters("java.lang.String").matches(tree, state)) {
            return buildDescription(tree)
                    .setMessage("Use .named(java.lang.String) to avoid eagerly configuring this task")
                    .addFix(SuggestedFix.replace(
                            state.getEndPosition(ASTHelpers.getReceiver(tree)),
                            state.getEndPosition(tree.getMethodSelect()),
                            ".named"))
                    .build();
        }

        if (GET_BY_NAME.withParameters("java.lang.String", "groovy.lang.Closure").matches(tree, state)) {
            return buildDescription(tree)
                    .setMessage("Use .named(java.lang.String).configure(org.gradle.api.Action) "
                            + "to avoid eagerly configuring this task")
                    .build();
        }

        if (CROSS_PROJECT_LOOKUP.matches(tree, state)) {
            return buildDescription(tree)
                    .setMessage("Accessing tasks from another project requires a "
                            + "specific ordering of project evaluation.")
                    .build();
        }

        return Description.NO_MATCH;
    }

    private Description replaceWithRegister(MethodInvocationTree tree, VisitorState state, String signature) {
        return buildDescription(tree)
                .setMessage("Use .register(" + signature + ") to avoid eagerly configuring this task")
                .addFix(SuggestedFix.replace(
                        state.getEndPosition(ASTHelpers.getReceiver(tree)),
                        state.getEndPosition(tree.getMethodSelect()),
                        ".register"))
                .build();
    }
}
