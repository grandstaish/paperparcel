package nz.bradcampbell.realmexample.adapter;

import io.realm.RealmList;
import io.realm.RealmModel;
import paperparcel.RegisterAdapter;
import paperparcel.TypeAdapter;
import paperparcel.adapter.AbstractMutableCollectionAdapter;

@RegisterAdapter
public class RealmListTypeAdapter<T extends RealmModel> extends
    AbstractMutableCollectionAdapter<RealmList<T>, T> {

  public RealmListTypeAdapter(TypeAdapter<T> itemAdapter) {
    super(itemAdapter);
  }

  @Override protected RealmList<T> newCollection(int size) {
    return new RealmList<>();
  }
}
