package nz.bradcampbell.dataparcel.internal;

public class Strings {

  public static String capitalizeFirstCharacter(String s) {
    if (s == null || s.length() == 0) {
      return s;
    }
    return s.substring(0, 1).toUpperCase() + s.substring(1);
  }
}
