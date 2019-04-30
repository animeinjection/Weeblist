package com.animeinjection.weeblist.api;

import java.io.IOException;

public interface ServiceListener<T> {
  void onResponse(T response);
  void onError(IOException error);
}
