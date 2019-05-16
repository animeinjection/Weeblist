package com.animeinjection.weeblist.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;
import com.google.common.base.Preconditions;
import okhttp3.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

@Singleton
public class ImageUtils {

  @Inject OkHttpClient okHttpClient;

  @Inject
  public ImageUtils() {}

  public void loadImage(String url, ImageView imageView) {
    Preconditions.checkNotNull(url);
    Preconditions.checkNotNull(imageView);
    Request request = new Request.Builder().get().url(url).build();
    okHttpClient.newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        // do nothing
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        ThreadUtil.getUiThreadHandler().post(() -> {
          if (imageView.getContext() != null && response.body() != null) {
            Bitmap imageBitmap = BitmapFactory.decodeStream(response.body().byteStream());
            imageView.setImageDrawable(new BitmapDrawable(imageView.getResources(), imageBitmap));
          }
        });
      }
    });
  }
}
