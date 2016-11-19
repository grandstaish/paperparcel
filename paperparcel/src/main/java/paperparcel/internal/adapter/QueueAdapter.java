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

package paperparcel.internal.adapter;

import java.util.ArrayDeque;
import java.util.Queue;
import paperparcel.TypeAdapter;

/**
 * Default {@link TypeAdapter} for {@link Queue} types
 *
 * <p>The {@link Queue} used by default is {@link ArrayDeque}
 */
public final class QueueAdapter<T> extends AbstractMutableCollectionAdapter<Queue<T>, T> {
  public QueueAdapter(TypeAdapter<T> itemAdapter) {
    super(itemAdapter);
  }

  @Override protected Queue<T> newCollection(int size) {
    return new ArrayDeque<>(size);
  }
}
