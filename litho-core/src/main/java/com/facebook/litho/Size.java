/*
 * Copyright 2014-present Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.facebook.litho;

import com.facebook.infer.annotation.ThreadConfined;

/** Public API for MeasureOutput. */
@ThreadConfined(ThreadConfined.ANY)
public class Size {

  public int width;
  public int height;

  public Size() {
    this.width = 0;
    this.height = 0;
  }

  public Size(int width, int height) {
    this.width = width;
    this.height = height;
  }
}
