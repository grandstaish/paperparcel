package nz.bradcampbell.paperparcel;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import java.util.Arrays;
import javax.tools.JavaFileObject;
import org.junit.Test;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;

public class ExcludeFieldsTests {

  @Test public void transientFieldIsIgnoredTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import nz.bradcampbell.paperparcel.PaperParcel;",
            "@PaperParcel",
            "public final class Test {",
            "  private transient final int child = 0;",
            "}"
        ));

    JavaFileObject expectedSource =
        JavaFileObjects.forSourceString("test/TestParcel", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import java.lang.Override;",
            "import nz.bradcampbell.paperparcel.TypedParcelable;",
            "public final class TestParcel implements TypedParcelable<Test> {",
            "  public static final Parcelable.Creator<TestParcel> CREATOR = ",
            "      new Parcelable.Creator<TestParcel>() {",
            "    @Override public TestParcel createFromParcel(Parcel in) {",
            "      Test data = new Test();",
            "      return new TestParcel(data);",
            "    }",
            "    @Override public TestParcel[] newArray(int size) {",
            "      return new TestParcel[size];",
            "    }",
            "  };",
            "  private final Test data;",
            "  public TestParcel(Test data) {",
            "    this.data = data;",
            "  }",
            "  @Override public Test get() {",
            "    return this.data;",
            "  }",
            "  @Override public int describeContents() {",
            "    return 0;",
            "  }",
            "  @Override public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void staticFieldIsIgnoredTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import nz.bradcampbell.paperparcel.PaperParcel;",
            "@PaperParcel",
            "public final class Test {",
            "  private static final int child = 0;",
            "}"
        ));

    JavaFileObject expectedSource =
        JavaFileObjects.forSourceString("test/TestParcel", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import java.lang.Override;",
            "import nz.bradcampbell.paperparcel.TypedParcelable;",
            "public final class TestParcel implements TypedParcelable<Test> {",
            "  public static final Parcelable.Creator<TestParcel> CREATOR = ",
            "      new Parcelable.Creator<TestParcel>() {",
            "    @Override public TestParcel createFromParcel(Parcel in) {",
            "      Test data = new Test();",
            "      return new TestParcel(data);",
            "    }",
            "    @Override public TestParcel[] newArray(int size) {",
            "      return new TestParcel[size];",
            "    }",
            "  };",
            "  private final Test data;",
            "  public TestParcel(Test data) {",
            "    this.data = data;",
            "  }",
            "  @Override public Test get() {",
            "    return this.data;",
            "  }",
            "  @Override public int describeContents() {",
            "    return 0;",
            "  }",
            "  @Override public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void excludeFieldByNameTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import nz.bradcampbell.paperparcel.ExcludeFields;",
            "import nz.bradcampbell.paperparcel.FieldMatcher;",
            "import nz.bradcampbell.paperparcel.PaperParcel;",
            "@PaperParcel",
            "@ExcludeFields(@FieldMatcher(name=\"child\"))",
            "public final class Test {",
            "  public int child;",
            "}"
        ));

    JavaFileObject expectedSource =
        JavaFileObjects.forSourceString("test/TestParcel", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import java.lang.Override;",
            "import nz.bradcampbell.paperparcel.TypedParcelable;",
            "public final class TestParcel implements TypedParcelable<Test> {",
            "  public static final Parcelable.Creator<TestParcel> CREATOR = ",
            "      new Parcelable.Creator<TestParcel>() {",
            "    @Override public TestParcel createFromParcel(Parcel in) {",
            "      Test data = new Test();",
            "      return new TestParcel(data);",
            "    }",
            "    @Override public TestParcel[] newArray(int size) {",
            "      return new TestParcel[size];",
            "    }",
            "  };",
            "  private final Test data;",
            "  public TestParcel(Test data) {",
            "    this.data = data;",
            "  }",
            "  @Override public Test get() {",
            "    return this.data;",
            "  }",
            "  @Override public int describeContents() {",
            "    return 0;",
            "  }",
            "  @Override public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void excludeFieldByTypeTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import nz.bradcampbell.paperparcel.ExcludeFields;",
            "import nz.bradcampbell.paperparcel.FieldMatcher;",
            "import nz.bradcampbell.paperparcel.PaperParcel;",
            "@PaperParcel",
            "@ExcludeFields(@FieldMatcher(type=int.class))",
            "public final class Test {",
            "  public int child;",
            "}"
        ));

    JavaFileObject expectedSource =
        JavaFileObjects.forSourceString("test/TestParcel", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import java.lang.Override;",
            "import nz.bradcampbell.paperparcel.TypedParcelable;",
            "public final class TestParcel implements TypedParcelable<Test> {",
            "  public static final Parcelable.Creator<TestParcel> CREATOR = ",
            "      new Parcelable.Creator<TestParcel>() {",
            "    @Override public TestParcel createFromParcel(Parcel in) {",
            "      Test data = new Test();",
            "      return new TestParcel(data);",
            "    }",
            "    @Override public TestParcel[] newArray(int size) {",
            "      return new TestParcel[size];",
            "    }",
            "  };",
            "  private final Test data;",
            "  public TestParcel(Test data) {",
            "    this.data = data;",
            "  }",
            "  @Override public Test get() {",
            "    return this.data;",
            "  }",
            "  @Override public int describeContents() {",
            "    return 0;",
            "  }",
            "  @Override public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void excludeFieldByDeclaringClassTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import nz.bradcampbell.paperparcel.ExcludeFields;",
            "import nz.bradcampbell.paperparcel.FieldMatcher;",
            "import nz.bradcampbell.paperparcel.PaperParcel;",
            "@PaperParcel",
            "@ExcludeFields(@FieldMatcher(declaringClass=Test.class))",
            "public final class Test {",
            "  public int child;",
            "}"
        ));

    JavaFileObject expectedSource =
        JavaFileObjects.forSourceString("test/TestParcel", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import java.lang.Override;",
            "import nz.bradcampbell.paperparcel.TypedParcelable;",
            "public final class TestParcel implements TypedParcelable<Test> {",
            "  public static final Parcelable.Creator<TestParcel> CREATOR = ",
            "      new Parcelable.Creator<TestParcel>() {",
            "    @Override public TestParcel createFromParcel(Parcel in) {",
            "      Test data = new Test();",
            "      return new TestParcel(data);",
            "    }",
            "    @Override public TestParcel[] newArray(int size) {",
            "      return new TestParcel[size];",
            "    }",
            "  };",
            "  private final Test data;",
            "  public TestParcel(Test data) {",
            "    this.data = data;",
            "  }",
            "  @Override public Test get() {",
            "    return this.data;",
            "  }",
            "  @Override public int describeContents() {",
            "    return 0;",
            "  }",
            "  @Override public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void excludeFieldByAnnotationTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import nz.bradcampbell.paperparcel.ExcludeFields;",
            "import nz.bradcampbell.paperparcel.FieldMatcher;",
            "import nz.bradcampbell.paperparcel.PaperParcel;",
            "@PaperParcel",
            "@ExcludeFields(@FieldMatcher(annotation=Exclude.class))",
            "public final class Test {",
            "  @Exclude public int child;",
            "}"
        ));

    JavaFileObject excludeAnnotation =
        JavaFileObjects.forSourceString("test.Exclude", Joiner.on('\n').join(
            "package test;",
            "public @interface Exclude {",
            "}"
        ));

    JavaFileObject expectedSource =
        JavaFileObjects.forSourceString("test/TestParcel", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import java.lang.Override;",
            "import nz.bradcampbell.paperparcel.TypedParcelable;",
            "public final class TestParcel implements TypedParcelable<Test> {",
            "  public static final Parcelable.Creator<TestParcel> CREATOR = ",
            "      new Parcelable.Creator<TestParcel>() {",
            "    @Override public TestParcel createFromParcel(Parcel in) {",
            "      Test data = new Test();",
            "      return new TestParcel(data);",
            "    }",
            "    @Override public TestParcel[] newArray(int size) {",
            "      return new TestParcel[size];",
            "    }",
            "  };",
            "  private final Test data;",
            "  public TestParcel(Test data) {",
            "    this.data = data;",
            "  }",
            "  @Override public Test get() {",
            "    return this.data;",
            "  }",
            "  @Override public int describeContents() {",
            "    return 0;",
            "  }",
            "  @Override public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(source, excludeAnnotation))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void emptyFieldMatcherMatchesAllFieldsTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import nz.bradcampbell.paperparcel.ExcludeFields;",
            "import nz.bradcampbell.paperparcel.FieldMatcher;",
            "import nz.bradcampbell.paperparcel.PaperParcel;",
            "@PaperParcel",
            "@ExcludeFields(@FieldMatcher())",
            "public final class Test {",
            "  public int child;",
            "}"
        ));

    JavaFileObject expectedSource =
        JavaFileObjects.forSourceString("test/TestParcel", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import java.lang.Override;",
            "import nz.bradcampbell.paperparcel.TypedParcelable;",
            "public final class TestParcel implements TypedParcelable<Test> {",
            "  public static final Parcelable.Creator<TestParcel> CREATOR = ",
            "      new Parcelable.Creator<TestParcel>() {",
            "    @Override public TestParcel createFromParcel(Parcel in) {",
            "      Test data = new Test();",
            "      return new TestParcel(data);",
            "    }",
            "    @Override public TestParcel[] newArray(int size) {",
            "      return new TestParcel[size];",
            "    }",
            "  };",
            "  private final Test data;",
            "  public TestParcel(Test data) {",
            "    this.data = data;",
            "  }",
            "  @Override public Test get() {",
            "    return this.data;",
            "  }",
            "  @Override public int describeContents() {",
            "    return 0;",
            "  }",
            "  @Override public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void excludeSameNamedFieldInBaseClassOnlyTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import nz.bradcampbell.paperparcel.ExcludeFields;",
            "import nz.bradcampbell.paperparcel.FieldMatcher;",
            "import nz.bradcampbell.paperparcel.PaperParcel;",
            "@PaperParcel",
            "@ExcludeFields(@FieldMatcher(name=\"child\", declaringClass=Base.class))",
            "public final class Test extends Base {",
            "  public int child;",
            "}"
        ));

    JavaFileObject baseClass =
        JavaFileObjects.forSourceString("test.Base", Joiner.on('\n').join(
            "package test;",
            "public class Base {",
            "  private int child;",
            "}"
        ));


    JavaFileObject expectedSource =
        JavaFileObjects.forSourceString("test/TestParcel", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import java.lang.Override;",
            "import nz.bradcampbell.paperparcel.TypedParcelable;",
            "public final class TestParcel implements TypedParcelable<Test> {",
            "  public static final Parcelable.Creator<TestParcel> CREATOR = ",
            "      new Parcelable.Creator<TestParcel>() {",
            "    @Override public TestParcel createFromParcel(Parcel in) {",
            "      int child = in.readInt();",
            "      Test data = new Test();",
            "      data.child = child;",
            "      return new TestParcel(data);",
            "    }",
            "    @Override public TestParcel[] newArray(int size) {",
            "      return new TestParcel[size];",
            "    }",
            "  };",
            "  private final Test data;",
            "  public TestParcel(Test data) {",
            "    this.data = data;",
            "  }",
            "  @Override public Test get() {",
            "    return this.data;",
            "  }",
            "  @Override public int describeContents() {",
            "    return 0;",
            "  }",
            "  @Override public void writeToParcel(Parcel dest, int flags) {",
            "    int child = this.data.child;",
            "    dest.writeInt(child);",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(source, baseClass))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }
}
