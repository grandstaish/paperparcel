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

package paperparcel;

/** Utility methods for Strings */
final class Strings {

  /**
   * Capitalizes the first character of {@code s}. If {@code s} is {@code null} or empty,
   * {@code s} is returned unmodified
   */
  static String capitalizeAsciiOnly(String s) {
    if (s == null || s.isEmpty()) {
      return s;
    }
    char c = s.charAt(0);
    if (isLowerCaseAsciiOnly(c)) {
      return Character.toUpperCase(c) + s.substring(1);
    }
    return s;
  }

  /**
   * "fooBar" -> "FOOBar"
   * "FooBar" -> "FOOBar"
   * "foo" -> "FOO"
   */
  static String capitalizeFirstWordAsciiOnly(String s) {
    if (s == null || s.isEmpty()) {
      return s;
    }
    int secondWordStart = s.length();
    for (int i = 1; i < s.length(); i++) {
      if (!isLowerCaseAsciiOnly(s.charAt(i))) {
        secondWordStart = i;
        break;
      }
    }
    return toUpperCaseAsciiOnly(s.substring(0, secondWordStart)) + s.substring(secondWordStart);
  }

  private static boolean isLowerCaseAsciiOnly(char c) {
    return c >= 'a' && c <= 'z';
  }

  private static String toUpperCaseAsciiOnly(String s) {
    StringBuilder builder = new StringBuilder(s.length());
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      builder.append(isLowerCaseAsciiOnly(c) ? Character.toUpperCase(c) : c);
    }
    return builder.toString();
  }
}
