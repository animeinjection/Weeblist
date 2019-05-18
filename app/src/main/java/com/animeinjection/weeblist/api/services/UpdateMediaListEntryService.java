package com.animeinjection.weeblist.api.services;

import android.util.Log;
import androidx.annotation.NonNull;
import com.animeinjection.weeblist.api.AnilistRequest;
import com.animeinjection.weeblist.api.AnilistResponse;
import com.animeinjection.weeblist.api.objects.MediaListStatus;
import com.animeinjection.weeblist.api.services.UpdateMediaListEntryService.UpdateMediaListEntryRequest;
import com.animeinjection.weeblist.api.services.UpdateMediaListEntryService.UpdateMediaListEntryResponse;
import com.animeinjection.weeblist.auth.AuthDataStore;
import com.google.common.base.Preconditions;
import okhttp3.OkHttpClient;

import javax.inject.Inject;
import java.util.Locale;

public class UpdateMediaListEntryService extends GraphQLService<UpdateMediaListEntryRequest, UpdateMediaListEntryResponse> {

  @Inject
  public UpdateMediaListEntryService(OkHttpClient okHttpClient, AuthDataStore authDataStore) {
    super(okHttpClient, authDataStore, new UpdateMediaListEntryResponse.Factory());

  }

  public UpdateMediaListEntryRequest newRequest() {
    return new UpdateMediaListEntryRequest();
  }

  public static class UpdateMediaListEntryRequest extends AnilistRequest {
    private static final String REQUEST_BODY_FORMAT =
        "mutation {"
            + "SaveMediaListEntry("
                + "id: %s"
                + "%s" // progress
                + "%s" //score
                + "%s) {" //status
                    + "id "
                    + "progress "
                    + "status "
                    + "score"
                +"}"
            + "}";
    private static final String PROGRESS_FORMAT = ", progress: %s";
    private static final String SCORE_FORMAT = ", scoreRaw: %s";
    private static final String STATUS_FORMAT = ", status: %s";

    private int mediaListEntryId;
    private int progress = -1;
    private int scoreRaw = -1;
    private MediaListStatus status;

    public UpdateMediaListEntryRequest() {}

    public void setMediaListEntryId(int id) {
      mediaListEntryId = id;
    }

    public void setProgress(int progress) {
      this.progress = progress;
    }

    public void setScoreRaw(int scoreRaw) {
      this.scoreRaw = scoreRaw;
    }

    public void setScore(float score) {
      this.scoreRaw = (int) score * 10;
    }

    public void setStatus(MediaListStatus status) {
      this.status = status;
    }

    @NonNull
    @Override
    protected String buildGraphQLQuery() {
      return String.format(
          Locale.US,
          REQUEST_BODY_FORMAT,
          mediaListEntryId,
          buildProgress(),
          buildScoreRaw(),
          buildStatus());
    }

    private String buildProgress() {
      return progress == -1 ? "" : String.format(Locale.US, PROGRESS_FORMAT, progress);
    }

    private String buildScoreRaw() {
      return scoreRaw == -1 ? "" : String.format(Locale.US, SCORE_FORMAT, scoreRaw);
    }

    private String buildStatus() {
      Log.d("UpdateMediaListEntryRequest", "status is " + (status == null ? "null" : status.name()));
      return status == null ? "" : String.format(Locale.US, STATUS_FORMAT, status.name());
    }

    @Override
    protected void validate() {
      Preconditions.checkArgument(mediaListEntryId > 0);
      Preconditions.checkArgument(progress > -1 || scoreRaw > -1);
    }
  }

  public static class UpdateMediaListEntryResponse extends AnilistResponse {

    private static class Factory implements AnilistResponse.Factory<UpdateMediaListEntryResponse> {
      @Override
      public UpdateMediaListEntryResponse newResponseObject() {
        return new UpdateMediaListEntryResponse();
      }
    }

    public int getProgress() {
      return parsedResponse.data.saveMediaListEntry.progress;
    }

    public MediaListStatus getStatus() {
      return parsedResponse.data.saveMediaListEntry.status;
    }

    public float getScore() {
      return parsedResponse.data.saveMediaListEntry.score;
    }
  }
}
