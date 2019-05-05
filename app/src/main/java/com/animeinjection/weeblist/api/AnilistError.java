package com.animeinjection.weeblist.api;

public class AnilistError extends Exception {
  private final AnilistResponse errorResponse;

  public AnilistError(Throwable cause) {
    super(cause);
    errorResponse = null;
  }

  public AnilistError(AnilistResponse errorResponse) {
    this.errorResponse = errorResponse;
  }

  public AnilistResponse getErrorResponse() {
    return errorResponse;
  }
}
