package nz.bradcampbell.dataparcel;

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
        "import nz.bradcampbell.dataparcel.DataParcel;",
        "@DataParcel",
        "public final class Test {",
        "private final String testString;",
        "public Test(String testString) {",
        "this.testString = testString;",
        "}",
        "}"
    ));

    assertAbout(javaSource()).that(source)
        .processedWith(new DataParcelProcessor())
        .failsToCompile();
  }

  @Test public void genericClassAnnotatedTest() throws Exception {
    JavaFileObject invalidSource = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.dataparcel.DataParcel;",
        "@DataParcel",
        "public final class Test<T> {",
        "private final T test;",
        "public Test(T test) {",
        "this.test = test;",
        "}",
        "}"
    ));

    JavaFileObject validSource = JavaFileObjects.forSourceString("test.Test1", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.dataparcel.DataParcel;",
        "@DataParcel",
        "public final class Test1 {",
        "private final String testString;",
        "public Test1(String testString) {",
        "this.testString = testString;",
        "}",
        "}"
    ));

    assertAbout(javaSources()).that(Arrays.asList(invalidSource, validSource))
        .processedWith(new DataParcelProcessor())
        .failsToCompile();
  }
}
