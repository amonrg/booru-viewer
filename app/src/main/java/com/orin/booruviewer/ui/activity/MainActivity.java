package com.orin.booruviewer.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

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
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private List<Post> posts;
    private PostAdapter postAdapter;
    private Integer pageId = 0;
    private TextView txtError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, 2);
        RecyclerView recyclerView = findViewById(R.id.recycler_view_posts);
        txtError = findViewById(R.id.txt_error);

        posts = new ArrayList<>();
        postAdapter = new PostAdapter(posts);
        pageId = 0;

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(postAdapter);
        txtError.setVisibility(View.GONE);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    if (gridLayoutManager.findLastCompletelyVisibleItemPosition() == posts.size() - 1 && gridLayoutManager.findFirstVisibleItemPosition() != 0) {
                        pageId++;
                        showPosts(pageId);
                    }
                }
            }
        });

        initToolbar();
        initRefreshLayout();

        showPosts(pageId);
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
                pageId = 0;
                posts.clear();
                showPosts(pageId);
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

    private void showPosts(final Integer pid) {
        final Executor executor = Executors.newSingleThreadExecutor();
        final Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Set<Tag> tags = new LinkedHashSet<>();
                    FileUtils.getInstance().readTags(tags);
                    posts.addAll(GelbooruApi.getInstance().fetchPostsFromPage(pid, tags));

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            postAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (Exception e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (posts.isEmpty()) {
                                txtError.setVisibility(View.VISIBLE);
                                txtError.setText(R.string.no_images);
                            }
                        }
                    });
                }
            }
        });
    }
}