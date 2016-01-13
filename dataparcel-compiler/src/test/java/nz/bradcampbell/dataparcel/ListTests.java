package nz.bradcampbell.dataparcel;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import javax.tools.JavaFileObject;

import java.util.List;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;
import static java.util.Arrays.asList;

public class ListTests {

  @Test public void nullableListOfParcelableTypesTest() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import android.support.annotation.Nullable;",
        "import nz.bradcampbell.dataparcel.DataParcel;",
        "import java.util.List;",
        "@DataParcel",
        "public final class Test {",
        "@Nullable private final List<Integer> testList;",
        "public Test(@Nullable List<Integer> testList) {",
        "this.testList = testList;",
        "}",
        "@Nullable public List<Integer> component1() {",
        "return this.testList;",
        "}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/TestParcel", Joiner.on('\n').join(
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
        "List<Integer> component1 = null;",
        "if (in.readInt() == 0) {",
        "component1 = (List<Integer>) in.readArrayList(getClass().getClassLoader());",
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
        "if (data.component1() == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "List<Integer> component1 = data.component1();",
        "dest.writeList(component1);",
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

  @Test public void nullableListOfNonParcelableTypesTest() throws Exception {
    JavaFileObject dataClassRoot = JavaFileObjects.forSourceString("test.Root", Joiner.on('\n').join(
        "package test;",
        "import android.support.annotation.Nullable;",
        "import nz.bradcampbell.dataparcel.DataParcel;",
        "import java.util.List;",
        "@DataParcel",
        "public final class Root {",
        "@Nullable private final List<Child> child;",
        "public Root(@Nullable List<Child> child) {",
        "this.child = child;",
        "}",
        "@Nullable public List<Child> component1() {",
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

    JavaFileObject rootParcel = JavaFileObjects.forSourceString("test/RootParcel", Joiner.on('\n').join(
        "package test;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.lang.Override;",
        "import java.util.ArrayList;",
        "import java.util.List;",
        "public class RootParcel implements Parcelable {",
        "public static final Parcelable.Creator<RootParcel> CREATOR = new Parcelable.Creator<RootParcel>() {",
        "@Override public RootParcel createFromParcel(Parcel in) {",
        "return new RootParcel(in);",
        "}",
        "@Override public RootParcel[] newArray(int size) {",
        "return new RootParcel[size];",
        "}",
        "};",
        "private final Root data;",
        "private RootParcel(Root data) {",
        "this.data = data;",
        "}",
        "private RootParcel(Parcel in) {",
        "List<Child> component1 = null;",
        "if (in.readInt() == 0) {",
        "List<ChildParcel> component1Wrapped = (List<ChildParcel>) in.readArrayList(getClass().getClassLoader());",
        "component1 = new ArrayList<Child>(component1Wrapped.size());",
        "for (ChildParcel _component1Wrapped : component1Wrapped) {",
        "Child _component1 = null;",
        "_component1 = _component1Wrapped.getContents();",
        "component1.add(_component1);",
        "}",
        "}",
        "this.data = new Root(component1);",
        "}",
        "public static final RootParcel wrap(Root data) {",
        "return new RootParcel(data);",
        "}",
        "public Root getContents() {",
        "return data;",
        "}",
        "@Override public int describeContents() {",
        "return 0;",
        "}",
        "@Override public void writeToParcel(Parcel dest, int flags) {",
        "if (data.component1() == null) {",
        "dest.writeInt(1);",
        "} else {",
        "dest.writeInt(0);",
        "List<Child> component1 = data.component1();",
        "List<ChildParcel> component1Wrapped = new ArrayList<ChildParcel>(component1.size());",
        "for (Child component1Item : component1) {",
        "ChildParcel _component1 = ChildParcel.wrap(component1Item);",
        "component1Wrapped.add(_component1);",
        "}",
        "dest.writeList(component1Wrapped);",
        "}",
        "}",
        "}"
    ));

    JavaFileObject childParcel = JavaFileObjects.forSourceString("test/ChildParcel", Joiner.on('\n').join(
        "package test;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.lang.Integer;",
        "import java.lang.Override;",
        "public class ChildParcel implements Parcelable {",
        "public static final Parcelable.Creator<ChildParcel> CREATOR = new Parcelable.Creator<ChildParcel>() {",
        "@Override public ChildParcel createFromParcel(Parcel in) {",
        "return new ChildParcel(in);",
        "}",
        "@Override public ChildParcel[] newArray(int size) {",
        "return new ChildParcel[size];",
        "}",
        "};",
        "private final Child data;",
        "private ChildParcel(Child data) {",
        "this.data = data;",
        "}",
        "private ChildParcel(Parcel in) {",
        "Integer component1 = null;",
        "component1 = (Integer) in.readValue(Integer.class.getClassLoader());",
        "this.data = new Child(component1);",
        "}",
        "public static final ChildParcel wrap(Child data) {",
        "return new ChildParcel(data);",
        "}",
        "public Child getContents() {",
        "return data;",
        "}",
        "@Override public int describeContents() {",
        "return 0;",
        "}",
        "@Override public void writeToParcel(Parcel dest, int flags) {",
        "Integer component1 = data.component1();",
        "dest.writeValue(component1);",
        "}",
        "}"
    ));

    assertAbout(javaSources()).that(asList(dataClassRoot, dataClassChild))
        .processedWith(new DataParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(rootParcel, childParcel);
  }

  @Test public void listOfParcelableTypesTest() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.dataparcel.DataParcel;",
        "import java.util.List;",
        "@DataParcel",
        "public final class Test {",
        "private final List<Integer> testList;",
        "public Test(List<Integer> testList) {",
        "this.testList = testList;",
        "}",
        "public List<Integer> component1() {",
        "return this.testList;",
        "}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/TestParcel", Joiner.on('\n').join(
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
        "List<Integer> component1 = null;",
        "component1 = (List<Integer>) in.readArrayList(getClass().getClassLoader());",
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
        "List<Integer> component1 = data.component1();",
        "dest.writeList(component1);",
        "}",
        "}"
    ));

    assertAbout(javaSource()).that(source)
        .processedWith(new DataParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void listOfDataTypesTest() throws Exception {
    JavaFileObject dataClassRoot = JavaFileObjects.forSourceString("test.Root", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.dataparcel.DataParcel;",
        "import java.util.List;",
        "@DataParcel",
        "public final class Root {",
        "private final List<Child> child;",
        "public Root(List<Child> child) {",
        "this.child = child;",
        "}",
        "public List<Child> component1() {",
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

    JavaFileObject rootParcel = JavaFileObjects.forSourceString("test/RootParcel", Joiner.on('\n').join(
        "package test;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.lang.Override;",
        "import java.util.ArrayList;",
        "import java.util.List;",
        "public class RootParcel implements Parcelable {",
        "public static final Parcelable.Creator<RootParcel> CREATOR = new Parcelable.Creator<RootParcel>() {",
        "@Override public RootParcel createFromParcel(Parcel in) {",
        "return new RootParcel(in);",
        "}",
        "@Override public RootParcel[] newArray(int size) {",
        "return new RootParcel[size];",
        "}",
        "};",
        "private final Root data;",
        "private RootParcel(Root data) {",
        "this.data = data;",
        "}",
        "private RootParcel(Parcel in) {",
        "List<Child> component1 = null;",
        "List<ChildParcel> component1Wrapped = (List<ChildParcel>) in.readArrayList(getClass().getClassLoader());",
        "component1 = new ArrayList<Child>(component1Wrapped.size());",
        "for (ChildParcel _component1Wrapped : component1Wrapped) {",
        "Child _component1 = null;",
        "_component1 = _component1Wrapped.getContents();",
        "component1.add(_component1);",
        "}",
        "this.data = new Root(component1);",
        "}",
        "public static final RootParcel wrap(Root data) {",
        "return new RootParcel(data);",
        "}",
        "public Root getContents() {",
        "return data;",
        "}",
        "@Override public int describeContents() {",
        "return 0;",
        "}",
        "@Override public void writeToParcel(Parcel dest, int flags) {",
        "List<Child> component1 = data.component1();",
        "List<ChildParcel> component1Wrapped = new ArrayList<ChildParcel>(component1.size());",
        "for (Child component1Item : component1) {",
        "ChildParcel _component1 = ChildParcel.wrap(component1Item);",
        "component1Wrapped.add(_component1);",
        "}",
        "dest.writeList(component1Wrapped);",
        "}",
        "}"
    ));

    JavaFileObject childParcel = JavaFileObjects.forSourceString("test/ChildParcel", Joiner.on('\n').join(
        "package test;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.lang.Integer;",
        "import java.lang.Override;",
        "public class ChildParcel implements Parcelable {",
        "public static final Parcelable.Creator<ChildParcel> CREATOR = new Parcelable.Creator<ChildParcel>() {",
        "@Override public ChildParcel createFromParcel(Parcel in) {",
        "return new ChildParcel(in);",
        "}",
        "@Override public ChildParcel[] newArray(int size) {",
        "return new ChildParcel[size];",
        "}",
        "};",
        "private final Child data;",
        "private ChildParcel(Child data) {",
        "this.data = data;",
        "}",
        "private ChildParcel(Parcel in) {",
        "Integer component1 = null;",
        "component1 = (Integer) in.readValue(Integer.class.getClassLoader());",
        "this.data = new Child(component1);",
        "}",
        "public static final ChildParcel wrap(Child data) {",
        "return new ChildParcel(data);",
        "}",
        "public Child getContents() {",
        "return data;",
        "}",
        "@Override public int describeContents() {",
        "return 0;",
        "}",
        "@Override public void writeToParcel(Parcel dest, int flags) {",
        "Integer component1 = data.component1();",
        "dest.writeValue(component1);",
        "}",
        "}"
    ));

    assertAbout(javaSources()).that(asList(dataClassRoot, dataClassChild))
        .processedWith(new DataParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(rootParcel, childParcel);
  }

  @Test public void listOfListOfDataTypesTest() throws Exception {
    JavaFileObject dataClassRoot = JavaFileObjects.forSourceString("test.Root", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.dataparcel.DataParcel;",
        "import java.util.List;",
        "@DataParcel",
        "public final class Root {",
        "private final List<List<Child>> child;",
        "public Root(List<List<Child>> child) {",
        "this.child = child;",
        "}",
        "public List<List<Child>> component1() {",
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

    JavaFileObject rootParcel = JavaFileObjects.forSourceString("test/RootParcel", Joiner.on('\n').join(
        "package test;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.lang.Override;",
        "import java.util.ArrayList;",
        "import java.util.List;",
        "public class RootParcel implements Parcelable {",
        "public static final Parcelable.Creator<RootParcel> CREATOR = new Parcelable.Creator<RootParcel>() {",
        "@Override public RootParcel createFromParcel(Parcel in) {",
        "return new RootParcel(in);",
        "}",
        "@Override public RootParcel[] newArray(int size) {",
        "return new RootParcel[size];",
        "}",
        "};",
        "private final Root data;",
        "private RootParcel(Root data) {",
        "this.data = data;",
        "}",
        "private RootParcel(Parcel in) {",
        "List<List<Child>> component1 = null;",
        "List<List<ChildParcel>> component1Wrapped = (List<List<ChildParcel>>) in.readArrayList(getClass().getClassLoader());",
        "component1 = new ArrayList<List<Child>>(component1Wrapped.size());",
        "for (List<ChildParcel> _component1Wrapped : component1Wrapped) {",
        "List<Child> _component1 = null;",
        "_component1 = new ArrayList<Child>(_component1Wrapped.size());",
        "for (ChildParcel __component1Wrapped : _component1Wrapped) {",
        "Child __component1 = null;",
        "__component1 = __component1Wrapped.getContents();",
        "_component1.add(__component1);",
        "}",
        "component1.add(_component1);",
        "}",
        "this.data = new Root(component1);",
        "}",
        "public static final RootParcel wrap(Root data) {",
        "return new RootParcel(data);",
        "}",
        "public Root getContents() {",
        "return data;",
        "}",
        "@Override public int describeContents() {",
        "return 0;",
        "}",
        "@Override public void writeToParcel(Parcel dest, int flags) {",
        "List<List<Child>> component1 = data.component1();",
        "List<List<ChildParcel>> component1Wrapped = new ArrayList<List<ChildParcel>>(component1.size());",
        "for (List<Child> component1Item : component1) {",
        "List<Child> _component1 = component1Item;",
        "List<ChildParcel> _component1Wrapped = new ArrayList<ChildParcel>(_component1.size());",
        "for (Child _component1Item : _component1) {",
        "ChildParcel __component1 = ChildParcel.wrap(_component1Item);",
        "_component1Wrapped.add(__component1);",
        "}",
        "component1Wrapped.add(_component1Wrapped);",
        "}",
        "dest.writeList(component1Wrapped);",
        "}",
        "}"
    ));

    JavaFileObject childParcel = JavaFileObjects.forSourceString("test/ChildParcel", Joiner.on('\n').join(
        "package test;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.lang.Integer;",
        "import java.lang.Override;",
        "public class ChildParcel implements Parcelable {",
        "public static final Parcelable.Creator<ChildParcel> CREATOR = new Parcelable.Creator<ChildParcel>() {",
        "@Override public ChildParcel createFromParcel(Parcel in) {",
        "return new ChildParcel(in);",
        "}",
        "@Override public ChildParcel[] newArray(int size) {",
        "return new ChildParcel[size];",
        "}",
        "};",
        "private final Child data;",
        "private ChildParcel(Child data) {",
        "this.data = data;",
        "}",
        "private ChildParcel(Parcel in) {",
        "Integer component1 = null;",
        "component1 = (Integer) in.readValue(Integer.class.getClassLoader());",
        "this.data = new Child(component1);",
        "}",
        "public static final ChildParcel wrap(Child data) {",
        "return new ChildParcel(data);",
        "}",
        "public Child getContents() {",
        "return data;",
        "}",
        "@Override public int describeContents() {",
        "return 0;",
        "}",
        "@Override public void writeToParcel(Parcel dest, int flags) {",
        "Integer component1 = data.component1();",
        "dest.writeValue(component1);",
        "}",
        "}"
    ));

    assertAbout(javaSources()).that(asList(dataClassRoot, dataClassChild))
        .processedWith(new DataParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(rootParcel, childParcel);
  }

  @Test public void listOfParcelableMapsTest() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.dataparcel.DataParcel;",
        "import java.util.List;",
        "import java.util.Map;",
        "@DataParcel",
        "public final class Test {",
        "private final List<Map<Integer, Integer>> testList;",
        "public Test(List<Map<Integer, Integer>> testList) {",
        "this.testList = testList;",
        "}",
        "public List<Map<Integer, Integer>> component1() {",
        "return this.testList;",
        "}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/TestParcel", Joiner.on('\n').join(
        "package test;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.lang.Integer;",
        "import java.lang.Override;",
        "import java.util.List;",
        "import java.util.Map;",
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
        "List<Map<Integer, Integer>> component1 = null;",
        "component1 = (List<Map<Integer, Integer>>) in.readArrayList(getClass().getClassLoader());",
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
        "List<Map<Integer, Integer>> component1 = data.component1();",
        "dest.writeList(component1);",
        "}",
        "}"
    ));

    assertAbout(javaSource()).that(source)
        .processedWith(new DataParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void listOfNonParcelableMapsTest() throws Exception {
    JavaFileObject dataClassRoot = JavaFileObjects.forSourceString("test.Root", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.dataparcel.DataParcel;",
        "import java.util.List;",
        "import java.util.Map;",
        "@DataParcel",
        "public final class Root {",
        "private final List<Map<Integer, Child>> child;",
        "public Root(List<Map<Integer, Child>> child) {",
        "this.child = child;",
        "}",
        "public List<Map<Integer, Child>> component1() {",
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

    JavaFileObject rootParcel = JavaFileObjects.forSourceString("test/RootParcel", Joiner.on('\n').join(
        "package test;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.lang.Integer;",
        "import java.lang.Override;",
        "import java.util.ArrayList;",
        "import java.util.HashMap;",
        "import java.util.List;",
        "import java.util.Map;",
        "public class RootParcel implements Parcelable {",
        "public static final Parcelable.Creator<RootParcel> CREATOR = new Parcelable.Creator<RootParcel>() {",
        "@Override public RootParcel createFromParcel(Parcel in) {",
        "return new RootParcel(in);",
        "}",
        "@Override public RootParcel[] newArray(int size) {",
        "return new RootParcel[size];",
        "}",
        "};",
        "private final Root data;",
        "private RootParcel(Root data) {",
        "this.data = data;",
        "}",
        "private RootParcel(Parcel in) {",
        "List<Map<Integer, Child>> component1 = null;",
        "List<Map<Integer, ChildParcel>> component1Wrapped = (List<Map<Integer, ChildParcel>>) in.readArrayList(getClass().getClassLoader());",
        "component1 = new ArrayList<Map<Integer, Child>>(component1Wrapped.size());",
        "for (Map<Integer, ChildParcel> _component1Wrapped : component1Wrapped) {",
        "Map<Integer, Child> _component1 = null;",
        "_component1 = new HashMap<Integer, Child>(_component1Wrapped.size());",
        "for (Integer __component1Wrapped : _component1Wrapped.keySet()) {",
        "Integer __component1 = null;",
        "__component1 = __component1Wrapped;",
        "ChildParcel $_component1Wrapped = _component1Wrapped.get(__component1Wrapped);",
        "Child $_component1 = null;",
        "$_component1 = $_component1Wrapped.getContents();",
        "_component1.put(__component1, $_component1);",
        "}",
        "component1.add(_component1);",
        "}",
        "this.data = new Root(component1);",
        "}",
        "public static final RootParcel wrap(Root data) {",
        "return new RootParcel(data);",
        "}",
        "public Root getContents() {",
        "return data;",
        "}",
        "@Override public int describeContents() {",
        "return 0;",
        "}",
        "@Override public void writeToParcel(Parcel dest, int flags) {",
        "List<Map<Integer, Child>> component1 = data.component1();",
        "List<Map<Integer, ChildParcel>> component1Wrapped = new ArrayList<Map<Integer, ChildParcel>>(component1.size());",
        "for (Map<Integer, Child> component1Item : component1) {",
        "Map<Integer, Child> _component1 = component1Item;",
        "Map<Integer, ChildParcel> _component1Wrapped = new HashMap<Integer, ChildParcel>(_component1.size());",
        "for (Integer _component1Item : _component1.keySet()) {",
        "Integer __component1 = _component1Item;",
        "ChildParcel $_component1 = ChildParcel.wrap(_component1.get(_component1Item));",
        "_component1Wrapped.put(__component1, $_component1);",
        "}",
        "component1Wrapped.add(_component1Wrapped);",
        "}",
        "dest.writeList(component1Wrapped);",
        "}",
        "}"
    ));

    JavaFileObject childParcel = JavaFileObjects.forSourceString("test/ChildParcel", Joiner.on('\n').join(
        "package test;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.lang.Integer;",
        "import java.lang.Override;",
        "public class ChildParcel implements Parcelable {",
        "public static final Parcelable.Creator<ChildParcel> CREATOR = new Parcelable.Creator<ChildParcel>() {",
        "@Override public ChildParcel createFromParcel(Parcel in) {",
        "return new ChildParcel(in);",
        "}",
        "@Override public ChildParcel[] newArray(int size) {",
        "return new ChildParcel[size];",
        "}",
        "};",
        "private final Child data;",
        "private ChildParcel(Child data) {",
        "this.data = data;",
        "}",
        "private ChildParcel(Parcel in) {",
        "Integer component1 = null;",
        "component1 = (Integer) in.readValue(Integer.class.getClassLoader());",
        "this.data = new Child(component1);",
        "}",
        "public static final ChildParcel wrap(Child data) {",
        "return new ChildParcel(data);",
        "}",
        "public Child getContents() {",
        "return data;",
        "}",
        "@Override public int describeContents() {",
        "return 0;",
        "}",
        "@Override public void writeToParcel(Parcel dest, int flags) {",
        "Integer component1 = data.component1();",
        "dest.writeValue(component1);",
        "}",
        "}"
    ));

    assertAbout(javaSources()).that(asList(dataClassRoot, dataClassChild))
        .processedWith(new DataParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(rootParcel, childParcel);
  }

  @Test public void listOfNonParcelableArraysTest() throws Exception {
    JavaFileObject dataClassRoot = JavaFileObjects.forSourceString("test.Root", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.dataparcel.DataParcel;",
        "import java.util.List;",
        "@DataParcel",
        "public final class Root {",
        "private final List<Child[]> child;",
        "public Root(List<Child[]> child) {",
        "this.child = child;",
        "}",
        "public List<Child[]> component1() {",
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

    JavaFileObject rootParcel = JavaFileObjects.forSourceString("test/RootParcel", Joiner.on('\n').join(
        "package test;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.lang.Override;",
        "import java.util.ArrayList;",
        "import java.util.List;",
        "public class RootParcel implements Parcelable {",
        "public static final Parcelable.Creator<RootParcel> CREATOR = new Parcelable.Creator<RootParcel>() {",
        "@Override public RootParcel createFromParcel(Parcel in) {",
        "return new RootParcel(in);",
        "}",
        "@Override public RootParcel[] newArray(int size) {",
        "return new RootParcel[size];",
        "}",
        "};",
        "private final Root data;",
        "private RootParcel(Root data) {",
        "this.data = data;",
        "}",
        "private RootParcel(Parcel in) {",
        "List<Child[]> component1 = null;",
        "List<ChildParcel[]> component1Wrapped = (List<ChildParcel[]>) in.readArrayList(getClass().getClassLoader());",
        "component1 = new ArrayList<Child[]>(component1Wrapped.size());",
        "for (ChildParcel[] _component1Wrapped : component1Wrapped) {",
        "Child[] _component1 = null;",
        "_component1 = new Child[_component1Wrapped.length];",
        "for (int _component1Index = 0; _component1Index < _component1Wrapped.length; _component1Index++) {",
        "Child __component1 = null;",
        "ChildParcel __component1Wrapped = _component1Wrapped[_component1Index];",
        "__component1 = __component1Wrapped.getContents();",
        "_component1[_component1Index] = __component1;",
        "}",
        "component1.add(_component1);",
        "}",
        "this.data = new Root(component1);",
        "}",
        "public static final RootParcel wrap(Root data) {",
        "return new RootParcel(data);",
        "}",
        "public Root getContents() {",
        "return data;",
        "}",
        "@Override public int describeContents() {",
        "return 0;",
        "}",
        "@Override public void writeToParcel(Parcel dest, int flags) {",
        "List<Child[]> component1 = data.component1();",
        "List<ChildParcel[]> component1Wrapped = new ArrayList<ChildParcel[]>(component1.size());",
        "for (Child[] component1Item : component1) {",
        "Child[] _component1 = component1Item;",
        "ChildParcel[] _component1Wrapped = new ChildParcel[_component1.length];",
        "for (int _component1Index = 0; _component1Index < _component1.length; _component1Index++) {",
        "ChildParcel __component1 = ChildParcel.wrap(_component1[_component1Index]);",
        "_component1Wrapped[_component1Index] = __component1;",
        "}",
        "component1Wrapped.add(_component1Wrapped);",
        "}",
        "dest.writeList(component1Wrapped);",
        "}",
        "}"
    ));

    JavaFileObject childParcel = JavaFileObjects.forSourceString("test/ChildParcel", Joiner.on('\n').join(
        "package test;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.lang.Integer;",
        "import java.lang.Override;",
        "public class ChildParcel implements Parcelable {",
        "public static final Parcelable.Creator<ChildParcel> CREATOR = new Parcelable.Creator<ChildParcel>() {",
        "@Override public ChildParcel createFromParcel(Parcel in) {",
        "return new ChildParcel(in);",
        "}",
        "@Override public ChildParcel[] newArray(int size) {",
        "return new ChildParcel[size];",
        "}",
        "};",
        "private final Child data;",
        "private ChildParcel(Child data) {",
        "this.data = data;",
        "}",
        "private ChildParcel(Parcel in) {",
        "Integer component1 = null;",
        "component1 = (Integer) in.readValue(Integer.class.getClassLoader());",
        "this.data = new Child(component1);",
        "}",
        "public static final ChildParcel wrap(Child data) {",
        "return new ChildParcel(data);",
        "}",
        "public Child getContents() {",
        "return data;",
        "}",
        "@Override public int describeContents() {",
        "return 0;",
        "}",
        "@Override public void writeToParcel(Parcel dest, int flags) {",
        "Integer component1 = data.component1();",
        "dest.writeValue(component1);",
        "}",
        "}"
    ));

    assertAbout(javaSources()).that(asList(dataClassRoot, dataClassChild))
        .processedWith(new DataParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(rootParcel, childParcel);
  }

  @Test public void arrayListOfParcelableTypesTest() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.dataparcel.DataParcel;",
        "import java.util.ArrayList;",
        "@DataParcel",
        "public final class Test {",
        "private final ArrayList<Integer> testList;",
        "public Test(ArrayList<Integer> testList) {",
        "this.testList = testList;",
        "}",
        "public ArrayList<Integer> component1() {",
        "return this.testList;",
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
        "ArrayList<Integer> component1 = null;",
        "component1 = new ArrayList<Integer>();",
        "in.readList(component1, getClass().getClassLoader());",
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
        "ArrayList<Integer> component1 = data.component1();",
        "dest.writeList(component1);",
        "}",
        "}"
    ));

    assertAbout(javaSource()).that(source)
        .processedWith(new DataParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void linkedListOfParcelableTypesTest() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.dataparcel.DataParcel;",
        "import java.util.LinkedList;",
        "@DataParcel",
        "public final class Test {",
        "private final LinkedList<Integer> testList;",
        "public Test(LinkedList<Integer> testList) {",
        "this.testList = testList;",
        "}",
        "public LinkedList<Integer> component1() {",
        "return this.testList;",
        "}",
        "}"
    ));

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/TestParcel", Joiner.on('\n').join(
        "package test;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.lang.Integer;",
        "import java.lang.Override;",
        "import java.util.LinkedList;",
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
        "LinkedList<Integer> component1 = null;",
        "component1 = new LinkedList<Integer>();",
        "in.readList(component1, getClass().getClassLoader());",
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
        "LinkedList<Integer> component1 = data.component1();",
        "dest.writeList(component1);",
        "}",
        "}"
    ));

    assertAbout(javaSource()).that(source)
        .processedWith(new DataParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void linkedListOfNonParcelableTypesTest() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.dataparcel.DataParcel;",
        "import java.util.LinkedList;",
        "@DataParcel",
        "public final class Test {",
        "private final LinkedList<Child> testList;",
        "public Test(LinkedList<Child> testList) {",
        "this.testList = testList;",
        "}",
        "public LinkedList<Child> component1() {",
        "return this.testList;",
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
        "import java.util.LinkedList;",
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
        "LinkedList<Child> component1 = null;",
        "LinkedList<ChildParcel> component1Wrapped = new LinkedList<ChildParcel>();",
        "in.readList(component1Wrapped, getClass().getClassLoader());",
        "component1 = new LinkedList<Child>();",
        "for (ChildParcel _component1Wrapped : component1Wrapped) {",
        "Child _component1 = null;",
        "_component1 = _component1Wrapped.getContents();",
        "component1.add(_component1);",
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
        "LinkedList<Child> component1 = data.component1();",
        "LinkedList<ChildParcel> component1Wrapped = new LinkedList<ChildParcel>();",
        "for (Child component1Item : component1) {",
        "ChildParcel _component1 = ChildParcel.wrap(component1Item);",
        "component1Wrapped.add(_component1);",
        "}",
        "dest.writeList(component1Wrapped);",
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
