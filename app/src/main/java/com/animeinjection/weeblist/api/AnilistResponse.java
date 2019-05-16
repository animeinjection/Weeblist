package com.animeinjection.weeblist.api;

import android.util.Log;
import com.animeinjection.weeblist.api.objects.Query;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public abstract class AnilistResponse {
  private static final String LOG_TAG = "AnilistResponse";

  protected Query parsedResponse;

  public void setResponseJson(String responseJson) throws AnilistError {
//    Log.d(LOG_TAG, responseJson);
    try {
      Gson gson = new Gson();
      parsedResponse = gson.fromJson(responseJson, Query.class);
    } catch (JsonSyntaxException e) {
      throw new AnilistError(e);
    }
    if (parsedResponse.errors != null) {
      throw new AnilistError(this);
    }
  }

  public interface Factory<T extends AnilistResponse> {
    T newResponseObject();
  }
}
