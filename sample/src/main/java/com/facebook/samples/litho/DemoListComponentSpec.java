/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.facebook.samples.litho;

import androidx.annotation.Nullable;
import com.facebook.litho.Component;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.annotations.FromEvent;
import com.facebook.litho.annotations.LayoutSpec;
import com.facebook.litho.annotations.OnCreateLayout;
import com.facebook.litho.annotations.OnEvent;
import com.facebook.litho.annotations.Prop;
import com.facebook.litho.sections.SectionContext;
import com.facebook.litho.sections.common.DataDiffSection;
import com.facebook.litho.sections.common.OnCheckIsSameContentEvent;
import com.facebook.litho.sections.common.OnCheckIsSameItemEvent;
import com.facebook.litho.sections.common.RenderEvent;
import com.facebook.litho.sections.widget.RecyclerCollectionComponent;
import com.facebook.litho.widget.ComponentRenderInfo;
import com.facebook.litho.widget.RenderInfo;
import java.util.Arrays;
import java.util.List;

@LayoutSpec
class DemoListComponentSpec {

  private static final String MAIN_SCREEN = "main_screen";

  @OnCreateLayout
  static Component onCreateLayout(
      ComponentContext c, @Prop List<DemoListActivity.DemoListDataModel> dataModels) {
    return RecyclerCollectionComponent.create(c)
        .section(
            DataDiffSection.<DemoListActivity.DemoListDataModel>create(new SectionContext(c))
                .data(dataModels)
                .renderEventHandler(DemoListComponent.onRender(c))
                .onCheckIsSameItemEventHandler(DemoListComponent.isSameItem(c))
                .onCheckIsSameContentEventHandler(DemoListComponent.isSameContent(c))
                .build())
        .disablePTR(true)
        .testKey(MAIN_SCREEN)
        .build();
  }

  @OnEvent(RenderEvent.class)
  static RenderInfo onRender(
      ComponentContext c,
      @Prop @Nullable int[] parentIndices,
      @FromEvent DemoListActivity.DemoListDataModel model,
      @FromEvent int index) {
    return ComponentRenderInfo.create()
        .component(
            DemoListItemComponent.create(c)
                .model(model)
                .currentIndices(getUpdatedIndices(parentIndices, index))
                .build())
        .build();
  }

  static int[] getUpdatedIndices(@Nullable int[] parentIndices, int currentIndex) {
    if (parentIndices == null) {
      return new int[] {currentIndex};
    }
    final int[] updatedIndices = Arrays.copyOf(parentIndices, parentIndices.length + 1);
    updatedIndices[parentIndices.length] = currentIndex;
    return updatedIndices;
  }

  /**
   * Called during DataDiffSection's diffing to determine if two objects represent the same item.
   * See {@link androidx.recyclerview.widget.DiffUtil.Callback#areItemsTheSame(int, int)} for more
   * info.
   *
   * @return true if the two objects in the event represent the same item.
   */
  @OnEvent(OnCheckIsSameItemEvent.class)
  static boolean isSameItem(
      ComponentContext c,
      @FromEvent DemoListActivity.DemoListDataModel previousItem,
      @FromEvent DemoListActivity.DemoListDataModel nextItem) {
    return previousItem == nextItem;
  }

  /**
   * Called during DataDiffSection's diffing to determine if two objects contain the same data. This
   * is used to detect of contents of an item have changed. See {@link
   * androidx.recyclerview.widget.DiffUtil.Callback#areContentsTheSame(int, int)} for more info.
   *
   * @return true if the two objects contain the same data.
   */
  @OnEvent(OnCheckIsSameContentEvent.class)
  static boolean isSameContent(
      ComponentContext c,
      @FromEvent DemoListActivity.DemoListDataModel previousItem,
      @FromEvent DemoListActivity.DemoListDataModel nextItem) {
    // We're only displaying the name so checking if that's equal here is enough for our use case.
    return previousItem == null
        ? nextItem == null
        : previousItem.name == null
            ? nextItem.name == null
            : nextItem.name.equals(previousItem.name);
  }
}
