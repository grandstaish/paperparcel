package nz.bradcampbell.paperparcel;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

public class WildcardTypeTests {

  @Test public void wildcardTypeTest() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import org.jetbrains.annotations.Nullable;",
        "import nz.bradcampbell.paperparcel.PaperParcel;",
        "import java.util.List;",
        "@PaperParcel",
        "public final class Test {",
        "@Nullable private final List<List<? extends Integer>> child;",
        "public Test(@Nullable List<List<? extends Integer>> child) {",
        "this.child = child;",
        "}",
        "@Nullable public List<List<? extends Integer>> getChild() {",
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
        "import java.util.ArrayList;",
        "import java.util.List;",
        "public final class TestParcel implements Parcelable {",
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
        "List<List<? extends Integer>> outChild = null;",
        "if (in.readInt() == 0) {",
        "int childSize = in.readInt();",
        "List<List<? extends Integer>> child = new ArrayList<List<? extends Integer>>(childSize);",
        "for (int childIndex = 0; childIndex < childSize; childIndex++) {",
        "List<? extends Integer> outChildItem = null;",
        "if (in.readInt() == 0) {",
        "int childItemSize = in.readInt();",
        "List<Integer> childItem = new ArrayList<Integer>(childItemSize);",
        "for (int childItemIndex = 0; childItemIndex < childItemSize; childItemIndex++) {",
        "Integer childItemItem = (Integer) in.readValue(null);",
        "childItem.add(childItemItem);",
        "}",
        "outChildItem = childItem;",
        "}",
        "child.add(outChildItem);",
        "}",
        "outChild = child;",
        "}",
        "this.data = new Test(outChild);",
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
        "List<List<? extends Integer>> child = data.getChild();",
        "if (child == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "int childSize = child.size();",
        "dest.writeInt(childSize);",
        "for (int childIndex = 0; childIndex < childSize; childIndex++) {",
        "List<? extends Integer> childItem = child.get(childIndex);",
        "if (childItem == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "int childParamSize = childItem.size();",
        "dest.writeInt(childParamSize);",
        "for (int childParamIndex = 0; childParamIndex < childParamSize; childParamIndex++) {",
        "Integer childParamItem = childItem.get(childParamIndex);",
        "dest.writeValue(childParamItem);",
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
}
