/*
 * (c) Copyright 2018 Dan Fox. All rights reserved.
 */

package io.github.iamdanfox.gradleclippy;

import com.google.errorprone.BugCheckerRefactoringTestHelper;
import com.google.errorprone.CompilationTestHelper;
import org.junit.Before;
import org.junit.Test;

// This doesn't seem to work from IntelliJ, run ./gradlew test --tests "*TaskConfigurationAvoidanceTest"
public class TaskConfigurationAvoidanceTest {
    private CompilationTestHelper compilationHelper;
    private BugCheckerRefactoringTestHelper fixHelper;

    @Before
    public void before() {
        compilationHelper = CompilationTestHelper.newInstance(TaskConfigurationAvoidance.class, getClass());
        fixHelper = BugCheckerRefactoringTestHelper.newInstance(new TaskConfigurationAvoidance(), getClass());
    }

    @Test
    public void create_with_string() {
        fixHelper
                .addInputLines(
                        "MyProject.java",
                        "import org.gradle.api.Plugin;",
                        "import org.gradle.api.Project;",
                        "public class MyProject implements Plugin<Project> {",
                        "  public void apply(Project project) {",
                        "    String name = \"myTask\";",
                        "    // BUG: Diagnostic contains: .register(java.lang.String)",
                        "    project.getTasks().create(name);",
                        "  }",
                        "}")
                .addOutputLines(
                        "MyProject.java",
                        "import org.gradle.api.Plugin;",
                        "import org.gradle.api.Project;",
                        "public class MyProject implements Plugin<Project> {",
                        "  public void apply(Project project) {",
                        "    String name = \"myTask\";",
                        "    // BUG: Diagnostic contains: .register(java.lang.String)",
                        "    project.getTasks().register(name);",
                        "  }",
                        "}")
                .doTest();
    }

    @Test
    public void create_with_string_and_closure() {
        fixHelper
                .addInputLines(
                        "MyProject.java",
                        "import org.gradle.api.Plugin;",
                        "import org.gradle.api.Project;",
                        "public class MyProject implements Plugin<Project> {",
                        "  public void apply(Project project) {",
                        "    groovy.lang.Closure closure = null;",
                        "    // BUG: Diagnostic contains: .register(java.lang.String, org.gradle.api.Action)",
                        "    project.getTasks().create(\"myTask\", closure);",
                        "  }",
                        "}")
                .addOutputLines(
                        "MyProject.java",
                        "import org.gradle.api.Plugin;",
                        "import org.gradle.api.Project;",
                        "public class MyProject implements Plugin<Project> {",
                        "  public void apply(Project project) {",
                        "    groovy.lang.Closure closure = null;",
                        "    // BUG: Diagnostic contains: .register(java.lang.String, org.gradle.api.Action)",
                        "    project.getTasks().register(\"myTask\", closure);",
                        "  }",
                        "}")
                .doTest();
    }
}
