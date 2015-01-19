package com.georgemavroidis.feed;

import android.os.Bundle;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

/**
 * Created by george on 14-11-12.
 */
public class YoutubeActivity extends YouTubeBaseActivity implements
        YouTubePlayer.OnInitializedListener{

    public static final String API_KEY = "AIzaSyB0oGK4Ozx7-hAbRRIZXr5xLFnL9rpNGKs";
    public static String VIDEO_ID = "o7VVHhK9zf0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().hide();
        Bundle extras = this.getIntent().getExtras();
        if ( extras != null ) {
            if ( extras.containsKey("vidid") ) {
                VIDEO_ID = extras.getString("vidid");
            }
        }
        setContentView(R.layout.custom_youtube);

        YouTubePlayerView youTubePlayerView = (YouTubePlayerView)findViewById(R.id.youtubeplayerview);
        youTubePlayerView.initialize(API_KEY, this);
        if(MainActivity.mediaPlayer != null) {
            MainActivity.mediaPlayer.stop();
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if (!b) {
            youTubePlayer.loadVideo(VIDEO_ID);
            youTubePlayer.setFullscreen(true);
//            youTubePlayer.play();
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult result) {

    }




}
