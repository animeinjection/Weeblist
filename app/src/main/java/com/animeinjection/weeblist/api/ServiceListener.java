package com.animeinjection.weeblist.api;

import java.util.function.Consumer;

public interface ServiceListener<T extends AnilistResponse> {
  void onResponse(T response);
  void onError(AnilistError error);

  static <T extends AnilistResponse> ServiceListener<T> from(
      Consumer<T> onResponse, Consumer<AnilistError> onError) {
    return new ServiceListener<T>() {
      @Override
      public void onResponse(T response) {
        onResponse.accept(response);
      }

      @Override
      public void onError(AnilistError error) {
        onError.accept(error);
      }
    };
  }
}
