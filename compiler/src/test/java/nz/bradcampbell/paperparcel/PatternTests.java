package nz.bradcampbell.paperparcel;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import javax.tools.JavaFileObject;
import org.junit.Test;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class PatternTests {

  @Test public void patternTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import java.util.regex.Pattern;",
            "import nz.bradcampbell.paperparcel.PaperParcel;",
            "@PaperParcel",
            "public final class Test {",
            "  private final Pattern child;",
            "  public Test(Pattern child) {",
            "    this.child = child;",
            "  }",
            "  public Pattern getChild() {",
            "    return this.child;",
            "  }",
            "}"
        ));

    JavaFileObject expectedSource =
        JavaFileObjects.forSourceString("test/Test$$Wrapper", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import java.lang.Override;",
            "import java.lang.RuntimeException;",
            "import java.util.regex.Pattern;",
            "import java.util.regex.PatternSyntaxException;",
            "import nz.bradcampbell.paperparcel.internal.ParcelableWrapper;",
            "public final class Test$$Wrapper implements ParcelableWrapper<Test> {",
            "  public static final Parcelable.Creator<Test$$Wrapper> CREATOR = ",
            "      new Parcelable.Creator<Test$$Wrapper>() {",
            "    @Override public Test$$Wrapper createFromParcel(Parcel in) {",
            "      Pattern outChild = null;",
            "      if (in.readInt() == 0) {",
            "        Pattern child = null;",
            "        try {",
            "          child = Pattern.compile(in.readString(), in.readInt());",
            "        } catch(PatternSyntaxException e) {",
            "          throw new RuntimeException(e);",
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
            "    Pattern child = this.data.getChild();",
            "    if (child == null) {",
            "      dest.writeInt(1);",
            "    } else {",
            "      dest.writeInt(0);",
            "      dest.writeString(child.pattern());",
            "      dest.writeInt(child.flags());",
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
