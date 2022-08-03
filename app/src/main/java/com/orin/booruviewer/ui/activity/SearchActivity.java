package com.orin.booruviewer.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.orin.booruviewer.R;
import com.orin.booruviewer.api.GelbooruApi;
import com.orin.booruviewer.entity.Tag;
import com.orin.booruviewer.ui.adapter.TagAdapter;
import com.orin.booruviewer.util.FileUtils;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SearchActivity extends AppCompatActivity {
    private Set<Tag> tags;
    private RecyclerView.Adapter tagAdapter;
    private AutoCompleteTextView txtTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        txtTag = findViewById(R.id.tag_txt);
        tags = new LinkedHashSet<>();
        FileUtils.getInstance().readTags(tags);
        initToolbar();
        initComponents();
    }

    private void initComponents() {
        Button btnAdd = findViewById(R.id.add_tag_button);
        RecyclerView tagsRecyclerView = findViewById(R.id.tags_recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        tagsRecyclerView.setLayoutManager(layoutManager);
        tagAdapter = new TagAdapter(this, tags, TagAdapter.ViewType.ACTIVITY);
        tagsRecyclerView.setAdapter(tagAdapter);

        initAutocomplete();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtTag.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Write a tag", Toast.LENGTH_SHORT).show();
                } else {
                    addTag(txtTag.getText().toString());
                    txtTag.getText().clear();
                }
            }
        });
    }

    private void initAutocomplete() {
        final String[] autotag = new String[]{"","","","","","","","","",""};
        final ArrayAdapter<String> autotagAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, autotag);

        txtTag.setAdapter(autotagAdapter);

        txtTag.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                final Executor executor = Executors.newSingleThreadExecutor();
                final Handler handler = new Handler(Looper.getMainLooper());

                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        final String[] autotag2 = GelbooruApi.getInstance().fetchAutocompleteSuggestions(s.toString());

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                autotagAdapter.clear();
                                autotagAdapter.addAll(autotag2);
                                autotagAdapter.getFilter().filter(txtTag.getText(), null);
                            }
                        });
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void addTag(final String name) {
        final Tag tag = new Tag(name);
        final Executor executor = Executors.newSingleThreadExecutor();
        final Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() {
                tag.setType(GelbooruApi.getInstance().fetchTagType(name));
                tags.add(tag);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        tagAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.search_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.app_bar_save) {
            Intent i = new Intent(this, MainActivity.class);

            FileUtils.getInstance().saveTags(tags);
            Toast.makeText(this, "Tags saved", Toast.LENGTH_LONG).show();
            startActivity(i);
        } else if (item.getItemId() == R.id.app_bar_delete) {
            tags.clear();
            tagAdapter.notifyDataSetChanged();
        }
        return super.onOptionsItemSelected(item);
    }
}
