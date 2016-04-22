package nz.bradcampbell.paperparcel;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import javax.tools.JavaFileObject;
import org.junit.Test;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;
import static java.util.Arrays.asList;

public class ParcelableTests {

  @Test public void bitmapTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.graphics.Bitmap;",
            "import android.os.Parcelable;",
            "import nz.bradcampbell.paperparcel.PaperParcel;",
            "@PaperParcel",
            "public final class Test {",
            "  private final Bitmap child;",
            "  public Test(Bitmap child) {",
            "    this.child = child;",
            "  }",
            "  public Bitmap getChild() {",
            "    return this.child;",
            "  }",
            "}"
        ));

    JavaFileObject expectedSource =
        JavaFileObjects.forSourceString("test/Test$$Wrapper", Joiner.on('\n').join(
            "package test;",
            "import android.graphics.Bitmap;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import java.lang.Override;",
            "import nz.bradcampbell.paperparcel.internal.ParcelableWrapper;",
            "public final class Test$$Wrapper implements ParcelableWrapper<Test> {",
            "  public static final Parcelable.Creator<Test$$Wrapper> CREATOR = ",
            "      new Parcelable.Creator<Test$$Wrapper>() {",
            "    @Override public Test$$Wrapper createFromParcel(Parcel in) {",
            "      Bitmap outChild = null;",
            "      if (in.readInt() == 0) {",
            "        outChild = (Bitmap) in.readParcelable(Bitmap.class.getClassLoader());",
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
            "    Bitmap child = this.data.getChild();",
            "    if (child == null) {",
            "      dest.writeInt(1);",
            "    } else {",
            "      dest.writeInt(0);",
            "      dest.writeParcelable(child, flags);",
            "    }",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void extendsParcelableTest() throws Exception {
    JavaFileObject dataClassRoot =
        JavaFileObjects.forSourceString("test.Root", Joiner.on('\n').join(
            "package test;",
            "import nz.bradcampbell.paperparcel.PaperParcel;",
            "import java.util.List;",
            "@PaperParcel",
            "public final class Root {",
            "  private final Child child;",
            "  public Root(Child child) {",
            "    this.child = child;",
            "  }",
            "  public Child getChild() {",
            "    return this.child;",
            "  }",
            "}"
        ));

    JavaFileObject customParcelable =
        JavaFileObjects.forSourceString("test.CustomParcelable", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcelable;",
            "public interface CustomParcelable extends Parcelable {",
            "}"
        ));

    JavaFileObject dataClassChild =
        JavaFileObjects.forSourceString("test.Child", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "public final class Child implements CustomParcelable {",
            "  private final Integer child;",
            "  public Child(Integer child) {",
            "    this.child = child;",
            "  }",
            "  public Integer getChild() {",
            "    return this.child;",
            "  }",
            "  public static final Parcelable.Creator<Child> CREATOR = ",
            "      new Parcelable.Creator<Child>() {",
            "    @Override public Child createFromParcel(Parcel in) {",
            "      return new Child(in);",
            "    }",
            "    @Override public Child[] newArray(int size) {",
            "      return new Child[size];",
            "    }",
            "  };",
            "  private Child(Parcel in) {",
            "    child = in.readInt();",
            "  }",
            "  @Override public int describeContents() {",
            "    return 0;",
            "  }",
            "  @Override public void writeToParcel(Parcel dest, int flags) {",
            "    dest.writeInt(child);",
            "  }",
            "}"
        ));

    JavaFileObject rootParcel =
        JavaFileObjects.forSourceString("test/Root$$Wrapper", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import java.lang.Override;",
            "import nz.bradcampbell.paperparcel.internal.ParcelableWrapper;",
            "public final class Root$$Wrapper implements ParcelableWrapper<Root> {",
            "  public static final Parcelable.Creator<Root$$Wrapper> CREATOR = ",
            "      new Parcelable.Creator<Root$$Wrapper>() {",
            "    @Override public Root$$Wrapper createFromParcel(Parcel in) {",
            "      Child outChild = null;",
            "      if (in.readInt() == 0) {",
            "        outChild = (Child) in.readParcelable(Child.class.getClassLoader());",
            "      }",
            "      Root data = new Root(outChild);",
            "      return new Root$$Wrapper(data);",
            "    }",
            "    @Override public Root$$Wrapper[] newArray(int size) {",
            "      return new Root$$Wrapper[size];",
            "    }",
            "  };",
            "  private final Root data;",
            "  public Root$$Wrapper(Root data) {",
            "    this.data = data;",
            "  }",
            "  @Override public Root get() {",
            "    return this.data;",
            "  }",
            "  @Override public int describeContents() {",
            "    return 0;",
            "  }",
            "  @Override public void writeToParcel(Parcel dest, int flags) {",
            "    Child child = this.data.getChild();",
            "    if (child == null) {",
            "      dest.writeInt(1);",
            "    } else {",
            "      dest.writeInt(0);",
            "      dest.writeParcelable(child, flags);",
            "    }",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(asList(dataClassRoot, customParcelable, dataClassChild))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(rootParcel);
  }
}
