package com.animeinjection.weeblist.util;

import android.os.Handler;
import android.os.Looper;

public class ThreadUtil {
  private static Handler mainThreadHandler;

  public static Handler getUiThreadHandler() {
    if (mainThreadHandler == null) {
      mainThreadHandler = new Handler(Looper.getMainLooper());
    }
    return mainThreadHandler;
  }
}
