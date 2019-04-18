package com.animeinjection.weeblist.animelist;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.animeinjection.weeblist.R;
import com.animeinjection.weeblist.injection.ComponentFetcher;
import com.google.common.collect.ImmutableList;

import java.util.List;

public class AnimeListFragment extends Fragment {

  public interface Injector {
    void inject(AnimeListFragment fragment);
  }

  private RecyclerView recyclerView;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ComponentFetcher.fromContext(getContext(), Injector.class).inject(this);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.anime_list_fragment, container, false);
    recyclerView = root.findViewById(R.id.recycler_view);
    recyclerView.setAdapter(new AnimeListAdapter(getContext()));
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    return root;
  }

  private static class AnimeItemHolder extends RecyclerView.ViewHolder {
    private TextView titleView;

    public AnimeItemHolder(View v) {
      super(v);
      titleView = v.findViewById(R.id.title);
    }

    public void bindData(String title) {
      titleView.setText(title);
    }
  }

  private static class AnimeListAdapter extends RecyclerView.Adapter<AnimeItemHolder> {

    private Context context;
    private List<String> titles = ImmutableList.of("Your Favorite Anime"); // TODO(alyssa): actual data

    public AnimeListAdapter(Context context) {
      this.context = context;
    }

    @NonNull
    @Override
    public AnimeItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View v = LayoutInflater.from(context).inflate(R.layout.anime_item, parent, false);
      return new AnimeItemHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimeItemHolder holder, int position) {
      holder.bindData(titles.get(position));
    }

    @Override
    public int getItemCount() {
      return titles.size();
    }
  }
}
