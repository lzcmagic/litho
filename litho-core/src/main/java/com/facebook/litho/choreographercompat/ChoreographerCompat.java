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
package com.facebook.litho.choreographercompat;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.Choreographer;

/** See {@link ChoreographerCompatImpl}. Interface exists for mocking out in tests. */
public interface ChoreographerCompat {

  void postFrameCallback(FrameCallback callbackWrapper);

  void postFrameCallbackDelayed(FrameCallback callbackWrapper, long delayMillis);

  void removeFrameCallback(FrameCallback callbackWrapper);

  /**
   * This class provides a compatibility wrapper around the JellyBean FrameCallback with methods to
   * access cached wrappers for submitting a real FrameCallback to a Choreographer or a Runnable to
   * a Handler.
   */
  abstract class FrameCallback {

    private Runnable mRunnable;
    private Choreographer.FrameCallback mFrameCallback;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    Choreographer.FrameCallback getFrameCallback() {
      if (mFrameCallback == null) {
        mFrameCallback =
            new Choreographer.FrameCallback() {
              @Override
              public void doFrame(long frameTimeNanos) {
                ChoreographerCompat.FrameCallback.this.doFrame(frameTimeNanos);
              }
            };
      }
      return mFrameCallback;
    }

    Runnable getRunnable() {
      if (mRunnable == null) {
        mRunnable =
            new Runnable() {
              @Override
              public void run() {
                doFrame(System.nanoTime());
              }
            };
      }
      return mRunnable;
    }

    public abstract void doFrame(long frameTimeNanos);
  }
}
