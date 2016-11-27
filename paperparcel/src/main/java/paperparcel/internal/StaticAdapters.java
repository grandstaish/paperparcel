package paperparcel.internal;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Size;
import android.util.SizeF;
import android.util.SparseBooleanArray;
import paperparcel.TypeAdapter;

@SuppressWarnings({ "WeakerAccess", "unused" }) // Used by generated code
public final class StaticAdapters {

  public static final TypeAdapter<Integer> INTEGER_ADAPTER = new TypeAdapter<Integer>() {
    @NonNull @Override public Integer readFromParcel(@NonNull Parcel source) {
      return source.readInt();
    }

    @Override public void writeToParcel(@NonNull Integer value, @NonNull Parcel dest, int flags) {
      dest.writeInt(value);
    }
  };

  public static final TypeAdapter<Boolean> BOOLEAN_ADAPTER = new TypeAdapter<Boolean>() {
    @NonNull @Override public Boolean readFromParcel(@NonNull Parcel source) {
      return source.readInt() == 1;
    }

    @Override public void writeToParcel(@NonNull Boolean value, @NonNull Parcel dest, int flags) {
      dest.writeInt(value ? 1 : 0);
    }
  };

  public static final TypeAdapter<Double> DOUBLE_ADAPTER = new TypeAdapter<Double>() {
    @NonNull @Override public Double readFromParcel(@NonNull Parcel source) {
      return source.readDouble();
    }

    @Override public void writeToParcel(@NonNull Double value, @NonNull Parcel dest, int flags) {
      dest.writeDouble(value);
    }
  };

  public static final TypeAdapter<Float> FLOAT_ADAPTER = new TypeAdapter<Float>() {
    @NonNull @Override public Float readFromParcel(@NonNull Parcel source) {
      return source.readFloat();
    }

    @Override public void writeToParcel(@NonNull Float value, @NonNull Parcel dest, int flags) {
      dest.writeFloat(value);
    }
  };

  public static final TypeAdapter<Long> LONG_ADAPTER = new TypeAdapter<Long>() {
    @NonNull @Override public Long readFromParcel(@NonNull Parcel source) {
      return source.readLong();
    }

    @Override public void writeToParcel(@NonNull Long value, @NonNull Parcel dest, int flags) {
      dest.writeLong(value);
    }
  };

  public static final TypeAdapter<Byte> BYTE_ADAPTER = new TypeAdapter<Byte>() {
    @NonNull @Override public Byte readFromParcel(@NonNull Parcel source) {
      return source.readByte();
    }

    @Override public void writeToParcel(@NonNull Byte value, @NonNull Parcel dest, int flags) {
      dest.writeByte(value);
    }
  };

  public static final TypeAdapter<Character> CHARACTER_ADAPTER = new TypeAdapter<Character>() {
    @NonNull @Override public Character readFromParcel(@NonNull Parcel source) {
      return (char) source.readInt();
    }

    @Override public void writeToParcel(@NonNull Character value, @NonNull Parcel dest, int flags) {
      dest.writeInt(value);
    }
  };

  public static final TypeAdapter<Short> SHORT_ADAPTER = new TypeAdapter<Short>() {
    @NonNull @Override public Short readFromParcel(@NonNull Parcel source) {
      return (short) source.readInt();
    }

    @Override public void writeToParcel(@NonNull Short value, @NonNull Parcel dest, int flags) {
      dest.writeInt(value.intValue());
    }
  };

  public static final TypeAdapter<boolean[]> BOOLEAN_ARRAY_ADAPTER =
      new TypeAdapter<boolean[]>() {
        @Nullable @Override public boolean[] readFromParcel(@NonNull Parcel source) {
          return source.createBooleanArray();
        }

        @Override public void writeToParcel(@Nullable boolean[] value, @NonNull Parcel dest, int flags) {
          dest.writeBooleanArray(value);
        }
      };

  public static final TypeAdapter<Bundle> BUNDLE_ADAPTER = new TypeAdapter<Bundle>() {
    @Nullable @Override public Bundle readFromParcel(@NonNull Parcel source) {
      return source.readBundle(getClass().getClassLoader());
    }

    @Override public void writeToParcel(@Nullable Bundle value, @NonNull Parcel dest, int flags) {
      dest.writeBundle(value);
    }
  };

  public static final TypeAdapter<byte[]> BYTE_ARRAY_ADAPTER = new TypeAdapter<byte[]>() {
    @Nullable @Override public byte[] readFromParcel(@NonNull Parcel source) {
      return source.createByteArray();
    }

    @Override public void writeToParcel(@Nullable byte[] value, @NonNull Parcel dest, int flags) {
      dest.writeByteArray(value);
    }
  };

  public static final TypeAdapter<char[]> CHAR_ARRAY_ADAPTER = new TypeAdapter<char[]>() {
    @Nullable @Override public char[] readFromParcel(@NonNull Parcel source) {
      return source.createCharArray();
    }

    @Override public void writeToParcel(@Nullable char[] value, @NonNull Parcel dest, int flags) {
      dest.writeCharArray(value);
    }
  };

  public static final TypeAdapter<CharSequence> CHAR_SEQUENCE_ADAPTER =
      new TypeAdapter<CharSequence>() {
        @Nullable @Override public CharSequence readFromParcel(@NonNull Parcel source) {
          return TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(source);
        }

        @Override public void writeToParcel(@Nullable CharSequence value, @NonNull Parcel dest, int flags) {
          TextUtils.writeToParcel(value, dest, flags);
        }
      };

  public static final TypeAdapter<double[]> DOUBLE_ARRAY_ADAPTER =
      new TypeAdapter<double[]>() {
        @Nullable @Override public double[] readFromParcel(@NonNull Parcel source) {
          return source.createDoubleArray();
        }

        @Override public void writeToParcel(@Nullable double[] value, @NonNull Parcel dest, int flags) {
          dest.writeDoubleArray(value);
        }
      };

  public static final TypeAdapter<float[]> FLOAT_ARRAY_ADAPTER = new TypeAdapter<float[]>() {
    @Nullable @Override public float[] readFromParcel(@NonNull Parcel source) {
      return source.createFloatArray();
    }

    @Override public void writeToParcel(@Nullable float[] value, @NonNull Parcel dest, int flags) {
      dest.writeFloatArray(value);
    }
  };

  public static final TypeAdapter<IBinder> IBINDER_ADAPTER = new TypeAdapter<IBinder>() {
    @Nullable @Override public IBinder readFromParcel(@NonNull Parcel source) {
      return source.readStrongBinder();
    }

    @Override public void writeToParcel(@Nullable IBinder value, @NonNull Parcel dest, int flags) {
      dest.writeStrongBinder(value);
    }
  };

  public static final TypeAdapter<int[]> INT_ARRAY_ADAPTER = new TypeAdapter<int[]>() {
    @Nullable @Override public int[] readFromParcel(@NonNull Parcel source) {
      return source.createIntArray();
    }

    @Override public void writeToParcel(@Nullable int[] value, @NonNull Parcel dest, int flags) {
      dest.writeIntArray(value);
    }
  };

  public static final TypeAdapter<long[]> LONG_ARRAY_ADAPTER = new TypeAdapter<long[]>() {
    @Nullable @Override public long[] readFromParcel(@NonNull Parcel source) {
      return source.createLongArray();
    }

    @Override public void writeToParcel(@Nullable long[] value, @NonNull Parcel dest, int flags) {
      dest.writeLongArray(value);
    }
  };

  public static final TypeAdapter<PersistableBundle> PERSISTABLE_BUNDLE_ADAPTER =
      new TypeAdapter<PersistableBundle>() {
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Nullable @Override public PersistableBundle readFromParcel(@NonNull Parcel source) {
          return source.readPersistableBundle(getClass().getClassLoader());
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override public void writeToParcel(@Nullable PersistableBundle value, @NonNull Parcel dest, int flags) {
          dest.writePersistableBundle(value);
        }
      };

  public static final TypeAdapter<short[]> SHORT_ARRAY_ADAPTER = new TypeAdapter<short[]>() {
    @NonNull @Override public short[] readFromParcel(@NonNull Parcel source) {
      int size = source.readInt();
      short[] value = new short[size];
      for (int i = 0; i < size; i++) {
        value[i] = (short) source.readInt();
      }
      return value;
    }

    @Override public void writeToParcel(@NonNull short[] value, @NonNull Parcel dest, int flags) {
      dest.writeInt(value.length);
      for (short s : value) {
        dest.writeInt((int) s);
      }
    }
  };

  public static final TypeAdapter<Size> SIZE_ADAPTER = new TypeAdapter<Size>() {
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @NonNull @Override public Size readFromParcel(@NonNull Parcel source) {
      return source.readSize();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override public void writeToParcel(@NonNull Size value, @NonNull Parcel dest, int flags) {
      dest.writeSize(value);
    }
  };

  public static final TypeAdapter<SizeF> SIZE_F_ADAPTER = new TypeAdapter<SizeF>() {
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @NonNull @Override public SizeF readFromParcel(@NonNull Parcel source) {
      return source.readSizeF();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override public void writeToParcel(@NonNull SizeF value, @NonNull Parcel dest, int flags) {
      dest.writeSizeF(value);
    }
  };

  public static TypeAdapter<SparseBooleanArray> SPARSE_BOOLEAN_ARRAY_ADAPTER =
      new TypeAdapter<SparseBooleanArray>() {
        @Nullable @Override public SparseBooleanArray readFromParcel(@NonNull Parcel source) {
          return source.readSparseBooleanArray();
        }

        @Override
        public void writeToParcel(@Nullable SparseBooleanArray value, @NonNull Parcel dest, int flags) {
          dest.writeSparseBooleanArray(value);
        }
      };

  public static final TypeAdapter<String> STRING_ADAPTER = new TypeAdapter<String>() {
    @Nullable @Override public String readFromParcel(@NonNull Parcel source) {
      return source.readString();
    }

    @Override public void writeToParcel(@Nullable String value, @NonNull Parcel dest, int flags) {
      dest.writeString(value);
    }
  };

  private StaticAdapters() {
    throw new AssertionError("No instances.");
  }
}
