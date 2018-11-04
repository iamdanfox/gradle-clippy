/*
 * (c) Copyright 2018 Dan Fox. All rights reserved.
 */

package io.github.iamdanfox.gradleclippy;

import com.google.auto.service.AutoService;
import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.matchers.Description;
import com.google.errorprone.matchers.method.MethodMatchers;
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

    @Override
    public Description matchMethodInvocation(MethodInvocationTree tree, VisitorState state) {
        // https://docs.gradle.org/current/userguide/task_configuration_avoidance.html
        MethodMatchers.MethodNameMatcher matcher = MethodMatchers.instanceMethod()
                .onExactClass("org.gradle.api.tasks.TaskContainer")
                .named("create");

        if (matcher.matches(tree, state)) {
            return buildDescription(tree)
                    .setMessage("TODO")
                    .build();
        }

        return Description.NO_MATCH;
    }
}
