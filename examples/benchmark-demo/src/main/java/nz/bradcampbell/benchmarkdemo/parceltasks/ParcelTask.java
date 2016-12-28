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

package nz.bradcampbell.benchmarkdemo.parceltasks;

import android.os.AsyncTask;
import android.os.Parcel;
import java.util.concurrent.TimeUnit;

public abstract class ParcelTask<T> extends AsyncTask<Void, Void, ParcelResult> {
  public interface ParcelListener {
    void onComplete(ParcelTask parcelTask, ParcelResult parcelResult);
  }

  private final ParcelListener parcelListener;
  private final T response;

  ParcelTask(ParcelListener parcelListener, T response) {
    this.parcelListener = parcelListener;
    this.response = response;
  }

  @Override
  protected ParcelResult doInBackground(Void... params) {
    System.gc();
    Parcel parcel = Parcel.obtain();
    long startTime = System.nanoTime();
    int objectCount = writeThenRead(response, parcel);
    long endTime = System.nanoTime();
    long duration = TimeUnit.NANOSECONDS.toMicros(endTime - startTime);
    parcel.recycle();
    return new ParcelResult(duration, objectCount);
  }

  @Override
  protected void onPostExecute(ParcelResult parcelResult) {
    parcelListener.onComplete(this, parcelResult);
  }

  protected abstract int writeThenRead(T response, Parcel parcel);
}
