package com.orin.booruviewer.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.orin.booruviewer.R;
import com.orin.booruviewer.entity.Post;
import com.orin.booruviewer.ui.dialog.TagsDialog;
import com.orin.booruviewer.util.FileUtils;

public class VideoActivity extends AppCompatActivity {
    private SimpleExoPlayer player;
    private Post post;
    private PlayerView playerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        initToolbar();
        Intent intent = getIntent();
        PhotoView photoView;

        post = (Post) intent.getSerializableExtra("post");
        playerView = findViewById(R.id.player_view);
        photoView = findViewById(R.id.photo_view);

        if (FileUtils.getInstance().getFileExtension(post).equals("gif")) {
            playerView.setVisibility(View.GONE);
            Glide.with(this).asGif().load(post.getFileurl()).into(photoView);
        } else {
            photoView.setVisibility(View.GONE);
            playVideo(post.getFileurl());
        }
    }

    private void playVideo(String url) {
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getApplicationContext(), Util.getUserAgent(getApplicationContext(), "Booru Viewer"));
        MediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(url));

        player = new SimpleExoPlayer.Builder(getApplicationContext()).build();

        player.prepare(videoSource);
        playerView.setPlayer(player);
        player.setPlayWhenReady(true);
        player.setRepeatMode(Player.REPEAT_MODE_ALL);

    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.video_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.app_bar_download:
                Toast.makeText(getApplicationContext(), "Downloading video", Toast.LENGTH_SHORT).show();
                FileUtils.getInstance().downloadFile(post);
                break;
            case R.id.app_bar_tags:
                DialogFragment dialogFragment = new TagsDialog(post.getTagsSet());
                dialogFragment.show(getSupportFragmentManager(), "dialog");
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null)
            player.release();
    }
}