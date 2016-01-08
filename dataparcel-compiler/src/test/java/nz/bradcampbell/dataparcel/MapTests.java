package nz.bradcampbell.dataparcel;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;
import static java.util.Arrays.asList;

public class MapTests {

  @Test public void mapOfParcelableTypesTest() throws Exception {
    JavaFileObject dataClass = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.dataparcel.DataParcel;",
        "import java.util.Map;",
        "@DataParcel",
        "public final class Test {",
        "private final Map<Integer, Integer> child;",
        "public Test(Map<Integer, Integer> child) {",
        "this.child = child;",
        "}",
        "public Map<Integer, Integer> component1() {",
        "return this.child;",
        "}",
        "}"
    ));

    JavaFileObject testParcel = JavaFileObjects.forSourceString("test/TestParcel", Joiner.on('\n').join(
        "package test;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.lang.Integer;",
        "import java.lang.Override;",
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
        "Map<Integer, Integer> component1 = null;",
        "component1 = (Map<Integer, Integer>) in.readHashMap(getClass().getClassLoader());",
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
        "Map<Integer, Integer> component1 = data.component1();",
        "dest.writeMap(component1);",
        "}",
        "}"
    ));

    assertAbout(javaSource()).that(dataClass)
        .processedWith(new DataParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(testParcel);
  }

  @Test public void mapWithDataTypeAsKeyTest() throws Exception {
    JavaFileObject dataClassRoot = JavaFileObjects.forSourceString("test.Root", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.dataparcel.DataParcel;",
        "import java.util.Map;",
        "@DataParcel",
        "public final class Root {",
        "private final Map<Child, Integer> child;",
        "public Root(Map<Child, Integer> child) {",
        "this.child = child;",
        "}",
        "public Map<Child, Integer> component1() {",
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
        "import java.util.HashMap;",
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
        "Map<Child, Integer> component1 = null;",
        "Map<ChildParcel, Integer> component1Wrapped = (Map<ChildParcel, Integer>) in.readHashMap(getClass().getClassLoader());",
        "component1 = new HashMap<>(component1Wrapped.size());",
        "for (ChildParcel _component1Wrapped : component1Wrapped.keySet()) {",
        "Child _component1 = null;",
        "_component1 = _component1Wrapped.getContents();",
        "Integer $component1Wrapped = component1Wrapped.get(_component1Wrapped);",
        "Integer $component1 = null;",
        "$component1 = $component1Wrapped;",
        "component1.put(_component1, $component1);",
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
        "Map<Child, Integer> component1 = data.component1();",
        "Map<ChildParcel, Integer> component1Wrapped = new HashMap<>(component1.size());",
        "for (Child component1Item : component1.keySet()) {",
        "ChildParcel _component1 = ChildParcel.wrap(component1Item);",
        "Integer $component1 = component1.get(component1Item);",
        "component1Wrapped.put(_component1, $component1);",
        "}",
        "dest.writeMap(component1Wrapped);",
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
        "component1 = in.readInt();",
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
        "dest.writeInt(component1);",
        "}",
        "}"
    ));

    assertAbout(javaSources()).that(asList(dataClassRoot, dataClassChild))
        .processedWith(new DataParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(rootParcel, childParcel);
  }

  @Test public void mapWithDataTypeAsValueTest() throws Exception {
    JavaFileObject dataClassRoot = JavaFileObjects.forSourceString("test.Root", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.dataparcel.DataParcel;",
        "import java.util.Map;",
        "@DataParcel",
        "public final class Root {",
        "private final Map<Integer, Child> child;",
        "public Root(Map<Integer, Child> child) {",
        "this.child = child;",
        "}",
        "public Map<Integer, Child> component1() {",
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
        "import java.util.HashMap;",
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
        "Map<Integer, Child> component1 = null;",
        "Map<Integer, ChildParcel> component1Wrapped = (Map<Integer, ChildParcel>) in.readHashMap(getClass().getClassLoader());",
        "component1 = new HashMap<>(component1Wrapped.size());",
        "for (Integer _component1Wrapped : component1Wrapped.keySet()) {",
        "Integer _component1 = null;",
        "_component1 = _component1Wrapped;",
        "ChildParcel $component1Wrapped = component1Wrapped.get(_component1Wrapped);",
        "Child $component1 = null;",
        "$component1 = $component1Wrapped.getContents();",
        "component1.put(_component1, $component1);",
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
        "Map<Integer, Child> component1 = data.component1();",
        "Map<Integer, ChildParcel> component1Wrapped = new HashMap<>(component1.size());",
        "for (Integer component1Item : component1.keySet()) {",
        "Integer _component1 = component1Item;",
        "ChildParcel $component1 = ChildParcel.wrap(component1.get(component1Item));",
        "component1Wrapped.put(_component1, $component1);",
        "}",
        "dest.writeMap(component1Wrapped);",
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
        "component1 = in.readInt();",
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
        "dest.writeInt(component1);",
        "}",
        "}"
    ));

    assertAbout(javaSources()).that(asList(dataClassRoot, dataClassChild))
        .processedWith(new DataParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(rootParcel, childParcel);
  }

  @Test public void mapWithDataTypeAsKeyAndValueTest() throws Exception {
    JavaFileObject dataClassRoot = JavaFileObjects.forSourceString("test.Root", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.dataparcel.DataParcel;",
        "import java.util.Map;",
        "@DataParcel",
        "public final class Root {",
        "private final Map<Child, Child> child;",
        "public Root(Map<Child, Child> child) {",
        "this.child = child;",
        "}",
        "public Map<Child, Child> component1() {",
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
        "import java.util.HashMap;",
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
        "Map<Child, Child> component1 = null;",
        "Map<ChildParcel, ChildParcel> component1Wrapped = (Map<ChildParcel, ChildParcel>) in.readHashMap(getClass().getClassLoader());",
        "component1 = new HashMap<>(component1Wrapped.size());",
        "for (ChildParcel _component1Wrapped : component1Wrapped.keySet()) {",
        "Child _component1 = null;",
        "_component1 = _component1Wrapped.getContents();",
        "ChildParcel $component1Wrapped = component1Wrapped.get(_component1Wrapped);",
        "Child $component1 = null;",
        "$component1 = $component1Wrapped.getContents();",
        "component1.put(_component1, $component1);",
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
        "Map<Child, Child> component1 = data.component1();",
        "Map<ChildParcel, ChildParcel> component1Wrapped = new HashMap<>(component1.size());",
        "for (Child component1Item : component1.keySet()) {",
        "ChildParcel _component1 = ChildParcel.wrap(component1Item);",
        "ChildParcel $component1 = ChildParcel.wrap(component1.get(component1Item));",
        "component1Wrapped.put(_component1, $component1);",
        "}",
        "dest.writeMap(component1Wrapped);",
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
        "component1 = in.readInt();",
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
        "dest.writeInt(component1);",
        "}",
        "}"
    ));

    assertAbout(javaSources()).that(asList(dataClassRoot, dataClassChild))
        .processedWith(new DataParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(rootParcel, childParcel);
  }

  @Test public void mapWithParcelableListAsValueTest() throws Exception {
    JavaFileObject dataClassRoot = JavaFileObjects.forSourceString("test.Root", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.dataparcel.DataParcel;",
        "import java.lang.Integer;",
        "import java.util.Map;",
        "import java.util.List;",
        "@DataParcel",
        "public final class Root {",
        "private final Map<Integer, List<Integer>> child;",
        "public Root(Map<Integer, List<Integer>> child) {",
        "this.child = child;",
        "}",
        "public Map<Integer, List<Integer>> component1() {",
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
        "Map<Integer, List<Integer>> component1 = null;",
        "component1 = (Map<Integer, List<Integer>>) in.readHashMap(getClass().getClassLoader());",
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
        "Map<Integer, List<Integer>> component1 = data.component1();",
        "dest.writeMap(component1);",
        "}",
        "}"
    ));

    assertAbout(javaSource()).that(dataClassRoot)
        .processedWith(new DataParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(rootParcel);
  }

  @Test public void mapWithNonParcelableListAsValueTest() throws Exception {
    JavaFileObject dataClassRoot = JavaFileObjects.forSourceString("test.Root", Joiner.on('\n').join(
        "package test;",
        "import nz.bradcampbell.dataparcel.DataParcel;",
        "import java.util.Map;",
        "import java.util.List;",
        "@DataParcel",
        "public final class Root {",
        "private final Map<Integer, List<Child>> child;",
        "public Root(Map<Integer, List<Child>> child) {",
        "this.child = child;",
        "}",
        "public Map<Integer, List<Child>> component1() {",
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
        "Map<Integer, List<Child>> component1 = null;",
        "Map<Integer, List<ChildParcel>> component1Wrapped = (Map<Integer, List<ChildParcel>>) in.readHashMap(getClass().getClassLoader());",
        "component1 = new HashMap<>(component1Wrapped.size());",
        "for (Integer _component1Wrapped : component1Wrapped.keySet()) {",
        "Integer _component1 = null;",
        "_component1 = _component1Wrapped;",
        "List<ChildParcel> $component1Wrapped = component1Wrapped.get(_component1Wrapped);",
        "List<Child> $component1 = null;",
        "$component1 = new ArrayList<>($component1Wrapped.size());",
        "for (ChildParcel _$component1Wrapped : $component1Wrapped) {",
        "Child _$component1 = null;",
        "_$component1 = _$component1Wrapped.getContents();",
        "$component1.add(_$component1);",
        "}",
        "component1.put(_component1, $component1);",
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
        "Map<Integer, List<Child>> component1 = data.component1();",
        "Map<Integer, List<ChildParcel>> component1Wrapped = new HashMap<>(component1.size());",
        "for (Integer component1Item : component1.keySet()) {",
        "Integer _component1 = component1Item;",
        "List<Child> $component1 = component1.get(component1Item);",
        "List<ChildParcel> $component1Wrapped = new ArrayList<>($component1.size());",
        "for (Child $component1Item : $component1) {",
        "ChildParcel _$component1 = ChildParcel.wrap($component1Item);",
        "$component1Wrapped.add(_$component1);",
        "}",
        "component1Wrapped.put(_component1, $component1Wrapped);",
        "}",
        "dest.writeMap(component1Wrapped);",
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
        "component1 = in.readInt();",
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
        "dest.writeInt(component1);",
        "}",
        "}"
    ));

    assertAbout(javaSources()).that(asList(dataClassRoot, dataClassChild))
        .processedWith(new DataParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(rootParcel, childParcel);
  }
}
