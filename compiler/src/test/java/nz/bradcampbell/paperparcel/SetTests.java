package nz.bradcampbell.paperparcel;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;
import static java.util.Arrays.asList;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

public class SetTests {

  @Test public void setOfParcelableTypesTest() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.paperparcel.PaperParcel;",
        "import java.util.Set;",
        "@PaperParcel",
        "public final class Test {",
        "private final Set<Integer> child;",
        "public Test(Set<Integer> child) {",
        "this.child = child;",
        "}",
        "public Set<Integer> getChild() {",
        "return this.child;",
        "}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/TestParcel", Joiner.on('\n').join(
        "package test;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.lang.Integer;",
        "import java.lang.Override;",
        "import java.util.LinkedHashSet;",
        "import java.util.Set;",
        "import nz.bradcampbell.paperparcel.TypedParcelable;",
        "public final class TestParcel implements TypedParcelable<Test> {",
        "public static final Parcelable.Creator<TestParcel> CREATOR = new Parcelable.Creator<TestParcel>() {",
        "@Override public TestParcel createFromParcel(Parcel in) {",
        "Set<Integer> outChild = null;",
        "if (in.readInt() == 0) {",
        "int childSize = in.readInt();",
        "Set<Integer> child = new LinkedHashSet<Integer>(childSize);",
        "for (int childIndex = 0; childIndex < childSize; childIndex++) {",
        "Integer outChildItem = null;",
        "if (in.readInt() == 0) {",
        "outChildItem = in.readInt();",
        "}",
        "child.add(outChildItem);",
        "}",
        "outChild = child;",
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
        "Set<Integer> child = data.getChild();",
        "if (child == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "int childSize = child.size();",
        "dest.writeInt(childSize);",
        "for (Integer childItem : child) {",
        "if (childItem == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "dest.writeInt(childItem);",
        "}",
        "}",
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

  @Test public void setOfNonParcelableTypesTest() throws Exception {
    JavaFileObject dataClassRoot = JavaFileObjects.forSourceString("test.Root", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.paperparcel.PaperParcel;",
        "import java.util.Set;",
        "@PaperParcel",
        "public final class Root {",
        "private final Set<Child> child;",
        "public Root(Set<Child> child) {",
        "this.child = child;",
        "}",
        "public Set<Child> getChild() {",
        "return this.child;",
        "}",
        "}"
    ));

    JavaFileObject dataClassChild = JavaFileObjects.forSourceString("test.Child", Joiner.on('\n').join(
        "package test;",
        "public final class Child {",
        "private final Integer child;",
        "public Child(Integer child) {",
        "this.child = child;",
        "}",
        "public Integer getChild() {",
        "return this.child;",
        "}",
        "}"
    ));

    JavaFileObject rootParcel = JavaFileObjects.forSourceString("test/RootParcel", Joiner.on('\n').join(
        "package test;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.lang.Integer;",
        "import java.lang.Override;",
        "import java.util.LinkedHashSet;",
        "import java.util.Set;",
        "import nz.bradcampbell.paperparcel.TypedParcelable;",
        "public final class RootParcel implements TypedParcelable<Root> {",
        "public static final Parcelable.Creator<RootParcel> CREATOR = new Parcelable.Creator<RootParcel>() {",
        "@Override public RootParcel createFromParcel(Parcel in) {",
        "Set<Child> outChild = null;",
        "if (in.readInt() == 0) {",
        "int childSize = in.readInt();",
        "Set<Child> child = new LinkedHashSet<Child>(childSize);",
        "for (int childIndex = 0; childIndex < childSize; childIndex++) {",
        "Child outChildItem = null;",
        "if (in.readInt() == 0) {",
        "Integer outChildItemChild = null;",
        "if (in.readInt() == 0) {",
        "outChildItemChild = in.readInt();",
        "}",
        "outChildItem = new Child(outChildItemChild);",
        "}",
        "child.add(outChildItem);",
        "}",
        "outChild = child;",
        "}",
        "Root data = new Root(outChild);",
        "return new RootParcel(data);",
        "}",
        "@Override public RootParcel[] newArray(int size) {",
        "return new RootParcel[size];",
        "}",
        "};",
        "public final Root data;",
        "public RootParcel(Root data) {",
        "this.data = data;",
        "}",
        "@Override public int describeContents() {",
        "return 0;",
        "}",
        "@Override public void writeToParcel(Parcel dest, int flags) {",
        "Set<Child> child = data.getChild();",
        "if (child == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "int childSize = child.size();",
        "dest.writeInt(childSize);",
        "for (Child childItem : child) {",
        "if (childItem == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "if (childItem.getChild() == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "dest.writeInt(childItem.getChild());",
        "}",
        "}",
        "}",
        "}",
        "}",
        "}"
    ));

    assertAbout(javaSources()).that(asList(dataClassRoot, dataClassChild))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(rootParcel);
  }

  @Test public void hashSetOfNonParcelableTypesTest() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.paperparcel.PaperParcel;",
        "import java.util.HashSet;",
        "@PaperParcel",
        "public final class Test {",
        "private final HashSet<Child> child;",
        "public Test(HashSet<Child> child) {",
        "this.child = child;",
        "}",
        "public HashSet<Child> getChild() {",
        "return this.child;",
        "}",
        "}"
    ));

    JavaFileObject dataClassChild = JavaFileObjects.forSourceString("test.Child", Joiner.on('\n').join(
        "package test;",
        "public final class Child {",
        "private final Integer child;",
        "public Child(Integer child) {",
        "this.child = child;",
        "}",
        "public Integer getChild() {",
        "return this.child;",
        "}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/TestParcel", Joiner.on('\n').join(
        "package test;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.lang.Integer;",
        "import java.lang.Override;",
        "import java.util.HashSet;",
        "import nz.bradcampbell.paperparcel.TypedParcelable;",
        "public final class TestParcel implements TypedParcelable<Test> {",
        "public static final Parcelable.Creator<TestParcel> CREATOR = new Parcelable.Creator<TestParcel>() {",
        "@Override public TestParcel createFromParcel(Parcel in) {",
        "HashSet<Child> outChild = null;",
        "if (in.readInt() == 0) {",
        "int childSize = in.readInt();",
        "HashSet<Child> child = new HashSet<Child>();",
        "for (int childIndex = 0; childIndex < childSize; childIndex++) {",
        "Child outChildItem = null;",
        "if (in.readInt() == 0) {",
        "Integer outChildItemChild = null;",
        "if (in.readInt() == 0) {",
        "outChildItemChild = in.readInt();",
        "}",
        "outChildItem = new Child(outChildItemChild);",
        "}",
        "child.add(outChildItem);",
        "}",
        "outChild = child;",
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
        "HashSet<Child> child = data.getChild();",
        "if (child == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "int childSize = child.size();",
        "dest.writeInt(childSize);",
        "for (Child childItem : child) {",
        "if (childItem == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "if (childItem.getChild() == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "dest.writeInt(childItem.getChild());",
        "}",
        "}",
        "}",
        "}",
        "}",
        "}"
    ));

    assertAbout(javaSources()).that(asList(source, dataClassChild))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }
}
