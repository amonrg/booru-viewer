package com.orin.booruviewer.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.orin.booruviewer.R;
import com.orin.booruviewer.api.ApiCallback;
import com.orin.booruviewer.api.GelbooruApi;
import com.orin.booruviewer.entity.Post;
import com.orin.booruviewer.entity.Tag;
import com.orin.booruviewer.ui.adapter.PostAdapter;
import com.orin.booruviewer.util.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private Set<Tag> tags = new LinkedHashSet<>();
    private int pid = 0;
    private List<Post> posts = new ArrayList<>();
    private PostAdapter postAdapter;

    private ApiCallback postsCallback = new ApiCallback() {
        @Override
        public void onSuccess(JSONArray response) {
            TextView txtError = findViewById(R.id.txt_error);

            txtError.setVisibility(View.GONE);
            loadPosts(response, posts);
        }

        @Override
        public void onError(String errorMsg) {
            TextView txtError = findViewById(R.id.txt_error);

            txtError.setVisibility(View.VISIBLE);
            txtError.setText(errorMsg);
            System.out.println(errorMsg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FileUtils.getInstance().readTags(tags);
        GelbooruApi.getInstance().fetchPostsFromPage(pid, tags, postsCallback);

        postAdapter = new PostAdapter(posts);

        initToolbar();
        initRefreshLayout();
        initGridLayout();
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
                GelbooruApi.getInstance().fetchPostsFromPage(pid, tags, postsCallback);

                refreshLayout.setRefreshing(false);
            }
        });
    }

    private void initGridLayout() {
        final RecyclerView recyclerView = findViewById(R.id.recycler_view_posts);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, 2);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(postAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    if (gridLayoutManager.findLastCompletelyVisibleItemPosition() == posts.size() - 1 && gridLayoutManager.findFirstVisibleItemPosition() != 0) {
                        GelbooruApi.getInstance().fetchPostsFromPage(++pid, tags, postsCallback);
                        System.out.println(pid);
                    }
                }
            }
        });
    }

    private void loadPosts(JSONArray response, List<Post> posts) {
        try {
            int length = response.length();
            for (int i = 0; i < length; i++) {
                JSONObject jsonObject = response.getJSONObject(i);
                StringBuilder thumburl = new StringBuilder();
                Post post = new Post();

                post.setFilename(jsonObject.getString("image"));
                post.setFileurl(jsonObject.getString("file_url"));
                post.setId(jsonObject.getInt("id"));
                post.setTags(jsonObject.getString("tags"));

                thumburl.append("https://gelbooru.com/thumbnails/").
                        append(jsonObject.getString("directory")).
                        append("/thumbnail_").
                        append(jsonObject.getString("hash")).
                        append(".jpg");

                post.setThumburl(thumburl.toString());
                posts.add(post);
            }
            postAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
}