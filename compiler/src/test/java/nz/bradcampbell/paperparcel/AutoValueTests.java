package nz.bradcampbell.paperparcel;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

import com.google.auto.value.processor.AutoValueProcessor;
import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

public class AutoValueTests {
  @Test public void basicAutoValueTest() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import com.google.auto.value.AutoValue;",
        "import android.os.Parcelable;",
        "import java.util.Date;",
        "@AutoValue",
        "public abstract class Test implements Parcelable {",
        "public abstract int count();",
        "}"
    ));

    JavaFileObject autoValueSubclass = JavaFileObjects.forSourceString("test/AutoValue_Test", Joiner.on('\n').join(
        "package test;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.lang.ClassLoader;",
        "import java.lang.Override;",
        "import nz.bradcampbell.paperparcel.PaperParcel;",
        "import nz.bradcampbell.paperparcel.PaperParcels;",
        "@PaperParcel",
        "public final class AutoValue_Test extends $AutoValue_Test {",
        "private static final ClassLoader CLASS_LOADER = AutoValue_Test.class.getClassLoader();",
        "public static final Parcelable.Creator<AutoValue_Test> CREATOR = new Parcelable.Creator<AutoValue_Test>() {",
        "@Override public AutoValue_Test createFromParcel(Parcel in) {",
        "return PaperParcels.unwrap(in.readParcelable(CLASS_LOADER));",
        "}",
        "@Override public AutoValue_Test[] newArray(int size) {",
        "return new AutoValue_Test[size];",
        "}",
        "};",
        "AutoValue_Test(int count) {",
        "super(count);",
        "}",
        "@Override",
        "public void writeToParcel(Parcel dest, int flags) {",
        "dest.writeParcelable(PaperParcels.wrap(this), flags);",
        "}",
        "@Override",
        "public int describeContents() {",
        "return 0;",
        "}",
        "}"
    ));

    JavaFileObject wrapperSource = JavaFileObjects.forSourceString("test/AutoValue_TestParcel", Joiner.on('\n').join(
        "package test;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.lang.Override;",
        "import nz.bradcampbell.paperparcel.TypedParcelable;",
        "public final class AutoValue_TestParcel implements TypedParcelable<AutoValue_Test> {",
        "public static final Parcelable.Creator<AutoValue_TestParcel> CREATOR = new Parcelable.Creator<AutoValue_TestParcel>() {",
        "@Override public AutoValue_TestParcel createFromParcel(Parcel in) {",
        "int count = in.readInt();",
        "AutoValue_Test data = new AutoValue_Test(count);",
        "return new AutoValue_TestParcel(data);",
        "}",
        "@Override public AutoValue_TestParcel[] newArray(int size) {",
        "return new AutoValue_TestParcel[size];",
        "}",
        "};",
        "private final AutoValue_Test data;",
        "public AutoValue_TestParcel(AutoValue_Test data) {",
        "this.data = data;",
        "}",
        "@Override public AutoValue_Test get() {",
        "return this.data;",
        "}",
        "@Override public int describeContents() {",
        "return 0;",
        "}",
        "@Override public void writeToParcel(Parcel dest, int flags) {",
        "int count = this.data.count();",
        "dest.writeInt(count);",
        "}",
        "}"
    ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor(), new AutoValueProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(autoValueSubclass, wrapperSource);
  }

  @Test public void omitDescribeContentsWhenAlreadyDefinedTest() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import com.google.auto.value.AutoValue;",
        "import android.os.Parcelable;",
        "import java.util.Date;",
        "@AutoValue",
        "public abstract class Test implements Parcelable {",
        "public abstract int count();",
        "@Override",
        "public int describeContents() {",
        "return 0;",
        "}",
        "}"
    ));

    JavaFileObject autoValueSubclass = JavaFileObjects.forSourceString("test/AutoValue_Test", Joiner.on('\n').join(
        "package test;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.lang.ClassLoader;",
        "import java.lang.Override;",
        "import nz.bradcampbell.paperparcel.PaperParcel;",
        "import nz.bradcampbell.paperparcel.PaperParcels;",
        "@PaperParcel",
        "public final class AutoValue_Test extends $AutoValue_Test {",
        "private static final ClassLoader CLASS_LOADER = AutoValue_Test.class.getClassLoader();",
        "public static final Parcelable.Creator<AutoValue_Test> CREATOR = new Parcelable.Creator<AutoValue_Test>() {",
        "@Override public AutoValue_Test createFromParcel(Parcel in) {",
        "return PaperParcels.unwrap(in.readParcelable(CLASS_LOADER));",
        "}",
        "@Override public AutoValue_Test[] newArray(int size) {",
        "return new AutoValue_Test[size];",
        "}",
        "};",
        "AutoValue_Test(int count) {",
        "super(count);",
        "}",
        "@Override",
        "public void writeToParcel(Parcel dest, int flags) {",
        "dest.writeParcelable(PaperParcels.wrap(this), flags);",
        "}",
        "}"
    ));

    JavaFileObject wrapperSource = JavaFileObjects.forSourceString("test/AutoValue_TestParcel", Joiner.on('\n').join(
        "package test;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.lang.Override;",
        "import nz.bradcampbell.paperparcel.TypedParcelable;",
        "public final class AutoValue_TestParcel implements TypedParcelable<AutoValue_Test> {",
        "public static final Parcelable.Creator<AutoValue_TestParcel> CREATOR = new Parcelable.Creator<AutoValue_TestParcel>() {",
        "@Override public AutoValue_TestParcel createFromParcel(Parcel in) {",
        "int count = in.readInt();",
        "AutoValue_Test data = new AutoValue_Test(count);",
        "return new AutoValue_TestParcel(data);",
        "}",
        "@Override public AutoValue_TestParcel[] newArray(int size) {",
        "return new AutoValue_TestParcel[size];",
        "}",
        "};",
        "private final AutoValue_Test data;",
        "public AutoValue_TestParcel(AutoValue_Test data) {",
        "this.data = data;",
        "}",
        "@Override public AutoValue_Test get() {",
        "return this.data;",
        "}",
        "@Override",
        "public int describeContents() {",
        "return 0;",
        "}",
        "@Override",
        "public void writeToParcel(Parcel dest, int flags) {",
        "int count = this.data.count();",
        "dest.writeInt(count);",
        "}",
        "}"
    ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor(), new AutoValueProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(autoValueSubclass, wrapperSource);
  }

  @Test public void failWhenWriteToParcelAlreadyDefinedTest() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import com.google.auto.value.AutoValue;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.util.Date;",
        "@AutoValue",
        "public abstract class Test implements Parcelable {",
        "public abstract int count();",
        "@Override",
        "public void writeToParcel(Parcel dest, int flags) {",
        "}",
        "}"
    ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor(), new AutoValueProcessor())
        .failsToCompile();
  }

  @Test public void failWhenCreatorAlreadyDefinedTest() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import com.google.auto.value.AutoValue;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.util.Date;",
        "@AutoValue",
        "public abstract class Test implements Parcelable {",
        "public abstract int count();",
        "public static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
        "@Override public Test createFromParcel(Parcel in) {",
        "return null;",
        "}",
        "@Override public Test[] newArray(int size) {",
        "return new Test[size];",
        "}",
        "};",
        "}"
    ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor(), new AutoValueProcessor())
        .failsToCompile();
  }
}
