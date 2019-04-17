package com.animeinjection.weeblist.api;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.IOException;
import java.util.function.BiConsumer;

public class OkCallback {
  public static Callback from(final BiConsumer<Call, Response> success, final BiConsumer<Call, IOException> failure) {
    return new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        failure.accept(call, e);
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        success.accept(call, response);
      }
    };
  }
}
