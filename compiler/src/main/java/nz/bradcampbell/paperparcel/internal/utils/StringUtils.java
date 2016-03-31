package nz.bradcampbell.paperparcel.internal.utils;

import java.util.Set;

public class StringUtils {

  public static String capitalizeFirstCharacter(String s) {
    if (s == null || s.length() == 0) {
      return s;
    }
    return s.substring(0, 1).toUpperCase() + s.substring(1);
  }

  public static String getUniqueName(String initial, Set<String> scopedNames) {
    String result = initial;
    int n = 1;
    while (scopedNames.contains(result)) {
      result += n++;
    }
    return result;
  }
}
