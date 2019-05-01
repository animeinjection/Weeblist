package com.animeinjection.weeblist.api;

import androidx.annotation.NonNull;

import java.util.Locale;

public abstract class AnilistRequest {
  private static final String REQUEST_BODY_FORMAT = "{\"query\":\"%s\",\"variables\":%s}";
  private static final String DEFAULT_VARIABLES = "{}";

  protected abstract @NonNull String buildGraphQLQuery();

  protected @NonNull String buildGraphQLVariables() {
    return DEFAULT_VARIABLES;
  }

  public String buildRequestBody() {
    return String.format(Locale.US, REQUEST_BODY_FORMAT, buildGraphQLQuery(), buildGraphQLVariables());
  }

}
