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

package paperparcel;

import com.squareup.javapoet.CodeBlock;
import java.util.Iterator;

/** Utility methods for {@link CodeBlock}s */
final class CodeBlocks {

  /** Joins multiple {@link CodeBlock}s together with a given delimiter */
  @SuppressWarnings("SameParameterValue")
  static CodeBlock join(Iterable<CodeBlock> codeBlocks, String delimiter) {
    CodeBlock.Builder builder = CodeBlock.builder();
    Iterator<CodeBlock> iterator = codeBlocks.iterator();
    while (iterator.hasNext()) {
      builder.add(iterator.next());
      if (iterator.hasNext()) {
        builder.add(delimiter);
      }
    }
    return builder.build();
  }

  private CodeBlocks() {}
}
