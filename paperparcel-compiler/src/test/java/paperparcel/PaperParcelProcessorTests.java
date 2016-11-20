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
            "import android.util.ArrayMap;",
            "import android.util.ArraySet;",
            "import android.util.LongSparseArray;",
            "import android.util.SparseArray;",
            "import android.util.SparseBooleanArray;",
            "import android.util.SparseIntArray;",
            "import android.util.SparseLongArray;",
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
            "  public SparseIntArray bn;",
            "  public SparseLongArray bo;",
            "  public ArrayMap<Integer, Boolean> bp;",
            "  public ArraySet<Integer> bq;",
            "  public LongSparseArray<Integer> br;",
            "  public TestEnum bs;",
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
            "import android.util.ArrayMap;",
            "import android.util.ArraySet;",
            "import android.util.LongSparseArray;",
            "import android.util.Size;",
            "import android.util.SizeF;",
            "import android.util.SparseArray;",
            "import android.util.SparseBooleanArray;",
            "import android.util.SparseIntArray;",
            "import android.util.SparseLongArray;",
            "import java.util.Collection;",
            "import java.util.List;",
            "import java.util.Map;",
            "import java.util.Set;",
            "import javax.annotation.Generated;",
            "import paperparcel.internal.ArrayMapAdapter;",
            "import paperparcel.internal.ArraySetAdapter;",
            "import paperparcel.internal.BooleanAdapter;",
            "import paperparcel.internal.BooleanArrayAdapter;",
            "import paperparcel.internal.BundleAdapter;",
            "import paperparcel.internal.ByteAdapter;",
            "import paperparcel.internal.ByteArrayAdapter;",
            "import paperparcel.internal.CharArrayAdapter;",
            "import paperparcel.internal.CharSequenceAdapter;",
            "import paperparcel.internal.CharacterAdapter;",
            "import paperparcel.internal.CollectionAdapter;",
            "import paperparcel.internal.DoubleAdapter;",
            "import paperparcel.internal.DoubleArrayAdapter;",
            "import paperparcel.internal.EnumAdapter;",
            "import paperparcel.internal.FloatAdapter;",
            "import paperparcel.internal.FloatArrayAdapter;",
            "import paperparcel.internal.IBinderAdapter;",
            "import paperparcel.internal.IntArrayAdapter;",
            "import paperparcel.internal.IntegerAdapter;",
            "import paperparcel.internal.ListAdapter;",
            "import paperparcel.internal.LongAdapter;",
            "import paperparcel.internal.LongArrayAdapter;",
            "import paperparcel.internal.LongSparseArrayAdapter;",
            "import paperparcel.internal.MapAdapter;",
            "import paperparcel.internal.ParcelableAdapter;",
            "import paperparcel.internal.PersistableBundleAdapter;",
            "import paperparcel.internal.SetAdapter;",
            "import paperparcel.internal.ShortAdapter;",
            "import paperparcel.internal.ShortArrayAdapter;",
            "import paperparcel.internal.SizeAdapter;",
            "import paperparcel.internal.SizeFAdapter;",
            "import paperparcel.internal.SparseArrayAdapter;",
            "import paperparcel.internal.SparseBooleanArrayAdapter;",
            "import paperparcel.internal.SparseIntArrayAdapter;",
            "import paperparcel.internal.SparseLongArrayAdapter;",
            "import paperparcel.internal.StringAdapter;",
            "import paperparcel.internal.StringArrayAdapter;",
            GeneratedLines.GENERATED_ANNOTATION,
            "final class PaperParcelTest {",
            "  static final ListAdapter<Integer> INTEGER_LIST_ADAPTER = new ListAdapter<Integer>(IntegerAdapter.INSTANCE);",
            "  static final MapAdapter<Integer, Integer> INTEGER_INTEGER_MAP_ADAPTER = new MapAdapter<Integer, Integer>(IntegerAdapter.INSTANCE, IntegerAdapter.INSTANCE);",
            "  static final ParcelableAdapter<TestParcelable> TEST_PARCELABLE_PARCELABLE_ADAPTER = new ParcelableAdapter<TestParcelable>();",
            "  static final SetAdapter<Integer> INTEGER_SET_ADAPTER = new SetAdapter<Integer>(IntegerAdapter.INSTANCE);",
            "  static final SparseArrayAdapter<Integer> INTEGER_SPARSE_ARRAY_ADAPTER = new SparseArrayAdapter<Integer>(IntegerAdapter.INSTANCE);",
            "  static final CollectionAdapter<Integer> INTEGER_COLLECTION_ADAPTER = new CollectionAdapter<Integer>(IntegerAdapter.INSTANCE);",
            "  static final ArrayMapAdapter<Integer, Boolean> INTEGER_BOOLEAN_ARRAY_MAP_ADAPTER = new ArrayMapAdapter<Integer, Boolean>(IntegerAdapter.INSTANCE, BooleanAdapter.INSTANCE);",
            "  static final ArraySetAdapter<Integer> INTEGER_ARRAY_SET_ADAPTER = new ArraySetAdapter<Integer>(IntegerAdapter.INSTANCE);",
            "  static final LongSparseArrayAdapter<Integer> INTEGER_LONG_SPARSE_ARRAY_ADAPTER = new LongSparseArrayAdapter<Integer>(IntegerAdapter.INSTANCE);",
            "  static final EnumAdapter<TestEnum> TEST_ENUM_ENUM_ADAPTER = new EnumAdapter<TestEnum>();",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override public Test createFromParcel(Parcel in) {",
            "      boolean aa = in.readInt() == 1;",
            "      Boolean ab = BooleanAdapter.INSTANCE.readFromParcel(in);",
            "      byte ac = in.readByte();",
            "      Byte ad = ByteAdapter.INSTANCE.readFromParcel(in);",
            "      Bundle ae = BundleAdapter.INSTANCE.readFromParcel(in);",
            "      CharSequence af = CharSequenceAdapter.INSTANCE.readFromParcel(in);",
            "      List<Integer> ag = PaperParcelTest.INTEGER_LIST_ADAPTER.readFromParcel(in);",
            "      char ah = (char) in.readInt();",
            "      Character ai = CharacterAdapter.INSTANCE.readFromParcel(in);",
            "      double aj = in.readDouble();",
            "      Double ak = DoubleAdapter.INSTANCE.readFromParcel(in);",
            "      float al = in.readFloat();",
            "      Float am = FloatAdapter.INSTANCE.readFromParcel(in);",
            "      int an = in.readInt();",
            "      Integer ao = IntegerAdapter.INSTANCE.readFromParcel(in);",
            "      long ap = in.readLong();",
            "      Long aq = LongAdapter.INSTANCE.readFromParcel(in);",
            "      Map<Integer, Integer> ar = PaperParcelTest.INTEGER_INTEGER_MAP_ADAPTER.readFromParcel(in);",
            "      TestParcelable as = PaperParcelTest.TEST_PARCELABLE_PARCELABLE_ADAPTER.readFromParcel(in);",
            "      PersistableBundle at = PersistableBundleAdapter.INSTANCE.readFromParcel(in);",
            "      Set<Integer> au = PaperParcelTest.INTEGER_SET_ADAPTER.readFromParcel(in);",
            "      short av = (short) in.readInt();",
            "      Short aw = ShortAdapter.INSTANCE.readFromParcel(in);",
            "      SizeF ax = SizeFAdapter.INSTANCE.readFromParcel(in);",
            "      Size ay = SizeAdapter.INSTANCE.readFromParcel(in);",
            "      SparseArray<Integer> az = PaperParcelTest.INTEGER_SPARSE_ARRAY_ADAPTER.readFromParcel(in);",
            "      String ba = StringAdapter.INSTANCE.readFromParcel(in);",
            "      boolean[] bb = BooleanArrayAdapter.INSTANCE.readFromParcel(in);",
            "      byte[] bc = ByteArrayAdapter.INSTANCE.readFromParcel(in);",
            "      char[] bd = CharArrayAdapter.INSTANCE.readFromParcel(in);",
            "      double[] be = DoubleArrayAdapter.INSTANCE.readFromParcel(in);",
            "      float[] bf = FloatArrayAdapter.INSTANCE.readFromParcel(in);",
            "      int[] bg = IntArrayAdapter.INSTANCE.readFromParcel(in);",
            "      long[] bh = LongArrayAdapter.INSTANCE.readFromParcel(in);",
            "      short[] bi = ShortArrayAdapter.INSTANCE.readFromParcel(in);",
            "      String[] bj = StringArrayAdapter.INSTANCE.readFromParcel(in);",
            "      SparseBooleanArray bk = SparseBooleanArrayAdapter.INSTANCE.readFromParcel(in);",
            "      Collection<Integer> bl = PaperParcelTest.INTEGER_COLLECTION_ADAPTER.readFromParcel(in);",
            "      IBinder bm = IBinderAdapter.INSTANCE.readFromParcel(in);",
            "      SparseIntArray bn = SparseIntArrayAdapter.INSTANCE.readFromParcel(in);",
            "      SparseLongArray bo = SparseLongArrayAdapter.INSTANCE.readFromParcel(in);",
            "      ArrayMap<Integer, Boolean> bp = PaperParcelTest.INTEGER_BOOLEAN_ARRAY_MAP_ADAPTER.readFromParcel(in);",
            "      ArraySet<Integer> bq = PaperParcelTest.INTEGER_ARRAY_SET_ADAPTER.readFromParcel(in);",
            "      LongSparseArray<Integer> br = PaperParcelTest.INTEGER_LONG_SPARSE_ARRAY_ADAPTER.readFromParcel(in);",
            "      TestEnum bs = PaperParcelTest.TEST_ENUM_ENUM_ADAPTER.readFromParcel(in);",
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
            "      data.bo = bo;",
            "      data.bp = bp;",
            "      data.bq = bq;",
            "      data.br = br;",
            "      data.bs = bs;",
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
            "    BooleanAdapter.INSTANCE.writeToParcel(data.ab, dest, flags);",
            "    dest.writeByte(data.ac);",
            "    ByteAdapter.INSTANCE.writeToParcel(data.ad, dest, flags);",
            "    BundleAdapter.INSTANCE.writeToParcel(data.ae, dest, flags);",
            "    CharSequenceAdapter.INSTANCE.writeToParcel(data.af, dest, flags);",
            "    PaperParcelTest.INTEGER_LIST_ADAPTER.writeToParcel(data.ag, dest, flags);",
            "    dest.writeInt(data.ah);",
            "    CharacterAdapter.INSTANCE.writeToParcel(data.ai, dest, flags);",
            "    dest.writeDouble(data.aj);",
            "    DoubleAdapter.INSTANCE.writeToParcel(data.ak, dest, flags);",
            "    dest.writeFloat(data.al);",
            "    FloatAdapter.INSTANCE.writeToParcel(data.am, dest, flags);",
            "    dest.writeInt(data.an);",
            "    IntegerAdapter.INSTANCE.writeToParcel(data.ao, dest, flags);",
            "    dest.writeLong(data.ap);",
            "    LongAdapter.INSTANCE.writeToParcel(data.aq, dest, flags);",
            "    PaperParcelTest.INTEGER_INTEGER_MAP_ADAPTER.writeToParcel(data.ar, dest, flags);",
            "    PaperParcelTest.TEST_PARCELABLE_PARCELABLE_ADAPTER.writeToParcel(data.as, dest, flags);",
            "    PersistableBundleAdapter.INSTANCE.writeToParcel(data.at, dest, flags);",
            "    PaperParcelTest.INTEGER_SET_ADAPTER.writeToParcel(data.au, dest, flags);",
            "    dest.writeInt(data.av);",
            "    ShortAdapter.INSTANCE.writeToParcel(data.aw, dest, flags);",
            "    SizeFAdapter.INSTANCE.writeToParcel(data.ax, dest, flags);",
            "    SizeAdapter.INSTANCE.writeToParcel(data.ay, dest, flags);",
            "    PaperParcelTest.INTEGER_SPARSE_ARRAY_ADAPTER.writeToParcel(data.az, dest, flags);",
            "    StringAdapter.INSTANCE.writeToParcel(data.ba, dest, flags);",
            "    BooleanArrayAdapter.INSTANCE.writeToParcel(data.bb, dest, flags);",
            "    ByteArrayAdapter.INSTANCE.writeToParcel(data.bc, dest, flags);",
            "    CharArrayAdapter.INSTANCE.writeToParcel(data.bd, dest, flags);",
            "    DoubleArrayAdapter.INSTANCE.writeToParcel(data.be, dest, flags);",
            "    FloatArrayAdapter.INSTANCE.writeToParcel(data.bf, dest, flags);",
            "    IntArrayAdapter.INSTANCE.writeToParcel(data.bg, dest, flags);",
            "    LongArrayAdapter.INSTANCE.writeToParcel(data.bh, dest, flags);",
            "    ShortArrayAdapter.INSTANCE.writeToParcel(data.bi, dest, flags);",
            "    StringArrayAdapter.INSTANCE.writeToParcel(data.bj, dest, flags);",
            "    SparseBooleanArrayAdapter.INSTANCE.writeToParcel(data.bk, dest, flags);",
            "    PaperParcelTest.INTEGER_COLLECTION_ADAPTER.writeToParcel(data.bl, dest, flags);",
            "    IBinderAdapter.INSTANCE.writeToParcel(data.bm, dest, flags);",
            "    SparseIntArrayAdapter.INSTANCE.writeToParcel(data.bn, dest, flags);",
            "    SparseLongArrayAdapter.INSTANCE.writeToParcel(data.bo, dest, flags);",
            "    PaperParcelTest.INTEGER_BOOLEAN_ARRAY_MAP_ADAPTER.writeToParcel(data.bp, dest, flags);",
            "    PaperParcelTest.INTEGER_ARRAY_SET_ADAPTER.writeToParcel(data.bq, dest, flags);",
            "    PaperParcelTest.INTEGER_LONG_SPARSE_ARRAY_ADAPTER.writeToParcel(data.br, dest, flags);",
            "    PaperParcelTest.TEST_ENUM_ENUM_ADAPTER.writeToParcel(data.bs, dest, flags);",
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
        .withErrorContaining(ErrorMessages.PAPERPARCEL_ON_INTERFACE)
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
        .withErrorContaining(ErrorMessages.REGISTERADAPTER_ON_INTERFACE)
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
            "test.Test", "child", ErrorMessages.SITE_URL))
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
            "test.Test", "child", "Test()", ErrorMessages.SITE_URL))
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
            "import javax.annotation.Generated;",
            GeneratedLines.GENERATED_ANNOTATION,
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
        .withErrorContaining(String.format(ErrorMessages.RAW_FIELD, "test.Test", "child"))
        .in(source)
        .onLine(8);
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
            "import javax.annotation.Generated;",
            GeneratedLines.GENERATED_ANNOTATION,
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
            "import javax.annotation.Generated;",
            GeneratedLines.GENERATED_ANNOTATION,
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
            "import javax.annotation.Generated;",
            GeneratedLines.GENERATED_ANNOTATION,
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
            "import javax.annotation.Generated;",
            GeneratedLines.GENERATED_ANNOTATION,
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
            "import javax.annotation.Generated;",
            GeneratedLines.GENERATED_ANNOTATION,
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
            "import java.util.List;",
            "import java.util.Map;",
            "import android.os.Parcel;",
            "@RegisterAdapter",
            "public class ReallySpecificTypeAdapter<T1, T2> implements TypeAdapter<Map<List<T1>[], Map<T1, T2>>> {",
            "  public Map<List<T1>[], Map<T1, T2>> readFromParcel(Parcel in) {",
            "    return null;",
            "  }",
            "  public void writeToParcel(Map<List<T1>[], Map<T1, T2>> value, Parcel dest, int flags) {",
            "  }",
            "}"
        ));

    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "import java.util.List;",
            "import java.util.Map;",
            "@PaperParcel",
            "public class Test implements Parcelable {",
            "  public Map<List<Integer>[], Map<Integer, Boolean>> field1;",
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
            "import java.util.List;",
            "import java.util.Map;",
            "import javax.annotation.Generated;",
            "import paperparcel.internal.IntegerAdapter;",
            "import paperparcel.internal.MapAdapter;",
            GeneratedLines.GENERATED_ANNOTATION,
            "final class PaperParcelTest {",
            "  static final ReallySpecificTypeAdapter<Integer, Boolean> INTEGER_BOOLEAN_REALLY_SPECIFIC_TYPE_ADAPTER = new ReallySpecificTypeAdapter<Integer, Boolean>();",
            "  static final MapAdapter<Integer, Integer> INTEGER_INTEGER_MAP_ADAPTER = new MapAdapter<Integer, Integer>(IntegerAdapter.INSTANCE, IntegerAdapter.INSTANCE);",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      Map<List<Integer>[], Map<Integer, Boolean>> field1 = PaperParcelTest.INTEGER_BOOLEAN_REALLY_SPECIFIC_TYPE_ADAPTER.readFromParcel(in);",
            "      Map<Integer, Integer> field2 = PaperParcelTest.INTEGER_INTEGER_MAP_ADAPTER.readFromParcel(in);",
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
            "    PaperParcelTest.INTEGER_BOOLEAN_REALLY_SPECIFIC_TYPE_ADAPTER.writeToParcel(data.field1, dest, flags);",
            "    PaperParcelTest.INTEGER_INTEGER_MAP_ADAPTER.writeToParcel(data.field2, dest, flags);",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(reallySpecificAdapter, source))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void genericArrayTypeAdapterTest() {
    JavaFileObject genericArrayAdapter =
        JavaFileObjects.forSourceString("test.GenericArrayAdapter", Joiner.on('\n').join(
            "package test;",
            "import paperparcel.RegisterAdapter;",
            "import paperparcel.TypeAdapter;",
            "import android.os.Parcel;",
            "@RegisterAdapter",
            "public class GenericArrayAdapter<T> implements TypeAdapter<T[]> {",
            "  public T[] readFromParcel(Parcel in) {",
            "    return null;",
            "  }",
            "  public void writeToParcel(T[] value, Parcel dest, int flags) {",
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
            "  public Integer[] field;",
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
            "import javax.annotation.Generated;",
            GeneratedLines.GENERATED_ANNOTATION,
            "final class PaperParcelTest {",
            "  static final GenericArrayAdapter<Integer> INTEGER_GENERIC_ARRAY_ADAPTER = new GenericArrayAdapter<Integer>();",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      Integer[] field = PaperParcelTest.INTEGER_GENERIC_ARRAY_ADAPTER.readFromParcel(in);",
            "      Test data = new Test();",
            "      data.field = field;",
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
            "    PaperParcelTest.INTEGER_GENERIC_ARRAY_ADAPTER.writeToParcel(data.field, dest, flags);",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(genericArrayAdapter, source))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void wildcardAdapterTest() {
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

    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import java.util.List;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel",
            "public class Test implements Parcelable {",
            "  public List<? extends Integer> field;",
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
            "import javax.annotation.Generated;",
            GeneratedLines.GENERATED_ANNOTATION,
            "final class PaperParcelTest {",
            "  static final WildcardAdapter WILDCARD_ADAPTER = new WildcardAdapter();",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      List<? extends Integer> field = PaperParcelTest.WILDCARD_ADAPTER.readFromParcel(in);",
            "      Test data = new Test();",
            "      data.field = field;",
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
            "    PaperParcelTest.WILDCARD_ADAPTER.writeToParcel(data.field, dest, flags);",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(wildcardAdapter, source))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void genericWildcardAdapterTest() {
    JavaFileObject wildcardAdapter =
        JavaFileObjects.forSourceString("test.WildcardAdapter", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import java.util.List;",
            "import paperparcel.TypeAdapter;",
            "import paperparcel.RegisterAdapter;",
            "@RegisterAdapter",
            "public class WildcardAdapter<T> implements TypeAdapter<List<? extends T>> {",
            "  @Override public List<? extends T> readFromParcel(Parcel source) { return null; }",
            "  @Override public void writeToParcel(List<? extends T> value, Parcel dest, int flags) {}",
            "}"
        ));

    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import java.util.List;",
            "import paperparcel.PaperParcel;",
            "@PaperParcel",
            "public class Test implements Parcelable {",
            "  public List<? extends Integer> field;",
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
            "import javax.annotation.Generated;",
            GeneratedLines.GENERATED_ANNOTATION,
            "final class PaperParcelTest {",
            "  static final WildcardAdapter<Integer> INTEGER_WILDCARD_ADAPTER = new WildcardAdapter<Integer>();",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      List<? extends Integer> field = PaperParcelTest.INTEGER_WILDCARD_ADAPTER.readFromParcel(in);",
            "      Test data = new Test();",
            "      data.field = field;",
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
            "    PaperParcelTest.INTEGER_WILDCARD_ADAPTER.writeToParcel(data.field, dest, flags);",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(wildcardAdapter, source))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
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
            "import javax.annotation.Generated;",
            "import paperparcel.internal.ParcelableAdapter;",
            GeneratedLines.GENERATED_ANNOTATION,
            "final class PaperParcelTest {",
            "  static final ParcelableAdapter<Parcelable> PARCELABLE_PARCELABLE_ADAPTER = new ParcelableAdapter<Parcelable>();",
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
            "import javax.annotation.Generated;",
            "import paperparcel.internal.MapAdapter;",
            "import paperparcel.internal.ParcelableAdapter;",
            "import paperparcel.internal.StringAdapter;",
            GeneratedLines.GENERATED_ANNOTATION,
            "final class PaperParcelTest {",
            "  static final ParcelableAdapter<Parcelable> PARCELABLE_PARCELABLE_ADAPTER = new ParcelableAdapter<Parcelable>();",
            "  static final MapAdapter<Parcelable, String> PARCELABLE_STRING_MAP_ADAPTER = new MapAdapter<Parcelable, String>(PaperParcelTest.PARCELABLE_PARCELABLE_ADAPTER, StringAdapter.INSTANCE);",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      Map<Parcelable, String> value = PaperParcelTest.PARCELABLE_STRING_MAP_ADAPTER.readFromParcel(in);",
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
            "    PaperParcelTest.PARCELABLE_STRING_MAP_ADAPTER.writeToParcel(data.value, dest, flags);",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
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
            "import javax.annotation.Generated;",
            GeneratedLines.GENERATED_ANNOTATION,
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
            "import javax.annotation.Generated;",
            GeneratedLines.GENERATED_ANNOTATION,
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
            "import javax.annotation.Generated;",
            GeneratedLines.GENERATED_ANNOTATION,
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
            "import javax.annotation.Generated;",
            GeneratedLines.GENERATED_ANNOTATION,
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
            "import javax.annotation.Generated;",
            GeneratedLines.GENERATED_ANNOTATION,
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
            "import javax.annotation.Generated;",
            GeneratedLines.GENERATED_ANNOTATION,
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
            "import javax.annotation.Generated;",
            GeneratedLines.GENERATED_ANNOTATION,
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
            "import javax.annotation.Generated;",
            "import paperparcel.internal.IntArrayAdapter;",
            "import paperparcel.internal.ListAdapter;",
            GeneratedLines.GENERATED_ANNOTATION,
            "final class PaperParcelTest {",
            "  static final ListAdapter<int[]> INT_ARRAY_LIST_ADAPTER = new ListAdapter<int[]>(IntArrayAdapter.INSTANCE);",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      List<int[]> value = PaperParcelTest.INT_ARRAY_LIST_ADAPTER.readFromParcel(in);",
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
            "    PaperParcelTest.INT_ARRAY_LIST_ADAPTER.writeToParcel(data.value(), dest, flags);",
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
            "import javax.annotation.Generated;",
            GeneratedLines.GENERATED_ANNOTATION,
            "final class PaperParcelTest {",
            "  static final MyClassAdapter MY_CLASS_ADAPTER = new MyClassAdapter();",
            "  static final test.clash.MyClassAdapter MY_CLASS_ADAPTER_1 = new test.clash.MyClassAdapter();",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      MyClass value1 = PaperParcelTest.MY_CLASS_ADAPTER.readFromParcel(in);",
            "      test.clash.MyClass value2 = PaperParcelTest.MY_CLASS_ADAPTER_1.readFromParcel(in);",
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
            "    PaperParcelTest.MY_CLASS_ADAPTER.writeToParcel(data.value1(), dest, flags);",
            "    PaperParcelTest.MY_CLASS_ADAPTER_1.writeToParcel(data.value2(), dest, flags);",
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
            "import javax.annotation.Generated;",
            GeneratedLines.GENERATED_ANNOTATION,
            "final class PaperParcelTest {",
            "  static final MyClassAdapter MY_CLASS_ADAPTER = new MyClassAdapter();",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      int in1 = in.readInt();",
            "      int data = in.readInt();",
            "      int dest = in.readInt();",
            "      int flags = in.readInt();",
            "      MyClass MY_CLASS_ADAPTER = PaperParcelTest.MY_CLASS_ADAPTER.readFromParcel(in);",
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
            "    PaperParcelTest.MY_CLASS_ADAPTER.writeToParcel(data.MY_CLASS_ADAPTER, dest, flags);",
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
            "import javax.annotation.Generated;",
            "import paperparcel.internal.Utils;",
            GeneratedLines.GENERATED_ANNOTATION,
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
            "import javax.annotation.Generated;",
            "import paperparcel.internal.IntegerAdapter;",
            "import paperparcel.internal.ListAdapter;",
            "import paperparcel.internal.Utils;",
            GeneratedLines.GENERATED_ANNOTATION,
            "final class PaperParcelTest {",
            "  static final ListAdapter<Integer> INTEGER_LIST_ADAPTER = new ListAdapter<Integer>(IntegerAdapter.INSTANCE);",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      List<Integer> reflectIt = PaperParcelTest.INTEGER_LIST_ADAPTER.readFromParcel(in);",
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
            "    PaperParcelTest.INTEGER_LIST_ADAPTER.writeToParcel(Utils.readField(List.class, Test.class, data, \"reflectIt\"), dest, flags);",
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
            "import javax.annotation.Generated;",
            "import paperparcel.internal.IntegerAdapter;",
            "import paperparcel.internal.ListAdapter;",
            "import paperparcel.internal.Utils;",
            GeneratedLines.GENERATED_ANNOTATION,
            "final class PaperParcelTest {",
            "  static final ListAdapter<Integer> INTEGER_LIST_ADAPTER = new ListAdapter<Integer>(IntegerAdapter.INSTANCE);",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      List<Integer> reflectIt = PaperParcelTest.INTEGER_LIST_ADAPTER.readFromParcel(in);",
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
            "    PaperParcelTest.INTEGER_LIST_ADAPTER.writeToParcel(data.reflectIt(), dest, flags);",
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
            "import javax.annotation.Generated;",
            "import paperparcel.internal.IntegerAdapter;",
            "import paperparcel.internal.ListAdapter;",
            "import paperparcel.internal.Utils;",
            GeneratedLines.GENERATED_ANNOTATION,
            "final class PaperParcelTest {",
            "  static final ListAdapter<Integer> INTEGER_LIST_ADAPTER = new ListAdapter<Integer>(IntegerAdapter.INSTANCE);",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      List<Integer> reflectIt = PaperParcelTest.INTEGER_LIST_ADAPTER.readFromParcel(in);",
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
            "    PaperParcelTest.INTEGER_LIST_ADAPTER.writeToParcel(Utils.readField(List.class, Test.class, data, \"reflectIt\"), dest, flags);",
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
            "import javax.annotation.Generated;",
            "import paperparcel.internal.IntegerAdapter;",
            "import paperparcel.internal.ListAdapter;",
            "import paperparcel.internal.Utils;",
            GeneratedLines.GENERATED_ANNOTATION,
            "final class PaperParcelTest {",
            "  static final ListAdapter<Integer> INTEGER_LIST_ADAPTER = new ListAdapter<Integer>(IntegerAdapter.INSTANCE);",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      List<Integer> reflectIt = PaperParcelTest.INTEGER_LIST_ADAPTER.readFromParcel(in);",
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
            "    PaperParcelTest.INTEGER_LIST_ADAPTER.writeToParcel(Utils.readField(List.class, Test.class, data, \"reflectIt\"), dest, flags);",
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
            "import javax.annotation.Generated;",
            "import paperparcel.internal.IntegerAdapter;",
            "import paperparcel.internal.ListAdapter;",
            GeneratedLines.GENERATED_ANNOTATION,
            "final class PaperParcelTest {",
            "  static final ListAdapter<Integer> INTEGER_LIST_ADAPTER = new ListAdapter<Integer>(IntegerAdapter.INSTANCE);",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      List<Integer> value = PaperParcelTest.INTEGER_LIST_ADAPTER.readFromParcel(in);",
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
            "    PaperParcelTest.INTEGER_LIST_ADAPTER.writeToParcel(data.value, dest, flags);",
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
            "import javax.annotation.Generated;",
            "import paperparcel.internal.IntegerAdapter;",
            "import paperparcel.internal.ListAdapter;",
            "import paperparcel.internal.Utils;",
            GeneratedLines.GENERATED_ANNOTATION,
            "final class PaperParcelTest {",
            "  static final ListAdapter<Integer> INTEGER_LIST_ADAPTER = new ListAdapter<Integer>(IntegerAdapter.INSTANCE);",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      List<Integer> value = PaperParcelTest.INTEGER_LIST_ADAPTER.readFromParcel(in);",
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
            "    PaperParcelTest.INTEGER_LIST_ADAPTER.writeToParcel(data.value(), dest, flags);",
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
            "import javax.annotation.Generated;",
            GeneratedLines.GENERATED_ANNOTATION,
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

}
