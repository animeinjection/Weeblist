package com.animeinjection.weeblist.injection;

import android.content.Context;

public class ComponentFetcher {
  @SuppressWarnings("unchecked")
  public static <T> T fromContext(Context context, Class<T> clazz) {
    if (HasComponent.class.isAssignableFrom(context.getClass())) {
      return ((HasComponent<T>) context).component();
    } else {
      throw new RuntimeException("Failed to fetch component! Is this the right context?");
    }
  }

}
