package nz.bradcampbell.paperparcel;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import java.util.Arrays;
import javax.tools.JavaFileObject;
import org.junit.Test;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;

public class ErrorTests {

  @Test public void getterHasAParameterTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import nz.bradcampbell.paperparcel.PaperParcel;",
            "@PaperParcel",
            "public final class Test {",
            "  private final int child;",
            "  public Test(int child) {",
            "    this.child = child;",
            "  }",
            "  public int getChild(int x) {",
            "    return this.child;",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .failsToCompile()
        .withErrorContaining("PaperParcel cannot read from the field named \"child\" which was "
            + "found when processing test.Test. The field must either be non-private, or have a "
            + "getter method with no arguments and have one of the following names: [child, "
            + "isChild, hasChild, getChild]. Alternatively you can exclude the field by making it "
            + "static, transient, or using the ExcludeFields annotation on test.Test")
        .in(source)
        .onLine(5);
  }

  @Test public void getterHasWrongReturnTypeTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import nz.bradcampbell.paperparcel.PaperParcel;",
            "@PaperParcel",
            "public final class Test {",
            "  private final int child;",
            "  public Test(int child) {",
            "    this.child = child;",
            "  }",
            "  public long getChild() {",
            "    return this.child;",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .failsToCompile()
        .withErrorContaining("PaperParcel cannot read from the field named \"child\" which was "
            + "found when processing test.Test. The field must either be non-private, or have a "
            + "getter method with no arguments and have one of the following names: [child, "
            + "isChild, hasChild, getChild]. Alternatively you can exclude the field by making it "
            + "static, transient, or using the ExcludeFields annotation on test.Test")
        .in(source)
        .onLine(5);
  }

  @Test public void getterHasWrongNameTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import nz.bradcampbell.paperparcel.PaperParcel;",
            "@PaperParcel",
            "public final class Test {",
            "  private final int child;",
            "  public Test(int child) {",
            "    this.child = child;",
            "  }",
            "  public int getKid() {",
            "    return this.child;",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .failsToCompile()
        .withErrorContaining("PaperParcel cannot read from the field named \"child\" which was "
            + "found when processing test.Test. The field must either be non-private, or have a "
            + "getter method with no arguments and have one of the following names: [child, "
            + "isChild, hasChild, getChild]. Alternatively you can exclude the field by making it "
            + "static, transient, or using the ExcludeFields annotation on test.Test")
        .in(source)
        .onLine(5);
  }

  @Test public void setterHasNoParametersTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import nz.bradcampbell.paperparcel.PaperParcel;",
            "@PaperParcel",
            "public final class Test {",
            "  private final int child;",
            "  public int getChild() {",
            "    return this.child;",
            "  }",
            "  public void setChild() {",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .failsToCompile()
        .withErrorContaining("PaperParcel cannot write to the field named \"child\" which was "
            + "found when processing test.Test. The field must either be have a constructor "
            + "argument named child, be non-private, or have a setter method with one int "
            + "parameter and have one of the following names: [child, setChild]. Alternatively "
            + "you can exclude the field by making it static, transient, or using the "
            + "ExcludeFields annotation on test.Test")
        .in(source)
        .onLine(5);
  }

  @Test public void constructorArgHasWrongNameTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import nz.bradcampbell.paperparcel.PaperParcel;",
            "@PaperParcel",
            "public final class Test {",
            "  private final int child;",
            "  public Test(int kid) {",
            "    this.child = kid;",
            "  }",
            "  public int getChild() {",
            "    return this.child;",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .failsToCompile()
        .withErrorContaining("PaperParcel cannot write to the field named \"child\" which was "
            + "found when processing test.Test. The field must either be have a constructor "
            + "argument named child, be non-private, or have a setter method with one int "
            + "parameter and have one of the following names: [child, setChild]. Alternatively "
            + "you can exclude the field by making it static, transient, or using the "
            + "ExcludeFields annotation on test.Test")
        .in(source)
        .onLine(5);
  }

  @Test public void genericClassAnnotatedTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import nz.bradcampbell.paperparcel.PaperParcel;",
            "@PaperParcel",
            "public final class Test<T> {",
            "  public T child;",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .failsToCompile()
        .withErrorContaining("@PaperParcel cannot be applied to a class with type parameters")
        .in(source)
        .onLine(4);
  }

  @Test public void abstractClassAnnotatedTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import nz.bradcampbell.paperparcel.PaperParcel;",
            "@PaperParcel",
            "public abstract class Test {",
            "  public int child;",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .failsToCompile()
        .withErrorContaining("@PaperParcel cannot be applied to an abstract class")
        .in(source)
        .onLine(4);
  }

  @Test public void interfaceClassAnnotatedTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import nz.bradcampbell.paperparcel.PaperParcel;",
            "@PaperParcel",
            "public interface Test {",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .failsToCompile()
        .withErrorContaining("@PaperParcel cannot be applied to an interface")
        .in(source)
        .onLine(4);
  }

  @Test public void defaultAdapterIsAnInterfaceTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import nz.bradcampbell.paperparcel.DefaultAdapter;",
            "import nz.bradcampbell.paperparcel.TypeAdapter;",
            "@DefaultAdapter",
            "public interface Test extends TypeAdapter<Integer> {",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .failsToCompile()
        .withErrorContaining("@DefaultAdapter cannot be applied to an interface")
        .in(source)
        .onLine(5);
  }

  @Test public void defaultAdapterIsAbstractTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import nz.bradcampbell.paperparcel.DefaultAdapter;",
            "import nz.bradcampbell.paperparcel.TypeAdapter;",
            "@DefaultAdapter",
            "public abstract class Test implements TypeAdapter<Integer> {",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .failsToCompile()
        .withErrorContaining("@DefaultAdapter cannot be applied to an abstract class")
        .in(source)
        .onLine(5);
  }

  @Test public void unsupportedTypeTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import nz.bradcampbell.paperparcel.PaperParcel;",
            "import java.util.List;",
            "@PaperParcel",
            "public final class Test {",
            "  public List<MyCustomType> child;",
            "}"
        ));

    JavaFileObject unknownType =
        JavaFileObjects.forSourceString("test.MyCustomType", Joiner.on('\n').join(
            "package test;",
            "public final class MyCustomType {",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(source, unknownType))
        .processedWith(new PaperParcelProcessor())
        .failsToCompile()
        .withErrorContaining("PaperParcel does not know how to process test.Test because the "
            + "child field is a java.util.List<test.MyCustomType> and test.MyCustomType is not a "
            + "supported PaperParcel type. Define a TypeAdapter<test.MyCustomType> to add support "
            + "for test.MyCustomType objects. Alternatively you can exclude the field by making it "
            + "static, transient, or using the ExcludeFields annotation on test.Test");
  }

  @Test public void noVisibleConstructorTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import nz.bradcampbell.paperparcel.PaperParcel;",
            "import java.util.Date;",
            "import java.util.List;",
            "@PaperParcel",
            "public final class Test {",
            "  private Test() {",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .failsToCompile()
        .withErrorContaining("PaperParcel requires at least one non-private constructor, but "
            + "could not find one in test.Test")
        .in(source)
        .onLine(6);
  }

  @Test public void unsatisfiableConstructorTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import nz.bradcampbell.paperparcel.PaperParcel;",
            "import java.util.Date;",
            "import java.util.List;",
            "@PaperParcel",
            "public final class Test {",
            "  private final String s1;",
            "  private final String s2;",
            "  public Test(String s1, String missing, String s2) {",
            "    this.s1 = s1;",
            "    this.s2 = s2;",
            "  }",
            "  public String s1() {",
            "    return s1;",
            "  }",
            "  public String s2() {",
            "    return s2;",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .failsToCompile()
        .withErrorContaining("PaperParcel cannot satisfy constructor "
            + "Test(java.lang.String,java.lang.String,java.lang.String). PaperParcel was able to "
            + "find 2 arguments, but needed 3. The missing arguments were [missing]")
        .in(source)
        .onLine(9);
  }

  @Test public void baseClassHasFieldWithTheSameNameAsSuperclassTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import nz.bradcampbell.paperparcel.PaperParcel;",
            "import java.util.Date;",
            "import java.util.List;",
            "@PaperParcel",
            "public final class Test extends Base {",
            "  private final String test;",
            "  public Test(String test) {",
            "    super(test);",
            "    this.test = test;",
            "  }",
            "  public String test() {",
            "    return test;",
            "  }",
            "}"
        ));

    JavaFileObject base =
        JavaFileObjects.forSourceString("test.Base", Joiner.on('\n').join(
            "package test;",
            "import nz.bradcampbell.paperparcel.PaperParcel;",
            "import java.util.Date;",
            "import java.util.List;",
            "public class Base {",
            "  private final String test;",
            "  public Base(String test) {",
            "    this.test = test;",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(source, base))
        .processedWith(new PaperParcelProcessor())
        .failsToCompile()
        .withErrorContaining("PaperParcel cannot process test.Test because it has two non-ignored "
            + "fields named \"test\". The first can be found in test.Test and the second can be "
            + "found in test.Base")
        .in(source)
        .onLine(7);
  }
}
