package nz.bradcampbell.paperparcel;

import com.google.auto.value.processor.AutoValueProcessor;
import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import java.util.Arrays;
import javax.tools.JavaFileObject;
import org.junit.Test;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;

public class AutoValueTests {

  @Test public void basicAutoValueTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import com.google.auto.value.AutoValue;",
            "import android.os.Parcelable;",
            "import java.util.Date;",
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
            "import java.lang.ClassLoader;",
            "import java.lang.Override;",
            "import nz.bradcampbell.paperparcel.PaperParcel;",
            "import nz.bradcampbell.paperparcel.PaperParcels;",
            "@PaperParcel",
            "public final class AutoValue_Test extends $AutoValue_Test {",
            "  private static final ClassLoader CLASS_LOADER = ",
            "      AutoValue_Test.class.getClassLoader();",
            "  public static final Parcelable.Creator<AutoValue_Test> CREATOR = ",
            "      new Parcelable.Creator<AutoValue_Test>() {",
            "    @Override public AutoValue_Test createFromParcel(Parcel in) {",
            "      return PaperParcels.unwrap(in.readParcelable(CLASS_LOADER));",
            "    }",
            "    @Override public AutoValue_Test[] newArray(int size) {",
            "      return new AutoValue_Test[size];",
            "    }",
            "  };",
            "  AutoValue_Test(int count) {",
            "    super(count);",
            "  }",
            "  @Override",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "    dest.writeParcelable(PaperParcels.wrap(this), flags);",
            "  }",
            "  @Override",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "}"
        ));

    JavaFileObject wrapperSource =
        JavaFileObjects.forSourceString("test/AutoValue_Test$$Wrapper", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import java.lang.Override;",
            "import nz.bradcampbell.paperparcel.internal.ParcelableWrapper;",
            "public final class AutoValue_Test$$Wrapper implements ParcelableWrapper<AutoValue_Test> {",
            "  public static final Parcelable.Creator<AutoValue_Test$$Wrapper> CREATOR = ",
            "      new Parcelable.Creator<AutoValue_Test$$Wrapper>() {",
            "    @Override public AutoValue_Test$$Wrapper createFromParcel(Parcel in) {",
            "      int count = in.readInt();",
            "      AutoValue_Test data = new AutoValue_Test(count);",
            "      return new AutoValue_Test$$Wrapper(data);",
            "    }",
            "    @Override public AutoValue_Test$$Wrapper[] newArray(int size) {",
            "      return new AutoValue_Test$$Wrapper[size];",
            "    }",
            "  };",
            "  private final AutoValue_Test data;",
            "  public AutoValue_Test$$Wrapper(AutoValue_Test data) {",
            "    this.data = data;",
            "  }",
            "  @Override public AutoValue_Test get() {",
            "    return this.data;",
            "  }",
            "  @Override public int describeContents() {",
            "    return 0;",
            "  }",
            "  @Override public void writeToParcel(Parcel dest, int flags) {",
            "    int count = this.data.count();",
            "    dest.writeInt(count);",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor(), new AutoValueProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(autoValueSubclass, wrapperSource);
  }

  @Test public void omitDescribeContentsWhenAlreadyDefinedTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import com.google.auto.value.AutoValue;",
            "import android.os.Parcelable;",
            "import java.util.Date;",
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
            "import java.lang.ClassLoader;",
            "import java.lang.Override;",
            "import nz.bradcampbell.paperparcel.PaperParcel;",
            "import nz.bradcampbell.paperparcel.PaperParcels;",
            "@PaperParcel",
            "public final class AutoValue_Test extends $AutoValue_Test {",
            "  private static final ClassLoader CLASS_LOADER = ",
            "      AutoValue_Test.class.getClassLoader();",
            "  public static final Parcelable.Creator<AutoValue_Test> CREATOR = ",
            "      new Parcelable.Creator<AutoValue_Test>() {",
            "    @Override public AutoValue_Test createFromParcel(Parcel in) {",
            "      return PaperParcels.unwrap(in.readParcelable(CLASS_LOADER));",
            "    }",
            "    @Override public AutoValue_Test[] newArray(int size) {",
            "      return new AutoValue_Test[size];",
            "    }",
            "  };",
            "  AutoValue_Test(int count) {",
            "    super(count);",
            "  }",
            "  @Override",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "    dest.writeParcelable(PaperParcels.wrap(this), flags);",
            "  }",
            "}"
        ));

    JavaFileObject wrapperSource =
        JavaFileObjects.forSourceString("test/AutoValue_Test$$Wrapper", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import java.lang.Override;",
            "import nz.bradcampbell.paperparcel.internal.ParcelableWrapper;",
            "public final class AutoValue_Test$$Wrapper implements ParcelableWrapper<AutoValue_Test> {",
            "  public static final Parcelable.Creator<AutoValue_Test$$Wrapper> CREATOR = ",
            "      new Parcelable.Creator<AutoValue_Test$$Wrapper>() {",
            "    @Override public AutoValue_Test$$Wrapper createFromParcel(Parcel in) {",
            "      int count = in.readInt();",
            "      AutoValue_Test data = new AutoValue_Test(count);",
            "      return new AutoValue_Test$$Wrapper(data);",
            "    }",
            "    @Override public AutoValue_Test$$Wrapper[] newArray(int size) {",
            "      return new AutoValue_Test$$Wrapper[size];",
            "    }",
            "  };",
            "  private final AutoValue_Test data;",
            "  public AutoValue_Test$$Wrapper(AutoValue_Test data) {",
            "    this.data = data;",
            "  }",
            "  @Override public AutoValue_Test get() {",
            "    return this.data;",
            "  }",
            "  @Override",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  @Override",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "    int count = this.data.count();", "dest.writeInt(count);",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor(), new AutoValueProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(autoValueSubclass, wrapperSource);
  }

  @Test public void failWhenWriteToParcelAlreadyDefinedTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import com.google.auto.value.AutoValue;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import java.util.Date;",
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
        .withErrorContaining("Manual implementation of Parcelable#writeToParcel(Parcel,int) "
            + "found when processing test.Test. Remove this so PaperParcel can automatically "
            + "generate the implementation for you.")
        .in(source)
        .onLine(10);
  }

  @Test public void failWhenCreatorAlreadyDefinedTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import com.google.auto.value.AutoValue;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import java.util.Date;",
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
        .withErrorContaining("Manual implementation of a static Parcelable.Creator<T> CREATOR "
            + "field found when processing test.Test. Remove this so PaperParcel can "
            + "automatically generate the implementation for you.")
        .in(source)
        .onLine(9);
  }

  @Test public void fieldScopedTypeAdaptersWorkWithAutoValueTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import com.google.auto.value.AutoValue;",
            "import android.os.Parcelable;",
            "import java.util.Date;",
            "import nz.bradcampbell.paperparcel.TypeAdapters;",
            "@AutoValue",
            "public abstract class Test implements Parcelable {",
            "  @TypeAdapters(DateTypeAdapter.class) public abstract Date test();",
            "}"
        ));

    JavaFileObject typeAdapter =
        JavaFileObjects.forSourceString("test.DateTypeAdapter", Joiner.on('\n').join(
            "package test;",
            "import nz.bradcampbell.paperparcel.TypeAdapter;",
            "import java.util.Date;",
            "import android.os.Parcel;",
            "public class DateTypeAdapter implements TypeAdapter<Date> {",
            "  public Date readFromParcel(Parcel in) {",
            "    return new Date(in.readLong());",
            "  }",
            "  public void writeToParcel(Date value, Parcel dest, int flags) {",
            "    dest.writeLong(value.getTime());",
            "  }",
            "}"
        ));

    JavaFileObject autoValueSubclass =
        JavaFileObjects.forSourceString("test/AutoValue_Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import java.lang.ClassLoader;",
            "import java.lang.Override;",
            "import java.util.Date;",
            "import nz.bradcampbell.paperparcel.PaperParcel;",
            "import nz.bradcampbell.paperparcel.PaperParcels;",
            "@PaperParcel",
            "public final class AutoValue_Test extends $AutoValue_Test {",
            "  private static final ClassLoader CLASS_LOADER = ",
            "      AutoValue_Test.class.getClassLoader();",
            "  public static final Parcelable.Creator<AutoValue_Test> CREATOR = ",
            "      new Parcelable.Creator<AutoValue_Test>() {",
            "    @Override public AutoValue_Test createFromParcel(Parcel in) {",
            "      return PaperParcels.unwrap(in.readParcelable(CLASS_LOADER));",
            "    }",
            "    @Override public AutoValue_Test[] newArray(int size) {",
            "      return new AutoValue_Test[size];",
            "    }",
            "  };",
            "  AutoValue_Test(Date test) {",
            "    super(test);",
            "  }",
            "  @Override",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "    dest.writeParcelable(PaperParcels.wrap(this), flags);",
            "  }",
            "  @Override",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "}"
        ));

    JavaFileObject wrapperSource =
        JavaFileObjects.forSourceString("test/AutoValue_Test$$Wrapper", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import java.lang.Override;",
            "import java.util.Date;",
            "import nz.bradcampbell.paperparcel.internal.ParcelableWrapper;",
            "public final class AutoValue_Test$$Wrapper implements ParcelableWrapper<AutoValue_Test> {",
            "  public static final Parcelable.Creator<AutoValue_Test$$Wrapper> CREATOR = ",
            "      new Parcelable.Creator<AutoValue_Test$$Wrapper>() {",
            "    @Override public AutoValue_Test$$Wrapper createFromParcel(Parcel in) {",
            "      DateTypeAdapter dateTypeAdapter = new DateTypeAdapter();",
            "      Date outTest = null;",
            "      if (in.readInt() == 0) {",
            "        outTest = dateTypeAdapter.readFromParcel(in);",
            "      }",
            "      AutoValue_Test data = new AutoValue_Test(outTest);",
            "      return new AutoValue_Test$$Wrapper(data);",
            "    }",
            "    @Override public AutoValue_Test$$Wrapper[] newArray(int size) {",
            "      return new AutoValue_Test$$Wrapper[size];",
            "    }",
            "  };",
            "  private final AutoValue_Test data;",
            "  public AutoValue_Test$$Wrapper(AutoValue_Test data) {",
            "    this.data = data;",
            "  }",
            "  @Override public AutoValue_Test get() {",
            "    return this.data;",
            "  }",
            "  @Override public int describeContents() {",
            "    return 0;",
            "  }",
            "  @Override public void writeToParcel(Parcel dest, int flags) {",
            "    DateTypeAdapter dateTypeAdapter = new DateTypeAdapter();",
            "    Date test = this.data.test();",
            "    if (test == null) {",
            "      dest.writeInt(1);",
            "    } else {",
            "      dest.writeInt(0);",
            "      dateTypeAdapter.writeToParcel(test, dest, flags);",
            "    }",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(source, typeAdapter))
        .processedWith(new PaperParcelProcessor(), new AutoValueProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(autoValueSubclass, wrapperSource);
  }
}
