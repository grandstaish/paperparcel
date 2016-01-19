package nz.bradcampbell.dataparcel;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;
import static java.util.Arrays.asList;

public class ArrayTests {

  @Test public void nullableBooleanArrayTest() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.support.annotation.Nullable;",
        "import nz.bradcampbell.dataparcel.DataParcel;",
        "@DataParcel",
        "public final class Test {",
        "@Nullable private final Boolean[] child;",
        "public Test(@Nullable Boolean[] child) {",
        "this.child = child;",
        "}",
        "@Nullable public Boolean[] component1() {",
        "return this.child;",
        "}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/TestParcel", Joiner.on('\n').join(
        "package test;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.lang.Boolean;",
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
        "Boolean[] outComponent1 = null;",
        "if (in.readInt() == 0) {",
        "int component1Size = in.readInt();",
        "Boolean[] component1 = new Boolean[component1Size];",
        "for (int component1Index = 0; component1Index < component1Size; component1Index++) {",
        "Boolean component1Item = (Boolean) in.readValue(null);",
        "component1[component1Index] = component1Item;",
        "}",
        "outComponent1 = component1;",
        "}",
        "this.data = new Test(outComponent1);",
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
        "Boolean[] component1 = data.component1();",
        "if (component1 == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "int component1Size = component1.length;",
        "dest.writeInt(component1Size);",
        "for (int component1Index = 0; component1Index < component1Size; component1Index++) {",
        "Boolean component1Item = component1[component1Index];",
        "dest.writeValue(component1Item);",
        "}",
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

  @Test public void arrayOfListsOfNonParcelableObjectsTest() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.dataparcel.DataParcel;",
        "import java.util.List;",
        "@DataParcel",
        "public final class Test {",
        "private final List<Child>[] child;",
        "public Test(List<Child>[] child) {",
        "this.child = child;",
        "}",
        "public List<Child>[] component1() {",
        "return this.child;",
        "}",
        "}"
    ));

    JavaFileObject dataClassChild = JavaFileObjects.forSourceString("test.Child", Joiner.on('\n').join(
        "package test;",
        "public final class Child {",
        "private final Integer test;",
        "public Child(Integer test) {",
        "this.test = test;",
        "}",
        "public Integer component1() {",
        "return this.test;",
        "}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/TestParcel", Joiner.on('\n').join(
        "package test;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.lang.Override;",
        "import java.util.ArrayList;",
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
        "int component1Size = in.readInt();",
        "List<Child>[] component1 = new List[component1Size];",
        "for (int component1Index = 0; component1Index < component1Size; component1Index++) {",
        "List<Child> outComponent1Item = null;",
        "if (in.readInt() == 0) {",
        "int component1ItemSize = in.readInt();",
        "List<Child> component1Item = new ArrayList<Child>(component1ItemSize);",
        "for (int component1ItemIndex = 0; component1ItemIndex < component1ItemSize; component1ItemIndex++) {",
        "Child outComponent1ItemItem = null;",
        "if (in.readInt() == 0) {",
        "ChildParcel component1ItemItemParcel = ChildParcel.CREATOR.createFromParcel(in);",
        "outComponent1ItemItem = component1ItemItemParcel.getContents();",
        "}",
        "component1Item.add(outComponent1ItemItem);",
        "}",
        "outComponent1Item = component1Item;",
        "}",
        "component1[component1Index] = outComponent1Item;",
        "}",
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
        "List<Child>[] component1 = data.component1();",
        "int component1Size = component1.length;",
        "dest.writeInt(component1Size);",
        "for (int component1Index = 0; component1Index < component1Size; component1Index++) {",
        "List<Child> component1Item = component1[component1Index];",
        "if (component1Item == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "int component1ComponentSize = component1Item.size();",
        "dest.writeInt(component1ComponentSize);",
        "for (int component1ComponentIndex = 0; component1ComponentIndex < component1ComponentSize; component1ComponentIndex++) {",
        "Child component1ComponentItem = component1Item.get(component1ComponentIndex);",
        "if (component1ComponentItem == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "ChildParcel component1ComponentParamParcel = ChildParcel.wrap(component1ComponentItem);",
        "component1ComponentParamParcel.writeToParcel(dest, 0);",
        "}",
        "}",
        "}",
        "}",
        "}",
        "}"
    ));

    assertAbout(javaSources()).that(asList(source, dataClassChild))
        .processedWith(new DataParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void nonParcelableArrayOfArraysTest() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.dataparcel.DataParcel;",
        "import android.util.SparseArray;",
        "@DataParcel",
        "public final class Test {",
        "private final Child[][] child;",
        "public Test(Child[][] child) {",
        "this.child = child;",
        "}",
        "public Child[][] component1() {",
        "return this.child;",
        "}",
        "}"
    ));

    JavaFileObject dataClassChild = JavaFileObjects.forSourceString("test.Child", Joiner.on('\n').join(
        "package test;",
        "public final class Child {",
        "private final Integer test;",
        "public Child(Integer test) {",
        "this.test = test;",
        "}",
        "public Integer component1() {",
        "return this.test;",
        "}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/TestParcel", Joiner.on('\n').join(
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
        "int component1Size = in.readInt();",
        "Child[][] component1 = new Child[component1Size][];",
        "for (int component1Index = 0; component1Index < component1Size; component1Index++) {",
        "Child[] outComponent1Item = null;",
        "if (in.readInt() == 0) {",
        "int component1ItemSize = in.readInt();",
        "Child[] component1Item = new Child[component1ItemSize];",
        "for (int component1ItemIndex = 0; component1ItemIndex < component1ItemSize; component1ItemIndex++) {",
        "Child outComponent1ItemItem = null;",
        "if (in.readInt() == 0) {",
        "ChildParcel component1ItemItemParcel = ChildParcel.CREATOR.createFromParcel(in);",
        "outComponent1ItemItem = component1ItemItemParcel.getContents();",
        "}",
        "component1Item[component1ItemIndex] = outComponent1ItemItem;",
        "}",
        "outComponent1Item = component1Item;",
        "}",
        "component1[component1Index] = outComponent1Item;",
        "}",
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
        "Child[][] component1 = data.component1();",
        "int component1Size = component1.length;",
        "dest.writeInt(component1Size);",
        "for (int component1Index = 0; component1Index < component1Size; component1Index++) {",
        "Child[] component1Item = component1[component1Index];",
        "if (component1Item == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "int component1ComponentSize = component1Item.length;",
        "dest.writeInt(component1ComponentSize);",
        "for (int component1ComponentIndex = 0; component1ComponentIndex < component1ComponentSize; component1ComponentIndex++) {",
        "Child component1ComponentItem = component1Item[component1ComponentIndex];",
        "if (component1ComponentItem == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "ChildParcel component1ComponentComponentParcel = ChildParcel.wrap(component1ComponentItem);",
        "component1ComponentComponentParcel.writeToParcel(dest, 0);",
        "}",
        "}",
        "}",
        "}",
        "}",
        "}"
    ));

    assertAbout(javaSources()).that(asList(source, dataClassChild))
        .processedWith(new DataParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void parcelableArrayTest() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.graphics.Bitmap;",
        "import nz.bradcampbell.dataparcel.DataParcel;",
        "@DataParcel",
        "public final class Test {",
        "private final Bitmap[] child;",
        "public Test(Bitmap[] child) {",
        "this.child = child;",
        "}",
        "public Bitmap[] component1() {",
        "return this.child;",
        "}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/TestParcel", Joiner.on('\n').join(
        "package test;",
        "import android.graphics.Bitmap;",
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
        "int component1Size = in.readInt();",
        "Bitmap[] component1 = new Bitmap[component1Size];",
        "for (int component1Index = 0; component1Index < component1Size; component1Index++) {",
        "Bitmap outComponent1Item = null;",
        "if (in.readInt() == 0) {",
        "outComponent1Item = Bitmap.CREATOR.createFromParcel(in);",
        "}",
        "component1[component1Index] = outComponent1Item;",
        "}",
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
        "Bitmap[] component1 = data.component1();",
        "int component1Size = component1.length;",
        "dest.writeInt(component1Size);",
        "for (int component1Index = 0; component1Index < component1Size; component1Index++) {",
        "Bitmap component1Item = component1[component1Index];",
        "if (component1Item == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "component1Item.writeToParcel(dest, 0);",
        "}",
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

  @Test public void bitmapArrayTest() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.graphics.Bitmap;",
        "import nz.bradcampbell.dataparcel.DataParcel;",
        "@DataParcel",
        "public final class Test {",
        "private final Bitmap[] child;",
        "public Test(Bitmap[] child) {",
        "this.child = child;",
        "}",
        "public Bitmap[] component1() {",
        "return this.child;",
        "}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/TestParcel", Joiner.on('\n').join(
        "package test;",
        "import android.graphics.Bitmap;",
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
        "int component1Size = in.readInt();",
        "Bitmap[] component1 = new Bitmap[component1Size];",
        "for (int component1Index = 0; component1Index < component1Size; component1Index++) {",
        "Bitmap outComponent1Item = null;",
        "if (in.readInt() == 0) {",
        "outComponent1Item = Bitmap.CREATOR.createFromParcel(in);",
        "}",
        "component1[component1Index] = outComponent1Item;",
        "}",
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
        "Bitmap[] component1 = data.component1();",
        "int component1Size = component1.length;",
        "dest.writeInt(component1Size);",
        "for (int component1Index = 0; component1Index < component1Size; component1Index++) {",
        "Bitmap component1Item = component1[component1Index];",
        "if (component1Item == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "component1Item.writeToParcel(dest, 0);",
        "}",
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

  @Test public void nonParcelableArrayTest() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.dataparcel.DataParcel;",
        "@DataParcel",
        "public final class Test {",
        "private final Child[] child;",
        "public Test(Child[] child) {",
        "this.child = child;",
        "}",
        "public Child[] component1() {",
        "return this.child;",
        "}",
        "}"
    ));

    JavaFileObject dataClassChild = JavaFileObjects.forSourceString("test.Child", Joiner.on('\n').join(
        "package test;",
        "public final class Child {",
        "private final Integer test;",
        "public Child(Integer test) {",
        "this.test = test;",
        "}",
        "public Integer component1() {",
        "return this.test;",
        "}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/TestParcel", Joiner.on('\n').join(
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
        "int component1Size = in.readInt();",
        "Child[] component1 = new Child[component1Size];",
        "for (int component1Index = 0; component1Index < component1Size; component1Index++) {",
        "Child outComponent1Item = null;",
        "if (in.readInt() == 0) {",
        "ChildParcel component1ItemParcel = ChildParcel.CREATOR.createFromParcel(in);",
        "outComponent1Item = component1ItemParcel.getContents();",
        "}",
        "component1[component1Index] = outComponent1Item;",
        "}",
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
        "Child[] component1 = data.component1();",
        "int component1Size = component1.length;",
        "dest.writeInt(component1Size);",
        "for (int component1Index = 0; component1Index < component1Size; component1Index++) {",
        "Child component1Item = component1[component1Index];",
        "if (component1Item == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "ChildParcel component1ComponentParcel = ChildParcel.wrap(component1Item);",
        "component1ComponentParcel.writeToParcel(dest, 0);",
        "}",
        "}",
        "}",
        "}"
    ));

    assertAbout(javaSources()).that(asList(source, dataClassChild))
        .processedWith(new DataParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void genericNonParcelableArrayTest() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.dataparcel.DataParcel;",
        "@DataParcel",
        "public final class Test {",
        "private final Child<Integer>[][] child;",
        "public Test(Child<Integer>[][] child) {",
        "this.child = child;",
        "}",
        "public Child<Integer>[][] component1() {",
        "return this.child;",
        "}",
        "}"
    ));

    JavaFileObject dataClassChild = JavaFileObjects.forSourceString("test.Child", Joiner.on('\n').join(
        "package test;",
        "public final class Child<T> {",
        "private final Integer test;",
        "public Child(Integer test) {",
        "this.test = test;",
        "}",
        "public Integer component1() {",
        "return this.test;",
        "}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/TestParcel", Joiner.on('\n').join(
        "package test;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.lang.Integer;",
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
        "int component1Size = in.readInt();",
        "Child<Integer>[][] component1 = new Child[component1Size][];",
        "for (int component1Index = 0; component1Index < component1Size; component1Index++) {",
        "Child<Integer>[] outComponent1Item = null;",
        "if (in.readInt() == 0) {",
        "int component1ItemSize = in.readInt();",
        "Child<Integer>[] component1Item = new Child[component1ItemSize];",
        "for (int component1ItemIndex = 0; component1ItemIndex < component1ItemSize; component1ItemIndex++) {",
        "Child<Integer> outComponent1ItemItem = null;",
        "if (in.readInt() == 0) {",
        "ChildParcel component1ItemItemParcel = ChildParcel.CREATOR.createFromParcel(in);",
        "outComponent1ItemItem = component1ItemItemParcel.getContents();",
        "}",
        "component1Item[component1ItemIndex] = outComponent1ItemItem;",
        "}",
        "outComponent1Item = component1Item;",
        "}",
        "component1[component1Index] = outComponent1Item;",
        "}",
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
        "Child<Integer>[][] component1 = data.component1();",
        "int component1Size = component1.length;",
        "dest.writeInt(component1Size);",
        "for (int component1Index = 0; component1Index < component1Size; component1Index++) {",
        "Child<Integer>[] component1Item = component1[component1Index];",
        "if (component1Item == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "int component1ComponentSize = component1Item.length;",
        "dest.writeInt(component1ComponentSize);",
        "for (int component1ComponentIndex = 0; component1ComponentIndex < component1ComponentSize; component1ComponentIndex++) {",
        "Child<Integer> component1ComponentItem = component1Item[component1ComponentIndex];",
        "if (component1ComponentItem == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "ChildParcel component1ComponentComponentParcel = ChildParcel.wrap(component1ComponentItem);",
        "component1ComponentComponentParcel.writeToParcel(dest, 0);",
        "}",
        "}",
        "}",
        "}",
        "}",
        "}"
    ));

    assertAbout(javaSources()).that(asList(source, dataClassChild))
        .processedWith(new DataParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }
}
