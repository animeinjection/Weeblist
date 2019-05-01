package com.animeinjection.weeblist.api;

import com.animeinjection.weeblist.api.objects.Query;
import com.google.gson.Gson;

public abstract class AnilistResponse {
  protected Query parsedResponse;

  public void setResponseJson(String responseJson) {
    Gson gson = new Gson();
    parsedResponse = gson.fromJson(responseJson, Query.class);
  }

  public interface Factory<T> {
    T newResponseObject();
  }
}
