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

import java.lang.reflect.Field;

public final class Utils {

  /** Reads a fields value via reflection. */
  @SuppressWarnings({ "unchecked", "UnusedParameters", "unused", "TryWithIdenticalCatches" })
  public static <T> T readField(
      Class<T> type, Class<?> enclosingClass, Object target, String field) {
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
  @SuppressWarnings({ "unchecked", "unused", "TryWithIdenticalCatches" })
  public static void writeField(
      Object value, Class<?> enclosingClass, Object target, String field) {
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

  private Utils() {
    throw new AssertionError("No instances.");
  }
}
