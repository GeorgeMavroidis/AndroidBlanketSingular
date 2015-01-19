package com.georgemavroidis.feed.AdapterPackage;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by george on 14-10-20.
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {
    YoutubeFragment y1 = YoutubeFragment.newInstance("jacksgap");
    TwitterFragment twitter = TwitterFragment.newInstance("jackharries");
    InstagramFragment instagram = InstagramFragment.newInstance("jackharries");
    TumblrFragment tumblr = TumblrFragment.newInstance("troyesivan.tumblr.com");
    MusicFragment music = MusicFragment.newInstance("troyesivan");


    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);

    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                // Top Rated fragment activity
//                return new Fragment();
                return y1;
//                return new YoutubeFragment();
            case 1:
                // Games fragment activity
                return twitter;
//                return new YoutubeFragment();
//                return new GamesFragment();
            case 2:
                // Movies fragment activity
                return instagram;
//                return new YoutubeFragment();
//                return new MoviesFragment();
            case 3:
                // Movies fragment activity
                return instagram;
//                return new YoutubeFragment();
//                return new MoviesFragment();
            case 4:
                // Movies fragment activity
                return tumblr;
//                return new YoutubeFragment();
//                return new MoviesFragment();
            case 5:
                return tumblr;
            case 6:
                return music;
            default:
                return new Fragment();

        }

    }



    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 3;
    }

}
