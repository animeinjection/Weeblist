package com.animeinjection.weeblist.identity;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Identity {
  public abstract String getUserName();
  public abstract String getUserId();

  public static Identity create(String userName, String userId) {
    return new AutoValue_Identity(userName, userId);
  }
}
