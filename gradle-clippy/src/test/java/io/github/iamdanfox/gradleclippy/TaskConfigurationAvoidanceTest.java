/*
 * (c) Copyright 2018 Dan Fox. All rights reserved.
 */

package io.github.iamdanfox.gradleclippy;

import com.google.errorprone.CompilationTestHelper;
import org.junit.Before;
import org.junit.Test;

// This doesn't seem to work from IntelliJ, run ./gradlew test --tests "*TaskConfigurationAvoidanceTest"
public class TaskConfigurationAvoidanceTest {
    private CompilationTestHelper compilationHelper;

    @Before
    public void before() {
        compilationHelper = CompilationTestHelper.newInstance(TaskConfigurationAvoidance.class, getClass());
    }

    @Test
    public void create_with_string() {
        compilationHelper.addSourceLines("MyProject.java",
                "import org.gradle.api.Plugin;",
                "import org.gradle.api.Project;",
                "public class MyProject implements Plugin<Project> {",
                "  public void apply(Project project) {",
                "    // BUG: Diagnostic contains: TaskConfigurationAvoidance",
                "    project.getTasks().create(\"myTask\");",
                "  }",
                "}")
                .doTest();
    }

    @Test
    public void create_with_string_and_closure() {
        compilationHelper.addSourceLines("MyProject.java",
                "import org.gradle.api.Plugin;",
                "import org.gradle.api.Project;",
                "public class MyProject implements Plugin<Project> {",
                "  public void apply(Project project) {",
                "    groovy.lang.Closure closure = null;",
                "    project.getTasks().create(\"myTask\", closure);",
                "  }",
                "}")
                .doTest();
    }
}
