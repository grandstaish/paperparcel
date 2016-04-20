package nz.bradcampbell.paperparcel;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import javax.tools.JavaFileObject;
import org.junit.Test;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class SetTests {

  @Test public void setOfParcelableTypesTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import nz.bradcampbell.paperparcel.PaperParcel;",
            "import java.util.Set;",
            "@PaperParcel",
            "public final class Test {",
            "  private final Set<Integer> child;",
            "  public Test(Set<Integer> child) {",
            "    this.child = child;",
            "  }",
            "  public Set<Integer> getChild() {",
            "    return this.child;",
            "  }",
            "}"
        ));

    JavaFileObject expectedSource =
        JavaFileObjects.forSourceString("test/TestParcel", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import java.lang.Integer;",
            "import java.lang.Override;",
            "import java.util.LinkedHashSet;",
            "import java.util.Set;",
            "import nz.bradcampbell.paperparcel.TypedParcelable;",
            "public final class TestParcel implements TypedParcelable<Test> {",
            "  public static final Parcelable.Creator<TestParcel> CREATOR = ",
            "      new Parcelable.Creator<TestParcel>() {",
            "    @Override public TestParcel createFromParcel(Parcel in) {",
            "      Set<Integer> outChild = null;",
            "      if (in.readInt() == 0) {",
            "        int childSize = in.readInt();",
            "        Set<Integer> child = new LinkedHashSet<Integer>(childSize);",
            "        for (int childIndex = 0; childIndex < childSize; childIndex++) {",
            "          Integer outChildItem = null;",
            "          if (in.readInt() == 0) {",
            "            outChildItem = in.readInt();",
            "          }",
            "          child.add(outChildItem);",
            "        }",
            "        outChild = child;",
            "      }",
            "      Test data = new Test(outChild);",
            "      return new TestParcel(data);",
            "    }",
            "    @Override public TestParcel[] newArray(int size) {",
            "      return new TestParcel[size];",
            "    }",
            "  };",
            "  private final Test data;",
            "  public TestParcel(Test data) {",
            "    this.data = data;",
            "  }",
            "  @Override public Test get() {",
            "    return this.data;",
            "  }",
            "  @Override public int describeContents() {",
            "    return 0;",
            "  }",
            "  @Override public void writeToParcel(Parcel dest, int flags) {",
            "    Set<Integer> child = this.data.getChild();", "if (child == null) {",
            "      dest.writeInt(1);",
            "    } else {",
            "      dest.writeInt(0);",
            "      int childSize = child.size();",
            "      dest.writeInt(childSize);",
            "      for (Integer childItem : child) {",
            "        if (childItem == null) {",
            "          dest.writeInt(1);",
            "        } else {",
            "          dest.writeInt(0);", "dest.writeInt(childItem);",
            "        }",
            "      }",
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

  @Test public void hashSetOfIntegersTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import nz.bradcampbell.paperparcel.PaperParcel;",
            "import java.util.HashSet;",
            "@PaperParcel",
            "public final class Test {",
            "  private final HashSet<Integer> child;",
            "  public Test(HashSet<Integer> child) {",
            "    this.child = child;",
            "  }",
            "  public HashSet<Integer> getChild() {",
            "    return this.child;",
            "  }",
            "}"
        ));

    JavaFileObject expectedSource =
        JavaFileObjects.forSourceString("test/TestParcel", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import java.lang.Integer;",
            "import java.lang.Override;",
            "import java.util.HashSet;",
            "import nz.bradcampbell.paperparcel.TypedParcelable;",
            "public final class TestParcel implements TypedParcelable<Test> {",
            "  public static final Parcelable.Creator<TestParcel> CREATOR = ",
            "      new Parcelable.Creator<TestParcel>() {",
            "    @Override public TestParcel createFromParcel(Parcel in) {",
            "      HashSet<Integer> outChild = null;",
            "      if (in.readInt() == 0) {",
            "        int childSize = in.readInt();",
            "        HashSet<Integer> child = new HashSet<Integer>();",
            "        for (int childIndex = 0; childIndex < childSize; childIndex++) {",
            "          Integer outChildItem = null;",
            "          if (in.readInt() == 0) {",
            "            outChildItem = in.readInt();",
            "          }",
            "          child.add(outChildItem);",
            "        }",
            "        outChild = child;",
            "      }",
            "      Test data = new Test(outChild);",
            "      return new TestParcel(data);",
            "    }",
            "    @Override public TestParcel[] newArray(int size) {",
            "      return new TestParcel[size];",
            "    }",
            "  };",
            "  private final Test data;",
            "  public TestParcel(Test data) {",
            "    this.data = data;",
            "  }",
            "  @Override public Test get() {",
            "    return this.data;",
            "  }",
            "  @Override public int describeContents() {",
            "    return 0;",
            "  }",
            "  @Override public void writeToParcel(Parcel dest, int flags) {",
            "    HashSet<Integer> child = this.data.getChild();",
            "    if (child == null) {",
            "      dest.writeInt(1);",
            "    } else {",
            "      dest.writeInt(0);",
            "      int childSize = child.size();",
            "      dest.writeInt(childSize);",
            "      for (Integer childItem : child) {",
            "        if (childItem == null) {",
            "          dest.writeInt(1);",
            "        } else {",
            "          dest.writeInt(0);",
            "          dest.writeInt(childItem);",
            "        }",
            "      }",
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
}
