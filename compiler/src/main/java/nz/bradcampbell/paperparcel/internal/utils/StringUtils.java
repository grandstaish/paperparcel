package nz.bradcampbell.paperparcel.internal.utils;

public class StringUtils {

  public static String capitalizeFirstCharacter(String s) {
    if (s == null || s.length() == 0) {
      return s;
    }
    return s.substring(0, 1).toUpperCase() + s.substring(1);
  }
}
