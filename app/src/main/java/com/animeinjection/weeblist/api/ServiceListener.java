package com.animeinjection.weeblist.api;

import java.io.IOException;
import java.util.function.Consumer;

public interface ServiceListener<T> {
  void onResponse(T response);
  void onError(IOException error);

  static <T> ServiceListener<T> from(Consumer<T> onResponse, Consumer<IOException> onError) {
    return new ServiceListener<T>() {
      @Override
      public void onResponse(T response) {
        onResponse.accept(response);
      }

      @Override
      public void onError(IOException error) {
        onError.accept(error);
      }
    };
  }
}
