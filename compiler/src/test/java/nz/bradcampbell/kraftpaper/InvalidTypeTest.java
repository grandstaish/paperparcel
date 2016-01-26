package nz.bradcampbell.kraftpaper;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import javax.tools.JavaFileObject;

import java.util.Arrays;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;

public class InvalidTypeTest {

  @Test public void noGetterMethodTest() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.kraftpaper.KraftPaper;",
        "@KraftPaper",
        "public final class Test {",
        "private final String child;",
        "public Test(String child) {",
        "this.child = child;",
        "}",
        "}"
    ));

    assertAbout(javaSource()).that(source)
        .processedWith(new KraftPaperProcessor())
        .failsToCompile();
  }

  @Test public void genericClassAnnotatedTest() throws Exception {
    JavaFileObject invalidSource = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.kraftpaper.KraftPaper;",
        "@KraftPaper",
        "public final class Test<T> {",
        "private final T child;",
        "public Test(T child) {",
        "this.child = child;",
        "}",
        "}"
    ));

    JavaFileObject validSource = JavaFileObjects.forSourceString("test.Test1", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.kraftpaper.KraftPaper;",
        "@KraftPaper",
        "public final class Test1 {",
        "private final String child;",
        "public Test1(String child) {",
        "this.child = child;",
        "}",
        "}"
    ));

    assertAbout(javaSources()).that(Arrays.asList(invalidSource, validSource))
        .processedWith(new KraftPaperProcessor())
        .failsToCompile();
  }
}
