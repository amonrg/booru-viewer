package com.orin.booruviewer.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.orin.booruviewer.R;
import com.orin.booruviewer.api.GelbooruApi;
import com.orin.booruviewer.entity.Post;
import com.orin.booruviewer.entity.Tag;
import com.orin.booruviewer.ui.adapter.PostAdapter;
import com.orin.booruviewer.util.FileUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static Set<Tag> tags = new LinkedHashSet<>();
    private static Integer pid = 0;
    private static List<Post> posts = new ArrayList<>();
    private static RecyclerView recyclerView;
    private static boolean isAdapterAdded = false;
    private static PostAdapter postAdapter;
    private GridLayoutManager gridLayoutManager;

    private static class FetchPostsTask extends AsyncTask<Integer, Void, List<Post>> {
        @Override
        protected List<Post> doInBackground(Integer... params) {
            return GelbooruApi.getInstance().fetchPostsFromPage(params[0], tags);
        }

        @Override
        protected void onPostExecute(List<Post> fetchedPosts) {
            posts.addAll(fetchedPosts);
            if (isAdapterAdded) {
                postAdapter.notifyDataSetChanged();
            } else {
                postAdapter = new PostAdapter(posts);
                recyclerView.setAdapter(postAdapter);
                isAdapterAdded = true;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FileUtils.getInstance().readTags(tags);
        gridLayoutManager = new GridLayoutManager(MainActivity.this, 2);

        new FetchPostsTask().execute(pid);
        recyclerView = findViewById(R.id.recycler_view_posts);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    if (gridLayoutManager.findLastCompletelyVisibleItemPosition() == posts.size() - 1 && gridLayoutManager.findFirstVisibleItemPosition() != 0) {
                        pid++;
                        new FetchPostsTask().execute(pid);
                    }
                }
            }
        });

        initToolbar();
        initRefreshLayout();
    }

    private void initToolbar() {
        Toolbar myToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(myToolbar);
    }

    private void initRefreshLayout() {
        final SwipeRefreshLayout refreshLayout = findViewById(R.id.posts_refresh);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pid = 0;
                posts.clear();
                new FetchPostsTask().execute(pid);

                refreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.app_bar_search) {
            Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        new Thread(new Runnable() {
            @Override
            public void run() {
                ImageLoader.getInstance().clearDiskCache();
                ImageLoader.getInstance().clearMemoryCache();
                Glide.get(getApplicationContext()).clearDiskCache();
                Glide.get(getApplicationContext()).clearMemory();
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}