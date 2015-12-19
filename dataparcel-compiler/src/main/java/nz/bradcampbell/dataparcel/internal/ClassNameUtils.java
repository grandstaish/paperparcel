package nz.bradcampbell.dataparcel.internal;

public class ClassNameUtils {
  public static String getSimpleName(String canonicalName) {
    if (canonicalName == null) return null;
    int index = canonicalName.lastIndexOf(".");
    if (index != -1) {
      return canonicalName.substring(index + 1);
    }
    return canonicalName;
  }
}
