package com.orin.booruviewer.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.orin.booruviewer.R;
import com.orin.booruviewer.api.GelbooruApi;
import com.orin.booruviewer.entity.Post;
import com.orin.booruviewer.ui.activity.ImageActivity;
import com.orin.booruviewer.ui.activity.VideoActivity;
import com.orin.booruviewer.util.FileUtils;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private List<Post> posts;

    public PostAdapter(List<Post> posts) {
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_post, null);

        return new ViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        ImageLoader.getInstance().displayImage(posts.get(position).getThumburl(), holder.imageView, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                holder.progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                holder.progressBar.setVisibility(View.GONE);
            }
        });

        holder.post = posts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return this.posts.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imageView;
        private ProgressBar progressBar;
        private Post post;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            imageView = itemView.findViewById(R.id.thumbnail);
            progressBar = itemView.findViewById(R.id.progressBar);
        }

        @Override
        public void onClick(final View v) {
            showPost(v.getContext());
        }

        private void showPost(final Context ctx) {
            final Executor executor = Executors.newSingleThreadExecutor();
            final Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(new Runnable() {
                @Override
                public void run() {
                    GelbooruApi.getInstance().insertTagsType(post);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent;

                            if (FileUtils.getInstance().isAnimated(post))
                                intent = new Intent(ctx, VideoActivity.class);
                            else
                                intent = new Intent(ctx, ImageActivity.class);
                            intent.putExtra("post", post);
                            ctx.startActivity(intent);
                        }
                    });
                }
            });
        }
    }
}
