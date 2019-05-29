package com.animeinjection.weeblist.animelist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.animeinjection.weeblist.R;
import com.animeinjection.weeblist.api.objects.MediaListStatus;
import com.animeinjection.weeblist.injection.ComponentFetcher;
import com.google.android.material.tabs.TabLayout;
import com.google.common.collect.ImmutableList;

import javax.inject.Inject;

public class AnimeListPagerFragment extends Fragment {
  private static final String STATE_SELECTED_PAGE = "state_selected_page";

  public interface Injector {
    void inject(AnimeListPagerFragment fragment);
  }

  @Inject AnimeListController animeListController;

  private ViewPager viewPager;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ComponentFetcher.fromContext(getContext(), Injector.class).inject(this);
  }

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.list_pager_fragment, container, false);
    viewPager = root.findViewById(R.id.view_pager);
    TabLayout tabLayout = root.findViewById(R.id.tab_layout);
    tabLayout.setupWithViewPager(viewPager);
    AnimeListPagerAdapter pagerAdapter = new AnimeListPagerAdapter(getFragmentManager());
    viewPager.setAdapter(pagerAdapter);
    if (savedInstanceState != null) {
      int selectedPage = savedInstanceState.getInt(STATE_SELECTED_PAGE);
      viewPager.setCurrentItem(selectedPage, false);
    }

    return root;
  }

  @Override
  public void onResume() {
    super.onResume();
    animeListController.updateAnimeList();
  }

  @Override
  public void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putInt(STATE_SELECTED_PAGE, viewPager.getCurrentItem());
  }

  private static class AnimeListPagerAdapter extends FragmentStatePagerAdapter {
    private static final ImmutableList<MediaListStatus> STATUSES = ImmutableList.of(
        MediaListStatus.CURRENT,
        MediaListStatus.COMPLETED,
        MediaListStatus.PLANNING,
        MediaListStatus.PAUSED,
        MediaListStatus.DROPPED);

    public AnimeListPagerAdapter(@NonNull FragmentManager fm) {
      super(fm, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
      return AnimeListFragment.newInstance(STATUSES.get(position));
    }

    @Override
    public int getCount() {
      return STATUSES.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
      return STATUSES.get(position).toString();
    }


  }
}
