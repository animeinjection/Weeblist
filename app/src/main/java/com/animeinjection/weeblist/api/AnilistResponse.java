package com.animeinjection.weeblist.api;

public interface AnilistResponse {
  void setResponseJson(String responseJson);

  interface Factory<T> {
    T newResponseObject();
  }
}
