package nz.bradcampbell.paperparcel;

import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;
import static java.util.Arrays.asList;
import static nz.bradcampbell.paperparcel.ParcelMappingGenerator.PACKAGE_NAME;

import com.google.common.base.Joiner;
import com.google.common.truth.Truth;
import com.google.testing.compile.JavaFileObjects;

import javax.tools.JavaFileObject;

import org.junit.Test;

public class ParcelMappingGeneratorTests {
  @Test public void singleParcelable() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.paperparcel.PaperParcel;",
        "@PaperParcel",
        "public final class Test {",
        "private final boolean child;",
        "public Test(boolean child) {",
        "this.child = child;",
        "}",
        "public boolean getChild() {",
        "return this.child;",
        "}",
        "}"
    ));

    JavaFileObject centralLookup = JavaFileObjects.forSourceString(PACKAGE_NAME + ".PaperParcelMapping", Joiner.on("\n").join(
        "package " + PACKAGE_NAME + ";",
        "import java.lang.Class;",
        "import java.util.LinkedHashMap;",
        "import java.util.Map;",
        "import test.Test;",
        "import test.TestParcel;",
        "public final class PaperParcelMapping {",
        "private static final Map<Class, PaperParcels.Delegator> FROM_ORIGINAL = new LinkedHashMap<>();",
        "private static final Map<Class, PaperParcels.Delegator> FROM_PARCELABLE = new LinkedHashMap<>();",
        "static {",
        "PaperParcels.Delegator<Test, TestParcel> delegator0 = new PaperParcels.Delegator<Test, TestParcel>() {",
        "@Override public Test unwrap(TestParcel wrapper) {",
        "return wrapper.getContents();",
        "}",
        "@Override public TestParcel wrap(Test object) {",
        "return TestParcel.wrap(object);",
        "}",
        "};",
        "FROM_ORIGINAL.put(Test.class, delegator0);",
        "FROM_PARCELABLE.put(TestParcel.class, delegator0);",
        "}", // End of static block
        "}" // End of PaperParcels class
        ));
    Truth.assertAbout(javaSource())
        .that(source)
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(getParcelFile("test", "Test"), centralLookup);
  }

  @Test public void multipleParcelable() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.paperparcel.PaperParcel;",
        "@PaperParcel",
        "public final class Test {",
        "private final boolean child;",
        "public Test(boolean child) {",
        "this.child = child;",
        "}",
        "public boolean getChild() {",
        "return this.child;",
        "}",
        "}"
    ));

    JavaFileObject source2 = JavaFileObjects.forSourceString("test.bar.Test2", Joiner.on('\n').join(
        "package test.bar;",
        "import nz.bradcampbell.paperparcel.PaperParcel;",
        "@PaperParcel",
        "public final class Test2 {",
        "private final boolean child;",
        "public Test2(boolean child) {",
        "this.child = child;",
        "}",
        "public boolean getChild() {",
        "return this.child;",
        "}",
        "}"
    ));
    JavaFileObject centralLookup = JavaFileObjects.forSourceString(PACKAGE_NAME + ".PaperParcels", Joiner.on("\n").join(
        "package " + PACKAGE_NAME + ";",
        "import java.lang.Class;",
        "import java.util.LinkedHashMap;",
        "import java.util.Map;",
        "import test.Test;",
        "import test.TestParcel;",
        "import test.bar.Test2;",
        "import test.bar.Test2Parcel;",
        "public final class PaperParcelMapping {",
        "private static final Map<Class, PaperParcels.Delegator> FROM_ORIGINAL = new LinkedHashMap<>();",
        "private static final Map<Class, PaperParcels.Delegator> FROM_PARCELABLE = new LinkedHashMap<>();",
        "static {",
        "PaperParcels.Delegator<Test2, Test2Parcel> delegator0 = new PaperParcels.Delegator<Test2, Test2Parcel>() {",
        "@Override public Test2 unwrap(Test2Parcel wrapper) {",
        "return wrapper.getContents();",
        "}",
        "@Override public Test2Parcel wrap(Test2 object) {",
        "return Test2Parcel.wrap(object);",
        "}",
        "};",
        "FROM_ORIGINAL.put(Test2.class, delegator0);",
        "FROM_PARCELABLE.put(Test2Parcel.class, delegator0);",
        "PaperParcels.Delegator<Test, TestParcel> delegator1 = new PaperParcels.Delegator<Test, TestParcel>() {",
        "@Override public Test unwrap(TestParcel wrapper) {",
        "return wrapper.getContents();",
        "}",
        "@Override public TestParcel wrap(Test object) {",
        "return TestParcel.wrap(object);",
        "}",
        "};",
        "FROM_ORIGINAL.put(Test.class, delegator1);",
        "FROM_PARCELABLE.put(TestParcel.class, delegator1);",
        "}", // End of static block
        "}" // End of PaperParcels class
    ));
    Truth.assertAbout(javaSources())
        .that(asList(source, source2))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(getParcelFile("test", "Test"), getParcelFile("test.bar", "Test2"), centralLookup);
  }

  private static JavaFileObject getParcelFile(String packageName, String original) {
    String parcel = original + "Parcel";
    return JavaFileObjects.forSourceString(packageName + "." + original + "Parcel", Joiner.on('\n').join(
          "package " + packageName + ";",
          "import android.os.Parcel;",
          "import android.os.Parcelable;",
          "import java.lang.Override;",
          "import nz.bradcampbell.paperparcel.TypedParcelable;",
          "public final class " + parcel + " implements TypedParcelable<" + original + "> {",
          "public static final Parcelable.Creator<" + parcel + "> CREATOR = new Parcelable.Creator<" + parcel + ">() {",
          "@Override public " + parcel + " createFromParcel(Parcel in) {",
          "return new " + parcel + "(in);",
          "}",
          "@Override public " + parcel + "[] newArray(int size) {",
          "return new " + parcel + "[size];",
          "}",
          "};",
          "private final " + original + " data;",
          "private " + parcel + "(" + original + " data) {",
          "this.data = data;",
          "}",
          "private " + parcel + "(Parcel in) {",
          "boolean child = in.readInt() == 1;",
          "this.data = new " + original + "(child);",
          "}",
          "public static final " + parcel + " wrap(" + original + " data) {",
          "return new " + parcel + "(data);",
          "}",
          "public " + original + " getContents() {",
          "return data;",
          "}",
          "@Override public int describeContents() {",
          "return 0;",
          "}",
          "@Override public void writeToParcel(Parcel dest, int flags) {",
          "boolean child = data.getChild();",
          "dest.writeInt(child ? 1 : 0);",
          "}",
          "}"
      ));
  }
}
