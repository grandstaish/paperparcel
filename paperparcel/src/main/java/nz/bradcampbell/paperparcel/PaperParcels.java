package nz.bradcampbell.paperparcel;

import android.os.Parcelable;

import java.lang.reflect.Field;
import java.util.Map;

public final class PaperParcels {
  static final Map<Class, Delegator> FROM_ORIGINAL;
  static final Map<Class, Delegator> FROM_PARCELABLE;

  static {
    //noinspection TryWithIdenticalCatches
    try {
      Class clazz = Class.forName("nz.bradcampbell.paperparcel.PaperParcelMapping");
      FROM_ORIGINAL = getFieldValue(clazz, "FROM_ORIGINAL");
      FROM_PARCELABLE = getFieldValue(clazz, "FROM_PARCELABLE");
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  public static <ORIG, PARCELABLE extends Parcelable> PARCELABLE wrap(ORIG originalObj) {
    Class<?> type = originalObj.getClass();
    //noinspection unchecked
    Delegator<ORIG, PARCELABLE> delegator = FROM_ORIGINAL.get(type);
    return delegator.wrap(originalObj);
  }

  public static <ORIG, PARCELABLE extends Parcelable> ORIG unwrap(PARCELABLE parcelableObj) {
    Class<?> type = parcelableObj.getClass();
    //noinspection unchecked
    Delegator<ORIG, PARCELABLE> delegator = FROM_PARCELABLE.get(type);
    return delegator.unwrap(parcelableObj);
  }

  interface Delegator<ORIG, PARCELABLE extends Parcelable> {
    ORIG unwrap(PARCELABLE parcelableObj);

    PARCELABLE wrap(ORIG originalObj);
  }

  private static Map<Class, Delegator> getFieldValue(Class clazz, String fieldName)
      throws NoSuchFieldException, IllegalAccessException {
    Field field = clazz.getDeclaredField(fieldName);
    field.setAccessible(true);
    //noinspection unchecked
    return (Map<Class, Delegator>) field.get(null);
  }
}
