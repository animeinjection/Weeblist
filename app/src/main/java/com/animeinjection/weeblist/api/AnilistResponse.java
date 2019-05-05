package com.animeinjection.weeblist.api;

import android.util.Log;
import com.animeinjection.weeblist.api.objects.Query;
import com.google.gson.Gson;

public abstract class AnilistResponse {
  private static final String LOG_TAG = "AnilistResponse";

  protected Query parsedResponse;

  public void setResponseJson(String responseJson) {
    Log.d(LOG_TAG, responseJson);
    Gson gson = new Gson();
    parsedResponse = gson.fromJson(responseJson, Query.class);
  }

  public interface Factory<T> {
    T newResponseObject();
  }
}
