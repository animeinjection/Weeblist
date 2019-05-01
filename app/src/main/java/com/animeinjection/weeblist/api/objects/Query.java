package com.animeinjection.weeblist.api.objects;

import com.google.gson.annotations.SerializedName;

public class Query {
  public QueryData data;

  public static class QueryData {
    @SerializedName("MediaListCollection")
    public MediaListCollection mediaListCollection;
    @SerializedName("User")
    public User user;
    @SerializedName("Viewer")
    public User viewer;
  }
}
