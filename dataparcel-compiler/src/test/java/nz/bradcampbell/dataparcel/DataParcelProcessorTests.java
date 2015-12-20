package nz.bradcampbell.dataparcel;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class DataParcelProcessorTests {

  @Test public void emptyDataTest() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.support.annotation.NonNull;",
        "import android.support.annotation.Nullable;",
        "import nz.bradcampbell.dataparcel.DataParcel;",
        "@DataParcel",
        "public final class Test {",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/TestParcel",
        Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import java.lang.Override;",
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
            "this.data = new Test();",
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
            "}",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new DataParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void nullableTest() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.support.annotation.NonNull;",
            "import android.support.annotation.Nullable;",
            "import nz.bradcampbell.dataparcel.DataParcel;",
            "@DataParcel",
            "public final class Test {",
            "@NonNull private final Integer testInt;",
            "@Nullable private final String testString;",
            "public Test(@NonNull Integer testInt, @Nullable String testString) {",
            "this.testInt = testInt;",
            "this.testString = testString;",
            "}",
            "@NonNull public Integer component1() {",
            "return this.testInt;",
            "}",
            "@Nullable public String component2() {",
            "return this.testString;",
            "}",
            "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/TestParcel",
      Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import java.lang.Integer;",
            "import java.lang.Override;",
            "import java.lang.String;",
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
            "Integer component1 = in.readInt() : null;",
            "String component2 = null;",
            "if (in.readInt() == 0) {",
            "component2 = in.readString();",
            "}",
            "this.data = new Test(component1, component2);",
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
            "dest.writeInt(data.component1());",
            "if (data.component2() == null) {",
            "dest.writeInt(1);",
            "} else {",
            "dest.writeInt(0);",
            "dest.writeString(data.component2());",
            "}",
            "}",
            "}"
      ));

    assertAbout(javaSource()).that(source)
        .processedWith(new DataParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void enumTest() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.support.annotation.NonNull;",
            "import android.support.annotation.Nullable;",
            "import nz.bradcampbell.dataparcel.DataParcel;",
            "import java.util.List;",
            "@DataParcel",
            "public final class Test {",
            "private final TestEnum testEnum;",
            "public Test(TestEnum testEnum) {",
            "this.testEnum = testEnum;",
            "}",
            "enum TestEnum {",
            "ONE;",
            "}",
            "public TestEnum component1() {",
            "return this.testEnum;",
            "}",
            "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/TestParcel",
        Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import java.lang.Override;",
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
            "Test.TestEnum component1 = (Test.TestEnum) in.readSerializable();",
            "this.data = new Test(component1);",
            "}",
            "public static final TestParcel wrap(Test data) {",
            "return new TestParcel(data);",
            "}",
            "public Test getContents() {",
            "return data;",
            "}",
            "@Override",
            "public int describeContents() {",
            "return 0;",
            "}",
            "@Override",
            "public void writeToParcel(Parcel dest, int flags) {",
            "dest.writeSerializable(data.component1());",
            "}",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new DataParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

//  @Test public void nonValidTypeTest() throws Exception {
//    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
//        "package test;",
//        "import android.support.annotation.NonNull;",
//        "import android.support.annotation.Nullable;",
//        "import nz.bradcampbell.dataparcel.DataParcel;",
//        "import java.util.List;",
//        "@DataParcel",
//        "public final class Test {",
//        "private final TestType test;",
//        "public Test(TestType test) {",
//        "this.test = test;",
//        "}",
//        "@NonNull public TestType component1() {",
//        "return this.test;",
//        "}",
//        "public static class TestType {",
//        "}",
//        "}"
//    ));
//
//    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/TestParcel",
//        Joiner.on('\n').join(
//            "package test;",
//            "import android.os.Parcel;",
//            "import android.os.Parcelable;",
//            "import java.lang.Integer;",
//            "import java.lang.Override;",
//            "import java.util.List;",
//            "public class TestParcel implements Parcelable {",
//            "public static final Parcelable.Creator<TestParcel> CREATOR = new Parcelable.Creator<TestParcel>() {",
//            "@Override public TestParcel createFromParcel(Parcel in) {",
//            "return new TestParcel(in);",
//            "}",
//            "@Override public TestParcel[] newArray(int size) {",
//            "return new TestParcel[size];",
//            "}",
//            "};",
//            "private final Test data;",
//            "private TestParcel(Test data) {",
//            "this.data = data;",
//            "}",
//            "private TestParcel(Parcel in) {",
//            "List<Integer> component1 = (List<Integer>) in.readArrayList(getClass().getClassLoader());",
//            "this.data = new Test(component1);",
//            "}",
//            "public static final TestParcel wrap(Test data) {",
//            "return new TestParcel(data);",
//            "}",
//            "public Test getContents() {",
//            "return data;",
//            "}",
//            "@Override public int describeContents() {",
//            "return 0;",
//            "}",
//            "@Override public void writeToParcel(Parcel dest, int flags) {",
//            "dest.writeList(data.component1());",
//            "}",
//            "}"
//        ));
//
//    assertAbout(javaSource()).that(source)
//        .processedWith(new DataParcelProcessor())
//        .compilesWithoutError()
//        .and()
//        .generatesSources(expectedSource);
//  }

  @Test public void listOfValidTypesTest() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.support.annotation.NonNull;",
            "import android.support.annotation.Nullable;",
            "import nz.bradcampbell.dataparcel.DataParcel;",
            "import java.util.List;",
            "@DataParcel",
            "public final class Test {",
            "private final List<Integer> testList;",
            "public Test(List<Integer> testList) {",
            "this.testList = testList;",
            "}",
            "@NonNull public List<Integer> component1() {",
            "return this.testList;",
            "}",
            "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/TestParcel",
        Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import java.lang.Integer;",
            "import java.lang.Override;",
            "import java.util.List;",
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
            "List<Integer> component1 = (List<Integer>) in.readArrayList(getClass().getClassLoader());",
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
            "dest.writeList(data.component1());",
            "}",
            "}"
    ));

    assertAbout(javaSource()).that(source)
        .processedWith(new DataParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

//  @Test public void listOfNonValidTypesTest() throws Exception {
//    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
//        "package test;",
//        "import android.support.annotation.NonNull;",
//        "import android.support.annotation.Nullable;",
//        "import nz.bradcampbell.dataparcel.DataParcel;",
//        "import java.util.List;",
//        "@DataParcel",
//        "public final class Test {",
//        "private final List<TestType> testList;",
//        "public Test(List<TestType> testList) {",
//        "this.testList = testList;",
//        "}",
//        "@NonNull public List<TestType> component1() {",
//        "return this.testList;",
//        "}",
//        "public static class TestType {",
//        "}",
//        "}"
//    ));
//
//    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/TestParcel",
//        Joiner.on('\n').join(
//            "package test;",
//            "import android.os.Parcel;",
//            "import android.os.Parcelable;",
//            "import java.lang.Integer;",
//            "import java.lang.Override;",
//            "import java.util.List;",
//            "public class TestParcel implements Parcelable {",
//            "public static final Parcelable.Creator<TestParcel> CREATOR = new Parcelable.Creator<TestParcel>() {",
//            "@Override public TestParcel createFromParcel(Parcel in) {",
//            "return new TestParcel(in);",
//            "}",
//            "@Override public TestParcel[] newArray(int size) {",
//            "return new TestParcel[size];",
//            "}",
//            "};",
//            "private final Test data;",
//            "private TestParcel(Test data) {",
//            "this.data = data;",
//            "}",
//            "private TestParcel(Parcel in) {",
//            "List<Integer> component1 = (List<Integer>) in.readArrayList(getClass().getClassLoader());",
//            "this.data = new Test(component1);",
//            "}",
//            "public static final TestParcel wrap(Test data) {",
//            "return new TestParcel(data);",
//            "}",
//            "public Test getContents() {",
//            "return data;",
//            "}",
//            "@Override public int describeContents() {",
//            "return 0;",
//            "}",
//            "@Override public void writeToParcel(Parcel dest, int flags) {",
//            "dest.writeList(data.component1());",
//            "}",
//            "}"
//        ));
//
//    assertAbout(javaSource()).that(source)
//        .processedWith(new DataParcelProcessor())
//        .compilesWithoutError()
//        .and()
//        .generatesSources(expectedSource);
//  }
//
//  @Test public void mapOfValidTypesTest() throws Exception {
//    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
//        "package test;",
//        "import android.support.annotation.NonNull;",
//        "import android.support.annotation.Nullable;",
//        "import nz.bradcampbell.dataparcel.DataParcel;",
//        "import java.util.Map;",
//        "@DataParcel",
//        "public final class Test {",
//        "private final Map<Object, Object> testMap;",
//        "public Test(Map<Object, Object> testMap) {",
//        "this.testMap = testMap;",
//        "}",
//        "@NonNull public Map<Object, Object> component1() {",
//        "return this.testMap;",
//        "}",
//        "}"
//    ));
//
//    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/TestParcel",
//        Joiner.on('\n').join(
//            "package test;",
//            "import android.os.Parcel;",
//            "import android.os.Parcelable;",
//            "import java.lang.Integer;",
//            "import java.lang.Override;",
//            "import java.util.List;",
//            "public class TestParcel implements Parcelable {",
//            "public static final Parcelable.Creator<TestParcel> CREATOR = new Parcelable.Creator<TestParcel>() {",
//            "@Override public TestParcel createFromParcel(Parcel in) {",
//            "return new TestParcel(in);",
//            "}",
//            "@Override public TestParcel[] newArray(int size) {",
//            "return new TestParcel[size];",
//            "}",
//            "};",
//            "private final Test data;",
//            "private TestParcel(Test data) {",
//            "this.data = data;",
//            "}",
//            "private TestParcel(Parcel in) {",
//            "List<Integer> component1 = (List<Integer>) in.readArrayList(getClass().getClassLoader());",
//            "this.data = new Test(component1);",
//            "}",
//            "public static final TestParcel wrap(Test data) {",
//            "return new TestParcel(data);",
//            "}",
//            "public Test getContents() {",
//            "return data;",
//            "}",
//            "@Override public int describeContents() {",
//            "return 0;",
//            "}",
//            "@Override public void writeToParcel(Parcel dest, int flags) {",
//            "dest.writeList(data.component1());",
//            "}",
//            "}"
//        ));
//
//    assertAbout(javaSource()).that(source)
//        .processedWith(new DataParcelProcessor())
//        .compilesWithoutError()
//        .and()
//        .generatesSources(expectedSource);
//  }
}
