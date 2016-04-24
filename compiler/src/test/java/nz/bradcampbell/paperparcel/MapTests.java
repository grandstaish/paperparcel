package nz.bradcampbell.paperparcel;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import javax.tools.JavaFileObject;
import org.junit.Test;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class MapTests {

  @Test public void mapOfParcelableTypesTest() throws Exception {
    JavaFileObject dataClass =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import nz.bradcampbell.paperparcel.PaperParcel;",
            "import java.util.Map;",
            "@PaperParcel",
            "public final class Test {",
            "  private final Map<Integer, Integer> child;",
            "  public Test(Map<Integer, Integer> child) {",
            "    this.child = child;",
            "  }",
            "  public Map<Integer, Integer> getChild() {",
            "    return this.child;",
            "  }",
            "}"
        ));

    JavaFileObject expectedSource =
        JavaFileObjects.forSourceString("test/Test$$Wrapper", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import java.lang.Integer;",
            "import java.lang.Override;",
            "import java.util.LinkedHashMap;",
            "import java.util.Map;",
            "import nz.bradcampbell.paperparcel.internal.ParcelableWrapper;",
            "public final class Test$$Wrapper implements ParcelableWrapper<Test> {",
            "  public static final Parcelable.Creator<Test$$Wrapper> CREATOR = ",
            "      new Parcelable.Creator<Test$$Wrapper>() {",
            "    @Override public Test$$Wrapper createFromParcel(Parcel in) {",
            "      Map<Integer, Integer> outChild = null;",
            "      if (in.readInt() == 0) {",
            "        int childSize = in.readInt();",
            "        Map<Integer, Integer> child = new LinkedHashMap<Integer, Integer>(childSize);",
            "        for (int childIndex = 0; childIndex < childSize; childIndex++) {",
            "          Integer outChildKey = null;",
            "          if (in.readInt() == 0) {",
            "            outChildKey = in.readInt();",
            "          }",
            "          Integer outChildValue = null;",
            "          if (in.readInt() == 0) {",
            "            outChildValue = in.readInt();",
            "          }",
            "          child.put(outChildKey, outChildValue);",
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
            "    Map<Integer, Integer> child = this.data.getChild();",
            "    if (child == null) {",
            "      dest.writeInt(1);",
            "    } else {",
            "      dest.writeInt(0);",
            "      dest.writeInt(child.size());",
            "      for (Map.Entry<Integer, Integer> childEntry : child.entrySet()) {",
            "        if (childEntry.getKey() == null) {",
            "          dest.writeInt(1);",
            "        } else {",
            "          dest.writeInt(0);",
            "          dest.writeInt(childEntry.getKey());",
            "        }",
            "        if (childEntry.getValue() == null) {",
            "          dest.writeInt(1);",
            "        } else {",
            "          dest.writeInt(0);",
            "          dest.writeInt(childEntry.getValue());",
            "        }",
            "      }",
            "    }",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(dataClass)
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void mapWithParcelableListAsValueTest() throws Exception {
    JavaFileObject dataClassRoot =
        JavaFileObjects.forSourceString("test.Root", Joiner.on('\n').join(
            "package test;",
            "import nz.bradcampbell.paperparcel.PaperParcel;",
            "import java.lang.Integer;",
            "import java.util.Map;",
            "import java.util.List;",
            "@PaperParcel",
            "public final class Root {",
            "  private final Map<Integer, List<Integer>> child;",
            "  public Root(Map<Integer, List<Integer>> child) {",
            "    this.child = child;",
            "  }",
            "  public Map<Integer, List<Integer>> getChild() {",
            "    return this.child;",
            "  }",
            "}"
        ));

    JavaFileObject expectedSource =
        JavaFileObjects.forSourceString("test/Root$$Wrapper", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import java.lang.Integer;",
            "import java.lang.Override;",
            "import java.util.ArrayList;",
            "import java.util.LinkedHashMap;",
            "import java.util.List;",
            "import java.util.Map;",
            "import nz.bradcampbell.paperparcel.internal.ParcelableWrapper;",
            "public final class Root$$Wrapper implements ParcelableWrapper<Root> {",
            "  public static final Parcelable.Creator<Root$$Wrapper> CREATOR = ",
            "      new Parcelable.Creator<Root$$Wrapper>() {",
            "    @Override public Root$$Wrapper createFromParcel(Parcel in) {",
            "      Map<Integer, List<Integer>> outChild = null;",
            "      if (in.readInt() == 0) {",
            "        int childSize = in.readInt();",
            "        Map<Integer, List<Integer>> child = ",
            "            new LinkedHashMap<Integer, List<Integer>>(childSize);",
            "        for (int childIndex = 0; childIndex < childSize; childIndex++) {",
            "          Integer outChildKey = null;",
            "          if (in.readInt() == 0) {",
            "            outChildKey = in.readInt();",
            "          }",
            "          List<Integer> outChildValue = null;",
            "          if (in.readInt() == 0) {",
            "            int childValueSize = in.readInt();",
            "            List<Integer> childValue = new ArrayList<Integer>(childValueSize);",
            "            for (int childValueIndex = 0; childValueIndex < childValueSize; ",
            "                childValueIndex++) {",
            "              Integer outChildValueItem = null;",
            "              if (in.readInt() == 0) {",
            "                outChildValueItem = in.readInt();",
            "              }",
            "              childValue.add(outChildValueItem);",
            "            }",
            "            outChildValue = childValue;",
            "          }",
            "          child.put(outChildKey, outChildValue);",
            "        }",
            "        outChild = child;",
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
            "    Map<Integer, List<Integer>> child = this.data.getChild();",
            "    if (child == null) {",
            "      dest.writeInt(1);",
            "    } else {",
            "      dest.writeInt(0);",
            "      dest.writeInt(child.size());",
            "      for (Map.Entry<Integer, List<Integer>> childEntry : child.entrySet()) {",
            "        if (childEntry.getKey() == null) {",
            "          dest.writeInt(1);",
            "        } else {",
            "          dest.writeInt(0);",
            "          dest.writeInt(childEntry.getKey());",
            "        }",
            "        if (childEntry.getValue() == null) {",
            "          dest.writeInt(1);",
            "        } else {",
            "          dest.writeInt(0);",
            "          int childValueSize = childEntry.getValue().size();",
            "          dest.writeInt(childValueSize);",
            "          for (Integer childValueItem : childEntry.getValue()) {",
            "            if (childValueItem == null) {",
            "              dest.writeInt(1);",
            "            } else {",
            "              dest.writeInt(0);",
            "              dest.writeInt(childValueItem);",
            "            }",
            "          }",
            "        }",
            "      }",
            "    }",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(dataClassRoot)
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void treeMapOfParcelableTypesTest() throws Exception {
    JavaFileObject dataClass =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import nz.bradcampbell.paperparcel.PaperParcel;",
            "import java.util.TreeMap;",
            "@PaperParcel",
            "public final class Test {",
            "  private final TreeMap<Integer, Integer> child;",
            "  public Test(TreeMap<Integer, Integer> child) {",
            "    this.child = child;",
            "  }",
            "  public TreeMap<Integer, Integer> getChild() {",
            "    return this.child;",
            "  }",
            "}"
        ));

    JavaFileObject expectedSource =
        JavaFileObjects.forSourceString("test/Test$$Wrapper", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import java.lang.Integer;",
            "import java.lang.Override;",
            "import java.util.Map;",
            "import java.util.TreeMap;",
            "import nz.bradcampbell.paperparcel.internal.ParcelableWrapper;",
            "public final class Test$$Wrapper implements ParcelableWrapper<Test> {",
            "  public static final Parcelable.Creator<Test$$Wrapper> CREATOR = ",
            "      new Parcelable.Creator<Test$$Wrapper>() {",
            "    @Override public Test$$Wrapper createFromParcel(Parcel in) {",
            "      TreeMap<Integer, Integer> outChild = null;",
            "      if (in.readInt() == 0) {",
            "        int childSize = in.readInt();",
            "        TreeMap<Integer, Integer> child = new TreeMap<Integer, Integer>();",
            "        for (int childIndex = 0; childIndex < childSize; childIndex++) {",
            "          Integer outChildKey = null;",
            "          if (in.readInt() == 0) {",
            "            outChildKey = in.readInt();",
            "          }",
            "          Integer outChildValue = null;",
            "          if (in.readInt() == 0) {",
            "            outChildValue = in.readInt();",
            "          }",
            "          child.put(outChildKey, outChildValue);",
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
            "    TreeMap<Integer, Integer> child = this.data.getChild();",
            "    if (child == null) {",
            "      dest.writeInt(1);",
            "    } else {",
            "      dest.writeInt(0);",
            "      dest.writeInt(child.size());",
            "      for (Map.Entry<Integer, Integer> childEntry : child.entrySet()) {",
            "        if (childEntry.getKey() == null) {",
            "          dest.writeInt(1);",
            "        } else {",
            "          dest.writeInt(0);",
            "          dest.writeInt(childEntry.getKey());",
            "        }",
            "        if (childEntry.getValue() == null) {",
            "          dest.writeInt(1);",
            "        } else {",
            "          dest.writeInt(0);",
            "          dest.writeInt(childEntry.getValue());",
            "        }",
            "      }",
            "    }",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(dataClass)
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }
}
