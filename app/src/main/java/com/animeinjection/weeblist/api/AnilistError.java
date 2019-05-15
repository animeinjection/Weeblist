package com.animeinjection.weeblist.api;

import androidx.annotation.Nullable;

public class AnilistError extends Exception {
  private final AnilistResponse errorResponse;

  public AnilistError(Throwable cause) {
    super(cause);
    errorResponse = null;
  }

  public AnilistError(AnilistResponse errorResponse) {
    this.errorResponse = errorResponse;
  }

  @Nullable
  public AnilistResponse getErrorResponse() {
    return errorResponse;
  }
}
