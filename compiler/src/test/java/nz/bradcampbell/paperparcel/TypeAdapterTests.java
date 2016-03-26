package nz.bradcampbell.paperparcel;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;
import static java.util.Arrays.asList;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

public class TypeAdapterTests {

  @Test public void dateTypeAdapterTest() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import test.DateTypeAdapter;",
        "import nz.bradcampbell.paperparcel.TypeAdapters;",
        "import nz.bradcampbell.paperparcel.PaperParcel;",
        "import java.util.Date;",
        "@PaperParcel",
        "@TypeAdapters(DateTypeAdapter.class)",
        "public final class Test {",
        "private final Date child;",
        "public Test(Date child) {",
        "this.child = child;",
        "}",
        "public Date getChild() {",
        "return this.child;",
        "}",
        "}"
    ));

    JavaFileObject typeAdapter = JavaFileObjects.forSourceString("test.DateTypeAdapter", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.paperparcel.TypeAdapter;",
        "import java.util.Date;",
        "import android.os.Parcel;",
        "public class DateTypeAdapter implements TypeAdapter<Date> {",
        "public Date readFromParcel(Parcel in) {",
        "return new Date(in.readLong());",
        "}",
        "public void writeToParcel(Date value, Parcel dest) {",
        "dest.writeLong(value.getTime());",
        "}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/TestParcel", Joiner.on('\n').join(
        "package test;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.lang.Override;",
        "import java.util.Date;",
        "import nz.bradcampbell.paperparcel.TypedParcelable;",
        "public final class TestParcel implements TypedParcelable<Test> {",
        "private static final DateTypeAdapter TEST_DATE_TYPE_ADAPTER = new DateTypeAdapter();",
        "public static final Parcelable.Creator<TestParcel> CREATOR = new Parcelable.Creator<TestParcel>() {",
        "@Override public TestParcel createFromParcel(Parcel in) {",
        "Date outChild = null;",
        "if (in.readInt() == 0) {",
        "outChild = TEST_DATE_TYPE_ADAPTER.readFromParcel(in);",
        "}",
        "Test data = new Test(outChild);",
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
        "Date child = data.getChild();",
        "if (child == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "TEST_DATE_TYPE_ADAPTER.writeToParcel(child, dest);",
        "}",
        "}",
        "}"
    ));

    assertAbout(javaSources()).that(asList(source, typeAdapter))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }
}
