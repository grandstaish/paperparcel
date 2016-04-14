package nz.bradcampbell.paperparcel;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;
import static java.util.Arrays.asList;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

public class SerializableTests {

  @Test public void serializableTest() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import java.io.Serializable;",
        "import nz.bradcampbell.paperparcel.PaperParcel;",
        "@PaperParcel",
        "public final class Test {",
        "private final Serializable child;",
        "public Test(Serializable child) {",
        "this.child = child;",
        "}",
        "public Serializable getChild() {",
        "return this.child;",
        "}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/TestParcel", Joiner.on('\n').join(
        "package test;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.io.Serializable;",
        "import java.lang.Override;",
        "import nz.bradcampbell.paperparcel.TypedParcelable;",
        "public final class TestParcel implements TypedParcelable<Test> {",
        "public static final Parcelable.Creator<TestParcel> CREATOR = new Parcelable.Creator<TestParcel>() {",
        "@Override public TestParcel createFromParcel(Parcel in) {",
        "Serializable outChild = null;",
        "if (in.readInt() == 0) {",
        "outChild = (Serializable) in.readSerializable();",
        "}",
        "Test data = new Test(outChild);",
        "return new TestParcel(data);",
        "}",
        "@Override public TestParcel[] newArray(int size) {",
        "return new TestParcel[size];",
        "}",
        "};",
        "private final Test data;",
        "public TestParcel(Test data) {",
        "this.data = data;",
        "}",
        "@Override public Test get() {",
        "return this.data;",
        "}",
        "@Override public int describeContents() {",
        "return 0;",
        "}",
        "@Override public void writeToParcel(Parcel dest, int flags) {",
        "Serializable child = this.data.getChild();",
        "if (child == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "dest.writeSerializable(child);",
        "}",
        "}",
        "}"
    ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void customSerializableTest() throws Exception {
    JavaFileObject dataClass = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.paperparcel.PaperParcel;",
        "@PaperParcel",
        "public final class Test {",
        "private final Child child;",
        "public Test(Child child) {",
        "this.child = child;",
        "}",
        "public Child getChild() {",
        "return this.child;",
        "}",
        "}"
    ));

    JavaFileObject serializableClass = JavaFileObjects.forSourceString("test.Child", Joiner.on('\n').join(
        "package test;",
        "import java.io.Serializable;",
        "public final class Child implements Serializable {",
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
        "Child outChild = null;",
        "if (in.readInt() == 0) {",
        "outChild = (Child) in.readSerializable();",
        "}",
        "Test data = new Test(outChild);",
        "return new TestParcel(data);",
        "}",
        "@Override public TestParcel[] newArray(int size) {",
        "return new TestParcel[size];",
        "}",
        "};",
        "private final Test data;",
        "public TestParcel(Test data) {",
        "this.data = data;",
        "}",
        "@Override public Test get() {",
        "return this.data;",
        "}",
        "@Override public int describeContents() {",
        "return 0;",
        "}",
        "@Override public void writeToParcel(Parcel dest, int flags) {",
        "Child child = this.data.getChild();",
        "if (child == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "dest.writeSerializable(child);",
        "}",
        "}",
        "}"
    ));

    assertAbout(javaSources()).that(asList(dataClass, serializableClass))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void parcelableIsFavouredOverSerializableTest() throws Exception {
    JavaFileObject root = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.paperparcel.PaperParcel;",
        "@PaperParcel",
        "public final class Test {",
        "private final Child child;",
        "public Test(Child child) {",
        "this.child = child;",
        "}",
        "public Child getChild() {",
        "return this.child;",
        "}",
        "}"
    ));

    JavaFileObject child = JavaFileObjects.forSourceString("test.Child", Joiner.on('\n').join(
        "package test;",
        "import java.io.Serializable;",
        "import nz.bradcampbell.paperparcel.PaperParcel;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "@PaperParcel",
        "public final class Child implements Serializable, Parcelable {",
        "public static final Parcelable.Creator<Child> CREATOR = new Parcelable.Creator<Child>() {",
        "@Override public Child createFromParcel(Parcel in) {",
        "return new Child();",
        "}",
        "@Override public Child[] newArray(int size) {",
        "return new Child[size];",
        "}",
        "};",
        "@Override public int describeContents() {",
        "return 0;",
        "}",
        "@Override public void writeToParcel(Parcel dest, int flags) {",
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
        "Child outChild = null;",
        "if (in.readInt() == 0) {",
        "outChild = Child.CREATOR.createFromParcel(in);",
        "}",
        "Test data = new Test(outChild);",
        "return new TestParcel(data);",
        "}",
        "@Override public TestParcel[] newArray(int size) {",
        "return new TestParcel[size];",
        "}",
        "};",
        "private final Test data;",
        "public TestParcel(Test data) {",
        "this.data = data;",
        "}",
        "@Override public Test get() {",
        "return this.data;",
        "}",
        "@Override public int describeContents() {",
        "return 0;",
        "}",
        "@Override public void writeToParcel(Parcel dest, int flags) {",
        "Child child = this.data.getChild();",
        "if (child == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "child.writeToParcel(dest, flags);",
        "}",
        "}",
        "}"
    ));

    assertAbout(javaSources()).that(asList(root, child))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }
}
