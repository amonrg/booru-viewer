package com.orin.booruviewer.ui.adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.text.Html;
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
import com.orin.booruviewer.api.ApiCallback;
import com.orin.booruviewer.api.GelbooruApi;
import com.orin.booruviewer.entity.Post;
import com.orin.booruviewer.entity.Tag;
import com.orin.booruviewer.ui.activity.ImageActivity;
import com.orin.booruviewer.ui.activity.VideoActivity;
import com.orin.booruviewer.util.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.reciclerview_post, null);

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
        ImageView imageView;
        ProgressBar progressBar;
        Post post;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            imageView = itemView.findViewById(R.id.thumbnail);
            progressBar = itemView.findViewById(R.id.progressBar);
        }

        @Override
        public void onClick(final View v) {
            final Intent intent;

            if (FileUtils.getInstance().isAnimated(post)) {
                intent = new Intent(v.getContext(), VideoActivity.class);
            } else {
                intent = new Intent(v.getContext(), ImageActivity.class);
            }

            GelbooruApi.getInstance().fetchTagsType(Html.fromHtml(post.getTags()).toString(), new ApiCallback() {
                @Override
                public void onSuccess(JSONArray response) {
                    try {
                        int length = response.length();
                        for (int i = 0; i < length; i++) {
                            JSONObject jsonObject = response.getJSONObject(i);
                            Tag tag = new Tag(jsonObject.getString("tag"));

                            tag.setType(jsonObject.getString("type"));
                            post.addTag(tag);
                        }

                        intent.putExtra("post", post);
                        v.getContext().startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(String errorMsg) {
                    System.out.println(errorMsg);
                }
            });
        }
    }
}
