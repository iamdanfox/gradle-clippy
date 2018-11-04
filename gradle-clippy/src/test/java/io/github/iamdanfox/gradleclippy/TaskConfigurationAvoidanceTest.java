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
    public void create_with_map() {
        compilationHelper
                .addSourceLines(
                        "MyProject.java",
                        "import org.gradle.api.Plugin;",
                        "import org.gradle.api.Project;",
                        "public class MyProject implements Plugin<Project> {",
                        "  public void apply(Project project) {",
                        "    java.util.Map<String, Object> map = java.util.Collections.emptyMap();",
                        "    // BUG: Diagnostic contains: Use .register(...) to avoid eagerly configuring this task",
                        "    project.getTasks().create(map);",
                        "  }",
                        "}")
                .doTest();
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
                        "    project.getTasks().register(name);",
                        "  }",
                        "}")
                .doTest();
    }

    @Test
    public void create_with_string_and_class() {
        fixHelper
                .addInputLines(
                        "MyProject.java",
                        "import org.gradle.api.Plugin;",
                        "import org.gradle.api.Project;",
                        "public class MyProject implements Plugin<Project> {",
                        "  public void apply(Project project) {",
                        "    project.getTasks().create(\"name\", org.gradle.api.DefaultTask.class);",
                        "  }",
                        "}")
                .addOutputLines(
                        "MyProject.java",
                        "import org.gradle.api.Plugin;",
                        "import org.gradle.api.Project;",
                        "public class MyProject implements Plugin<Project> {",
                        "  public void apply(Project project) {",
                        "    project.getTasks().register(\"name\", org.gradle.api.DefaultTask.class);",
                        "  }",
                        "}")
                .doTest();
    }

    @Test
    public void create_with_string_and_closure() {
        compilationHelper
                .addSourceLines(
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
                .doTest();
    }

    @Test
    public void getByName_with_string() {
        fixHelper
                .addInputLines(
                        "MyProject.java",
                        "import org.gradle.api.Plugin;",
                        "import org.gradle.api.Project;",
                        "public class MyProject implements Plugin<Project> {",
                        "  public void apply(Project project) {",
                        "    project.getTasks().getByName(\"task\");",
                        "  }",
                        "}")
                .addOutputLines(
                        "MyProject.java",
                        "import org.gradle.api.Plugin;",
                        "import org.gradle.api.Project;",
                        "public class MyProject implements Plugin<Project> {",
                        "  public void apply(Project project) {",
                        "    project.getTasks().named(\"task\");",
                        "  }",
                        "}")
                .doTest();
    }

    @Test
    public void getByName_with_string_and_closure() {
        compilationHelper
                .addSourceLines(
                        "MyProject.java",
                        "import org.gradle.api.Plugin;",
                        "import org.gradle.api.Project;",
                        "public class MyProject implements Plugin<Project> {",
                        "  public void apply(Project project) {",
                        "    groovy.lang.Closure closure = null;",
                        "// BUG: Diagnostic contains: Use .named(java.lang.String).configure(org.gradle.api.Action)",
                        "    project.getTasks().getByName(\"task\", closure);",
                        "  }",
                        "}")
                .doTest();
    }

    @Test
    public void getByPath_with_string() {
        compilationHelper
                .addSourceLines(
                        "MyProject.java",
                        "import org.gradle.api.Plugin;",
                        "import org.gradle.api.Project;",
                        "public class MyProject implements Plugin<Project> {",
                        "  public void apply(Project project) {",
                        "    // BUG: Diagnostic contains: Accessing tasks from another project",
                        "    project.getTasks().getByPath(\":other:task\");",
                        "  }",
                        "}")
                .doTest();
    }

    @Test
    public void withType_getByName() {
        compilationHelper
                .addSourceLines(
                        "MyProject.java",
                        "import org.gradle.api.Plugin;",
                        "import org.gradle.api.Project;",
                        "public class MyProject implements Plugin<Project> {",
                        "  public void apply(Project project) {",
                        "    // BUG: Diagnostic contains: Use .named(java.lang.String)",
                        "    project.getTasks().withType(org.gradle.api.DefaultTask.class).getByName(\"foo\");",
                        "  }",
                        "}")
                .doTest();
    }
}
