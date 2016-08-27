package paperparcel;

import com.google.auto.value.processor.AutoValueProcessor;
import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import javax.tools.JavaFileObject;
import org.junit.Test;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class PaperParcelAutoValueExtensionTests {

  @Test public void basicAutoValueTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import com.google.auto.value.AutoValue;",
            "import android.os.Parcelable;",
            "@AutoValue",
            "public abstract class Test implements Parcelable {",
            "  public abstract int count();",
            "}"
        ));

    JavaFileObject autoValueSubclass =
        JavaFileObjects.forSourceString("test/AutoValue_Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import java.lang.Override;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel",
            "public final class AutoValue_Test extends $AutoValue_Test {",
            "  public static final Parcelable.Creator<AutoValue_Test> CREATOR = PaperParcelAutoValue_Test.CREATOR;",
            "  AutoValue_Test(int count) {",
            "    super(count);",
            "  }",
            "  @Override",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "    PaperParcelAutoValue_Test.writeToParcel(this, dest, flags);",
            "  }",
            "  @Override",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "}"
        ));

    JavaFileObject paperParcelOutput =
        JavaFileObjects.forSourceString("test/PaperParcelAutoValue_Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import javax.annotation.Generated;",
            "import paperparcel.adapter.IntegerAdapter;",
            GeneratedLines.GENERATED_ANNOTATION,
            "final class PaperParcelAutoValue_Test {",
            "  static final Parcelable.Creator<AutoValue_Test> CREATOR = new Parcelable.Creator<AutoValue_Test>() {",
            "    @Override",
            "    public AutoValue_Test createFromParcel(Parcel in) {",
            "      int count = IntegerAdapter.INSTANCE.readFromParcel(in);",
            "      AutoValue_Test data = new AutoValue_Test(count);",
            "      return data;",
            "    }",
            "    @Override",
            "    public AutoValue_Test[] newArray(int size) {",
            "      return new AutoValue_Test[size];",
            "    }",
            "  };",
            "  private PaperParcelAutoValue_Test() {",
            "  }",
            "  static void writeToParcel(AutoValue_Test data, Parcel dest, int flags) {",
            "    IntegerAdapter.INSTANCE.writeToParcel(data.count(), dest, flags);",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new AutoValueProcessor(), new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(autoValueSubclass, paperParcelOutput);
  }

  @Test public void failIfParcelableAutoValueClassHasTypeParametersTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import com.google.auto.value.AutoValue;",
            "import android.os.Parcelable;",
            "@AutoValue",
            "public abstract class Test<T> implements Parcelable {",
            "  public abstract int count();",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new AutoValueProcessor(), new PaperParcelProcessor())
        .failsToCompile()
        .withErrorContaining(ErrorMessages.AUTOVALUE_ON_GENERIC_CLASS)
        .in(source)
        .onLine(5);
  }

  @Test public void omitDescribeContentsWhenAlreadyDefinedTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import com.google.auto.value.AutoValue;",
            "import android.os.Parcelable;",
            "@AutoValue",
            "public abstract class Test implements Parcelable {",
            "  public abstract int count();",
            "  @Override",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "}"
        ));

    JavaFileObject autoValueSubclass =
        JavaFileObjects.forSourceString("test/AutoValue_Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import java.lang.Override;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel",
            "public final class AutoValue_Test extends $AutoValue_Test {",
            "  public static final Parcelable.Creator<AutoValue_Test> CREATOR = PaperParcelAutoValue_Test.CREATOR;",
            "  AutoValue_Test(int count) {",
            "    super(count);",
            "  }",
            "  @Override",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "    PaperParcelAutoValue_Test.writeToParcel(this, dest, flags);",
            "  }",
            "}"
        ));

    JavaFileObject paperParcelOutput =
        JavaFileObjects.forSourceString("test/PaperParcelAutoValue_Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import javax.annotation.Generated;",
            "import paperparcel.adapter.IntegerAdapter;",
            GeneratedLines.GENERATED_ANNOTATION,
            "final class PaperParcelAutoValue_Test {",
            "  static final Parcelable.Creator<AutoValue_Test> CREATOR = new Parcelable.Creator<AutoValue_Test>() {",
            "    @Override",
            "    public AutoValue_Test createFromParcel(Parcel in) {",
            "      int count = IntegerAdapter.INSTANCE.readFromParcel(in);",
            "      AutoValue_Test data = new AutoValue_Test(count);",
            "      return data;",
            "    }",
            "    @Override",
            "    public AutoValue_Test[] newArray(int size) {",
            "      return new AutoValue_Test[size];",
            "    }",
            "  };",
            "  private PaperParcelAutoValue_Test() {",
            "  }",
            "  static void writeToParcel(AutoValue_Test data, Parcel dest, int flags) {",
            "    IntegerAdapter.INSTANCE.writeToParcel(data.count(), dest, flags);",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor(), new AutoValueProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(autoValueSubclass, paperParcelOutput);
  }

  @Test public void failWhenWriteToParcelAlreadyDefinedTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import com.google.auto.value.AutoValue;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "@AutoValue",
            "public abstract class Test implements Parcelable {",
            "  public abstract int count();",
            "  @Override",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor(), new AutoValueProcessor())
        .failsToCompile()
        .withErrorContaining(String.format(ErrorMessages.MANUAL_IMPLEMENTATION_OF_WRITE_TO_PARCEL,
            "test.Test"))
        .in(source)
        .onLine(9);
  }

  @Test public void failWhenCreatorAlreadyDefinedTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import com.google.auto.value.AutoValue;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "@AutoValue",
            "public abstract class Test implements Parcelable {",
            "  public abstract int count();",
            "  public static final Parcelable.Creator<Test> CREATOR = ",
            "      new Parcelable.Creator<Test>() {",
            "    @Override public Test createFromParcel(Parcel in) {",
            "      return null;",
            "    }",
            "    @Override public Test[] newArray(int size) {",
            "      return new Test[size];",
            "    }",
            "  };",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor(), new AutoValueProcessor())
        .failsToCompile()
        .withErrorContaining(String.format(ErrorMessages.MANUAL_IMPLEMENTATION_OF_CREATOR,
            "test.Test"))
        .in(source)
        .onLine(8);
  }

}
