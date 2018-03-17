package com.rrdl.cinemapalace.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gigamole.navigationtabstrip.NavigationTabStrip;
import com.rrdl.cinemapalace.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class MoviesFragment extends Fragment {

    @BindView(R.id.friendPager)
    ViewPager viewPager;
    @BindView(R.id.nts)
    NavigationTabStrip tabLayout;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_all_movies, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Adapter adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(MoviesListFragment.newInstance(MoviesListFragment.FragmentListTypes.MOVIES), getString(R.string.fragment_title_movies));
        adapter.addFragment(MoviesListFragment.newInstance(MoviesListFragment.FragmentListTypes.TV_SERIES), getString(R.string.fragment_title_tv));
        adapter.addFragment(MoviesListFragment.newInstance(MoviesListFragment.FragmentListTypes.FAVOURITES), getString(R.string.fragment_title_favourite));

        viewPager.setAdapter(adapter);
        tabLayout.setViewPager(viewPager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    private static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        Adapter(FragmentManager fm) {
            super(fm);
        }

        void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }


}
