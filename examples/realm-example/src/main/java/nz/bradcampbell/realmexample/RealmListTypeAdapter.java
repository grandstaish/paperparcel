package nz.bradcampbell.realmexample;

import android.os.Parcel;
import android.support.annotation.NonNull;
import io.realm.RealmList;
import io.realm.RealmModel;
import paperparcel.AbstractAdapter;
import paperparcel.RegisterAdapter;
import paperparcel.TypeAdapter;

@RegisterAdapter
class RealmListTypeAdapter<T extends RealmModel> extends AbstractAdapter<RealmList<T>> {
  private final TypeAdapter<T> itemAdapter;

  RealmListTypeAdapter(TypeAdapter<T> itemAdapter) {
    this.itemAdapter = itemAdapter;
  }

  @SuppressWarnings("ConstantConditions") // Realm items can't be null
  @NonNull @Override protected RealmList<T> read(@NonNull Parcel source) {
    int size = source.readInt();
    RealmList<T> list = new RealmList<>();
    for (int i = 0; i < size; i++) {
      list.add(itemAdapter.readFromParcel(source));
    }
    return list;
  }

  @Override protected void write(@NonNull RealmList<T> value, @NonNull Parcel dest, int flags) {
    dest.writeInt(value.size());
    for (int i = 0; i < value.size(); i++) {
      itemAdapter.writeToParcel(value.get(i), dest, flags);
    }
  }
}
