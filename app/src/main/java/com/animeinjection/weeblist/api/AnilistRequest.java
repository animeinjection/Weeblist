package com.animeinjection.weeblist.api;

import androidx.annotation.NonNull;
public interface AnilistRequest {

  @NonNull String buildGraphQLRequestBody();

}
