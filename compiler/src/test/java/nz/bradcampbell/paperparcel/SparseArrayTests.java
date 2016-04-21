package nz.bradcampbell.paperparcel;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import javax.tools.JavaFileObject;
import org.junit.Test;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class SparseArrayTests {

  @Test public void sparseArrayOfParcelableTypesTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import nz.bradcampbell.paperparcel.PaperParcel;",
            "import android.util.SparseArray;",
            "@PaperParcel",
            "public final class Test {",
            "  private final SparseArray<Integer> child;",
            "  public Test(SparseArray<Integer> child) {",
            "    this.child = child;",
            "  }",
            "  public SparseArray<Integer> getChild() {",
            "    return this.child;",
            "  }",
            "}"
        ));

    JavaFileObject expectedSource =
        JavaFileObjects.forSourceString("test/Test$$Wrapper", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import android.util.SparseArray;",
            "import java.lang.Integer;",
            "import java.lang.Override;",
            "import nz.bradcampbell.paperparcel.internal.ParcelableWrapper;",
            "public final class Test$$Wrapper implements ParcelableWrapper<Test> {",
            "  public static final Parcelable.Creator<Test$$Wrapper> CREATOR = ",
            "      new Parcelable.Creator<Test$$Wrapper>() {",
            "    @Override public Test$$Wrapper createFromParcel(Parcel in) {",
            "      SparseArray<Integer> outChild = null;",
            "      if (in.readInt() == 0) {",
            "        int childSize = in.readInt();",
            "        SparseArray<Integer> child = new SparseArray<Integer>(childSize);",
            "        for (int childIndex = 0; childIndex < childSize; childIndex++) {",
            "          int childKey = in.readInt();",
            "          Integer outChildValue = null;",
            "          if (in.readInt() == 0) {",
            "            outChildValue = in.readInt();",
            "          }",
            "          child.put(childKey, outChildValue);",
            "        }",
            "        outChild = child;",
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
            "    SparseArray<Integer> child = this.data.getChild();",
            "    if (child == null) {",
            "      dest.writeInt(1);",
            "    } else {",
            "      dest.writeInt(0);",
            "      int childSize = child.size();",
            "      dest.writeInt(childSize);",
            "      for (int childIndex = 0; childIndex < childSize; childIndex++) {",
            "        int childKey = child.keyAt(childIndex);",
            "        dest.writeInt(childKey);",
            "        Integer childValue = child.get(childKey);",
            "        if (childValue == null) {",
            "          dest.writeInt(1);",
            "        } else {",
            "          dest.writeInt(0);",
            "          dest.writeInt(childValue);",
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
