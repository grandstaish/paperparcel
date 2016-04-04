package nz.bradcampbell.paperparcel;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

public class CustomMethodNameTests {

  @Test public void customMethodNameTest() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.paperparcel.PaperParcel;",
        "import nz.bradcampbell.paperparcel.AccessorName;",
        "@PaperParcel",
        "public final class Test {",
        "@AccessorName(\"customGetterMethod\")",
        "private final int child;",
        "public Test(int child) {",
        "this.child = child;",
        "}",
        "public int customGetterMethod() {",
        "return this.child;",
        "}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/TestParcel", Joiner.on('\n').join(
        "package test;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.lang.Override;",
        "import nz.bradcampbell.paperparcel.TypedParcelable;",
        "public final class TestParcel implements TypedParcelable<Test> {",
        "public static final Parcelable.Creator<TestParcel> CREATOR = new Parcelable.Creator<TestParcel>() {",
        "@Override public TestParcel createFromParcel(Parcel in) {",
        "int child = in.readInt();",
        "Test data = new Test(child);",
        "return new TestParcel(data);",
        "}",
        "@Override public TestParcel[] newArray(int size) {",
        "return new TestParcel[size];",
        "}",
        "};",
        "public final Test data;",
        "public TestParcel(Test data) {",
        "this.data = data;",
        "}",
        "@Override public int describeContents() {",
        "return 0;",
        "}",
        "@Override public void writeToParcel(Parcel dest, int flags) {",
        "int child = this.data.customGetterMethod();",
        "dest.writeInt(child);",
        "}",
        "}"
    ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void packageVisiblePropertyTest() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.paperparcel.PaperParcel;",
        "@PaperParcel",
        "public final class Test {",
        "final int child;",
        "public Test(int child) {",
        "this.child = child;",
        "}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/TestParcel", Joiner.on('\n').join(
        "package test;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.lang.Override;",
        "import nz.bradcampbell.paperparcel.TypedParcelable;",
        "public final class TestParcel implements TypedParcelable<Test> {",
        "public static final Parcelable.Creator<TestParcel> CREATOR = new Parcelable.Creator<TestParcel>() {",
        "@Override public TestParcel createFromParcel(Parcel in) {",
        "int child = in.readInt();",
        "Test data = new Test(child);",
        "return new TestParcel(data);",
        "}",
        "@Override public TestParcel[] newArray(int size) {",
        "return new TestParcel[size];",
        "}",
        "};",
        "public final Test data;",
        "public TestParcel(Test data) {",
        "this.data = data;",
        "}",
        "@Override public int describeContents() {",
        "return 0;",
        "}",
        "@Override public void writeToParcel(Parcel dest, int flags) {",
        "int child = this.data.child;",
        "dest.writeInt(child);",
        "}",
        "}"
    ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }
}
