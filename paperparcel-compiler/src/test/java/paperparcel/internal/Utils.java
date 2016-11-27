package paperparcel.internal;

import android.os.Parcel;
import paperparcel.TypeAdapter;

public final class Utils {

  public static <T> T readField(
      Class<T> type, Class<?> enclosingClass, Object target, String field) {
    throw new RuntimeException("Stub!");
  }

  public static void writeField(
      Object value, Class<?> enclosingClass, Object target, String field) {
    throw new RuntimeException("Stub!");
  }

  public static <T> T init(Class<T> type, Class[] argClasses, Object[] args) {
    throw new RuntimeException("Stub!");
  }

  public static <T> TypeAdapter<T> nullSafeClone(TypeAdapter<T> delegate) {
    throw new RuntimeException("Stub!");
  }

  public static <T> T readNullable(Parcel source, TypeAdapter<T> adapter) {
    throw new RuntimeException("Stub!");
  }

  public static <T> void writeNullable(T value, Parcel dest, int flags, TypeAdapter<T> adapter) {
    throw new RuntimeException("Stub!");
  }

  private Utils() {
    throw new RuntimeException("Stub!");
  }
}
