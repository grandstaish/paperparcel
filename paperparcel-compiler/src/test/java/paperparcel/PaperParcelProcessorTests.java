package paperparcel;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;
import java.util.Arrays;
import javax.tools.JavaFileObject;
import org.junit.Test;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;

public class PaperParcelProcessorTests {

  @Test public void allBuiltInAdaptersTest() {
    JavaFileObject testParcelable =
        JavaFileObjects.forSourceString("test.TestParcelable", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "public class TestParcelable implements Parcelable {",
            "  @Override",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  @Override",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject testEnum =
        JavaFileObjects.forSourceString("test.TestEnum", Joiner.on('\n').join(
            "package test;",
            "public enum TestEnum {",
            "  A,",
            "  B,",
            "}"
        ));

    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Bundle;",
            "import android.os.IBinder;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import android.os.PersistableBundle;",
            "import android.util.SparseArray;",
            "import android.util.SparseBooleanArray;",
            "import android.util.Size;",
            "import android.util.SizeF;",
            "import java.util.Collection;",
            "import java.util.List;",
            "import java.util.Map;",
            "import java.util.Set;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel",
            "public final class Test implements Parcelable {",
            "  public boolean aa;",
            "  public Boolean ab;",
            "  public byte ac;",
            "  public Byte ad;",
            "  public Bundle ae;",
            "  public CharSequence af;",
            "  public List<Integer> ag;",
            "  public char ah;",
            "  public Character ai;",
            "  public double aj;",
            "  public Double ak;",
            "  public float al;",
            "  public Float am;",
            "  public int an;",
            "  public Integer ao;",
            "  public long ap;",
            "  public Long aq;",
            "  public Map<Integer, Integer> ar;",
            "  public TestParcelable as;",
            "  public PersistableBundle at;",
            "  public Set<Integer> au;",
            "  public short av;",
            "  public Short aw;",
            "  public SizeF ax;",
            "  public Size ay;",
            "  public SparseArray<Integer> az;",
            "  public String ba;",
            "  public boolean[] bb;",
            "  public byte[] bc;",
            "  public char[] bd;",
            "  public double[] be;",
            "  public float[] bf;",
            "  public int[] bg;",
            "  public long[] bh;",
            "  public short[] bi;",
            "  public String[] bj;",
            "  public SparseBooleanArray bk;",
            "  public Collection<Integer> bl;",
            "  public IBinder bm;",
            "  public TestEnum bn;",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject expected =
        JavaFileObjects.forSourceString("test/PaperParcelTest", Joiner.on('\n').join(
            "package test;",
            "import android.os.Bundle;",
            "import android.os.IBinder;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import android.os.PersistableBundle;",
            "import android.support.annotation.NonNull;",
            "import android.util.Size;",
            "import android.util.SizeF;",
            "import android.util.SparseArray;",
            "import android.util.SparseBooleanArray;",
            "import java.util.Collection;",
            "import java.util.List;",
            "import java.util.Map;",
            "import java.util.Set;",
            "import paperparcel.TypeAdapter;",
            "import paperparcel.internal.ArrayAdapter;",
            "import paperparcel.internal.CollectionAdapter;",
            "import paperparcel.internal.EnumAdapter;",
            "import paperparcel.internal.ListAdapter;",
            "import paperparcel.internal.MapAdapter;",
            "import paperparcel.internal.ParcelableAdapter;",
            "import paperparcel.internal.SetAdapter;",
            "import paperparcel.internal.SparseArrayAdapter;",
            "import paperparcel.internal.StaticAdapters;",
            "import paperparcel.internal.Utils;",
            "final class PaperParcelTest {",
            "  static final TypeAdapter<List<Integer>> INTEGER_LIST_ADAPTER = ",
            "      new ListAdapter<Integer>(Utils.nullSafeClone(StaticAdapters.INTEGER_ADAPTER));",
            "  static final TypeAdapter<Map<Integer, Integer>> INTEGER_INTEGER_MAP_ADAPTER = ",
            "      new MapAdapter<Integer, Integer>(",
            "          Utils.nullSafeClone(StaticAdapters.INTEGER_ADAPTER), ",
            "          Utils.nullSafeClone(StaticAdapters.INTEGER_ADAPTER));",
            "  static final TypeAdapter<TestParcelable> TEST_PARCELABLE_PARCELABLE_ADAPTER = ",
            "      new ParcelableAdapter<TestParcelable>();",
            "  static final TypeAdapter<Set<Integer>> INTEGER_SET_ADAPTER = ",
            "      new SetAdapter<Integer>(Utils.nullSafeClone(StaticAdapters.INTEGER_ADAPTER));",
            "  static final TypeAdapter<SparseArray<Integer>> INTEGER_SPARSE_ARRAY_ADAPTER = ",
            "      new SparseArrayAdapter<Integer>(Utils.nullSafeClone(StaticAdapters.INTEGER_ADAPTER));",
            "  static final TypeAdapter<String[]> STRING_ARRAY_ADAPTER = ",
            "      new ArrayAdapter<String>(String.class, StaticAdapters.STRING_ADAPTER);",
            "  static final TypeAdapter<Collection<Integer>> INTEGER_COLLECTION_ADAPTER = ",
            "      new CollectionAdapter<Integer>(Utils.nullSafeClone(StaticAdapters.INTEGER_ADAPTER));",
            "  static final TypeAdapter<TestEnum> TEST_ENUM_ENUM_ADAPTER = ",
            "      new EnumAdapter<TestEnum>(TestEnum.class);",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override public Test createFromParcel(Parcel in) {",
            "      boolean aa = in.readInt() == 1;",
            "      Boolean ab = Utils.readNullable(in, StaticAdapters.BOOLEAN_ADAPTER);",
            "      byte ac = in.readByte();",
            "      Byte ad = Utils.readNullable(in, StaticAdapters.BYTE_ADAPTER);",
            "      Bundle ae = StaticAdapters.BUNDLE_ADAPTER.readFromParcel(in);",
            "      CharSequence af = StaticAdapters.CHAR_SEQUENCE_ADAPTER.readFromParcel(in);",
            "      List<Integer> ag = Utils.readNullable(in, PaperParcelTest.INTEGER_LIST_ADAPTER);",
            "      char ah = (char) in.readInt();",
            "      Character ai = Utils.readNullable(in, StaticAdapters.CHARACTER_ADAPTER);",
            "      double aj = in.readDouble();",
            "      Double ak = Utils.readNullable(in, StaticAdapters.DOUBLE_ADAPTER);",
            "      float al = in.readFloat();",
            "      Float am = Utils.readNullable(in, StaticAdapters.FLOAT_ADAPTER);",
            "      int an = in.readInt();",
            "      Integer ao = Utils.readNullable(in, StaticAdapters.INTEGER_ADAPTER);",
            "      long ap = in.readLong();",
            "      Long aq = Utils.readNullable(in, StaticAdapters.LONG_ADAPTER);",
            "      Map<Integer, Integer> ar = Utils.readNullable(in, PaperParcelTest.INTEGER_INTEGER_MAP_ADAPTER);",
            "      TestParcelable as = PaperParcelTest.TEST_PARCELABLE_PARCELABLE_ADAPTER.readFromParcel(in);",
            "      PersistableBundle at = StaticAdapters.PERSISTABLE_BUNDLE_ADAPTER.readFromParcel(in);",
            "      Set<Integer> au = Utils.readNullable(in, PaperParcelTest.INTEGER_SET_ADAPTER);",
            "      short av = (short) in.readInt();",
            "      Short aw = Utils.readNullable(in, StaticAdapters.SHORT_ADAPTER);",
            "      SizeF ax = Utils.readNullable(in, StaticAdapters.SIZE_F_ADAPTER);",
            "      Size ay = Utils.readNullable(in, StaticAdapters.SIZE_ADAPTER);",
            "      SparseArray<Integer> az = Utils.readNullable(in, PaperParcelTest.INTEGER_SPARSE_ARRAY_ADAPTER);",
            "      String ba = StaticAdapters.STRING_ADAPTER.readFromParcel(in);",
            "      boolean[] bb = StaticAdapters.BOOLEAN_ARRAY_ADAPTER.readFromParcel(in);",
            "      byte[] bc = StaticAdapters.BYTE_ARRAY_ADAPTER.readFromParcel(in);",
            "      char[] bd = StaticAdapters.CHAR_ARRAY_ADAPTER.readFromParcel(in);",
            "      double[] be = StaticAdapters.DOUBLE_ARRAY_ADAPTER.readFromParcel(in);",
            "      float[] bf = StaticAdapters.FLOAT_ARRAY_ADAPTER.readFromParcel(in);",
            "      int[] bg = StaticAdapters.INT_ARRAY_ADAPTER.readFromParcel(in);",
            "      long[] bh = StaticAdapters.LONG_ARRAY_ADAPTER.readFromParcel(in);",
            "      short[] bi = Utils.readNullable(in, StaticAdapters.SHORT_ARRAY_ADAPTER);",
            "      String[] bj = Utils.readNullable(in, PaperParcelTest.STRING_ARRAY_ADAPTER);",
            "      SparseBooleanArray bk = StaticAdapters.SPARSE_BOOLEAN_ARRAY_ADAPTER.readFromParcel(in);",
            "      Collection<Integer> bl = Utils.readNullable(in, PaperParcelTest.INTEGER_COLLECTION_ADAPTER);",
            "      IBinder bm = StaticAdapters.IBINDER_ADAPTER.readFromParcel(in);",
            "      TestEnum bn = Utils.readNullable(in, PaperParcelTest.TEST_ENUM_ENUM_ADAPTER);",
            "      Test data = new Test();",
            "      data.aa = aa;",
            "      data.ab = ab;",
            "      data.ac = ac;",
            "      data.ad = ad;",
            "      data.ae = ae;",
            "      data.af = af;",
            "      data.ag = ag;",
            "      data.ah = ah;",
            "      data.ai = ai;",
            "      data.aj = aj;",
            "      data.ak = ak;",
            "      data.al = al;",
            "      data.am = am;",
            "      data.an = an;",
            "      data.ao = ao;",
            "      data.ap = ap;",
            "      data.aq = aq;",
            "      data.ar = ar;",
            "      data.as = as;",
            "      data.at = at;",
            "      data.au = au;",
            "      data.av = av;",
            "      data.aw = aw;",
            "      data.ax = ax;",
            "      data.ay = ay;",
            "      data.az = az;",
            "      data.ba = ba;",
            "      data.bb = bb;",
            "      data.bc = bc;",
            "      data.bd = bd;",
            "      data.be = be;",
            "      data.bf = bf;",
            "      data.bg = bg;",
            "      data.bh = bh;",
            "      data.bi = bi;",
            "      data.bj = bj;",
            "      data.bk = bk;",
            "      data.bl = bl;",
            "      data.bm = bm;",
            "      data.bn = bn;",
            "      return data;",
            "    }",
            "    @Override public Test[] newArray(int size) {",
            "      return new Test[size];",
            "    }",
            "  };",
            "  private PaperParcelTest() {",
            "  }",
            "  static void writeToParcel(@NonNull Test data, @NonNull Parcel dest, int flags) {",
            "    dest.writeInt(data.aa ? 1 : 0);",
            "    Utils.writeNullable(data.ab, dest, flags, StaticAdapters.BOOLEAN_ADAPTER);",
            "    dest.writeByte(data.ac);",
            "    Utils.writeNullable(data.ad, dest, flags, StaticAdapters.BYTE_ADAPTER);",
            "    StaticAdapters.BUNDLE_ADAPTER.writeToParcel(data.ae, dest, flags);",
            "    StaticAdapters.CHAR_SEQUENCE_ADAPTER.writeToParcel(data.af, dest, flags);",
            "    Utils.writeNullable(data.ag, dest, flags, PaperParcelTest.INTEGER_LIST_ADAPTER);",
            "    dest.writeInt(data.ah);",
            "    Utils.writeNullable(data.ai, dest, flags, StaticAdapters.CHARACTER_ADAPTER);",
            "    dest.writeDouble(data.aj);",
            "    Utils.writeNullable(data.ak, dest, flags, StaticAdapters.DOUBLE_ADAPTER);",
            "    dest.writeFloat(data.al);",
            "    Utils.writeNullable(data.am, dest, flags, StaticAdapters.FLOAT_ADAPTER);",
            "    dest.writeInt(data.an);",
            "    Utils.writeNullable(data.ao, dest, flags, StaticAdapters.INTEGER_ADAPTER);",
            "    dest.writeLong(data.ap);",
            "    Utils.writeNullable(data.aq, dest, flags, StaticAdapters.LONG_ADAPTER);",
            "    Utils.writeNullable(data.ar, dest, flags, PaperParcelTest.INTEGER_INTEGER_MAP_ADAPTER);",
            "    PaperParcelTest.TEST_PARCELABLE_PARCELABLE_ADAPTER.writeToParcel(data.as, dest, flags);",
            "    StaticAdapters.PERSISTABLE_BUNDLE_ADAPTER.writeToParcel(data.at, dest, flags);",
            "    Utils.writeNullable(data.au, dest, flags, PaperParcelTest.INTEGER_SET_ADAPTER);",
            "    dest.writeInt(data.av);",
            "    Utils.writeNullable(data.aw, dest, flags, StaticAdapters.SHORT_ADAPTER);",
            "    Utils.writeNullable(data.ax, dest, flags, StaticAdapters.SIZE_F_ADAPTER);",
            "    Utils.writeNullable(data.ay, dest, flags, StaticAdapters.SIZE_ADAPTER);",
            "    Utils.writeNullable(data.az, dest, flags, PaperParcelTest.INTEGER_SPARSE_ARRAY_ADAPTER);",
            "    StaticAdapters.STRING_ADAPTER.writeToParcel(data.ba, dest, flags);",
            "    StaticAdapters.BOOLEAN_ARRAY_ADAPTER.writeToParcel(data.bb, dest, flags);",
            "    StaticAdapters.BYTE_ARRAY_ADAPTER.writeToParcel(data.bc, dest, flags);",
            "    StaticAdapters.CHAR_ARRAY_ADAPTER.writeToParcel(data.bd, dest, flags);",
            "    StaticAdapters.DOUBLE_ARRAY_ADAPTER.writeToParcel(data.be, dest, flags);",
            "    StaticAdapters.FLOAT_ARRAY_ADAPTER.writeToParcel(data.bf, dest, flags);",
            "    StaticAdapters.INT_ARRAY_ADAPTER.writeToParcel(data.bg, dest, flags);",
            "    StaticAdapters.LONG_ARRAY_ADAPTER.writeToParcel(data.bh, dest, flags);",
            "    Utils.writeNullable(data.bi, dest, flags, StaticAdapters.SHORT_ARRAY_ADAPTER);",
            "    Utils.writeNullable(data.bj, dest, flags, PaperParcelTest.STRING_ARRAY_ADAPTER);",
            "    StaticAdapters.SPARSE_BOOLEAN_ARRAY_ADAPTER.writeToParcel(data.bk, dest, flags);",
            "    Utils.writeNullable(data.bl, dest, flags, PaperParcelTest.INTEGER_COLLECTION_ADAPTER);",
            "    StaticAdapters.IBINDER_ADAPTER.writeToParcel(data.bm, dest, flags);",
            "    Utils.writeNullable(data.bn, dest, flags, PaperParcelTest.TEST_ENUM_ENUM_ADAPTER);",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(source, testParcelable, testEnum))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void failIfPaperParcelClassIsAbstractTest() {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel",
            "public abstract class Test implements Parcelable {",
            "  public int child;",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .failsToCompile()
        .withErrorContaining(ErrorMessages.PAPERPARCEL_ON_ABSTRACT_CLASS)
        .in(source)
        .onLine(5);
  }

  @Test public void failIfPaperParcelClassIsAnInterfaceTest() {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel",
            "public interface Test extends Parcelable {",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .failsToCompile()
        .withErrorContaining(ErrorMessages.PAPERPARCEL_ON_NON_CLASS)
        .in(source)
        .onLine(5);
  }

  @Test public void failIfRegisterAdapterClassIsNotATypeAdapter() {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import paperparcel.RegisterAdapter;",
            "import paperparcel.TypeAdapter;",
            "@RegisterAdapter",
            "public interface Test {",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .failsToCompile()
        .withErrorContaining(ErrorMessages.REGISTERADAPTER_ON_NON_TYPE_ADAPTER)
        .in(source)
        .onLine(5);
  }

  @Test public void failIfRegisterAdapterClassIsAnInterfaceTest() {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import paperparcel.RegisterAdapter;",
            "import paperparcel.TypeAdapter;",
            "@RegisterAdapter",
            "public interface Test extends TypeAdapter<Integer> {",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .failsToCompile()
        .withErrorContaining(ErrorMessages.REGISTERADAPTER_ON_NON_CLASS)
        .in(source)
        .onLine(5);
  }

  @Test public void failIfRegisterAdapterClassIsAbstractTest() {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import paperparcel.RegisterAdapter;",
            "import paperparcel.TypeAdapter;",
            "@RegisterAdapter",
            "public abstract class Test implements TypeAdapter<Integer> {",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .failsToCompile()
        .withErrorContaining(ErrorMessages.REGISTERADAPTER_ON_ABSTRACT_CLASS)
        .in(source)
        .onLine(5);
  }

  @Test public void failIfFieldIsInaccessibleTest() {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel",
            "public final class Test implements Parcelable {",
            "  private final int child;",
            "  public Test(int child) {",
            "    this.child = child;",
            "  }",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .failsToCompile()
        .withErrorContaining(String.format(ErrorMessages.FIELD_NOT_ACCESSIBLE,
            "test.Test", "child", ErrorMessages.SITE_URL + "#model-conventions"))
        .in(source)
        .onLine(7);
  }

  @Test public void failIfFieldIsNotWritableTest() {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel",
            "public final class Test implements Parcelable {",
            "  private final int child;",
            "  public int getChild() {",
            "    return this.child;",
            "  }",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .failsToCompile()
        .withErrorContaining(String.format(ErrorMessages.FIELD_NOT_WRITABLE,
            "test.Test", "child", "Test()", ErrorMessages.SITE_URL + "#model-conventions"))
        .in(source)
        .onLine(7);
  }

  @Test public void failIfConstructorArgumentHasNonMatchingNameTest() {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel",
            "public final class Test implements Parcelable {",
            "  private final int child;",
            "  public Test(int kid) {",
            "    this.child = kid;",
            "  }",
            "  public int getChild() {",
            "    return this.child;",
            "  }",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .failsToCompile()
        .withErrorContaining(String.format(ErrorMessages.UNMATCHED_CONSTRUCTOR_PARAMETER,
            "kid", "test.Test"))
        .in(source)
        .onLine(8);
  }

  @Test public void failIfConstructorArgumentHasMismatchedTypeTest() {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel",
            "public final class Test implements Parcelable {",
            "  private final int child;",
            "  public Test(long child) {",
            "    this.child = child;",
            "  }",
            "  public int getChild() {",
            "    return this.child;",
            "  }",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .failsToCompile()
        .withErrorContaining(String.format(ErrorMessages.UNMATCHED_CONSTRUCTOR_PARAMETER,
            "child", "test.Test"))
        .in(source)
        .onLine(8);
  }

  @Test public void singletonTest() {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel",
            "public final class Test implements Parcelable {",
            "  public static final Test INSTANCE = new Test();",
            "  private Test() {",
            "  }",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject expectedSource =
        JavaFileObjects.forSourceString("test/PaperParcelTest", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import android.support.annotation.NonNull;",
            "final class PaperParcelTest {",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      return Test.INSTANCE;",
            "    }",
            "    @Override",
            "    public Test[] newArray(int size) {",
            "      return new Test[size];",
            "    }",
            "  };",
            "  private PaperParcelTest() {",
            "  }",
            "  static void writeToParcel(@NonNull Test data, @NonNull Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

  @Test public void failIfTypeAdapterIsRaw() {
    JavaFileObject typeAdapter =
        JavaFileObjects.forSourceString("test.RawTypeAdapter", Joiner.on('\n').join(
            "package test;",
            "import paperparcel.RegisterAdapter;",
            "import paperparcel.TypeAdapter;",
            "import android.os.Parcel;",
            "@RegisterAdapter",
            "public class RawTypeAdapter<T> implements TypeAdapter {",
            "  public Object readFromParcel(Parcel in) {",
            "    return null;",
            "  }",
            "  public void writeToParcel(Object value, Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(typeAdapter)
        .processedWith(new PaperParcelProcessor())
        .failsToCompile()
        .withErrorContaining(ErrorMessages.REGISTERADAPTER_ON_RAW_TYPE_ADAPTER)
        .in(typeAdapter)
        .onLine(6);
  }

  @Test public void failIfConstructorHasRawTypeParameter() {
    JavaFileObject typeAdapter =
        JavaFileObjects.forSourceString("test.ListTypeAdapter", Joiner.on('\n').join(
            "package test;",
            "import paperparcel.RegisterAdapter;",
            "import paperparcel.TypeAdapter;",
            "import java.util.List;",
            "import android.os.Parcel;",
            "@RegisterAdapter",
            "public class ListTypeAdapter<T> implements TypeAdapter<List<T>> {",
            "  public ListTypeAdapter(TypeAdapter ta) {",
            "  }",
            "  public List<T> readFromParcel(Parcel in) {",
            "    return null;",
            "  }",
            "  public void writeToParcel(List<T> value, Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(typeAdapter)
        .processedWith(new PaperParcelProcessor())
        .failsToCompile()
        .withErrorContaining(ErrorMessages.RAW_TYPE_ADAPTER_IN_CONSTRUCTOR)
        .in(typeAdapter)
        .onLine(8);
  }

  @Test public void failIfThereAreNoVisibleConstructorsTest() {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import paperparcel.PaperParcel;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "@PaperParcel",
            "public final class Test implements Parcelable {",
            "  private Test() {",
            "  }",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .failsToCompile()
        .withErrorContaining("No visible constructor found")
        .in(source)
        .onLine(6);
  }

  @Test public void failIfGenericFieldTypeIsRaw() {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import paperparcel.PaperParcel;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import java.util.List;",
            "@PaperParcel",
            "public final class Test implements Parcelable {",
            "  private final List child;",
            "  public Test(List child) {",
            "    this.child = child;",
            "  }",
            "  public List getChild() {",
            "    return this.child;",
            "  }",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .failsToCompile()
        .withErrorContaining(ErrorMessages.FIELD_MISSING_TYPE_ARGUMENTS)
        .in(source)
        .onLine(8);
  }

  @Test public void failIfGenericFieldTypeIsRaw2() {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import paperparcel.PaperParcel;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import java.util.List;",
            "import java.util.Map;",
            "@PaperParcel",
            "public final class Test implements Parcelable {",
            "  private final List<Map> child;",
            "  public Test(List child) {",
            "    this.child = child;",
            "  }",
            "  public List getChild() {",
            "    return this.child;",
            "  }",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .failsToCompile()
        .withErrorContaining(ErrorMessages.FIELD_MISSING_TYPE_ARGUMENTS)
        .in(source)
        .onLine(9);
  }

  @Test public void basicExcludeTest() {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel",
            "public class Test implements Parcelable {",
            "  private int count;",
            "  private transient long someLong = 100;",
            "  public Test(int count) {",
            "    this.count = count;",
            "  }",
            "  public int count() {",
            "    return count;",
            "  }",
            "  @Override",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  @Override",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject expected =
        JavaFileObjects.forSourceString("test/PaperParcelTest", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import android.support.annotation.NonNull;",
            "final class PaperParcelTest {",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      int count = in.readInt();",
            "      Test data = new Test(count);",
            "      return data;",
            "    }",
            "    @Override",
            "    public Test[] newArray(int size) {",
            "      return new Test[size];",
            "    }",
            "  };",
            "  private PaperParcelTest() {",
            "  }",
            "  static void writeToParcel(@NonNull Test data, @NonNull Parcel dest, int flags) {",
            "    dest.writeInt(data.count());",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void usesSmallerConstructorWhenLargerConstructorCannotBeUsed() {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel",
            "public class Test implements Parcelable {",
            "  private int count;",
            "  private transient long someLong;",
            "  public Test(int count, long someLong) {",
            "    this.count = count;",
            "    this.someLong = someLong;",
            "  }",
            "  public Test(int count) {",
            "    this.count = count;",
            "    this.someLong = someLong;",
            "  }",
            "  public int count() {",
            "    return count;",
            "  }",
            "  @Override",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  @Override",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject expected =
        JavaFileObjects.forSourceString("test/PaperParcelTest", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import android.support.annotation.NonNull;",
            "final class PaperParcelTest {",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      int count = in.readInt();",
            "      Test data = new Test(count);",
            "      return data;",
            "    }",
            "    @Override",
            "    public Test[] newArray(int size) {",
            "      return new Test[size];",
            "    }",
            "  };",
            "  private PaperParcelTest() {",
            "  }",
            "  static void writeToParcel(@NonNull Test data, @NonNull Parcel dest, int flags) {",
            "    dest.writeInt(data.count());",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void prioritisesDirectAccessOverSetterAndGetterMethods() {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel",
            "public class Test implements Parcelable {",
            "  int count;",
            "  public int count() {",
            "    return count;",
            "  }",
            "  @Override",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  public void setCount(int count) {}",
            "  @Override",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject expected =
        JavaFileObjects.forSourceString("test/PaperParcelTest", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import android.support.annotation.NonNull;",
            "final class PaperParcelTest {",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      int count = in.readInt();",
            "      Test data = new Test();",
            "      data.count = count;",
            "      return data;",
            "    }",
            "    @Override",
            "    public Test[] newArray(int size) {",
            "      return new Test[size];",
            "    }",
            "  };",
            "  private PaperParcelTest() {",
            "  }",
            "  static void writeToParcel(@NonNull Test data, @NonNull Parcel dest, int flags) {",
            "    dest.writeInt(data.count);",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void usesSetterAndGetterMethodsForPrivateFields() {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel",
            "public class Test implements Parcelable {",
            "  private int count;",
            "  public int count() {",
            "    return count;",
            "  }",
            "  @Override",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  public void count(int count) {}",
            "  @Override",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject expected =
        JavaFileObjects.forSourceString("test/PaperParcelTest", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import android.support.annotation.NonNull;",
            "final class PaperParcelTest {",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      int count = in.readInt();",
            "      Test data = new Test();",
            "      data.count(count);",
            "      return data;",
            "    }",
            "    @Override",
            "    public Test[] newArray(int size) {",
            "      return new Test[size];",
            "    }",
            "  };",
            "  private PaperParcelTest() {",
            "  }",
            "  static void writeToParcel(@NonNull Test data, @NonNull Parcel dest, int flags) {",
            "    dest.writeInt(data.count());",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void handlesMixedConstructorDirectAccessAndSetterFields() {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel",
            "public class Test implements Parcelable {",
            "  private int count1;",
            "  int count2;",
            "  private int count3;",
            "  public Test(int count3) {}",
            "  public int count1() {",
            "    return count1;",
            "  }",
            "  public int count3() {",
            "    return count1;",
            "  }",
            "  @Override",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  public void count1(int count1) {}",
            "  @Override",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject expected =
        JavaFileObjects.forSourceString("test/PaperParcelTest", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import android.support.annotation.NonNull;",
            "final class PaperParcelTest {",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      int count2 = in.readInt();",
            "      int count1 = in.readInt();",
            "      int count3 = in.readInt();",
            "      Test data = new Test(count3);",
            "      data.count2 = count2;",
            "      data.count1(count1);",
            "      return data;",
            "    }",
            "    @Override",
            "    public Test[] newArray(int size) {",
            "      return new Test[size];",
            "    }",
            "  };",
            "  private PaperParcelTest() {",
            "  }",
            "  static void writeToParcel(@NonNull Test data, @NonNull Parcel dest, int flags) {",
            "    dest.writeInt(data.count2);",
            "    dest.writeInt(data.count1());",
            "    dest.writeInt(data.count3());",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void complexTypeAdapterTest() {
    JavaFileObject reallySpecificAdapter =
        JavaFileObjects.forSourceString("test.ReallySpecificTypeAdapter", Joiner.on('\n').join(
            "package test;",
            "import paperparcel.RegisterAdapter;",
            "import paperparcel.TypeAdapter;",
            "import java.util.HashMap;",
            "import java.util.Map;",
            "import java.util.List;",
            "import android.os.Parcel;",
            "@RegisterAdapter",
            "public class ReallySpecificTypeAdapter<T1, T2> implements TypeAdapter<HashMap<List<T1>[], Map<T1, T2>>> {",
            "  public HashMap<List<T1>[], Map<T1, T2>> readFromParcel(Parcel in) {",
            "    return null;",
            "  }",
            "  public void writeToParcel(HashMap<List<T1>[], Map<T1, T2>> value, Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "import java.util.HashMap;",
            "import java.util.List;",
            "import java.util.Map;",
            "@PaperParcel",
            "public class Test implements Parcelable {",
            "  public HashMap<List<Integer>[], Map<Integer, Boolean>> field1;",
            "  public Map<Integer, Integer> field2;",
            "  @Override",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  @Override",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject expected =
        JavaFileObjects.forSourceString("test/PaperParcelTest", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import android.support.annotation.NonNull;",
            "import java.util.HashMap;",
            "import java.util.List;",
            "import java.util.Map;",
            "import paperparcel.TypeAdapter;",
            "import paperparcel.internal.MapAdapter;",
            "import paperparcel.internal.StaticAdapters;",
            "import paperparcel.internal.Utils;",
            "final class PaperParcelTest {",
            "  static final TypeAdapter<HashMap<List<Integer>[], Map<Integer, Boolean>>> ",
            "      INTEGER_BOOLEAN_REALLY_SPECIFIC_TYPE_ADAPTER = ",
            "          new ReallySpecificTypeAdapter<Integer, Boolean>();",
            "  static final TypeAdapter<Map<Integer, Integer>> INTEGER_INTEGER_MAP_ADAPTER = ",
            "      new MapAdapter<Integer, Integer>(",
            "          Utils.nullSafeClone(StaticAdapters.INTEGER_ADAPTER), ",
            "          Utils.nullSafeClone(StaticAdapters.INTEGER_ADAPTER));",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      HashMap<List<Integer>[], Map<Integer, Boolean>> field1 = ",
            "          Utils.readNullable(in, PaperParcelTest.INTEGER_BOOLEAN_REALLY_SPECIFIC_TYPE_ADAPTER);",
            "      Map<Integer, Integer> field2 = ",
            "          Utils.readNullable(in, PaperParcelTest.INTEGER_INTEGER_MAP_ADAPTER);",
            "      Test data = new Test();",
            "      data.field1 = field1;",
            "      data.field2 = field2;",
            "      return data;",
            "    }",
            "    @Override",
            "    public Test[] newArray(int size) {",
            "      return new Test[size];",
            "    }",
            "  };",
            "  private PaperParcelTest() {",
            "  }",
            "  static void writeToParcel(@NonNull Test data, @NonNull Parcel dest, int flags) {",
            "    Utils.writeNullable(data.field1, dest, flags, PaperParcelTest.INTEGER_BOOLEAN_REALLY_SPECIFIC_TYPE_ADAPTER);",
            "    Utils.writeNullable(data.field2, dest, flags, PaperParcelTest.INTEGER_INTEGER_MAP_ADAPTER);",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(reallySpecificAdapter, source))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void failWhenTypeAdapterIsNotAvailableTest() {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "import java.util.Date;",
            "@PaperParcel",
            "public class Test implements Parcelable {",
            "  Date date;",
            "  @Override",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  @Override",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .failsToCompile()
        .withErrorContaining(String.format(ErrorMessages.FIELD_MISSING_TYPE_ADAPTER,
            "java.util.Date", ErrorMessages.SITE_URL + "#typeadapters"))
        .in(source)
        .onLine(8);
  }

  @Test public void failWhenWildcardTypeIsPresentInAdapterTest() {
    JavaFileObject wildcardAdapter =
        JavaFileObjects.forSourceString("test.WildcardAdapter", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import java.util.List;",
            "import paperparcel.TypeAdapter;",
            "import paperparcel.RegisterAdapter;",
            "@RegisterAdapter",
            "public class WildcardAdapter implements TypeAdapter<List<? extends Integer>> {",
            "  @Override public List<? extends Integer> readFromParcel(Parcel source) { return null; }",
            "  @Override public void writeToParcel(List<? extends Integer> value, Parcel dest, int flags) {}",
            "}"
        ));

    assertAbout(javaSource()).that(wildcardAdapter)
        .processedWith(new PaperParcelProcessor())
        .failsToCompile()
        .withErrorContaining(String.format(ErrorMessages.WILDCARD_IN_ADAPTED_TYPE,
            "WildcardAdapter", "java.util.List<? extends java.lang.Integer>"))
        .in(wildcardAdapter)
        .onLine(8);
  }

  @Test public void failWhenFieldIsWildcardTypeTest() {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "import java.util.Date;",
            "import java.util.List;",
            "@PaperParcel",
            "public class Test implements Parcelable {",
            "  List<? extends Date> dates;",
            "  @Override",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  @Override",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .failsToCompile()
        .withErrorContaining(ErrorMessages.WILDCARD_IN_FIELD_TYPE)
        .in(source)
        .onLine(9);
  }

  @Test public void basicGenericPaperParcelTest() {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel",
            "public final class Test<T extends Parcelable> implements Parcelable {",
            "  public T value;",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject expected =
        JavaFileObjects.forSourceString("test/PaperParcelTest", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import android.support.annotation.NonNull;",
            "import paperparcel.TypeAdapter;",
            "import paperparcel.internal.ParcelableAdapter;",
            "final class PaperParcelTest {",
            "  static final TypeAdapter<Parcelable> PARCELABLE_PARCELABLE_ADAPTER = ",
            "      new ParcelableAdapter<Parcelable>();",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      Parcelable value = PaperParcelTest.PARCELABLE_PARCELABLE_ADAPTER.readFromParcel(in);",
            "      Test data = new Test();",
            "      data.value = value;",
            "      return data;",
            "    }",
            "    @Override",
            "    public Test[] newArray(int size) {",
            "      return new Test[size];",
            "    }",
            "  };",
            "  private PaperParcelTest() {",
            "  }",
            "  static void writeToParcel(@NonNull Test data, @NonNull Parcel dest, int flags) {",
            "    PaperParcelTest.PARCELABLE_PARCELABLE_ADAPTER.writeToParcel(data.value, dest, flags);",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void complexGenericPaperParcelTest() {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "import java.util.Map;",
            "@PaperParcel",
            "public final class Test<K extends Parcelable, V extends String, T extends Map<K, V>> implements Parcelable {",
            "  public T value;",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject expected =
        JavaFileObjects.forSourceString("test/PaperParcelTest", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import android.support.annotation.NonNull;",
            "import java.util.Map;",
            "import paperparcel.TypeAdapter;",
            "import paperparcel.internal.MapAdapter;",
            "import paperparcel.internal.ParcelableAdapter;",
            "import paperparcel.internal.StaticAdapters;",
            "import paperparcel.internal.Utils;",
            "final class PaperParcelTest {",
            "  static final TypeAdapter<Parcelable> PARCELABLE_PARCELABLE_ADAPTER = ",
            "      new ParcelableAdapter<Parcelable>();",
            "  static final TypeAdapter<Map<Parcelable, String>> PARCELABLE_STRING_MAP_ADAPTER = ",
            "      new MapAdapter<Parcelable, String>(",
            "          PaperParcelTest.PARCELABLE_PARCELABLE_ADAPTER, StaticAdapters.STRING_ADAPTER);",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      Map<Parcelable, String> value = ",
            "          Utils.readNullable(in, PaperParcelTest.PARCELABLE_STRING_MAP_ADAPTER);",
            "      Test data = new Test();",
            "      data.value = value;",
            "      return data;",
            "    }",
            "    @Override",
            "    public Test[] newArray(int size) {",
            "      return new Test[size];",
            "    }",
            "  };",
            "  private PaperParcelTest() {",
            "  }",
            "  static void writeToParcel(@NonNull Test data, @NonNull Parcel dest, int flags) {",
            "    Utils.writeNullable(data.value, dest, flags, PaperParcelTest.PARCELABLE_STRING_MAP_ADAPTER);",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void recursiveAdapterTypeTest() {
    JavaFileObject typeAdapter =
        JavaFileObjects.forSourceString("test.MyAdapter", Joiner.on('\n').join(
            "package test;",
            "import paperparcel.RegisterAdapter;",
            "import paperparcel.TypeAdapter;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "@RegisterAdapter",
            "public class MyAdapter<T extends Comparable<T>> implements TypeAdapter<T> {",
            "  public T readFromParcel(Parcel in) {",
            "    return null;",
            "  }",
            "  public void writeToParcel(T value, Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "import java.util.Date;",
            "@PaperParcel",
            "public final class Test implements Parcelable {",
            "  public Date value;",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject expected =
        JavaFileObjects.forSourceString("test/PaperParcelTest", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import android.support.annotation.NonNull;",
            "import java.util.Date;",
            "import paperparcel.TypeAdapter;",
            "import paperparcel.internal.Utils;",
            "final class PaperParcelTest {",
            "  static final TypeAdapter<Date> DATE_MY_ADAPTER = new MyAdapter<Date>();",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      Date value = Utils.readNullable(in, PaperParcelTest.DATE_MY_ADAPTER);",
            "      Test data = new Test();",
            "      data.value = value;",
            "      return data;",
            "    }",
            "    @Override",
            "    public Test[] newArray(int size) {",
            "      return new Test[size];",
            "    }",
            "  };",
            "  private PaperParcelTest() {",
            "  }",
            "  static void writeToParcel(@NonNull Test data, @NonNull Parcel dest, int flags) {",
            "    Utils.writeNullable(data.value, dest, flags, PaperParcelTest.DATE_MY_ADAPTER);",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(source, typeAdapter))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void recursiveFieldTypeTest() {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel",
            "public final class Test<T extends Comparable<T>> implements Parcelable {",
            "  public T value;",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .failsToCompile()
        .withErrorContaining(ErrorMessages.FIELD_TYPE_IS_RECURSIVE)
        .in(source)
        .onLine(7);
  }

  @Test public void intersectionFieldTypeTest() {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "import java.io.Serializable;",
            "@PaperParcel",
            "public final class Test<T extends Number & Serializable> implements Parcelable {",
            "  public T value;",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .failsToCompile()
        .withErrorContaining(ErrorMessages.FIELD_TYPE_IS_INTERSECTION_TYPE)
        .in(source)
        .onLine(8);
  }

  @Test public void complexExcludeModifiersTest() {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "import java.lang.reflect.Modifier;",
            "@PaperParcel.Options(excludeModifiers = { Modifier.STATIC | Modifier.FINAL, Modifier.TRANSIENT })",
            "@PaperParcel",
            "public final class Test implements Parcelable {",
            "  static final long field1 = 0;",
            "  static long field2;",
            "  transient long field3;",
            "  long field4;",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject expected =
        JavaFileObjects.forSourceString("test/PaperParcelTest", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import android.support.annotation.NonNull;",
            "final class PaperParcelTest {",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      long field2 = in.readLong();",
            "      long field4 = in.readLong();",
            "      Test data = new Test();",
            "      data.field2 = field2;",
            "      data.field4 = field4;",
            "      return data;",
            "    }",
            "    @Override",
            "    public Test[] newArray(int size) {",
            "      return new Test[size];",
            "    }",
            "  };",
            "  private PaperParcelTest() {",
            "  }",
            "  static void writeToParcel(@NonNull Test data, @NonNull Parcel dest, int flags) {",
            "    dest.writeLong(data.field2);",
            "    dest.writeLong(data.field4);",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void basicExcludeAnnotationsTest() {
    JavaFileObject excludeAnnotation =
        JavaFileObjects.forSourceString("test.Exclude", Joiner.on('\n').join(
            "package test;",
            "public @interface Exclude {}"
        ));

    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel.Options(excludeAnnotations = Exclude.class)",
            "@PaperParcel",
            "public final class Test implements Parcelable {",
            "  public int value;",
            "  @Exclude public int ignore;",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject expected =
        JavaFileObjects.forSourceString("test/PaperParcelTest", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import android.support.annotation.NonNull;",
            "final class PaperParcelTest {",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      int value = in.readInt();",
            "      Test data = new Test();",
            "      data.value = value;",
            "      return data;",
            "    }",
            "    @Override",
            "    public Test[] newArray(int size) {",
            "      return new Test[size];",
            "    }",
            "  };",
            "  private PaperParcelTest() {",
            "  }",
            "  static void writeToParcel(@NonNull Test data, @NonNull Parcel dest, int flags) {",
            "    dest.writeInt(data.value);",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(source, excludeAnnotation))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void basicExcludeNonExposedFieldsTest() {
    JavaFileObject exposeAnnotation =
        JavaFileObjects.forSourceString("test.Expose", Joiner.on('\n').join(
            "package test;",
            "public @interface Expose {}"
        ));

    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel.Options(",
            "  excludeNonExposedFields = true,",
            "  exposeAnnotations = Expose.class",
            ")",
            "@PaperParcel",
            "public final class Test implements Parcelable {",
            "  @Expose public int value;",
            "  public int ignore;",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject expected =
        JavaFileObjects.forSourceString("test/PaperParcelTest", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import android.support.annotation.NonNull;",
            "final class PaperParcelTest {",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      int value = in.readInt();",
            "      Test data = new Test();",
            "      data.value = value;",
            "      return data;",
            "    }",
            "    @Override",
            "    public Test[] newArray(int size) {",
            "      return new Test[size];",
            "    }",
            "  };",
            "  private PaperParcelTest() {",
            "  }",
            "  static void writeToParcel(@NonNull Test data, @NonNull Parcel dest, int flags) {",
            "    dest.writeInt(data.value);",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(source, exposeAnnotation))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void inheritanceExcludeNonExposedFieldsTest() {
    JavaFileObject exposeAnnotation =
        JavaFileObjects.forSourceString("test.Expose", Joiner.on('\n').join(
            "package test;",
            "public @interface Expose {}"
        ));

    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel.Options(",
            "  excludeNonExposedFields = true,",
            "  exposeAnnotations = Expose.class",
            ")",
            "@PaperParcel",
            "public final class Test extends BaseTest implements Parcelable {",
            "  @Expose public int value;",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject baseClass =
        JavaFileObjects.forSourceString("test.BaseTest", Joiner.on('\n').join(
            "package test;",
            "public class BaseTest {",
            "  public int ignore;",
            "}"
        ));

    JavaFileObject expected =
        JavaFileObjects.forSourceString("test/PaperParcelTest", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import android.support.annotation.NonNull;",
            "final class PaperParcelTest {",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      int value = in.readInt();",
            "      Test data = new Test();",
            "      data.value = value;",
            "      return data;",
            "    }",
            "    @Override",
            "    public Test[] newArray(int size) {",
            "      return new Test[size];",
            "    }",
            "  };",
            "  private PaperParcelTest() {",
            "  }",
            "  static void writeToParcel(@NonNull Test data, @NonNull Parcel dest, int flags) {",
            "    dest.writeInt(data.value);",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(source, exposeAnnotation, baseClass))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void inheritanceExcludeAnnotationsTest() {
    JavaFileObject excludeAnnotation =
        JavaFileObjects.forSourceString("test.Exclude", Joiner.on('\n').join(
            "package test;",
            "public @interface Exclude {}"
        ));

    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel.Options(excludeAnnotations = Exclude.class)",
            "@PaperParcel",
            "public final class Test extends BaseTest implements Parcelable {",
            "  public int value;",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject baseClass =
        JavaFileObjects.forSourceString("test.BaseTest", Joiner.on('\n').join(
            "package test;",
            "public class BaseTest {",
            "  @Exclude public int ignore;",
            "}"
        ));

    JavaFileObject expected =
        JavaFileObjects.forSourceString("test/PaperParcelTest", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import android.support.annotation.NonNull;",
            "final class PaperParcelTest {",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      int value = in.readInt();",
            "      Test data = new Test();",
            "      data.value = value;",
            "      return data;",
            "    }",
            "    @Override",
            "    public Test[] newArray(int size) {",
            "      return new Test[size];",
            "    }",
            "  };",
            "  private PaperParcelTest() {",
            "  }",
            "  static void writeToParcel(@NonNull Test data, @NonNull Parcel dest, int flags) {",
            "    dest.writeInt(data.value);",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(source, baseClass, excludeAnnotation))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void sharedOptionsAnnotationTest() {
    JavaFileObject sharedOptions =
        JavaFileObjects.forSourceString("test.SharedOptions", Joiner.on('\n').join(
            "package test;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel.Options(excludeModifiers = {})",
            "public @interface SharedOptions {}"
        ));

    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "@SharedOptions",
            "@PaperParcel",
            "public final class Test implements Parcelable {",
            "  public transient int value;",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject expected =
        JavaFileObjects.forSourceString("test/PaperParcelTest", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import android.support.annotation.NonNull;",
            "final class PaperParcelTest {",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      int value = in.readInt();",
            "      Test data = new Test();",
            "      data.value = value;",
            "      return data;",
            "    }",
            "    @Override",
            "    public Test[] newArray(int size) {",
            "      return new Test[size];",
            "    }",
            "  };",
            "  private PaperParcelTest() {",
            "  }",
            "  static void writeToParcel(@NonNull Test data, @NonNull Parcel dest, int flags) {",
            "    dest.writeInt(data.value);",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(source, sharedOptions))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void exposeAnnotationsIgnoredWhenExcludeNonExposedFieldsIsFalse() {
    JavaFileObject exposeAnnotation =
        JavaFileObjects.forSourceString("test.Expose", Joiner.on('\n').join(
            "package test;",
            "public @interface Expose {}"
        ));

    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel.Options(",
            "  excludeNonExposedFields = false,",
            "  exposeAnnotations = Expose.class",
            ")",
            "@PaperParcel",
            "public final class Test implements Parcelable {",
            "  @Expose public int value;",
            "  public int ignore;",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject expected =
        JavaFileObjects.forSourceString("test/PaperParcelTest", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import android.support.annotation.NonNull;",
            "final class PaperParcelTest {",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      int value = in.readInt();",
            "      int ignore = in.readInt();",
            "      Test data = new Test();",
            "      data.value = value;",
            "      data.ignore = ignore;",
            "      return data;",
            "    }",
            "    @Override",
            "    public Test[] newArray(int size) {",
            "      return new Test[size];",
            "    }",
            "  };",
            "  private PaperParcelTest() {",
            "  }",
            "  static void writeToParcel(@NonNull Test data, @NonNull Parcel dest, int flags) {",
            "    dest.writeInt(data.value);",
            "    dest.writeInt(data.ignore);",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(source, exposeAnnotation))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void failWhenExcludeNonExposedFieldsIsTrueWithNoExposeAnnotations() {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel.Options(excludeNonExposedFields = true)",
            "@PaperParcel",
            "public final class Test implements Parcelable {",
            "  public int value;",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .failsToCompile()
        .withErrorContaining(ErrorMessages.OPTIONS_NO_EXPOSE_ANNOTATIONS)
        .in(source)
        .onLine(5);
  }

  @Test public void primitiveArrayAsTypeParameterTest() {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "import java.util.List;",
            "@PaperParcel",
            "public class Test implements Parcelable {",
            "  private List<int[]> value;",
            "  public Test(List<int[]> value) {",
            "    this.value = value;",
            "  }",
            "  public List<int[]> value() {",
            "    return value;",
            "  }",
            "  @Override",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  @Override",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject expected =
        JavaFileObjects.forSourceString("test/PaperParcelTest", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import android.support.annotation.NonNull;",
            "import java.util.List;",
            "import paperparcel.TypeAdapter;",
            "import paperparcel.internal.ListAdapter;",
            "import paperparcel.internal.StaticAdapters;",
            "import paperparcel.internal.Utils;",
            "final class PaperParcelTest {",
            "  static final TypeAdapter<List<int[]>> INT_ARRAY_LIST_ADAPTER = ",
            "      new ListAdapter<int[]>(StaticAdapters.INT_ARRAY_ADAPTER);",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      List<int[]> value = Utils.readNullable(in, PaperParcelTest.INT_ARRAY_LIST_ADAPTER);",
            "      Test data = new Test(value);",
            "      return data;",
            "    }",
            "    @Override",
            "    public Test[] newArray(int size) {",
            "      return new Test[size];",
            "    }",
            "  };",
            "  private PaperParcelTest() {",
            "  }",
            "  static void writeToParcel(@NonNull Test data, @NonNull Parcel dest, int flags) {",
            "    Utils.writeNullable(data.value(), dest, flags, PaperParcelTest.INT_ARRAY_LIST_ADAPTER);",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void adapterNameClashTest() {
    JavaFileObject myClass =
        JavaFileObjects.forSourceString("test.MyClass", Joiner.on('\n').join(
            "package test;",
            "public class MyClass {",
            "}"
        ));

    JavaFileObject yetAnotherMyClass =
        JavaFileObjects.forSourceString("test.clash.MyClass", Joiner.on('\n').join(
            "package test.clash;",
            "public class MyClass {",
            "}"
        ));

    JavaFileObject myClassAdapter =
        JavaFileObjects.forSourceString("test.MyClassAdapter", Joiner.on('\n').join(
            "package test;",
            "import paperparcel.RegisterAdapter;",
            "import paperparcel.TypeAdapter;",
            "import android.os.Parcel;",
            "@RegisterAdapter",
            "public class MyClassAdapter implements TypeAdapter<test.MyClass> {",
            "  public test.MyClass readFromParcel(Parcel in) {",
            "    return null;",
            "  }",
            "  public void writeToParcel(test.MyClass value, Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject yetAnotherMyClassAdapter =
        JavaFileObjects.forSourceString("test.clash.MyClassAdapter", Joiner.on('\n').join(
            "package test.clash;",
            "import paperparcel.RegisterAdapter;",
            "import paperparcel.TypeAdapter;",
            "import android.os.Parcel;",
            "@RegisterAdapter",
            "public class MyClassAdapter implements TypeAdapter<test.clash.MyClass> {",
            "  public test.clash.MyClass readFromParcel(Parcel in) {",
            "    return null;",
            "  }",
            "  public void writeToParcel(test.clash.MyClass value, Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel",
            "public class Test implements Parcelable {",
            "  private test.MyClass value1;",
            "  private test.clash.MyClass value2;",
            "  public Test(test.MyClass value1, test.clash.MyClass value2) {",
            "    this.value1 = value1;",
            "    this.value2 = value2;",
            "  }",
            "  public test.MyClass value1() {",
            "    return value1;",
            "  }",
            "  public test.clash.MyClass value2() {",
            "    return value2;",
            "  }",
            "  @Override",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  @Override",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject expected =
        JavaFileObjects.forSourceString("test/PaperParcelTest", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import android.support.annotation.NonNull;",
            "import paperparcel.TypeAdapter;",
            "import paperparcel.internal.Utils;",
            "final class PaperParcelTest {",
            "  static final TypeAdapter<MyClass> MY_CLASS_ADAPTER = new MyClassAdapter();",
            "  static final TypeAdapter<test.clash.MyClass> MY_CLASS_ADAPTER_1 = new test.clash.MyClassAdapter();",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      MyClass value1 = Utils.readNullable(in, PaperParcelTest.MY_CLASS_ADAPTER);",
            "      test.clash.MyClass value2 = Utils.readNullable(in, PaperParcelTest.MY_CLASS_ADAPTER_1);",
            "      Test data = new Test(value1, value2);",
            "      return data;",
            "    }",
            "    @Override",
            "    public Test[] newArray(int size) {",
            "      return new Test[size];",
            "    }",
            "  };",
            "  private PaperParcelTest() {",
            "  }",
            "  static void writeToParcel(@NonNull Test data, @NonNull Parcel dest, int flags) {",
            "    Utils.writeNullable(data.value1(), dest, flags, PaperParcelTest.MY_CLASS_ADAPTER);",
            "    Utils.writeNullable(data.value2(), dest, flags, PaperParcelTest.MY_CLASS_ADAPTER_1);",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(source, myClass, yetAnotherMyClass, myClassAdapter, yetAnotherMyClassAdapter))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void readFromParcelFieldsNameClashTest() {
    JavaFileObject myClass =
        JavaFileObjects.forSourceString("test.MyClass", Joiner.on('\n').join(
            "package test;",
            "public class MyClass {",
            "}"
        ));

    JavaFileObject myClassAdapter =
        JavaFileObjects.forSourceString("test.MyClassAdapter", Joiner.on('\n').join(
            "package test;",
            "import paperparcel.RegisterAdapter;",
            "import paperparcel.TypeAdapter;",
            "import android.os.Parcel;",
            "@RegisterAdapter",
            "public class MyClassAdapter implements TypeAdapter<test.MyClass> {",
            "  public test.MyClass readFromParcel(Parcel in) {",
            "    return null;",
            "  }",
            "  public void writeToParcel(test.MyClass value, Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel",
            "public class Test implements Parcelable {",
            "  int in;",
            "  int data;",
            "  int dest;",
            "  int flags;",
            "  MyClass MY_CLASS_ADAPTER;",
            "  @Override",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  @Override",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject expected =
        JavaFileObjects.forSourceString("test/PaperParcelTest", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import android.support.annotation.NonNull;",
            "import paperparcel.TypeAdapter;",
            "import paperparcel.internal.Utils;",
            "final class PaperParcelTest {",
            "  static final TypeAdapter<MyClass> MY_CLASS_ADAPTER = new MyClassAdapter();",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      int in1 = in.readInt();",
            "      int data = in.readInt();",
            "      int dest = in.readInt();",
            "      int flags = in.readInt();",
            "      MyClass MY_CLASS_ADAPTER = Utils.readNullable(in, PaperParcelTest.MY_CLASS_ADAPTER);",
            "      Test data1 = new Test();",
            "      data1.in = in1;",
            "      data1.data = data;",
            "      data1.dest = dest;",
            "      data1.flags = flags;",
            "      data1.MY_CLASS_ADAPTER = MY_CLASS_ADAPTER;",
            "      return data1;",
            "    }",
            "    @Override",
            "    public Test[] newArray(int size) {",
            "      return new Test[size];",
            "    }",
            "  };",
            "  private PaperParcelTest() {",
            "  }",
            "  static void writeToParcel(@NonNull Test data, @NonNull Parcel dest, int flags) {",
            "    dest.writeInt(data.in);",
            "    dest.writeInt(data.data);",
            "    dest.writeInt(data.dest);",
            "    dest.writeInt(data.flags);",
            "    Utils.writeNullable(data.MY_CLASS_ADAPTER, dest, flags, PaperParcelTest.MY_CLASS_ADAPTER);",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(myClass, myClassAdapter, source))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void basicReflectAnnotationsTest() {
    JavaFileObject excludeAnnotation =
        JavaFileObjects.forSourceString("test.Reflect", Joiner.on('\n').join(
            "package test;",
            "public @interface Reflect {}"
        ));

    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel.Options(reflectAnnotations = Reflect.class)",
            "@PaperParcel",
            "public final class Test implements Parcelable {",
            "  @Reflect private int reflectIt;",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject expected =
        JavaFileObjects.forSourceString("test/PaperParcelTest", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import android.support.annotation.NonNull;",
            "import paperparcel.internal.Utils;",
            "final class PaperParcelTest {",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      int reflectIt = in.readInt();",
            "      Test data = new Test();",
            "      Utils.writeField(reflectIt, Test.class, data, \"reflectIt\");",
            "      return data;",
            "    }",
            "    @Override",
            "    public Test[] newArray(int size) {",
            "      return new Test[size];",
            "    }",
            "  };",
            "  private PaperParcelTest() {",
            "  }",
            "  static void writeToParcel(@NonNull Test data, @NonNull Parcel dest, int flags) {",
            "    dest.writeInt(Utils.readField(int.class, Test.class, data, \"reflectIt\"));",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(source, excludeAnnotation))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void parameterizedTypeReflectAnnotationsTest() {
    JavaFileObject excludeAnnotation =
        JavaFileObjects.forSourceString("test.Reflect", Joiner.on('\n').join(
            "package test;",
            "public @interface Reflect {}"
        ));

    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "import java.util.List;",
            "@PaperParcel.Options(reflectAnnotations = Reflect.class)",
            "@PaperParcel",
            "public final class Test implements Parcelable {",
            "  @Reflect private List<Integer> reflectIt;",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject expected =
        JavaFileObjects.forSourceString("test/PaperParcelTest", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import android.support.annotation.NonNull;",
            "import java.util.List;",
            "import paperparcel.TypeAdapter;",
            "import paperparcel.internal.ListAdapter;",
            "import paperparcel.internal.StaticAdapters;",
            "import paperparcel.internal.Utils;",
            "final class PaperParcelTest {",
            "  static final TypeAdapter<List<Integer>> INTEGER_LIST_ADAPTER = ",
            "      new ListAdapter<Integer>(Utils.nullSafeClone(StaticAdapters.INTEGER_ADAPTER));",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      List<Integer> reflectIt = Utils.readNullable(in, PaperParcelTest.INTEGER_LIST_ADAPTER);",
            "      Test data = new Test();",
            "      Utils.writeField(reflectIt, Test.class, data, \"reflectIt\");",
            "      return data;",
            "    }",
            "    @Override",
            "    public Test[] newArray(int size) {",
            "      return new Test[size];",
            "    }",
            "  };",
            "  private PaperParcelTest() {",
            "  }",
            "  static void writeToParcel(@NonNull Test data, @NonNull Parcel dest, int flags) {",
            "    Utils.writeNullable(",
            "        Utils.readField(List.class, Test.class, data, \"reflectIt\"),",
            "        dest, flags, PaperParcelTest.INTEGER_LIST_ADAPTER);",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(source, excludeAnnotation))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void preferValidAccessorMethodOverReflectionTest() {
    JavaFileObject excludeAnnotation =
        JavaFileObjects.forSourceString("test.Reflect", Joiner.on('\n').join(
            "package test;",
            "public @interface Reflect {}"
        ));

    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "import java.util.List;",
            "@PaperParcel.Options(reflectAnnotations = Reflect.class)",
            "@PaperParcel",
            "public final class Test implements Parcelable {",
            "  @Reflect private List<Integer> reflectIt;",
            "  public List<Integer> reflectIt() {",
            "    return reflectIt;",
            "  }",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject expected =
        JavaFileObjects.forSourceString("test/PaperParcelTest", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import android.support.annotation.NonNull;",
            "import java.util.List;",
            "import paperparcel.TypeAdapter;",
            "import paperparcel.internal.ListAdapter;",
            "import paperparcel.internal.StaticAdapters;",
            "import paperparcel.internal.Utils;",
            "final class PaperParcelTest {",
            "  static final TypeAdapter<List<Integer>> INTEGER_LIST_ADAPTER = ",
            "      new ListAdapter<Integer>(Utils.nullSafeClone(StaticAdapters.INTEGER_ADAPTER));",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      List<Integer> reflectIt = Utils.readNullable(in, PaperParcelTest.INTEGER_LIST_ADAPTER);",
            "      Test data = new Test();",
            "      Utils.writeField(reflectIt, Test.class, data, \"reflectIt\");",
            "      return data;",
            "    }",
            "    @Override",
            "    public Test[] newArray(int size) {",
            "      return new Test[size];",
            "    }",
            "  };",
            "  private PaperParcelTest() {",
            "  }",
            "  static void writeToParcel(@NonNull Test data, @NonNull Parcel dest, int flags) {",
            "    Utils.writeNullable(data.reflectIt(), dest, flags, PaperParcelTest.INTEGER_LIST_ADAPTER);",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(source, excludeAnnotation))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void preferValidConstructorArgOverReflectionTest() {
    JavaFileObject excludeAnnotation =
        JavaFileObjects.forSourceString("test.Reflect", Joiner.on('\n').join(
            "package test;",
            "public @interface Reflect {}"
        ));

    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "import java.util.List;",
            "@PaperParcel.Options(reflectAnnotations = Reflect.class)",
            "@PaperParcel",
            "public final class Test implements Parcelable {",
            "  @Reflect private List<Integer> reflectIt;",
            "  public Test(List<Integer> reflectIt) {",
            "    this.reflectIt = reflectIt;",
            "  }",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject expected =
        JavaFileObjects.forSourceString("test/PaperParcelTest", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import android.support.annotation.NonNull;",
            "import java.util.List;",
            "import paperparcel.TypeAdapter;",
            "import paperparcel.internal.ListAdapter;",
            "import paperparcel.internal.StaticAdapters;",
            "import paperparcel.internal.Utils;",
            "final class PaperParcelTest {",
            "  static final TypeAdapter<List<Integer>> INTEGER_LIST_ADAPTER = ",
            "      new ListAdapter<Integer>(Utils.nullSafeClone(StaticAdapters.INTEGER_ADAPTER));",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      List<Integer> reflectIt = Utils.readNullable(in, PaperParcelTest.INTEGER_LIST_ADAPTER);",
            "      Test data = new Test(reflectIt);",
            "      return data;",
            "    }",
            "    @Override",
            "    public Test[] newArray(int size) {",
            "      return new Test[size];",
            "    }",
            "  };",
            "  private PaperParcelTest() {",
            "  }",
            "  static void writeToParcel(@NonNull Test data, @NonNull Parcel dest, int flags) {",
            "    Utils.writeNullable(",
            "        Utils.readField(List.class, Test.class, data, \"reflectIt\"),",
            "        dest, flags, PaperParcelTest.INTEGER_LIST_ADAPTER);",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(source, excludeAnnotation))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void preferValidSetterMethodOverReflectionTest() {
    JavaFileObject excludeAnnotation =
        JavaFileObjects.forSourceString("test.Reflect", Joiner.on('\n').join(
            "package test;",
            "public @interface Reflect {}"
        ));

    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "import java.util.List;",
            "@PaperParcel.Options(reflectAnnotations = Reflect.class)",
            "@PaperParcel",
            "public final class Test implements Parcelable {",
            "  @Reflect private List<Integer> reflectIt;",
            "  public void reflectIt(List<Integer> reflectIt) {",
            "    this.reflectIt = reflectIt;",
            "  }",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject expected =
        JavaFileObjects.forSourceString("test/PaperParcelTest", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import android.support.annotation.NonNull;",
            "import java.util.List;",
            "import paperparcel.TypeAdapter;",
            "import paperparcel.internal.ListAdapter;",
            "import paperparcel.internal.StaticAdapters;",
            "import paperparcel.internal.Utils;",
            "final class PaperParcelTest {",
            "  static final TypeAdapter<List<Integer>> INTEGER_LIST_ADAPTER = ",
            "      new ListAdapter<Integer>(Utils.nullSafeClone(StaticAdapters.INTEGER_ADAPTER));",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      List<Integer> reflectIt = Utils.readNullable(in, PaperParcelTest.INTEGER_LIST_ADAPTER);",
            "      Test data = new Test();",
            "      data.reflectIt(reflectIt);",
            "      return data;",
            "    }",
            "    @Override",
            "    public Test[] newArray(int size) {",
            "      return new Test[size];",
            "    }",
            "  };",
            "  private PaperParcelTest() {",
            "  }",
            "  static void writeToParcel(@NonNull Test data, @NonNull Parcel dest, int flags) {",
            "    Utils.writeNullable(",
            "        Utils.readField(List.class, Test.class, data, \"reflectIt\"),",
            "        dest, flags, PaperParcelTest.INTEGER_LIST_ADAPTER);",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(source, excludeAnnotation))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void preferDirectAccessOverReflectionTest() {
    JavaFileObject excludeAnnotation =
        JavaFileObjects.forSourceString("test.Reflect", Joiner.on('\n').join(
            "package test;",
            "public @interface Reflect {}"
        ));

    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "import java.util.List;",
            "@PaperParcel.Options(reflectAnnotations = Reflect.class)",
            "@PaperParcel",
            "public final class Test implements Parcelable {",
            "  @Reflect List<Integer> value;",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject expected =
        JavaFileObjects.forSourceString("test/PaperParcelTest", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import android.support.annotation.NonNull;",
            "import java.util.List;",
            "import paperparcel.TypeAdapter;",
            "import paperparcel.internal.ListAdapter;",
            "import paperparcel.internal.StaticAdapters;",
            "import paperparcel.internal.Utils;",
            "final class PaperParcelTest {",
            "  static final TypeAdapter<List<Integer>> INTEGER_LIST_ADAPTER = ",
            "      new ListAdapter<Integer>(Utils.nullSafeClone(StaticAdapters.INTEGER_ADAPTER));",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      List<Integer> value = Utils.readNullable(in, PaperParcelTest.INTEGER_LIST_ADAPTER);",
            "      Test data = new Test();",
            "      data.value = value;",
            "      return data;",
            "    }",
            "    @Override",
            "    public Test[] newArray(int size) {",
            "      return new Test[size];",
            "    }",
            "  };",
            "  private PaperParcelTest() {",
            "  }",
            "  static void writeToParcel(@NonNull Test data, @NonNull Parcel dest, int flags) {",
            "    Utils.writeNullable(data.value, dest, flags, PaperParcelTest.INTEGER_LIST_ADAPTER);",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(source, excludeAnnotation))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void constructorReflectionTest() {
    JavaFileObject excludeAnnotation =
        JavaFileObjects.forSourceString("test.Reflect", Joiner.on('\n').join(
            "package test;",
            "public @interface Reflect {}"
        ));

    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "import java.util.List;",
            "@PaperParcel.Options(reflectAnnotations = Reflect.class)",
            "@PaperParcel",
            "public final class Test implements Parcelable {",
            "  private List<Integer> value;",
            "  @Reflect private Test(List<Integer> value) {",
            "  }",
            "  public List<Integer> value() {",
            "    return this.value;",
            "  }",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject expected =
        JavaFileObjects.forSourceString("test/PaperParcelTest", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import android.support.annotation.NonNull;",
            "import java.util.List;",
            "import paperparcel.TypeAdapter;",
            "import paperparcel.internal.ListAdapter;",
            "import paperparcel.internal.StaticAdapters;",
            "import paperparcel.internal.Utils;",
            "final class PaperParcelTest {",
            "  static final TypeAdapter<List<Integer>> INTEGER_LIST_ADAPTER = ",
            "      new ListAdapter<Integer>(Utils.nullSafeClone(StaticAdapters.INTEGER_ADAPTER));",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      List<Integer> value = Utils.readNullable(in, PaperParcelTest.INTEGER_LIST_ADAPTER);",
            "      Test data = Utils.init(Test.class, new Class[] { List.class }, new Object[] { value });",
            "      return data;",
            "    }",
            "    @Override",
            "    public Test[] newArray(int size) {",
            "      return new Test[size];",
            "    }",
            "  };",
            "  private PaperParcelTest() {",
            "  }",
            "  static void writeToParcel(@NonNull Test data, @NonNull Parcel dest, int flags) {",
            "    Utils.writeNullable(data.value(), dest, flags, PaperParcelTest.INTEGER_LIST_ADAPTER);",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(source, excludeAnnotation))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void preferVisibleConstructorOverReflectionTest() {
    JavaFileObject excludeAnnotation =
        JavaFileObjects.forSourceString("test.Reflect", Joiner.on('\n').join(
            "package test;",
            "public @interface Reflect {}"
        ));

    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "import java.util.List;",
            "@PaperParcel.Options(reflectAnnotations = Reflect.class)",
            "@PaperParcel",
            "public final class Test implements Parcelable {",
            "  private int value1;",
            "  private int value2;",
            "  @Reflect private Test(int value1, int value2) {",
            "  }",
            "  Test(int value1) {",
            "  }",
            "  public int value1() {",
            "    return this.value1;",
            "  }",
            "  public int value2() {",
            "    return this.value2;",
            "  }",
            "  public void value2(int value2) {",
            "  }",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject expected =
        JavaFileObjects.forSourceString("test/PaperParcelTest", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import android.support.annotation.NonNull;",
            "final class PaperParcelTest {",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      int value1 = in.readInt();",
            "      int value2 = in.readInt();",
            "      Test data = new Test(value1);",
            "      data.value2(value2);",
            "      return data;",
            "    }",
            "    @Override",
            "    public Test[] newArray(int size) {",
            "      return new Test[size];",
            "    }",
            "  };",
            "  private PaperParcelTest() {",
            "  }",
            "  static void writeToParcel(@NonNull Test data, @NonNull Parcel dest, int flags) {",
            "    dest.writeInt(data.value1());",
            "    dest.writeInt(data.value2());",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(source, excludeAnnotation))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void nonNullFieldTest() {
    JavaFileObject nonNullAnnotation =
        JavaFileObjects.forSourceString("test.NonNull", Joiner.on('\n').join(
            "package test;",
            "public @interface NonNull {}"
        ));

    JavaFileObject notNullAnnotation =
        JavaFileObjects.forSourceString("test.NotNull", Joiner.on('\n').join(
            "package test;",
            "public @interface NotNull {}"
        ));

    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel",
            "public class Test implements Parcelable {",
            "  @NonNull public Integer value1;",
            "  @NotNull public Integer value2;",
            "  @Override",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  @Override",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject expected =
        JavaFileObjects.forSourceString("test/PaperParcelTest", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import android.support.annotation.NonNull;",
            "import paperparcel.internal.StaticAdapters;",
            "final class PaperParcelTest {",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      Integer value1 = StaticAdapters.INTEGER_ADAPTER.readFromParcel(in);",
            "      Integer value2 = StaticAdapters.INTEGER_ADAPTER.readFromParcel(in);",
            "      Test data = new Test();",
            "      data.value1 = value1;",
            "      data.value2 = value2;",
            "      return data;",
            "    }",
            "    @Override",
            "    public Test[] newArray(int size) {",
            "      return new Test[size];",
            "    }",
            "  };",
            "  private PaperParcelTest() {",
            "  }",
            "  static void writeToParcel(@NonNull Test data, @NonNull Parcel dest, int flags) {",
            "    StaticAdapters.INTEGER_ADAPTER.writeToParcel(data.value1, dest, flags);",
            "    StaticAdapters.INTEGER_ADAPTER.writeToParcel(data.value2, dest, flags);",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(nonNullAnnotation, notNullAnnotation, source))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void nullSafeAdapterTest() {
    JavaFileObject typeAdapter =
        JavaFileObjects.forSourceString("test.MyTypeAdapter", Joiner.on('\n').join(
            "package test;",
            "import paperparcel.RegisterAdapter;",
            "import paperparcel.TypeAdapter;",
            "import android.os.Parcel;",
            "import java.util.Date;",
            "@RegisterAdapter(nullSafe = true)",
            "public class MyTypeAdapter implements TypeAdapter<Date> {",
            "  public Date readFromParcel(Parcel in) {",
            "    return null;",
            "  }",
            "  public void writeToParcel(Date value, Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "import java.util.Date;",
            "@PaperParcel",
            "public class Test implements Parcelable {",
            "  public Date value;",
            "  @Override",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  @Override",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject expected =
        JavaFileObjects.forSourceString("test/PaperParcelTest", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import android.support.annotation.NonNull;",
            "import java.util.Date;",
            "import paperparcel.TypeAdapter;",
            "final class PaperParcelTest {",
            "  static final TypeAdapter<Date> MY_TYPE_ADAPTER = new MyTypeAdapter();",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      Date value = PaperParcelTest.MY_TYPE_ADAPTER.readFromParcel(in);",
            "      Test data = new Test();",
            "      data.value = value;",
            "      return data;",
            "    }",
            "    @Override",
            "    public Test[] newArray(int size) {",
            "      return new Test[size];",
            "    }",
            "  };",
            "  private PaperParcelTest() {",
            "  }",
            "  static void writeToParcel(@NonNull Test data, @NonNull Parcel dest, int flags) {",
            "    PaperParcelTest.MY_TYPE_ADAPTER.writeToParcel(data.value, dest, flags);",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(typeAdapter, source))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void mixedDependenciesTypeAdapterTest() {
    JavaFileObject myTypeAdapter =
        JavaFileObjects.forSourceString("test.MyTypeAdapter", Joiner.on('\n').join(
            "package test;",
            "import paperparcel.RegisterAdapter;",
            "import paperparcel.TypeAdapter;",
            "import java.util.HashMap;",
            "import android.os.Parcel;",
            "@RegisterAdapter",
            "public class MyTypeAdapter<K, V> implements TypeAdapter<HashMap<K, V>> {",
            "  public MyTypeAdapter(",
            "      TypeAdapter<K> keyAdapter, ",
            "      Class<K> keyClass, ",
            "      TypeAdapter<V> valueAdapter, ",
            "      Class<V> valueClass) {",
            "  }",
            "  public HashMap<K, V> readFromParcel(Parcel in) {",
            "    return null;",
            "  }",
            "  public void writeToParcel(HashMap<K, V> value, Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "import java.util.HashMap;",
            "@PaperParcel",
            "public class Test implements Parcelable {",
            "  public HashMap<Integer, Boolean> value;",
            "  @Override",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  @Override",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject expected =
        JavaFileObjects.forSourceString("test/PaperParcelTest", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import android.support.annotation.NonNull;",
            "import java.util.HashMap;",
            "import paperparcel.TypeAdapter;",
            "import paperparcel.internal.StaticAdapters;",
            "import paperparcel.internal.Utils;",
            "final class PaperParcelTest {",
            "  static final TypeAdapter<HashMap<Integer, Boolean>> INTEGER_BOOLEAN_MY_TYPE_ADAPTER = ",
            "      new MyTypeAdapter<Integer, Boolean>(",
            "          Utils.nullSafeClone(StaticAdapters.INTEGER_ADAPTER),",
            "          Integer.class,",
            "          Utils.nullSafeClone(StaticAdapters.BOOLEAN_ADAPTER),",
            "          Boolean.class);",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "       HashMap<Integer, Boolean> value = ",
            "          Utils.readNullable(in, PaperParcelTest.INTEGER_BOOLEAN_MY_TYPE_ADAPTER);",
            "      Test data = new Test();",
            "      data.value = value;",
            "      return data;",
            "    }",
            "    @Override",
            "    public Test[] newArray(int size) {",
            "      return new Test[size];",
            "    }",
            "  };",
            "  private PaperParcelTest() {",
            "  }",
            "  static void writeToParcel(@NonNull Test data, @NonNull Parcel dest, int flags) {",
            "    Utils.writeNullable(data.value, dest, flags, PaperParcelTest.INTEGER_BOOLEAN_MY_TYPE_ADAPTER);",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(myTypeAdapter, source))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void failWhenTypeAdapterClassDependencyIsRawTest() {
    JavaFileObject myTypeAdapter =
        JavaFileObjects.forSourceString("test.MyTypeAdapter", Joiner.on('\n').join(
            "package test;",
            "import paperparcel.RegisterAdapter;",
            "import paperparcel.TypeAdapter;",
            "import android.os.Parcel;",
            "@RegisterAdapter",
            "public class MyTypeAdapter implements TypeAdapter<Integer> {",
            "  public MyTypeAdapter(Class myClass) {",
            "  }",
            "  public Integer readFromParcel(Parcel in) {",
            "    return null;",
            "  }",
            "  public void writeToParcel(Integer value, Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(myTypeAdapter)
        .processedWith(new PaperParcelProcessor())
        .failsToCompile()
        .withErrorContaining(ErrorMessages.RAW_CLASS_TYPE_IN_CONSTRUCTOR)
        .in(myTypeAdapter)
        .onLine(7);
  }

  @Test public void genericTypeAdapterClassDependencyTest() {
    JavaFileObject myTypeAdapter =
        JavaFileObjects.forSourceString("test.MyTypeAdapter", Joiner.on('\n').join(
            "package test;",
            "import paperparcel.RegisterAdapter;",
            "import paperparcel.TypeAdapter;",
            "import android.os.Parcel;",
            "import java.util.ArrayList;",
            "import java.util.List;",
            "@RegisterAdapter",
            "public class MyTypeAdapter<T> implements TypeAdapter<ArrayList<T>> {",
            "  public MyTypeAdapter(Class<T> myClass) {",
            "  }",
            "  public ArrayList<T> readFromParcel(Parcel in) {",
            "    return null;",
            "  }",
            "  public void writeToParcel(ArrayList<T> value, Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "import java.util.ArrayList;",
            "import java.util.List;",
            "@PaperParcel",
            "public class Test implements Parcelable {",
            "  public ArrayList<List<Integer>> value;",
            "  @Override",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  @Override",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject expected =
        JavaFileObjects.forSourceString("test/PaperParcelTest", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import android.support.annotation.NonNull;",
            "import java.util.ArrayList;",
            "import java.util.List;",
            "import paperparcel.TypeAdapter;",
            "import paperparcel.internal.Utils;",
            "final class PaperParcelTest {",
            "  static final TypeAdapter<ArrayList<List<Integer>>> INTEGER_LIST_MY_TYPE_ADAPTER = ",
            "      new MyTypeAdapter<List<Integer>>((Class<List<Integer>>) (Class<?>) List.class);",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      ArrayList<List<Integer>> value = ",
            "          Utils.readNullable(in, PaperParcelTest.INTEGER_LIST_MY_TYPE_ADAPTER);",
            "      Test data = new Test();",
            "      data.value = value;",
            "      return data;",
            "    }",
            "    @Override",
            "    public Test[] newArray(int size) {",
            "      return new Test[size];",
            "    }",
            "  };",
            "  private PaperParcelTest() {",
            "  }",
            "  static void writeToParcel(@NonNull Test data, @NonNull Parcel dest, int flags) {",
            "    Utils.writeNullable(data.value, dest, flags, PaperParcelTest.INTEGER_LIST_MY_TYPE_ADAPTER);",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(myTypeAdapter, source))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void failIfPaperParcelClassExtendsAnotherPaperParcelClass() {
    JavaFileObject subclass =
        JavaFileObjects.forSourceString("test.BaseTest", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel",
            "public class BaseTest implements Parcelable {",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel",
            "public class Test extends BaseTest {",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(subclass, source))
        .processedWith(new PaperParcelProcessor())
        .failsToCompile()
        .withErrorContaining(ErrorMessages.PAPERPARCEL_EXTENDS_PAPERPARCEL)
        .in(source)
        .onLine(6);
  }

  @Test public void failIfPaperParcelDoesNotImplementParcelable() {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel",
            "public class Test {",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .failsToCompile()
        .withErrorContaining(ErrorMessages.PAPERPARCEL_ON_NON_PARCELABLE)
        .in(source)
        .onLine(4);
  }

  @Test public void failIfPaperParcelIsOnAnnotationClass() {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.TestAnnotation", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "import java.lang.annotation.Retention;",
            "@PaperParcel",
            "public class TestAnnotation implements Retention, Parcelable {",
            "  @Override public Class<? extends Retention> annotationType() {",
            "    return Retention.class;",
            "  }",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .failsToCompile()
        .withErrorContaining(ErrorMessages.PAPERPARCEL_ON_ANNOTATION)
        .in(source)
        .onLine(7);
  }

  @Test public void failIfPaperParcelIsOnPrivateNestedClass() {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.TestOuter", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "public class TestOuter {",
            "  @PaperParcel",
            "  private static class TestInner implements Parcelable {",
            "    public int describeContents() {",
            "      return 0;",
            "    }",
            "    public void writeToParcel(Parcel dest, int flags) {",
            "    }",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .failsToCompile()
        .withErrorContaining(ErrorMessages.PAPERPARCEL_ON_PRIVATE_CLASS)
        .in(source)
        .onLine(7);
  }

  @Test public void failIfPaperParcelIsOnNonStaticNestedClass() {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.TestOuter", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "public class TestOuter {",
            "  @PaperParcel",
            "  public class TestInner implements Parcelable {",
            "    public int describeContents() {",
            "      return 0;",
            "    }",
            "    public void writeToParcel(Parcel dest, int flags) {",
            "    }",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .failsToCompile()
        .withErrorContaining(ErrorMessages.PAPERPARCEL_ON_NON_STATIC_INNER_CLASS)
        .in(source)
        .onLine(7);
  }

  @Test public void failIfRegisterAdapterIsOnNonStaticNestedClass() {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.TestOuter", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import paperparcel.RegisterAdapter;",
            "import paperparcel.TypeAdapter;",
            "public class TestOuter {",
            "  @RegisterAdapter",
            "  public class TestInner implements TypeAdapter<Integer> {",
            "    public Integer readFromParcel(Parcel in) {",
            "      return null;",
            "    }",
            "    public void writeToParcel(Integer value, Parcel dest, int flags) {",
            "    }",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .failsToCompile()
        .withErrorContaining(ErrorMessages.REGISTER_ADAPTER_ON_NON_STATIC_INNER_CLASS)
        .in(source)
        .onLine(7);
  }

  @Test public void failIfRegisterAdapterClassIsEnclosedInNonPublicClass() {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.TestOuter", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import paperparcel.RegisterAdapter;",
            "import paperparcel.TypeAdapter;",
            "class TestOuter {",
            "  @RegisterAdapter",
            "  public class TestInner implements TypeAdapter<Integer> {",
            "    public Integer readFromParcel(Parcel in) {",
            "      return null;",
            "    }",
            "    public void writeToParcel(Integer value, Parcel dest, int flags) {",
            "    }",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .failsToCompile()
        .withErrorContaining(ErrorMessages.REGISTER_ADAPTER_NOT_VISIBLE)
        .in(source)
        .onLine(7);
  }

  @Test public void failIfRegisterAdapterIsOnNonPublicClass() {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.TestOuter", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import paperparcel.RegisterAdapter;",
            "import paperparcel.TypeAdapter;",
            "@RegisterAdapter",
            "class TestOuter implements TypeAdapter<Integer> {",
            "  public Integer readFromParcel(Parcel in) {",
            "    return null;",
            "  }",
            "  public void writeToParcel(Integer value, Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .failsToCompile()
        .withErrorContaining(ErrorMessages.REGISTER_ADAPTER_ON_NON_PUBLIC_CLASS)
        .in(source)
        .onLine(6);
  }

  @Test public void overrideDefaultAdapterTest() {
    JavaFileObject typeAdapter =
        JavaFileObjects.forSourceString("test.MyIntegerAdapter", Joiner.on('\n').join(
            "package test;",
            "import paperparcel.RegisterAdapter;",
            "import paperparcel.TypeAdapter;",
            "import android.os.Parcel;",
            "import paperparcel.internal.Utils;",
            "@RegisterAdapter(priority = RegisterAdapter.Priority.HIGH)",
            "public class MyIntegerAdapter implements TypeAdapter<Integer> {",
            "  public Integer readFromParcel(Parcel in) {",
            "    return null;",
            "  }",
            "  public void writeToParcel(Integer value, Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel",
            "public class Test implements Parcelable {",
            "  public Integer value;",
            "  @Override",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  @Override",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject expected =
        JavaFileObjects.forSourceString("test/PaperParcelTest", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import android.support.annotation.NonNull;",
            "import paperparcel.TypeAdapter;",
            "import paperparcel.internal.Utils;",
            "final class PaperParcelTest {",
            "  static final TypeAdapter<Integer> MY_INTEGER_ADAPTER = new MyIntegerAdapter();",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      Integer value = Utils.readNullable(in, PaperParcelTest.MY_INTEGER_ADAPTER);",
            "      Test data = new Test();",
            "      data.value = value;",
            "      return data;",
            "    }",
            "    @Override",
            "    public Test[] newArray(int size) {",
            "      return new Test[size];",
            "    }",
            "  };",
            "  private PaperParcelTest() {",
            "  }",
            "  static void writeToParcel(@NonNull Test data, @NonNull Parcel dest, int flags) {",
            "    Utils.writeNullable(data.value, dest, flags, PaperParcelTest.MY_INTEGER_ADAPTER);",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(typeAdapter, source))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void builtInAdapterIsUsedWhenCustomAdapterHasTheSamePriority() {
    JavaFileObject typeAdapter =
        JavaFileObjects.forSourceString("test.MyIntegerAdapter", Joiner.on('\n').join(
            "package test;",
            "import paperparcel.RegisterAdapter;",
            "import paperparcel.TypeAdapter;",
            "import android.os.Parcel;",
            "import paperparcel.internal.Utils;",
            "@RegisterAdapter",
            "public class MyIntegerAdapter implements TypeAdapter<Integer> {",
            "  public Integer readFromParcel(Parcel in) {",
            "    return null;",
            "  }",
            "  public void writeToParcel(Integer value, Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel",
            "public class Test implements Parcelable {",
            "  public Integer value;",
            "  @Override",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  @Override",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject expected =
        JavaFileObjects.forSourceString("test/PaperParcelTest", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import android.support.annotation.NonNull;",
            "import paperparcel.internal.StaticAdapters;",
            "import paperparcel.internal.Utils;",
            "final class PaperParcelTest {",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      Integer value = Utils.readNullable(in, StaticAdapters.INTEGER_ADAPTER);",
            "      Test data = new Test();",
            "      data.value = value;",
            "      return data;",
            "    }",
            "    @Override",
            "    public Test[] newArray(int size) {",
            "      return new Test[size];",
            "    }",
            "  };",
            "  private PaperParcelTest() {",
            "  }",
            "  static void writeToParcel(@NonNull Test data, @NonNull Parcel dest, int flags) {",
            "    Utils.writeNullable(data.value, dest, flags, StaticAdapters.INTEGER_ADAPTER);",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(typeAdapter, source))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void intersectionTypeTypeAdapterTest() {
    JavaFileObject typeAdapter =
        JavaFileObjects.forSourceString("test.MixedAdapter", Joiner.on('\n').join(
            "package test;",
            "import paperparcel.RegisterAdapter;",
            "import paperparcel.TypeAdapter;",
            "import java.io.Serializable;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "@RegisterAdapter(priority = RegisterAdapter.Priority.HIGH)",
            "public class MixedAdapter<T extends Parcelable & Serializable> ",
            "    implements TypeAdapter<T> {",
            "  public T readFromParcel(Parcel in) {",
            "    return null;",
            "  }",
            "  public void writeToParcel(T value, Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject myClass =
        JavaFileObjects.forSourceString("test.MyClass", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcelable;",
            "import java.io.Serializable;",
            "public abstract class MyClass implements Serializable, Parcelable {",
            "}"
        ));

    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel",
            "public class Test implements Parcelable {",
            "  public MyClass value1;",
            "  public Parcelable value2;",
            "  public String value3;",
            "  @Override",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  @Override",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject expected =
        JavaFileObjects.forSourceString("test/PaperParcelTest", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import android.support.annotation.NonNull;",
            "import paperparcel.TypeAdapter;",
            "import paperparcel.internal.ParcelableAdapter;",
            "import paperparcel.internal.StaticAdapters;",
            "import paperparcel.internal.Utils;",
            "final class PaperParcelTest {",
            "  static final TypeAdapter<MyClass> MY_CLASS_MIXED_ADAPTER = new MixedAdapter<MyClass>();",
            "  static final TypeAdapter<Parcelable> PARCELABLE_PARCELABLE_ADAPTER = ",
            "      new ParcelableAdapter<Parcelable>();",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      MyClass value1 = Utils.readNullable(in, PaperParcelTest.MY_CLASS_MIXED_ADAPTER);",
            "      Parcelable value2 = PaperParcelTest.PARCELABLE_PARCELABLE_ADAPTER.readFromParcel(in);",
            "      String value3 = StaticAdapters.STRING_ADAPTER.readFromParcel(in);",
            "      Test data = new Test();",
            "      data.value1 = value1;",
            "      data.value2 = value2;",
            "      data.value3 = value3;",
            "      return data;",
            "    }",
            "    @Override",
            "    public Test[] newArray(int size) {",
            "      return new Test[size];",
            "    }",
            "  };",
            "  private PaperParcelTest() {",
            "  }",
            "  static void writeToParcel(@NonNull Test data, @NonNull Parcel dest, int flags) {",
            "    Utils.writeNullable(data.value1, dest, flags, PaperParcelTest.MY_CLASS_MIXED_ADAPTER);",
            "    PaperParcelTest.PARCELABLE_PARCELABLE_ADAPTER.writeToParcel(data.value2, dest, flags);",
            "    StaticAdapters.STRING_ADAPTER.writeToParcel(data.value3, dest, flags);",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(typeAdapter, myClass, source))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void failWhenGenericTypeAdapterParameterDoesNotMatch() {
    JavaFileObject typeAdapter =
        JavaFileObjects.forSourceString("test.MixedAdapter", Joiner.on('\n').join(
            "package test;",
            "import paperparcel.RegisterAdapter;",
            "import paperparcel.TypeAdapter;",
            "import android.os.Parcel;",
            "@RegisterAdapter",
            "public class MixedAdapter<T extends Comparable<Integer>> implements TypeAdapter<T> {",
            "  public T readFromParcel(Parcel in) {",
            "    return null;",
            "  }",
            "  public void writeToParcel(T value, Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import java.util.Date;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel",
            "public class Test implements Parcelable {",
            "  public Date value;",
            "  @Override",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  @Override",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(typeAdapter, source))
        .processedWith(new PaperParcelProcessor())
        .failsToCompile()
        .withErrorContaining(String.format(ErrorMessages.FIELD_MISSING_TYPE_ADAPTER,
            "java.util.Date", ErrorMessages.SITE_URL + "#typeadapters"))
        .in(source)
        .onLine(8);
  }

  @Test public void typeParameterContainedInTypeArgumentTest() {
    JavaFileObject typeAdapter =
        JavaFileObjects.forSourceString("test.MixedAdapter", Joiner.on('\n').join(
            "package test;",
            "import paperparcel.RegisterAdapter;",
            "import paperparcel.TypeAdapter;",
            "import java.io.Serializable;",
            "import java.util.Date;",
            "import android.os.Parcel;",
            "@RegisterAdapter",
            "public class MixedAdapter<D extends Serializable & Comparable<Date>, T extends Comparable<D>> ",
            "    implements TypeAdapter<T> {",
            "  public T readFromParcel(Parcel in) {",
            "    return null;",
            "  }",
            "  public void writeToParcel(T value, Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import java.util.Date;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel",
            "public class Test implements Parcelable {",
            "  public Date value;",
            "  @Override",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  @Override",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject expected =
        JavaFileObjects.forSourceString("test/PaperParcelTest", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import android.support.annotation.NonNull;",
            "import java.util.Date;",
            "import paperparcel.TypeAdapter;",
            "import paperparcel.internal.Utils;",
            "final class PaperParcelTest {",
            "  static final TypeAdapter<Date> DATE_DATE_MIXED_ADAPTER = new MixedAdapter<Date, Date>();",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      Date value = Utils.readNullable(in, PaperParcelTest.DATE_DATE_MIXED_ADAPTER);",
            "      Test data = new Test();",
            "      data.value = value;",
            "      return data;",
            "    }",
            "    @Override",
            "    public Test[] newArray(int size) {",
            "      return new Test[size];",
            "    }",
            "  };",
            "  private PaperParcelTest() {",
            "  }",
            "  static void writeToParcel(@NonNull Test data, @NonNull Parcel dest, int flags) {",
            "    Utils.writeNullable(data.value, dest, flags, PaperParcelTest.DATE_DATE_MIXED_ADAPTER);",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(typeAdapter, source))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void typeParameterContainedInIntersectionTypeArgumentTest() {
    JavaFileObject typeAdapter =
        JavaFileObjects.forSourceString("test.MixedAdapter", Joiner.on('\n').join(
            "package test;",
            "import paperparcel.RegisterAdapter;",
            "import paperparcel.TypeAdapter;",
            "import java.io.Serializable;",
            "import java.util.Date;",
            "import android.os.Parcel;",
            "@RegisterAdapter",
            "public class MixedAdapter<D extends Serializable & Comparable<Date>, T extends Comparable<D> & Serializable> ",
            "    implements TypeAdapter<T> {",
            "  public T readFromParcel(Parcel in) {",
            "    return null;",
            "  }",
            "  public void writeToParcel(T value, Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import java.util.Date;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel",
            "public class Test implements Parcelable {",
            "  public Date value;",
            "  @Override",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  @Override",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject expected =
        JavaFileObjects.forSourceString("test/PaperParcelTest", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import android.support.annotation.NonNull;",
            "import java.util.Date;",
            "import paperparcel.TypeAdapter;",
            "import paperparcel.internal.Utils;",
            "final class PaperParcelTest {",
            "  static final TypeAdapter<Date> DATE_DATE_MIXED_ADAPTER = new MixedAdapter<Date, Date>();",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      Date value = Utils.readNullable(in, PaperParcelTest.DATE_DATE_MIXED_ADAPTER);",
            "      Test data = new Test();",
            "      data.value = value;",
            "      return data;",
            "    }",
            "    @Override",
            "    public Test[] newArray(int size) {",
            "      return new Test[size];",
            "    }",
            "  };",
            "  private PaperParcelTest() {",
            "  }",
            "  static void writeToParcel(@NonNull Test data, @NonNull Parcel dest, int flags) {",
            "    Utils.writeNullable(data.value, dest, flags, PaperParcelTest.DATE_DATE_MIXED_ADAPTER);",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(typeAdapter, source))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void typeParameterContainedInDeclaredTypeArgumentTest() {
    JavaFileObject typeAdapter =
        JavaFileObjects.forSourceString("test.MixedAdapter", Joiner.on('\n').join(
            "package test;",
            "import paperparcel.RegisterAdapter;",
            "import paperparcel.TypeAdapter;",
            "import java.io.Serializable;",
            "import java.util.Date;",
            "import java.util.Map;",
            "import android.os.Parcel;",
            "@RegisterAdapter",
            "public class MixedAdapter<D extends Serializable & Comparable<Date>, T extends Map<D, D>> ",
            "    implements TypeAdapter<T> {",
            "  public T readFromParcel(Parcel in) {",
            "    return null;",
            "  }",
            "  public void writeToParcel(T value, Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import java.util.Date;",
            "import java.util.Map;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel",
            "public class Test implements Parcelable {",
            "  public Map<Date, Date> value;",
            "  @Override",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  @Override",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject expected =
        JavaFileObjects.forSourceString("test/PaperParcelTest", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import android.support.annotation.NonNull;",
            "import java.util.Date;",
            "import java.util.Map;",
            "import paperparcel.TypeAdapter;",
            "import paperparcel.internal.Utils;",
            "final class PaperParcelTest {",
            "  static final TypeAdapter<Map<Date, Date>> DATE_DATE_DATE_MAP_MIXED_ADAPTER = ",
            "      new MixedAdapter<Date, Map<Date, Date>>();",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      Map<Date, Date> value = ",
            "          Utils.readNullable(in, PaperParcelTest.DATE_DATE_DATE_MAP_MIXED_ADAPTER);",
            "      Test data = new Test();",
            "      data.value = value;",
            "      return data;",
            "    }",
            "    @Override",
            "    public Test[] newArray(int size) {",
            "      return new Test[size];",
            "    }",
            "  };",
            "  private PaperParcelTest() {",
            "  }",
            "  static void writeToParcel(@NonNull Test data, @NonNull Parcel dest, int flags) {",
            "    Utils.writeNullable(data.value, dest, flags, PaperParcelTest.DATE_DATE_DATE_MAP_MIXED_ADAPTER);",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(typeAdapter, source))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void typeParameterExtendsBoundedTypeTest() {
    JavaFileObject typeAdapter =
        JavaFileObjects.forSourceString("test.MixedAdapter", Joiner.on('\n').join(
            "package test;",
            "import paperparcel.RegisterAdapter;",
            "import paperparcel.TypeAdapter;",
            "import java.io.Serializable;",
            "import java.util.Date;",
            "import android.os.Parcel;",
            "@RegisterAdapter",
            "public class MixedAdapter<D extends Serializable & Comparable<Date>, T extends D> ",
            "    implements TypeAdapter<T> {",
            "  public T readFromParcel(Parcel in) {",
            "    return null;",
            "  }",
            "  public void writeToParcel(T value, Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import java.util.Date;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel",
            "public class Test implements Parcelable {",
            "  public Date value;",
            "  @Override",
            "  public int describeContents() {",
            "    return 0;",
            "  }",
            "  @Override",
            "  public void writeToParcel(Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject expected =
        JavaFileObjects.forSourceString("test/PaperParcelTest", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import android.support.annotation.NonNull;",
            "import java.util.Date;",
            "import paperparcel.TypeAdapter;",
            "import paperparcel.internal.Utils;",
            "final class PaperParcelTest {",
            "  static final TypeAdapter<Date> DATE_DATE_MIXED_ADAPTER = new MixedAdapter<Date, Date>();",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      Date value = Utils.readNullable(in, PaperParcelTest.DATE_DATE_MIXED_ADAPTER);",
            "      Test data = new Test();",
            "      data.value = value;",
            "      return data;",
            "    }",
            "    @Override",
            "    public Test[] newArray(int size) {",
            "      return new Test[size];",
            "    }",
            "  };",
            "  private PaperParcelTest() {",
            "  }",
            "  static void writeToParcel(@NonNull Test data, @NonNull Parcel dest, int flags) {",
            "    Utils.writeNullable(data.value, dest, flags, PaperParcelTest.DATE_DATE_MIXED_ADAPTER);",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(typeAdapter, source))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

}
