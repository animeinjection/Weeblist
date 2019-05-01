package com.animeinjection.weeblist.api.objects;

import com.google.gson.annotations.SerializedName;

public class MediaTitle {
  public String romaji;
  public String english;
  @SerializedName("native")
  public String nativeTitle;
  public String userPreferred;
}
