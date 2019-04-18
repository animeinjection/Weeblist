package com.animeinjection.weeblist;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.animeinjection.weeblist.api.GraphQLService;
import com.animeinjection.weeblist.injection.ComponentFetcher;
import com.animeinjection.weeblist.injection.Qualifiers.ApplicationContext;

import javax.inject.Inject;
import java.net.URL;

public class OAuthFragment extends Fragment {
  private static final String OAUTH_AUTHORIZATION_ENDPOINT = "https://anilist.co/api/v2/oauth/authorize";
  private static final String OAUTH_CLIENT_ID = "1885";
  private static final String OAUTH_RESPONSE_TYPE = "token";

  public interface Injector {
    void inject(OAuthFragment fragment);
  }

  @Inject GraphQLService graphQLService;
  @Inject @ApplicationContext Context applicationContext;

  View loginButton;
  WebView webView;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ComponentFetcher.fromContext(getContext(), Injector.class).inject(this);
  }

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.oauth_fragment, container, false);

    loginButton = root.findViewById(R.id.login_button);
    loginButton.setOnClickListener(this::doLogin);

    webView = root.findViewById(R.id.oauth_web_view);

    return root;
  }

  private void doLogin(View unusedView) {
//    Toast.makeText(getContext(), "I don't know how to log in :(", Toast.LENGTH_SHORT).show();
    loginButton.setVisibility(View.GONE);
    webView.setVisibility(View.VISIBLE);
    String oAuthRequestUrl = OAUTH_AUTHORIZATION_ENDPOINT + "?client_id=" + OAUTH_CLIENT_ID + "&response_type=" + OAUTH_RESPONSE_TYPE;
    webView.loadUrl(oAuthRequestUrl);
  }

  private class MyOnClickListener implements OnClickListener {
    @Override
    public void onClick(View v) {
      doLogin(v);
    }
  }
}
