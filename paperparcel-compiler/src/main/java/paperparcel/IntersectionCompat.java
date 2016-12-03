/*
 * Copyright (C) 2014 Google, Inc.
 * Modifications copyright (C) 2016 Bradley Campbell.
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

package paperparcel;

import android.support.annotation.Nullable;
import com.google.common.base.Throwables;
import java.lang.reflect.Method;
import java.util.List;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

/**
 * Implementation modified from Google's auto-common MoreTypes.java.
 *
 * The representation of an intersection type, as in <T extends Number & Comparable<T>>, changed
 * between Java 7 and Java 8. In Java 7 it was modeled as a fake DeclaredType, and our logic
 * for DeclaredType does the right thing. In Java 8 it is modeled as a new type IntersectionType.
 * In order for our code to run on Java 7 (and Java 6) we can't even mention IntersectionType,
 * so we can't override visitIntersectionType(IntersectionType). Instead, we discover through
 * reflection whether IntersectionType exists, and if it does we extract the bounds of the
 * intersection and check them directly.
 */
final class IntersectionCompat {
  private static final Method GET_BOUNDS;

  static {
    Method m;
    try {
      Class<?> c = Class.forName("javax.lang.model.type.IntersectionType");
      m = c.getMethod("getBounds");
    } catch (Exception e) {
      m = null;
    }
    GET_BOUNDS = m;
  }

  static boolean isIntersectionType(TypeMirror t) {
    return t != null && t.getKind().name().equals("INTERSECTION");
  }

  @SuppressWarnings("unchecked")
  static List<? extends TypeMirror> getBounds(TypeMirror intersectionType) {
    List<? extends TypeMirror> bounds;
    try {
      bounds = (List<? extends TypeMirror>) GET_BOUNDS.invoke(intersectionType);
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
    return bounds;
  }

  static boolean isAssignableToIntersectionType(
      Types types,
      TypeMirror type,
      TypeMirror intersectionType,
      @Nullable String substituteTargetName,
      @Nullable TypeMirror substituteTargetType) {
    List<? extends TypeMirror> bounds = getBounds(intersectionType);
    boolean isAssignable = true;
    for (TypeMirror bound : bounds) {
      TypeMirror wildcardedUpperBound =
          Utils.substituteTypeVariables(types, bound, substituteTargetName, substituteTargetType);
      if (!types.isAssignable(type, wildcardedUpperBound)) {
        isAssignable = false;
        break;
      }
    }
    return isAssignable;
  }

  private IntersectionCompat() {
    throw new AssertionError(); // No instances.
  }
}
