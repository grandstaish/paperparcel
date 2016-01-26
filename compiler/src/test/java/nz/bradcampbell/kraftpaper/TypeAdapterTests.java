package nz.bradcampbell.kraftpaper;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;
import static java.util.Arrays.asList;

public class TypeAdapterTests {

  @Test public void dateTypeAdapterTest() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import test.DateTypeAdapter;",
        "import nz.bradcampbell.kraftpaper.KraftPaper;",
        "import java.util.Date;",
        "@KraftPaper(",
        "typeAdapters = {",
        "DateTypeAdapter.class",
        "}",
        ")",
        "public final class Test {",
        "private final Date child;",
        "public Test(Date child) {",
        "this.child = child;",
        "}",
        "public Date component1() {",
        "return this.child;",
        "}",
        "}"
    ));

    JavaFileObject typeAdapter = JavaFileObjects.forSourceString("test.DateTypeAdapter", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.kraftpaper.TypeAdapter;",
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
        "public class TestParcel implements Parcelable {",
        "public static final Parcelable.Creator<TestParcel> CREATOR = new Parcelable.Creator<TestParcel>() {",
        "@Override public TestParcel createFromParcel(Parcel in) {",
        "return new TestParcel(in);",
        "}",
        "@Override public TestParcel[] newArray(int size) {",
        "return new TestParcel[size];",
        "}",
        "};",
        "private final Test data;",
        "private TestParcel(Test data) {",
        "this.data = data;",
        "}",
        "private TestParcel(Parcel in) {",
        "DateTypeAdapter component1TypeAdapter = new DateTypeAdapter();",
        "Date component1 = component1TypeAdapter.readFromParcel(in);",
        "this.data = new Test(component1);",
        "}",
        "public static final TestParcel wrap(Test data) {",
        "return new TestParcel(data);",
        "}",
        "public Test getContents() {",
        "return data;",
        "}",
        "@Override public int describeContents() {",
        "return 0;",
        "}",
        "@Override public void writeToParcel(Parcel dest, int flags) {",
        "Date component1 = data.component1();",
        "DateTypeAdapter component1TypeAdapter = new DateTypeAdapter();",
        "component1TypeAdapter.writeToParcel(component1, dest);",
        "}",
        "}"
    ));

    assertAbout(javaSources()).that(asList(source, typeAdapter))
        .processedWith(new KraftPaperProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }
}
