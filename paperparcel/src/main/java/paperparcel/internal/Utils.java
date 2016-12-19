/*
 * Copyright (C) 2016 Bradley Campbell.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package paperparcel.internal;

import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import paperparcel.TypeAdapter;

@SuppressWarnings({ "WeakerAccess", "unused" }) // Used by generated code
public final class Utils {

  /** Reads a fields value via reflection. */
  @SuppressWarnings({ "unchecked", "UnusedParameters", "TryWithIdenticalCatches" })
  public static <T> T readField(
      @NonNull Class<T> type, @NonNull Class<?> enclosingClass,
      @NonNull Object target, @NonNull String field) {
    try {
      Field f = enclosingClass.getDeclaredField(field);
      f.setAccessible(true);
      return (T) f.get(target);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  /** Writes a value to a field via reflection. */
  @SuppressWarnings({ "unchecked", "TryWithIdenticalCatches" })
  public static void writeField(
      @NonNull Object value, @NonNull Class<?> enclosingClass,
      @NonNull Object target, @NonNull String field) {
    try {
      Field f = enclosingClass.getDeclaredField(field);
      f.setAccessible(true);
      f.set(target, value);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  /** Constructs an instance of {@code type} via reflection. */
  @SuppressWarnings({ "unchecked", "TryWithIdenticalCatches" })
  public static <T> T init(
      @NonNull Class<T> type, @NonNull Class[] argClasses, @NonNull Object[] args) {
    try {
      Constructor<T> constructor = type.getConstructor(argClasses);
      constructor.setAccessible(true);
      return constructor.newInstance(args);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    } catch (InstantiationException e) {
      throw new RuntimeException(e);
    } catch (InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Returns a type adapter equal to this type adapter, but with support for reading and writing
   * {@code null} values.
   */
  public static <T> TypeAdapter<T> nullSafeClone(@NonNull final TypeAdapter<T> delegate) {
    return new TypeAdapter<T>() {
      @Nullable @Override public T readFromParcel(@NonNull Parcel source) {
        return readNullable(source, delegate);
      }

      @Override public void writeToParcel(@Nullable T value, @NonNull Parcel dest, int flags) {
        writeNullable(value, dest, flags, delegate);
      }
    };
  }

  /**
   * Reads a {@code T} instance from {@code source} using {@code adapter}. This method can handle
   * {@code null} values. It should be used for reading objects that were written using
   * {@link #writeNullable(Object, Parcel, int, TypeAdapter)}.
   */
  @Nullable public static <T> T readNullable(
      @NonNull Parcel source, @NonNull TypeAdapter<T> adapter) {
    T value = null;
    if (source.readInt() == 1) {
      value = adapter.readFromParcel(source);
    }
    return value;
  }

  /**
   * Writes a {@code T} instance to {@code dest} using {@code adapter}. This method can handle
   * {@code null} values. When reading this type out of the parcel later, you should use
   * {@link #readNullable(Parcel, TypeAdapter)}.
   */
  public static <T> void writeNullable(
      @Nullable T value, @NonNull Parcel dest, int flags, @NonNull TypeAdapter<T> adapter) {
    if (value == null) {
      dest.writeInt(0);
    } else {
      dest.writeInt(1);
      adapter.writeToParcel(value, dest, flags);
    }
  }

  private Utils() {
    throw new AssertionError("No instances.");
  }
}
