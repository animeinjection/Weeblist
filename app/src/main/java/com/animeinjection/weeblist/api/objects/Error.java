package com.animeinjection.weeblist.api.objects;

public class Error {
  String message;
  String status;
  Location[] locations;

  public static class Location {
    int line;
    int column;
  }
}
