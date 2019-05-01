package com.animeinjection.weeblist.api.objects;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class MediaListEntry {
  public int id;
  public int userId;
  public int mediaId;
  public MediaListStatus status;
  public float score;
  public int progress;
  public int progressVolumes;
  public int repeat;
  @SerializedName("private")
  public boolean isPrivate;
  public String notes;
  public boolean hiddenFromStatusLists;
  public Map<String, Boolean> customLists;
  public Map<String, Float> advancedScores;
  public FuzzyDate startedAt;
  public FuzzyDate completedAt;
  public int createdAt;
  public Media media;
  public User user;
}
