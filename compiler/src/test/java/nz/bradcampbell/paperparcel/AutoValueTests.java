package nz.bradcampbell.paperparcel;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.auto.value.extension.AutoValueExtension;
import com.google.auto.value.processor.AutoValueProcessor;
import com.google.common.base.Joiner;
import com.google.testing.compile.CompilationRule;
import com.google.testing.compile.JavaFileObjects;

import nz.bradcampbell.paperparcel.util.SampleTypeWithParcelableContractSatisfied;
import org.junit.Rule;
import org.junit.Test;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;

public class AutoValueTests {
  @Rule public CompilationRule rule = new CompilationRule();

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
        "return PaperParcels.unsafeUnwrap(in.readParcelable(CLASS_LOADER));",
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
        "public final AutoValue_Test data;",
        "public AutoValue_TestParcel(AutoValue_Test data) {",
        "this.data = data;",
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
        "return PaperParcels.unsafeUnwrap(in.readParcelable(CLASS_LOADER));",
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
        "public final AutoValue_Test data;",
        "public AutoValue_TestParcel(AutoValue_Test data) {",
        "this.data = data;",
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

  @Test public void omitWriteToParcelWhenAlreadyDefinedTest() throws Exception {
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
        "return PaperParcels.unsafeUnwrap(in.readParcelable(CLASS_LOADER));",
        "}",
        "@Override public AutoValue_Test[] newArray(int size) {",
        "return new AutoValue_Test[size];",
        "}",
        "};",
        "AutoValue_Test(int count) {",
        "super(count);",
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
        "public final AutoValue_Test data;",
        "public AutoValue_TestParcel(AutoValue_Test data) {",
        "this.data = data;",
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

  @Test public void omitCreatorWhenAlreadyDefinedTest() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import com.google.auto.value.AutoValue;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.util.Date;",
        "@AutoValue",
        "public abstract class Test implements Parcelable {",
        "public abstract int count();",
        "public static final Parcelable.Creator<AutoValue_Test> CREATOR = new Parcelable.Creator<AutoValue_Test>() {",
        "@Override public AutoValue_Test createFromParcel(Parcel in) {",
        "return null;",
        "}",
        "@Override public AutoValue_Test[] newArray(int size) {",
        "return new AutoValue_Test[size];",
        "}",
        "};",
        "}"
    ));

    JavaFileObject autoValueSubclass = JavaFileObjects.forSourceString("test/AutoValue_Test", Joiner.on('\n').join(
        "package test;",
        "import android.os.Parcel;",
        "import java.lang.Override;",
        "import nz.bradcampbell.paperparcel.PaperParcel;",
        "import nz.bradcampbell.paperparcel.PaperParcels;",
        "@PaperParcel",
        "public final class AutoValue_Test extends $AutoValue_Test {",
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
        "public final AutoValue_Test data;",
        "public AutoValue_TestParcel(AutoValue_Test data) {",
        "this.data = data;",
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

  @Test public void omitWrapperWhenBothCreatorAndWriteToParcelAreAlreadyDefinedTest() throws Exception {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
        "package test;",
        "import com.google.auto.value.AutoValue;",
        "import android.os.Parcel;",
        "import android.os.Parcelable;",
        "import java.util.Date;",
        "@AutoValue",
        "public abstract class Test implements Parcelable {",
        "public abstract int count();",
        "public static final Parcelable.Creator<AutoValue_Test> CREATOR = new Parcelable.Creator<AutoValue_Test>() {",
        "@Override public AutoValue_Test createFromParcel(Parcel in) {",
        "return null;",
        "}",
        "@Override public AutoValue_Test[] newArray(int size) {",
        "return new AutoValue_Test[size];",
        "}",
        "};",
        "@Override",
        "public void writeToParcel(Parcel dest, int flags) {",
        "}",
        "}"
    ));

    JavaFileObject autoValueSubclass = JavaFileObjects.forSourceString("test/AutoValue_Test", Joiner.on('\n').join(
        "package test;",
        "import java.lang.Override;",
        "public final class AutoValue_Test extends $AutoValue_Test {",
        "AutoValue_Test(int count) {",
        "super(count);",
        "}",
        "@Override",
        "public int describeContents() {",
        "return 0;",
        "}",
        "}"
    ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor(), new AutoValueProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(autoValueSubclass);
  }

  @Test public void noOpWhenParcelableContractAlreadySatisfiedTest() throws Exception {
    PaperParcelAutoValueExtension extension = new PaperParcelAutoValueExtension();

    Elements elements = rule.getElements();
    TypeElement type = elements.getTypeElement(SampleTypeWithParcelableContractSatisfied.class.getCanonicalName());

    ProcessingEnvironment mockProcessingEnvironment = mock(ProcessingEnvironment.class);
    when(mockProcessingEnvironment.getElementUtils()).thenReturn(elements);
    when(mockProcessingEnvironment.getTypeUtils()).thenReturn(rule.getTypes());

    AutoValueExtension.Context mockContext = mock(AutoValueExtension.Context.class);
    when(mockContext.autoValueClass()).thenReturn(type);
    when(mockContext.processingEnvironment()).thenReturn(mockProcessingEnvironment);

    assertFalse(extension.applicable(mockContext));
  }
}
