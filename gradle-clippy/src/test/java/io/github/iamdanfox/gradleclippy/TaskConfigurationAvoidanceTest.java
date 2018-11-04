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
    public void smoke_test() {
        compilationHelper.addSourceLines(
                "Bean.java",
                "class Bean {",
                "Exception foo = new IllegalArgumentException(\"Foo\");",
                "}").doTest();
    }
}
