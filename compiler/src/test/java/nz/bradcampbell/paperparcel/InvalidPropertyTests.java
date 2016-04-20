package nz.bradcampbell.paperparcel;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import javax.tools.JavaFileObject;
import org.junit.Test;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class InvalidPropertyTests {

  @Test public void getterHasAParameterTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import nz.bradcampbell.paperparcel.PaperParcel;",
            "@PaperParcel",
            "public final class Test {",
            "  private final int child;",
            "  public Test(int child) {",
            "    this.child = child;",
            "  }",
            "  public int getChild(int x) {",
            "    return this.child;",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .failsToCompile()
        .withErrorContaining("PaperParcel cannot read from the field named \"child\" which was "
            + "found when processing test.Test. The field must either be non-private, or have a "
            + "getter method with no arguments and have one of the following names: [child, "
            + "isChild, getChild]. Alternatively you can exclude the field by making it static, "
            + "transient, or using the ExcludeFields annotation on test.Test")
        .in(source)
        .onLine(5);
  }

  @Test public void getterHasWrongReturnTypeTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import nz.bradcampbell.paperparcel.PaperParcel;",
            "@PaperParcel",
            "public final class Test {",
            "  private final int child;",
            "  public Test(int child) {",
            "    this.child = child;",
            "  }",
            "  public long getChild() {",
            "    return this.child;",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .failsToCompile()
        .withErrorContaining("PaperParcel cannot read from the field named \"child\" which was "
            + "found when processing test.Test. The field must either be non-private, or have a "
            + "getter method with no arguments and have one of the following names: [child, "
            + "isChild, getChild]. Alternatively you can exclude the field by making it static, "
            + "transient, or using the ExcludeFields annotation on test.Test")
        .in(source)
        .onLine(5);
  }

  @Test public void getterHasWrongNameTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import nz.bradcampbell.paperparcel.PaperParcel;",
            "@PaperParcel",
            "public final class Test {",
            "  private final int child;",
            "  public Test(int child) {",
            "    this.child = child;",
            "  }",
            "  public int getKid() {",
            "    return this.child;",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .failsToCompile()
        .withErrorContaining("PaperParcel cannot read from the field named \"child\" which was "
            + "found when processing test.Test. The field must either be non-private, or have a "
            + "getter method with no arguments and have one of the following names: [child, "
            + "isChild, getChild]. Alternatively you can exclude the field by making it static, "
            + "transient, or using the ExcludeFields annotation on test.Test")
        .in(source)
        .onLine(5);
  }

  @Test public void transientPropertyTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import nz.bradcampbell.paperparcel.PaperParcel;",
            "@PaperParcel",
            "public final class Test {",
            "  private transient final int child1 = 0;",
            "  private final int child2;",
            "  public Test(int child2) {",
            "    this.child2 = child2;",
            "  }",
            "  public int getChild1() {",
            "    return this.child1;",
            "  }",
            "  public int getChild2() {",
            "    return this.child2;",
            "  }",
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
            "      int child2 = in.readInt();",
            "      Test data = new Test(child2);",
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
            "    int child2 = this.data.getChild2();",
            "    dest.writeInt(child2);",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void genericClassAnnotatedTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import nz.bradcampbell.paperparcel.PaperParcel;",
            "@PaperParcel",
            "public final class Test<T> {",
            "  private final T child;",
            "  public Test(T child) {",
            "    this.child = child;",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .failsToCompile()
        .withErrorContaining("PaperParcel types cannot have type parameters")
        .in(source)
        .onLine(4);
  }
}
