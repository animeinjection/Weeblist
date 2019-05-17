package com.animeinjection.weeblist.api.objects;

public enum MediaListStatus {
  CURRENT("Currently Watching"),
  PLANNING("Plan to Watch"),
  COMPLETED("Completed"),
  DROPPED("Dropped"),
  PAUSED("On Hold"),
  REPEATING("Repeating");

  public final String text;

  MediaListStatus(String textRepresentation) {
    this.text = textRepresentation;
  }


  @Override
  public String toString() {
    return text;
  }
}
