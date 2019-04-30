package com.animeinjection.weeblist.api;

public abstract class AnilistResponse {
  public abstract void setResponseJson(String responseJson);

  public interface Factory<T> {
    T newResponseObject();
  }
}
