package nz.bradcampbell.paperparcel;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import javax.tools.JavaFileObject;
import org.junit.Test;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;
import static java.util.Arrays.asList;

public class TypeAdapterTests {

  @Test public void classScopedTypeAdapterTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import test.DateTypeAdapter;",
            "import nz.bradcampbell.paperparcel.TypeAdapters;",
            "import nz.bradcampbell.paperparcel.PaperParcel;",
            "import java.util.Date;",
            "@PaperParcel",
            "@TypeAdapters(DateTypeAdapter.class)",
            "public final class Test {",
            "  private final Date child;",
            "  public Test(Date child) {",
            "    this.child = child;",
            "  }",
            "  public Date getChild() {",
            "    return this.child;",
            "  }",
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

    JavaFileObject expectedSource =
        JavaFileObjects.forSourceString("test/Test$$Wrapper", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import java.lang.Override;",
            "import java.util.Date;",
            "import nz.bradcampbell.paperparcel.internal.ParcelableWrapper;",
            "public final class Test$$Wrapper implements ParcelableWrapper<Test> {",
            "  public static final Parcelable.Creator<Test$$Wrapper> CREATOR = ",
            "      new Parcelable.Creator<Test$$Wrapper>() {",
            "    @Override public Test$$Wrapper createFromParcel(Parcel in) {",
            "      DateTypeAdapter dateTypeAdapter = new DateTypeAdapter();",
            "      Date outChild = null;",
            "      if (in.readInt() == 0) {",
            "        outChild = dateTypeAdapter.readFromParcel(in);",
            "      }",
            "      Test data = new Test(outChild);",
            "      return new Test$$Wrapper(data);",
            "    }",
            "    @Override public Test$$Wrapper[] newArray(int size) {",
            "      return new Test$$Wrapper[size];",
            "    }",
            "  };",
            "  private final Test data;",
            "  public Test$$Wrapper(Test data) {",
            "    this.data = data;",
            "  }",
            "  @Override public Test get() {",
            "    return this.data;",
            "  }",
            "  @Override public int describeContents() {",
            "    return 0;",
            "  }",
            "  @Override public void writeToParcel(Parcel dest, int flags) {",
            "    DateTypeAdapter dateTypeAdapter = new DateTypeAdapter();",
            "    Date child = this.data.getChild();",
            "    if (child == null) {",
            "      dest.writeInt(1);",
            "    } else {",
            "      dest.writeInt(0);",
            "      dateTypeAdapter.writeToParcel(child, dest, flags);",
            "    }",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(asList(source, typeAdapter))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void defaultTypeAdapterTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import nz.bradcampbell.paperparcel.PaperParcel;",
            "import java.util.Date;",
            "@PaperParcel",
            "public final class Test {",
            "  private final Date child;",
            "  public Test(Date child) {",
            "    this.child = child;",
            "  }",
            "  public Date getChild() {",
            "    return this.child;",
            "  }",
            "}"
        ));

    JavaFileObject typeAdapter =
        JavaFileObjects.forSourceString("test.DateTypeAdapter", Joiner.on('\n').join(
            "package test;",
            "import nz.bradcampbell.paperparcel.DefaultAdapter;",
            "import nz.bradcampbell.paperparcel.TypeAdapter;",
            "import java.util.Date;",
            "import android.os.Parcel;",
            "@DefaultAdapter",
            "public class DateTypeAdapter implements TypeAdapter<Date> {",
            "  public Date readFromParcel(Parcel in) {",
            "    return new Date(in.readLong());",
            "  }",
            "  public void writeToParcel(Date value, Parcel dest, int flags) {",
            "    dest.writeLong(value.getTime());",
            "  }",
            "}"
        ));

    JavaFileObject expectedSource =
        JavaFileObjects.forSourceString("test/Test$$Wrapper", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import java.lang.Override;",
            "import java.util.Date;",
            "import nz.bradcampbell.paperparcel.internal.ParcelableWrapper;",
            "public final class Test$$Wrapper implements ParcelableWrapper<Test> {",
            "  public static final Parcelable.Creator<Test$$Wrapper> CREATOR = ",
            "      new Parcelable.Creator<Test$$Wrapper>() {",
            "    @Override public Test$$Wrapper createFromParcel(Parcel in) {",
            "      DateTypeAdapter dateTypeAdapter = new DateTypeAdapter();",
            "      Date outChild = null;",
            "      if (in.readInt() == 0) {",
            "        outChild = dateTypeAdapter.readFromParcel(in);",
            "      }",
            "      Test data = new Test(outChild);",
            "      return new Test$$Wrapper(data);",
            "    }",
            "    @Override public Test$$Wrapper[] newArray(int size) {",
            "      return new Test$$Wrapper[size];",
            "    }",
            "  };",
            "  private final Test data;",
            "  public Test$$Wrapper(Test data) {",
            "    this.data = data;",
            "  }",
            "  @Override public Test get() {",
            "    return this.data;",
            "  }",
            "  @Override public int describeContents() {",
            "    return 0;",
            "  }",
            "  @Override public void writeToParcel(Parcel dest, int flags) {",
            "    DateTypeAdapter dateTypeAdapter = new DateTypeAdapter();",
            "    Date child = this.data.getChild();",
            "    if (child == null) {",
            "      dest.writeInt(1);",
            "    } else {",
            "      dest.writeInt(0);",
            "      dateTypeAdapter.writeToParcel(child, dest, flags);",
            "    }",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(asList(source, typeAdapter))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void multipleDeclarationsOfTheSameTypeAdapterDoNoDuplicateTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import test.DateTypeAdapter;",
            "import nz.bradcampbell.paperparcel.TypeAdapters;",
            "import nz.bradcampbell.paperparcel.PaperParcel;",
            "import java.util.Date;",
            "@PaperParcel",
            "public final class Test {",
            "  @TypeAdapters(DateTypeAdapter.class) public Date child1;",
            "  @TypeAdapters(DateTypeAdapter.class) public Date child2;",
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

    JavaFileObject expectedSource =
        JavaFileObjects.forSourceString("test/Test$$Wrapper", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import java.lang.Override;",
            "import java.util.Date;",
            "import nz.bradcampbell.paperparcel.internal.ParcelableWrapper;",
            "public final class Test$$Wrapper implements ParcelableWrapper<Test> {",
            "  public static final Parcelable.Creator<Test$$Wrapper> CREATOR = ",
            "      new Parcelable.Creator<Test$$Wrapper>() {",
            "    @Override public Test$$Wrapper createFromParcel(Parcel in) {",
            "      DateTypeAdapter dateTypeAdapter = new DateTypeAdapter();",
            "      Date outChild1 = null;",
            "      if (in.readInt() == 0) {",
            "        outChild1 = dateTypeAdapter.readFromParcel(in);",
            "      }",
            "      Date outChild2 = null;",
            "      if (in.readInt() == 0) {",
            "        outChild2 = dateTypeAdapter.readFromParcel(in);",
            "      }",
            "      Test data = new Test();",
            "      data.child1 = outChild1;",
            "      data.child2 = outChild2;",
            "      return new Test$$Wrapper(data);",
            "    }",
            "    @Override public Test$$Wrapper[] newArray(int size) {",
            "      return new Test$$Wrapper[size];",
            "    }",
            "  };",
            "  private final Test data;",
            "  public Test$$Wrapper(Test data) {",
            "    this.data = data;",
            "  }",
            "  @Override public Test get() {",
            "    return this.data;",
            "  }",
            "  @Override public int describeContents() {",
            "    return 0;",
            "  }",
            "  @Override public void writeToParcel(Parcel dest, int flags) {",
            "    DateTypeAdapter dateTypeAdapter = new DateTypeAdapter();",
            "    Date child1 = this.data.child1;",
            "    if (child1 == null) {",
            "      dest.writeInt(1);",
            "    } else {",
            "      dest.writeInt(0);",
            "      dateTypeAdapter.writeToParcel(child1, dest, flags);",
            "    }",
            "    Date child2 = this.data.child2;",
            "    if (child2 == null) {",
            "      dest.writeInt(1);",
            "    } else {",
            "      dest.writeInt(0);",
            "      dateTypeAdapter.writeToParcel(child2, dest, flags);",
            "    }",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(asList(source, typeAdapter))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void singleVariableScopedTypeAdapterTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import nz.bradcampbell.paperparcel.TypeAdapters;",
            "import nz.bradcampbell.paperparcel.PaperParcel;",
            "import java.util.Date;",
            "@PaperParcel",
            "public final class Test {",
            "  @TypeAdapters(FastDateTypeAdapter.class) public Date child1;",
            "  public Date child2;",
            "}"
        ));

    JavaFileObject fastDateTypeAdapter =
        JavaFileObjects.forSourceString("test.FastDateTypeAdapter", Joiner.on('\n').join(
            "package test;",
            "import nz.bradcampbell.paperparcel.TypeAdapter;",
            "import java.util.Date;",
            "import android.os.Parcel;",
            "public class FastDateTypeAdapter implements TypeAdapter<Date> {",
            "  public Date readFromParcel(Parcel in) {",
            "    return new Date(in.readLong());",
            "  }",
            "  public void writeToParcel(Date value, Parcel dest, int flags) {",
            "    dest.writeLong(value.getTime());",
            "  }",
            "}"
        ));

    JavaFileObject slowDateTypeAdapter =
        JavaFileObjects.forSourceString("test.SlowDateTypeAdapter", Joiner.on('\n').join(
            "package test;",
            "import nz.bradcampbell.paperparcel.DefaultAdapter;",
            "import nz.bradcampbell.paperparcel.TypeAdapter;",
            "import java.util.Date;",
            "import android.os.Parcel;",
            "@DefaultAdapter",
            "public class SlowDateTypeAdapter implements TypeAdapter<Date> {",
            "  public Date readFromParcel(Parcel in) {",
            "    return (Date) in.readSerializable();",
            "  }",
            "  public void writeToParcel(Date value, Parcel dest, int flags) {",
            "    dest.writeSerializable(value);",
            "  }",
            "}"
        ));

    JavaFileObject expectedSource =
        JavaFileObjects.forSourceString("test/Test$$Wrapper", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import java.lang.Override;",
            "import java.util.Date;",
            "import nz.bradcampbell.paperparcel.internal.ParcelableWrapper;",
            "public final class Test$$Wrapper implements ParcelableWrapper<Test> {",
            "  public static final Parcelable.Creator<Test$$Wrapper> CREATOR = ",
            "      new Parcelable.Creator<Test$$Wrapper>() {",
            "    @Override public Test$$Wrapper createFromParcel(Parcel in) {",
            "      FastDateTypeAdapter fastDateTypeAdapter = new FastDateTypeAdapter();",
            "      SlowDateTypeAdapter slowDateTypeAdapter = new SlowDateTypeAdapter();",
            "      Date outChild1 = null;",
            "      if (in.readInt() == 0) {",
            "        outChild1 = fastDateTypeAdapter.readFromParcel(in);",
            "      }",
            "      Date outChild2 = null;",
            "      if (in.readInt() == 0) {",
            "        outChild2 = slowDateTypeAdapter.readFromParcel(in);",
            "      }",
            "      Test data = new Test();",
            "      data.child1 = outChild1;",
            "      data.child2 = outChild2;",
            "      return new Test$$Wrapper(data);",
            "    }",
            "    @Override public Test$$Wrapper[] newArray(int size) {",
            "      return new Test$$Wrapper[size];",
            "    }",
            "  };",
            "  private final Test data;",
            "  public Test$$Wrapper(Test data) {",
            "    this.data = data;",
            "  }",
            "  @Override public Test get() {",
            "    return this.data;",
            "  }",
            "  @Override public int describeContents() {",
            "    return 0;",
            "  }",
            "  @Override public void writeToParcel(Parcel dest, int flags) {",
            "    FastDateTypeAdapter fastDateTypeAdapter = new FastDateTypeAdapter();",
            "    SlowDateTypeAdapter slowDateTypeAdapter = new SlowDateTypeAdapter();",
            "    Date child1 = this.data.child1;",
            "    if (child1 == null) {",
            "      dest.writeInt(1);",
            "    } else {",
            "      dest.writeInt(0);",
            "      fastDateTypeAdapter.writeToParcel(child1, dest, flags);",
            "    }",
            "    Date child2 = this.data.child2;",
            "    if (child2 == null) {",
            "      dest.writeInt(1);",
            "    } else {",
            "      dest.writeInt(0);",
            "      slowDateTypeAdapter.writeToParcel(child2, dest, flags);",
            "    }",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(asList(source, fastDateTypeAdapter, slowDateTypeAdapter))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }
}
