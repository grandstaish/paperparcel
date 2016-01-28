package nz.bradcampbell.paperparcel;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

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
                "List<Integer> outChild = null;",
                "if (in.readInt() == 0) {",
                "int childSize = in.readInt();",
                "List<Integer> child = new ArrayList<Integer>(childSize);",
                "for (int childIndex = 0; childIndex < childSize; childIndex++) {",
                "Integer childItem = (Integer) in.readValue(null);",
                "child.add(childItem);",
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
                "List<Integer> child = data.getChild();",
                "if (child == null) {",
                "dest.writeInt(1);",
                "} else {",
                "dest.writeInt(0);",
                "int childSize = child.size();",
                "dest.writeInt(childSize);",
                "for (int childIndex = 0; childIndex < childSize; childIndex++) {",
                "Integer childItem = child.get(childIndex);",
                "dest.writeValue(childItem);",
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
