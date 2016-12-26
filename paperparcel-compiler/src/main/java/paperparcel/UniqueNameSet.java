/*
 * Copyright (C) 2014 The Dagger Authors.
 * Modifications copyright (C) 2016 Bradley Campbell.
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

import com.google.common.collect.Sets;
import java.util.Set;
import javax.lang.model.SourceVersion;

/**
 * A collector for names to be used in the same namespace that should not conflict. This class
 * also ensures that the names are not Java keywords.
 */
final class UniqueNameSet {
  private final Set<String> uniqueNames = Sets.newLinkedHashSet();

  private final String separator;

  UniqueNameSet() {
    this("");
  }

  UniqueNameSet(String separator) {
    this.separator = separator;
  }

  /**
   * Generates a unique and valid name using {@code base}. If {@code base} has not yet been added,
   * and is not a Java keyword; it will be returned as-is, otherwise it will have a differentiator
   * appended.
   */
  String getUniqueName(CharSequence base) {
    String name = base.toString();
    for (int differentiator = 1; isInvalidName(name); differentiator++) {
      name = base.toString() + separator + differentiator;
    }
    return name;
  }

  private boolean isInvalidName(String name) {
    return !uniqueNames.add(name) || SourceVersion.isKeyword(name);
  }
}
