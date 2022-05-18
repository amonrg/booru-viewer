package com.orin.booruviewer.ui.adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
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

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private List<Post> posts;
    private OnLoadMoreListener loadMoreListener;

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
        if (position >= getItemCount() - 1 && loadMoreListener != null) {
            loadMoreListener.onLoadMore();
        }

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

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public void setLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.loadMoreListener = onLoadMoreListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imageView;
        private ProgressBar progressBar;
        private Post post;

        private static class InsertTagsTypeTask extends AsyncTask<Object, Void, View> {
            private Post p;
            @Override
            protected View doInBackground(Object... params) {
                View view = (View)params[0];
                p = (Post)params[1];
                GelbooruApi.getInstance().insertTagsType(p);
                return view;
            }

            @Override
            protected void onPostExecute(View v) {
                final Intent intent;

                if (FileUtils.getInstance().isAnimated(p)) {
                    intent = new Intent(v.getContext(), VideoActivity.class);
                } else {
                    intent = new Intent(v.getContext(), ImageActivity.class);
                }
                intent.putExtra("post", p);
                v.getContext().startActivity(intent);
            }
        };

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            imageView = itemView.findViewById(R.id.thumbnail);
            progressBar = itemView.findViewById(R.id.progressBar);
        }

        @Override
        public void onClick(final View v) {
            new InsertTagsTypeTask().execute(v, post);
        }
    }
}
