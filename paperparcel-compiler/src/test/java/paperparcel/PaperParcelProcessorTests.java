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

  @Test public void allBuiltInAdaptersTest() throws Exception {
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
            "import android.graphics.Bitmap;",
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
            "import java.util.ArrayDeque;",
            "import java.util.Collection;",
            "import java.util.Deque;",
            "import java.util.List;",
            "import java.util.Map;",
            "import java.util.Set;",
            "import java.util.Queue;",
            "import java.util.ArrayList;",
            "import java.util.HashMap;",
            "import java.util.HashSet;",
            "import java.util.LinkedHashMap;",
            "import java.util.LinkedHashSet;",
            "import java.util.LinkedList;",
            "import java.util.SortedMap;",
            "import java.util.SortedSet;",
            "import java.util.TreeMap;",
            "import java.util.TreeSet;",
            "import java.math.BigInteger;",
            "import java.math.BigDecimal;",
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
            "  public ArrayList<Boolean> au;",
            "  public Set<Integer> av;",
            "  public short aw;",
            "  public Short ax;",
            "  public SizeF ay;",
            "  public Size az;",
            "  public SparseArray<Integer> ba;",
            "  public String bb;",
            "  public boolean[] bc;",
            "  public byte[] bd;",
            "  public char[] be;",
            "  public double[] bf;",
            "  public float[] bg;",
            "  public int[] bh;",
            "  public long[] bi;",
            "  public short[] bj;",
            "  public String[] bk;",
            "  public BigInteger bl;",
            "  public BigDecimal bm;",
            "  public SparseBooleanArray bn;",
            "  public HashMap<Integer, Boolean> bo;",
            "  public HashSet<Integer> bp;",
            "  public LinkedHashMap<Integer, Boolean> bq;",
            "  public LinkedHashSet<Integer> br;",
            "  public LinkedList<Integer> bs;",
            "  public SortedMap<Integer, Boolean> bt;",
            "  public SortedSet<Integer> bu;",
            "  public TreeMap<Integer, Boolean> bv;",
            "  public TreeSet<Integer> bw;",
            "  public Collection<Integer> bx;",
            "  public IBinder bz;",
            "  public SparseIntArray ca;",
            "  public SparseLongArray cb;",
            "  public ArrayMap<Integer, Boolean> cc;",
            "  public ArraySet<Integer> cd;",
            "  public LongSparseArray<Integer> ce;",
            "  public ArrayDeque<Integer> cf;",
            "  public Deque<Integer> cg;",
            "  public Queue<Integer> ch;",
            "  public TestEnum ci;",
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
            "import java.math.BigDecimal;",
            "import java.math.BigInteger;",
            "import java.util.ArrayDeque;",
            "import java.util.ArrayList;",
            "import java.util.Collection;",
            "import java.util.Deque;",
            "import java.util.HashMap;",
            "import java.util.HashSet;",
            "import java.util.LinkedHashMap;",
            "import java.util.LinkedHashSet;",
            "import java.util.LinkedList;",
            "import java.util.List;",
            "import java.util.Map;",
            "import java.util.Queue;",
            "import java.util.Set;",
            "import java.util.SortedMap;",
            "import java.util.SortedSet;",
            "import java.util.TreeMap;",
            "import java.util.TreeSet;",
            "import javax.annotation.Generated;",
            "import paperparcel.adapter.ArrayDequeAdapter;",
            "import paperparcel.adapter.ArrayListAdapter;",
            "import paperparcel.adapter.ArrayMapAdapter;",
            "import paperparcel.adapter.ArraySetAdapter;",
            "import paperparcel.adapter.BigDecimalAdapter;",
            "import paperparcel.adapter.BigIntegerAdapter;",
            "import paperparcel.adapter.BooleanAdapter;",
            "import paperparcel.adapter.BooleanArrayAdapter;",
            "import paperparcel.adapter.BundleAdapter;",
            "import paperparcel.adapter.ByteAdapter;",
            "import paperparcel.adapter.ByteArrayAdapter;",
            "import paperparcel.adapter.CharArrayAdapter;",
            "import paperparcel.adapter.CharSequenceAdapter;",
            "import paperparcel.adapter.CharacterAdapter;",
            "import paperparcel.adapter.CollectionAdapter;",
            "import paperparcel.adapter.DequeAdapter;",
            "import paperparcel.adapter.DoubleAdapter;",
            "import paperparcel.adapter.DoubleArrayAdapter;",
            "import paperparcel.adapter.EnumAdapter;",
            "import paperparcel.adapter.FloatAdapter;",
            "import paperparcel.adapter.FloatArrayAdapter;",
            "import paperparcel.adapter.HashMapAdapter;",
            "import paperparcel.adapter.HashSetAdapter;",
            "import paperparcel.adapter.IBinderAdapter;",
            "import paperparcel.adapter.IntArrayAdapter;",
            "import paperparcel.adapter.IntegerAdapter;",
            "import paperparcel.adapter.LinkedHashMapAdapter;",
            "import paperparcel.adapter.LinkedHashSetAdapter;",
            "import paperparcel.adapter.LinkedListAdapter;",
            "import paperparcel.adapter.ListAdapter;",
            "import paperparcel.adapter.LongAdapter;",
            "import paperparcel.adapter.LongArrayAdapter;",
            "import paperparcel.adapter.LongSparseArrayAdapter;",
            "import paperparcel.adapter.MapAdapter;",
            "import paperparcel.adapter.ParcelableAdapter;",
            "import paperparcel.adapter.PersistableBundleAdapter;",
            "import paperparcel.adapter.QueueAdapter;",
            "import paperparcel.adapter.SetAdapter;",
            "import paperparcel.adapter.ShortAdapter;",
            "import paperparcel.adapter.ShortArrayAdapter;",
            "import paperparcel.adapter.SizeAdapter;",
            "import paperparcel.adapter.SizeFAdapter;",
            "import paperparcel.adapter.SortedMapAdapter;",
            "import paperparcel.adapter.SortedSetAdapter;",
            "import paperparcel.adapter.SparseArrayAdapter;",
            "import paperparcel.adapter.SparseBooleanArrayAdapter;",
            "import paperparcel.adapter.SparseIntArrayAdapter;",
            "import paperparcel.adapter.SparseLongArrayAdapter;",
            "import paperparcel.adapter.StringAdapter;",
            "import paperparcel.adapter.StringArrayAdapter;",
            "import paperparcel.adapter.TreeMapAdapter;",
            "import paperparcel.adapter.TreeSetAdapter;",
            GeneratedLines.GENERATED_ANNOTATION,
            "final class PaperParcelTest {",
            "  private static final ListAdapter<Integer> INTEGER_LIST_ADAPTER = new ListAdapter<Integer>(IntegerAdapter.INSTANCE);",
            "  private static final MapAdapter<Integer, Integer> INTEGER_INTEGER_MAP_ADAPTER = new MapAdapter<Integer, Integer>(IntegerAdapter.INSTANCE, IntegerAdapter.INSTANCE);",
            "  private static final ParcelableAdapter<TestParcelable> TEST_PARCELABLE_PARCELABLE_ADAPTER = new ParcelableAdapter<TestParcelable>();",
            "  private static final ArrayListAdapter<Boolean> BOOLEAN_ARRAY_LIST_ADAPTER = new ArrayListAdapter<Boolean>(BooleanAdapter.INSTANCE);",
            "  private static final SetAdapter<Integer> INTEGER_SET_ADAPTER = new SetAdapter<Integer>(IntegerAdapter.INSTANCE);",
            "  private static final SparseArrayAdapter<Integer> INTEGER_SPARSE_ARRAY_ADAPTER = new SparseArrayAdapter<Integer>(IntegerAdapter.INSTANCE);",
            "  private static final HashMapAdapter<Integer, Boolean> INTEGER_BOOLEAN_HASH_MAP_ADAPTER = new HashMapAdapter<Integer, Boolean>(IntegerAdapter.INSTANCE, BooleanAdapter.INSTANCE);",
            "  private static final HashSetAdapter<Integer> INTEGER_HASH_SET_ADAPTER = new HashSetAdapter<Integer>(IntegerAdapter.INSTANCE);",
            "  private static final LinkedHashMapAdapter<Integer, Boolean> INTEGER_BOOLEAN_LINKED_HASH_MAP_ADAPTER = new LinkedHashMapAdapter<Integer, Boolean>(IntegerAdapter.INSTANCE, BooleanAdapter.INSTANCE);",
            "  private static final LinkedHashSetAdapter<Integer> INTEGER_LINKED_HASH_SET_ADAPTER = new LinkedHashSetAdapter<Integer>(IntegerAdapter.INSTANCE);",
            "  private static final LinkedListAdapter<Integer> INTEGER_LINKED_LIST_ADAPTER = new LinkedListAdapter<Integer>(IntegerAdapter.INSTANCE);",
            "  private static final SortedMapAdapter<Integer, Boolean> INTEGER_BOOLEAN_SORTED_MAP_ADAPTER = new SortedMapAdapter<Integer, Boolean>(IntegerAdapter.INSTANCE, BooleanAdapter.INSTANCE);",
            "  private static final SortedSetAdapter<Integer> INTEGER_SORTED_SET_ADAPTER = new SortedSetAdapter<Integer>(IntegerAdapter.INSTANCE);",
            "  private static final TreeMapAdapter<Integer, Boolean> INTEGER_BOOLEAN_TREE_MAP_ADAPTER = new TreeMapAdapter<Integer, Boolean>(IntegerAdapter.INSTANCE, BooleanAdapter.INSTANCE);",
            "  private static final TreeSetAdapter<Integer> INTEGER_TREE_SET_ADAPTER = new TreeSetAdapter<Integer>(IntegerAdapter.INSTANCE);",
            "  private static final CollectionAdapter<Integer> INTEGER_COLLECTION_ADAPTER = new CollectionAdapter<Integer>(IntegerAdapter.INSTANCE);",
            "  private static final ArrayMapAdapter<Integer, Boolean> INTEGER_BOOLEAN_ARRAY_MAP_ADAPTER = new ArrayMapAdapter<Integer, Boolean>(IntegerAdapter.INSTANCE, BooleanAdapter.INSTANCE);",
            "  private static final ArraySetAdapter<Integer> INTEGER_ARRAY_SET_ADAPTER = new ArraySetAdapter<Integer>(IntegerAdapter.INSTANCE);",
            "  private static final LongSparseArrayAdapter<Integer> INTEGER_LONG_SPARSE_ARRAY_ADAPTER = new LongSparseArrayAdapter<Integer>(IntegerAdapter.INSTANCE);",
            "  private static final ArrayDequeAdapter<Integer> INTEGER_ARRAY_DEQUE_ADAPTER = new ArrayDequeAdapter<Integer>(IntegerAdapter.INSTANCE);",
            "  private static final DequeAdapter<Integer> INTEGER_DEQUE_ADAPTER = new DequeAdapter<Integer>(IntegerAdapter.INSTANCE);",
            "  private static final QueueAdapter<Integer> INTEGER_QUEUE_ADAPTER = new QueueAdapter<Integer>(IntegerAdapter.INSTANCE);",
            "  private static final EnumAdapter<TestEnum> TEST_ENUM_ENUM_ADAPTER = new EnumAdapter<TestEnum>();",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override public Test createFromParcel(Parcel in) {",
            "      Boolean aa = BooleanAdapter.INSTANCE.readFromParcel(in);",
            "      Boolean ab = BooleanAdapter.INSTANCE.readFromParcel(in);",
            "      Byte ac = ByteAdapter.INSTANCE.readFromParcel(in);",
            "      Byte ad = ByteAdapter.INSTANCE.readFromParcel(in);",
            "      Bundle ae = BundleAdapter.INSTANCE.readFromParcel(in);",
            "      CharSequence af = CharSequenceAdapter.INSTANCE.readFromParcel(in);",
            "      List<Integer> ag = INTEGER_LIST_ADAPTER.readFromParcel(in);",
            "      Character ah = CharacterAdapter.INSTANCE.readFromParcel(in);",
            "      Character ai = CharacterAdapter.INSTANCE.readFromParcel(in);",
            "      Double aj = DoubleAdapter.INSTANCE.readFromParcel(in);",
            "      Double ak = DoubleAdapter.INSTANCE.readFromParcel(in);",
            "      Float al = FloatAdapter.INSTANCE.readFromParcel(in);",
            "      Float am = FloatAdapter.INSTANCE.readFromParcel(in);",
            "      Integer an = IntegerAdapter.INSTANCE.readFromParcel(in);",
            "      Integer ao = IntegerAdapter.INSTANCE.readFromParcel(in);",
            "      Long ap = LongAdapter.INSTANCE.readFromParcel(in);",
            "      Long aq = LongAdapter.INSTANCE.readFromParcel(in);",
            "      Map<Integer, Integer> ar = INTEGER_INTEGER_MAP_ADAPTER.readFromParcel(in);",
            "      TestParcelable as = TEST_PARCELABLE_PARCELABLE_ADAPTER.readFromParcel(in);",
            "      PersistableBundle at = PersistableBundleAdapter.INSTANCE.readFromParcel(in);",
            "      ArrayList<Boolean> au = BOOLEAN_ARRAY_LIST_ADAPTER.readFromParcel(in);",
            "      Set<Integer> av = INTEGER_SET_ADAPTER.readFromParcel(in);",
            "      Short aw = ShortAdapter.INSTANCE.readFromParcel(in);",
            "      Short ax = ShortAdapter.INSTANCE.readFromParcel(in);",
            "      SizeF ay = SizeFAdapter.INSTANCE.readFromParcel(in);",
            "      Size az = SizeAdapter.INSTANCE.readFromParcel(in);",
            "      SparseArray<Integer> ba = INTEGER_SPARSE_ARRAY_ADAPTER.readFromParcel(in);",
            "      String bb = StringAdapter.INSTANCE.readFromParcel(in);",
            "      boolean[] bc = BooleanArrayAdapter.INSTANCE.readFromParcel(in);",
            "      byte[] bd = ByteArrayAdapter.INSTANCE.readFromParcel(in);",
            "      char[] be = CharArrayAdapter.INSTANCE.readFromParcel(in);",
            "      double[] bf = DoubleArrayAdapter.INSTANCE.readFromParcel(in);",
            "      float[] bg = FloatArrayAdapter.INSTANCE.readFromParcel(in);",
            "      int[] bh = IntArrayAdapter.INSTANCE.readFromParcel(in);",
            "      long[] bi = LongArrayAdapter.INSTANCE.readFromParcel(in);",
            "      short[] bj = ShortArrayAdapter.INSTANCE.readFromParcel(in);",
            "      String[] bk = StringArrayAdapter.INSTANCE.readFromParcel(in);",
            "      BigInteger bl = BigIntegerAdapter.INSTANCE.readFromParcel(in);",
            "      BigDecimal bm = BigDecimalAdapter.INSTANCE.readFromParcel(in);",
            "      SparseBooleanArray bn = SparseBooleanArrayAdapter.INSTANCE.readFromParcel(in);",
            "      HashMap<Integer, Boolean> bo = INTEGER_BOOLEAN_HASH_MAP_ADAPTER.readFromParcel(in);",
            "      HashSet<Integer> bp = INTEGER_HASH_SET_ADAPTER.readFromParcel(in);",
            "      LinkedHashMap<Integer, Boolean> bq = INTEGER_BOOLEAN_LINKED_HASH_MAP_ADAPTER.readFromParcel(in);",
            "      LinkedHashSet<Integer> br = INTEGER_LINKED_HASH_SET_ADAPTER.readFromParcel(in);",
            "      LinkedList<Integer> bs = INTEGER_LINKED_LIST_ADAPTER.readFromParcel(in);",
            "      SortedMap<Integer, Boolean> bt = INTEGER_BOOLEAN_SORTED_MAP_ADAPTER.readFromParcel(in);",
            "      SortedSet<Integer> bu = INTEGER_SORTED_SET_ADAPTER.readFromParcel(in);",
            "      TreeMap<Integer, Boolean> bv = INTEGER_BOOLEAN_TREE_MAP_ADAPTER.readFromParcel(in);",
            "      TreeSet<Integer> bw = INTEGER_TREE_SET_ADAPTER.readFromParcel(in);",
            "      Collection<Integer> bx = INTEGER_COLLECTION_ADAPTER.readFromParcel(in);",
            "      IBinder bz = IBinderAdapter.INSTANCE.readFromParcel(in);",
            "      SparseIntArray ca = SparseIntArrayAdapter.INSTANCE.readFromParcel(in);",
            "      SparseLongArray cb = SparseLongArrayAdapter.INSTANCE.readFromParcel(in);",
            "      ArrayMap<Integer, Boolean> cc = INTEGER_BOOLEAN_ARRAY_MAP_ADAPTER.readFromParcel(in);",
            "      ArraySet<Integer> cd = INTEGER_ARRAY_SET_ADAPTER.readFromParcel(in);",
            "      LongSparseArray<Integer> ce = INTEGER_LONG_SPARSE_ARRAY_ADAPTER.readFromParcel(in);",
            "      ArrayDeque<Integer> cf = INTEGER_ARRAY_DEQUE_ADAPTER.readFromParcel(in);",
            "      Deque<Integer> cg = INTEGER_DEQUE_ADAPTER.readFromParcel(in);",
            "      Queue<Integer> ch = INTEGER_QUEUE_ADAPTER.readFromParcel(in);",
            "      TestEnum ci = TEST_ENUM_ENUM_ADAPTER.readFromParcel(in);",
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
            "      data.bt = bt;",
            "      data.bu = bu;",
            "      data.bv = bv;",
            "      data.bw = bw;",
            "      data.bx = bx;",
            "      data.bz = bz;",
            "      data.ca = ca;",
            "      data.cb = cb;",
            "      data.cc = cc;",
            "      data.cd = cd;",
            "      data.ce = ce;",
            "      data.cf = cf;",
            "      data.cg = cg;",
            "      data.ch = ch;",
            "      data.ci = ci;",
            "      return data;",
            "    }",
            "    @Override public Test[] newArray(int size) {",
            "      return new Test[size];",
            "    }",
            "  };",
            "  private PaperParcelTest() {",
            "  }",
            "  static void writeToParcel(@NonNull Test data, @NonNull Parcel dest, int flags) {",
            "    BooleanAdapter.INSTANCE.writeToParcel(data.aa, dest, flags);",
            "    BooleanAdapter.INSTANCE.writeToParcel(data.ab, dest, flags);",
            "    ByteAdapter.INSTANCE.writeToParcel(data.ac, dest, flags);",
            "    ByteAdapter.INSTANCE.writeToParcel(data.ad, dest, flags);",
            "    BundleAdapter.INSTANCE.writeToParcel(data.ae, dest, flags);",
            "    CharSequenceAdapter.INSTANCE.writeToParcel(data.af, dest, flags);",
            "    INTEGER_LIST_ADAPTER.writeToParcel(data.ag, dest, flags);",
            "    CharacterAdapter.INSTANCE.writeToParcel(data.ah, dest, flags);",
            "    CharacterAdapter.INSTANCE.writeToParcel(data.ai, dest, flags);",
            "    DoubleAdapter.INSTANCE.writeToParcel(data.aj, dest, flags);",
            "    DoubleAdapter.INSTANCE.writeToParcel(data.ak, dest, flags);",
            "    FloatAdapter.INSTANCE.writeToParcel(data.al, dest, flags);",
            "    FloatAdapter.INSTANCE.writeToParcel(data.am, dest, flags);",
            "    IntegerAdapter.INSTANCE.writeToParcel(data.an, dest, flags);",
            "    IntegerAdapter.INSTANCE.writeToParcel(data.ao, dest, flags);",
            "    LongAdapter.INSTANCE.writeToParcel(data.ap, dest, flags);",
            "    LongAdapter.INSTANCE.writeToParcel(data.aq, dest, flags);",
            "    INTEGER_INTEGER_MAP_ADAPTER.writeToParcel(data.ar, dest, flags);",
            "    TEST_PARCELABLE_PARCELABLE_ADAPTER.writeToParcel(data.as, dest, flags);",
            "    PersistableBundleAdapter.INSTANCE.writeToParcel(data.at, dest, flags);",
            "    BOOLEAN_ARRAY_LIST_ADAPTER.writeToParcel(data.au, dest, flags);",
            "    INTEGER_SET_ADAPTER.writeToParcel(data.av, dest, flags);",
            "    ShortAdapter.INSTANCE.writeToParcel(data.aw, dest, flags);",
            "    ShortAdapter.INSTANCE.writeToParcel(data.ax, dest, flags);",
            "    SizeFAdapter.INSTANCE.writeToParcel(data.ay, dest, flags);",
            "    SizeAdapter.INSTANCE.writeToParcel(data.az, dest, flags);",
            "    INTEGER_SPARSE_ARRAY_ADAPTER.writeToParcel(data.ba, dest, flags);",
            "    StringAdapter.INSTANCE.writeToParcel(data.bb, dest, flags);",
            "    BooleanArrayAdapter.INSTANCE.writeToParcel(data.bc, dest, flags);",
            "    ByteArrayAdapter.INSTANCE.writeToParcel(data.bd, dest, flags);",
            "    CharArrayAdapter.INSTANCE.writeToParcel(data.be, dest, flags);",
            "    DoubleArrayAdapter.INSTANCE.writeToParcel(data.bf, dest, flags);",
            "    FloatArrayAdapter.INSTANCE.writeToParcel(data.bg, dest, flags);",
            "    IntArrayAdapter.INSTANCE.writeToParcel(data.bh, dest, flags);",
            "    LongArrayAdapter.INSTANCE.writeToParcel(data.bi, dest, flags);",
            "    ShortArrayAdapter.INSTANCE.writeToParcel(data.bj, dest, flags);",
            "    StringArrayAdapter.INSTANCE.writeToParcel(data.bk, dest, flags);",
            "    BigIntegerAdapter.INSTANCE.writeToParcel(data.bl, dest, flags);",
            "    BigDecimalAdapter.INSTANCE.writeToParcel(data.bm, dest, flags);",
            "    SparseBooleanArrayAdapter.INSTANCE.writeToParcel(data.bn, dest, flags);",
            "    INTEGER_BOOLEAN_HASH_MAP_ADAPTER.writeToParcel(data.bo, dest, flags);",
            "    INTEGER_HASH_SET_ADAPTER.writeToParcel(data.bp, dest, flags);",
            "    INTEGER_BOOLEAN_LINKED_HASH_MAP_ADAPTER.writeToParcel(data.bq, dest, flags);",
            "    INTEGER_LINKED_HASH_SET_ADAPTER.writeToParcel(data.br, dest, flags);",
            "    INTEGER_LINKED_LIST_ADAPTER.writeToParcel(data.bs, dest, flags);",
            "    INTEGER_BOOLEAN_SORTED_MAP_ADAPTER.writeToParcel(data.bt, dest, flags);",
            "    INTEGER_SORTED_SET_ADAPTER.writeToParcel(data.bu, dest, flags);",
            "    INTEGER_BOOLEAN_TREE_MAP_ADAPTER.writeToParcel(data.bv, dest, flags);",
            "    INTEGER_TREE_SET_ADAPTER.writeToParcel(data.bw, dest, flags);",
            "    INTEGER_COLLECTION_ADAPTER.writeToParcel(data.bx, dest, flags);",
            "    IBinderAdapter.INSTANCE.writeToParcel(data.bz, dest, flags);",
            "    SparseIntArrayAdapter.INSTANCE.writeToParcel(data.ca, dest, flags);",
            "    SparseLongArrayAdapter.INSTANCE.writeToParcel(data.cb, dest, flags);",
            "    INTEGER_BOOLEAN_ARRAY_MAP_ADAPTER.writeToParcel(data.cc, dest, flags);",
            "    INTEGER_ARRAY_SET_ADAPTER.writeToParcel(data.cd, dest, flags);",
            "    INTEGER_LONG_SPARSE_ARRAY_ADAPTER.writeToParcel(data.ce, dest, flags);",
            "    INTEGER_ARRAY_DEQUE_ADAPTER.writeToParcel(data.cf, dest, flags);",
            "    INTEGER_DEQUE_ADAPTER.writeToParcel(data.cg, dest, flags);",
            "    INTEGER_QUEUE_ADAPTER.writeToParcel(data.ch, dest, flags);",
            "    TEST_ENUM_ENUM_ADAPTER.writeToParcel(data.ci, dest, flags);",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(source, testParcelable, testEnum))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void failIfPaperParcelClassIsAbstractTest() throws Exception {
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

  @Test public void failIfPaperParcelClassIsAnInterfaceTest() throws Exception {
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

  @Test public void failIfRegisterAdapterClassIsNotATypeAdapter() throws Exception {
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

  @Test public void failIfRegisterAdapterClassIsAnInterfaceTest() throws Exception {
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

  @Test public void failIfRegisterAdapterClassIsAbstractTest() throws Exception {
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

  @Test public void failIfFieldIsInaccessibleTest() throws Exception {
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

  @Test public void failIfFieldIsNotWritableTest() throws Exception {
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

  @Test public void failIfConstructorArgumentHasNonMatchingNameTest() throws Exception {
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

  @Test public void failIfConstructorArgumentHasMismatchedTypeTest() throws Exception {
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

  @Test public void singletonTest() throws Exception {
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

  @Test public void failIfTypeAdapterIsRaw() throws Exception {
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

  @Test public void failIfConstructorHasRawTypeParameter() throws Exception {
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

  @Test public void failIfThereAreNoVisibleConstructorsTest() throws Exception {
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

  @Test public void failIfGenericFieldTypeIsRaw() throws Exception {
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

  @Test public void basicExcludeTest() throws Exception {
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
            "import paperparcel.adapter.IntegerAdapter;",
            GeneratedLines.GENERATED_ANNOTATION,
            "final class PaperParcelTest {",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      Integer count = IntegerAdapter.INSTANCE.readFromParcel(in);",
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
            "    IntegerAdapter.INSTANCE.writeToParcel(data.count(), dest, flags);",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void usesSmallerConstructorWhenLargerConstructorCannotBeUsed() throws Exception {
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
            "import paperparcel.adapter.IntegerAdapter;",
            GeneratedLines.GENERATED_ANNOTATION,
            "final class PaperParcelTest {",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      Integer count = IntegerAdapter.INSTANCE.readFromParcel(in);",
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
            "    IntegerAdapter.INSTANCE.writeToParcel(data.count(), dest, flags);",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void prioritisesDirectAccessOverSetterAndGetterMethods() throws Exception {
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
            "import paperparcel.adapter.IntegerAdapter;",
            GeneratedLines.GENERATED_ANNOTATION,
            "final class PaperParcelTest {",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      Integer count = IntegerAdapter.INSTANCE.readFromParcel(in);",
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
            "    IntegerAdapter.INSTANCE.writeToParcel(data.count, dest, flags);",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void usesSetterAndGetterMethodsForPrivateFields() throws Exception {
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
            "import paperparcel.adapter.IntegerAdapter;",
            GeneratedLines.GENERATED_ANNOTATION,
            "final class PaperParcelTest {",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      Integer count = IntegerAdapter.INSTANCE.readFromParcel(in);",
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
            "    IntegerAdapter.INSTANCE.writeToParcel(data.count(), dest, flags);",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void handlesMixedConstructorDirectAccessAndSetterFields() throws Exception {
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
            "import paperparcel.adapter.IntegerAdapter;",
            GeneratedLines.GENERATED_ANNOTATION,
            "final class PaperParcelTest {",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      Integer count2 = IntegerAdapter.INSTANCE.readFromParcel(in);",
            "      Integer count1 = IntegerAdapter.INSTANCE.readFromParcel(in);",
            "      Integer count3 = IntegerAdapter.INSTANCE.readFromParcel(in);",
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
            "    IntegerAdapter.INSTANCE.writeToParcel(data.count2, dest, flags);",
            "    IntegerAdapter.INSTANCE.writeToParcel(data.count1(), dest, flags);",
            "    IntegerAdapter.INSTANCE.writeToParcel(data.count3(), dest, flags);",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void complexTypeAdapterTest() throws Exception {
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
            "import paperparcel.adapter.IntegerAdapter;",
            "import paperparcel.adapter.MapAdapter;",
            GeneratedLines.GENERATED_ANNOTATION,
            "final class PaperParcelTest {",
            "  private static final ReallySpecificTypeAdapter<Integer, Boolean> INTEGER_BOOLEAN_REALLY_SPECIFIC_TYPE_ADAPTER = new ReallySpecificTypeAdapter<Integer, Boolean>();",
            "  private static final MapAdapter<Integer, Integer> INTEGER_INTEGER_MAP_ADAPTER = new MapAdapter<Integer, Integer>(IntegerAdapter.INSTANCE, IntegerAdapter.INSTANCE);",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      Map<List<Integer>[], Map<Integer, Boolean>> field1 = INTEGER_BOOLEAN_REALLY_SPECIFIC_TYPE_ADAPTER.readFromParcel(in);",
            "      Map<Integer, Integer> field2 = INTEGER_INTEGER_MAP_ADAPTER.readFromParcel(in);",
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
            "    INTEGER_BOOLEAN_REALLY_SPECIFIC_TYPE_ADAPTER.writeToParcel(data.field1, dest, flags);",
            "    INTEGER_INTEGER_MAP_ADAPTER.writeToParcel(data.field2, dest, flags);",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(reallySpecificAdapter, source))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void genericArrayTypeAdapterTest() throws Exception {
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
            "  private static final GenericArrayAdapter<Integer> INTEGER_GENERIC_ARRAY_ADAPTER = new GenericArrayAdapter<Integer>();",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      Integer[] field = INTEGER_GENERIC_ARRAY_ADAPTER.readFromParcel(in);",
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
            "    INTEGER_GENERIC_ARRAY_ADAPTER.writeToParcel(data.field, dest, flags);",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(genericArrayAdapter, source))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void wildcardAdapterTest() throws Exception {
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
            "  private static final WildcardAdapter WILDCARD_ADAPTER = new WildcardAdapter();",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      List<? extends Integer> field = WILDCARD_ADAPTER.readFromParcel(in);",
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
            "    WILDCARD_ADAPTER.writeToParcel(data.field, dest, flags);",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(wildcardAdapter, source))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void genericWildcardAdapterTest() throws Exception {
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
            "  private static final WildcardAdapter<Integer> INTEGER_WILDCARD_ADAPTER = new WildcardAdapter<Integer>();",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      List<? extends Integer> field = INTEGER_WILDCARD_ADAPTER.readFromParcel(in);",
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
            "    INTEGER_WILDCARD_ADAPTER.writeToParcel(data.field, dest, flags);",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(wildcardAdapter, source))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void basicGenericPaperParcelTest() throws Exception {
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
            "import paperparcel.adapter.ParcelableAdapter;",
            GeneratedLines.GENERATED_ANNOTATION,
            "final class PaperParcelTest {",
            "  private static final ParcelableAdapter<Parcelable> PARCELABLE_PARCELABLE_ADAPTER = new ParcelableAdapter<Parcelable>();",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      Parcelable value = PARCELABLE_PARCELABLE_ADAPTER.readFromParcel(in);",
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
            "    PARCELABLE_PARCELABLE_ADAPTER.writeToParcel(data.value, dest, flags);",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void complexGenericPaperParcelTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "import java.util.HashMap;",
            "@PaperParcel",
            "public final class Test<K extends Parcelable, V extends String, T extends HashMap<K, V>> implements Parcelable {",
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
            "import java.util.HashMap;",
            "import javax.annotation.Generated;",
            "import paperparcel.adapter.HashMapAdapter;",
            "import paperparcel.adapter.ParcelableAdapter;",
            "import paperparcel.adapter.StringAdapter;",
            GeneratedLines.GENERATED_ANNOTATION,
            "final class PaperParcelTest {",
            "  private static final ParcelableAdapter<Parcelable> PARCELABLE_PARCELABLE_ADAPTER = new ParcelableAdapter<Parcelable>();",
            "  private static final HashMapAdapter<Parcelable, String> PARCELABLE_STRING_HASH_MAP_ADAPTER = new HashMapAdapter<Parcelable, String>(PARCELABLE_PARCELABLE_ADAPTER, StringAdapter.INSTANCE);",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      HashMap<Parcelable, String> value = PARCELABLE_STRING_HASH_MAP_ADAPTER.readFromParcel(in);",
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
            "    PARCELABLE_STRING_HASH_MAP_ADAPTER.writeToParcel(data.value, dest, flags);",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void complexExcludeWithModifiersTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "import java.lang.reflect.Modifier;",
            "@PaperParcel.Options(excludeFieldsWithModifiers = { Modifier.STATIC | Modifier.FINAL, Modifier.TRANSIENT })",
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
            "import paperparcel.adapter.LongAdapter;",
            GeneratedLines.GENERATED_ANNOTATION,
            "final class PaperParcelTest {",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      Long field2 = LongAdapter.INSTANCE.readFromParcel(in);",
            "      Long field4 = LongAdapter.INSTANCE.readFromParcel(in);",
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
            "    LongAdapter.INSTANCE.writeToParcel(data.field2, dest, flags);",
            "    LongAdapter.INSTANCE.writeToParcel(data.field4, dest, flags);",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void basicExcludeWithAnnotationTest() throws Exception {
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
            "@PaperParcel.Options(excludeFieldsWithAnnotations = Exclude.class)",
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
            "import paperparcel.adapter.IntegerAdapter;",
            GeneratedLines.GENERATED_ANNOTATION,
            "final class PaperParcelTest {",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      Integer value = IntegerAdapter.INSTANCE.readFromParcel(in);",
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
            "    IntegerAdapter.INSTANCE.writeToParcel(data.value, dest, flags);",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(source, excludeAnnotation))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void basicExcludeWithoutPackAnnotationTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "import paperparcel.Pack;",
            "@PaperParcel.Options(excludeFieldsWithoutPackAnnotation = true)",
            "@PaperParcel",
            "public final class Test implements Parcelable {",
            "  @Pack public int value;",
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
            "import paperparcel.adapter.IntegerAdapter;",
            GeneratedLines.GENERATED_ANNOTATION,
            "final class PaperParcelTest {",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      Integer value = IntegerAdapter.INSTANCE.readFromParcel(in);",
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
            "    IntegerAdapter.INSTANCE.writeToParcel(data.value, dest, flags);",
            "  }",
            "}"
        ));

    assertAbout(javaSource()).that(source)
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void inheritanceExcludeWithoutPackAnnotationTest() throws Exception {
    JavaFileObject source =
        JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
            "package test;",
            "import android.os.Parcel;",
            "import android.os.Parcelable;",
            "import paperparcel.PaperParcel;",
            "import paperparcel.Pack;",
            "@PaperParcel.Options(excludeFieldsWithoutPackAnnotation = true)",
            "@PaperParcel",
            "public final class Test extends BaseTest implements Parcelable {",
            "  @Pack public int value;",
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
            "import paperparcel.adapter.IntegerAdapter;",
            GeneratedLines.GENERATED_ANNOTATION,
            "final class PaperParcelTest {",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      Integer value = IntegerAdapter.INSTANCE.readFromParcel(in);",
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
            "    IntegerAdapter.INSTANCE.writeToParcel(data.value, dest, flags);",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(source, baseClass))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

  @Test public void inheritanceExcludeWithAnnotationTest() throws Exception {
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
            "@PaperParcel.Options(excludeFieldsWithAnnotations = Exclude.class)",
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
            "import paperparcel.adapter.IntegerAdapter;",
            GeneratedLines.GENERATED_ANNOTATION,
            "final class PaperParcelTest {",
            "  @NonNull",
            "  static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {",
            "    @Override",
            "    public Test createFromParcel(Parcel in) {",
            "      Integer value = IntegerAdapter.INSTANCE.readFromParcel(in);",
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
            "    IntegerAdapter.INSTANCE.writeToParcel(data.value, dest, flags);",
            "  }",
            "}"
        ));

    assertAbout(javaSources()).that(Arrays.asList(source, baseClass, excludeAnnotation))
        .processedWith(new PaperParcelProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expected);
  }

}
