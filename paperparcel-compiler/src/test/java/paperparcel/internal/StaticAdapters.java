package paperparcel.internal;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.PersistableBundle;
import android.util.Size;
import android.util.SizeF;
import android.util.SparseBooleanArray;
import paperparcel.TypeAdapter;

public final class StaticAdapters {

  public static final TypeAdapter<Integer> INTEGER_ADAPTER = new TypeAdapter<Integer>() {
    @Override public Integer readFromParcel(Parcel source) {
      throw new RuntimeException("Stub!");
    }

    @Override public void writeToParcel(Integer value, Parcel dest, int flags) {
      throw new RuntimeException("Stub!");
    }
  };

  public static final TypeAdapter<Boolean> BOOLEAN_ADAPTER = new TypeAdapter<Boolean>() {
    @Override public Boolean readFromParcel(Parcel source) {
      throw new RuntimeException("Stub!");
    }

    @Override public void writeToParcel(Boolean value, Parcel dest, int flags) {
      throw new RuntimeException("Stub!");
    }
  };

  public static final TypeAdapter<Double> DOUBLE_ADAPTER = new TypeAdapter<Double>() {
    @Override public Double readFromParcel(Parcel source) {
      throw new RuntimeException("Stub!");
    }

    @Override public void writeToParcel(Double value, Parcel dest, int flags) {
      throw new RuntimeException("Stub!");
    }
  };

  public static final TypeAdapter<Float> FLOAT_ADAPTER = new TypeAdapter<Float>() {
    @Override public Float readFromParcel(Parcel source) {
      throw new RuntimeException("Stub!");
    }

    @Override public void writeToParcel(Float value, Parcel dest, int flags) {
      throw new RuntimeException("Stub!");
    }
  };

  public static final TypeAdapter<Long> LONG_ADAPTER = new TypeAdapter<Long>() {
    @Override public Long readFromParcel(Parcel source) {
      throw new RuntimeException("Stub!");
    }

    @Override public void writeToParcel(Long value, Parcel dest, int flags) {
      throw new RuntimeException("Stub!");
    }
  };

  public static final TypeAdapter<Byte> BYTE_ADAPTER = new TypeAdapter<Byte>() {
    @Override public Byte readFromParcel(Parcel source) {
      throw new RuntimeException("Stub!");
    }

    @Override public void writeToParcel(Byte value, Parcel dest, int flags) {
      throw new RuntimeException("Stub!");
    }
  };

  public static final TypeAdapter<Character> CHARACTER_ADAPTER = new TypeAdapter<Character>() {
    @Override public Character readFromParcel(Parcel source) {
      throw new RuntimeException("Stub!");
    }

    @Override public void writeToParcel(Character value, Parcel dest, int flags) {
      throw new RuntimeException("Stub!");
    }
  };

  public static final TypeAdapter<Short> SHORT_ADAPTER = new TypeAdapter<Short>() {
    @Override public Short readFromParcel(Parcel source) {
      throw new RuntimeException("Stub!");
    }

    @Override public void writeToParcel(Short value, Parcel dest, int flags) {
      throw new RuntimeException("Stub!");
    }
  };

  public static final TypeAdapter<boolean[]> BOOLEAN_ARRAY_ADAPTER =
      new TypeAdapter<boolean[]>() {
        @Override public boolean[] readFromParcel(Parcel source) {
          throw new RuntimeException("Stub!");
        }

        @Override public void writeToParcel(boolean[] value, Parcel dest, int flags) {
          throw new RuntimeException("Stub!");
        }
      };


  public static final TypeAdapter<Bundle> BUNDLE_ADAPTER = new TypeAdapter<Bundle>() {
    @Override public Bundle readFromParcel(Parcel source) {
      throw new RuntimeException("Stub!");
    }

    @Override public void writeToParcel(Bundle value, Parcel dest, int flags) {
      throw new RuntimeException("Stub!");
    }
  };

  public static final TypeAdapter<byte[]> BYTE_ARRAY_ADAPTER = new TypeAdapter<byte[]>() {
    @Override public byte[] readFromParcel(Parcel source) {
      throw new RuntimeException("Stub!");
    }

    @Override public void writeToParcel(byte[] value, Parcel dest, int flags) {
      throw new RuntimeException("Stub!");
    }
  };

  public static final TypeAdapter<char[]> CHAR_ARRAY_ADAPTER = new TypeAdapter<char[]>() {
    @Override public char[] readFromParcel(Parcel source) {
      throw new RuntimeException("Stub!");
    }

    @Override public void writeToParcel(char[] value, Parcel dest, int flags) {
      throw new RuntimeException("Stub!");
    }
  };

  public static final TypeAdapter<CharSequence> CHAR_SEQUENCE_ADAPTER =
      new TypeAdapter<CharSequence>() {
        @Override public CharSequence readFromParcel(Parcel source) {
          throw new RuntimeException("Stub!");
        }

        @Override public void writeToParcel(CharSequence value, Parcel dest, int flags) {
          throw new RuntimeException("Stub!");
        }
      };

  public static final TypeAdapter<double[]> DOUBLE_ARRAY_ADAPTER =
      new TypeAdapter<double[]>() {
        @Override public double[] readFromParcel(Parcel source) {
          throw new RuntimeException("Stub!");
        }

        @Override public void writeToParcel(double[] value, Parcel dest, int flags) {
          throw new RuntimeException("Stub!");
        }
      };

  public static final TypeAdapter<float[]> FLOAT_ARRAY_ADAPTER = new TypeAdapter<float[]>() {
    @Override public float[] readFromParcel(Parcel source) {
      throw new RuntimeException("Stub!");
    }

    @Override public void writeToParcel(float[] value, Parcel dest, int flags) {
      throw new RuntimeException("Stub!");
    }
  };

  public static final TypeAdapter<IBinder> IBINDER_ADAPTER = new TypeAdapter<IBinder>() {
    @Override public IBinder readFromParcel(Parcel source) {
      throw new RuntimeException("Stub!");
    }

    @Override public void writeToParcel(IBinder value, Parcel dest, int flags) {
      throw new RuntimeException("Stub!");
    }
  };

  public static final TypeAdapter<int[]> INT_ARRAY_ADAPTER = new TypeAdapter<int[]>() {
    @Override public int[] readFromParcel(Parcel source) {
      throw new RuntimeException("Stub!");
    }

    @Override public void writeToParcel(int[] value, Parcel dest, int flags) {
      throw new RuntimeException("Stub!");
    }
  };

  public static final TypeAdapter<long[]> LONG_ARRAY_ADAPTER = new TypeAdapter<long[]>() {
    @Override public long[] readFromParcel(Parcel source) {
      throw new RuntimeException("Stub!");
    }

    @Override public void writeToParcel(long[] value, Parcel dest, int flags) {
      throw new RuntimeException("Stub!");
    }
  };

  public static final TypeAdapter<PersistableBundle> PERSISTABLE_BUNDLE_ADAPTER =
      new TypeAdapter<PersistableBundle>() {
        @Override public PersistableBundle readFromParcel(Parcel source) {
          throw new RuntimeException("Stub!");
        }

        @Override public void writeToParcel(PersistableBundle value, Parcel dest, int flags) {
          throw new RuntimeException("Stub!");
        }
      };

  public static final TypeAdapter<short[]> SHORT_ARRAY_ADAPTER = new TypeAdapter<short[]>() {
    @Override public short[] readFromParcel(Parcel source) {
      throw new RuntimeException("Stub!");
    }

    @Override public void writeToParcel(short[] value, Parcel dest, int flags) {
      throw new RuntimeException("Stub!");
    }
  };

  public static final TypeAdapter<Size> SIZE_ADAPTER = new TypeAdapter<Size>() {
    @Override public Size readFromParcel(Parcel source) {
      throw new RuntimeException("Stub!");
    }

    @Override public void writeToParcel(Size value, Parcel dest, int flags) {
      throw new RuntimeException("Stub!");
    }
  };

  public static final TypeAdapter<SizeF> SIZE_F_ADAPTER = new TypeAdapter<SizeF>() {
    @Override public SizeF readFromParcel(Parcel source) {
      throw new RuntimeException("Stub!");
    }

    @Override public void writeToParcel(SizeF value, Parcel dest, int flags) {
      throw new RuntimeException("Stub!");
    }
  };

  public static TypeAdapter<SparseBooleanArray> SPARSE_BOOLEAN_ARRAY_ADAPTER =
      new TypeAdapter<SparseBooleanArray>() {
        @Override public SparseBooleanArray readFromParcel(Parcel source) {
          throw new RuntimeException("Stub!");
        }

        @Override public void writeToParcel(SparseBooleanArray value, Parcel dest, int flags) {
          throw new RuntimeException("Stub!");
        }
      };

  public static final TypeAdapter<String> STRING_ADAPTER = new TypeAdapter<String>() {
    @Override public String readFromParcel(Parcel source) {
      throw new RuntimeException("Stub!");
    }

    @Override public void writeToParcel(String value, Parcel dest, int flags) {
      throw new RuntimeException("Stub!");
    }
  };

  private StaticAdapters() {
    throw new RuntimeException("Stub!");
  }
}
