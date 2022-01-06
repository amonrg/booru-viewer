package com.orin.booruviewer.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.orin.booruviewer.R;
import com.orin.booruviewer.entity.Tag;
import com.orin.booruviewer.ui.activity.ImageActivity;
import com.orin.booruviewer.ui.activity.MainActivity;
import com.orin.booruviewer.ui.activity.VideoActivity;
import com.orin.booruviewer.util.FileUtils;

import java.util.Set;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.ViewHolder> {
    private Set<Tag> tags;
    private LayoutInflater inflater;
    private ViewType type;
    private Context ctx;

    public enum ViewType {
        DIALOG,
        ACTIVITY
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageButton removeButton;
        public CardView cardView;

        public ViewHolder(View itemView, ViewType type) {
            super(itemView);

            switch (type) {
                case DIALOG:
                    textView = itemView.findViewById(R.id.txt_tag_dialog_name);
                    cardView = itemView.findViewById(R.id.cardview_dialog_tags);
                    break;
                case ACTIVITY:
                    textView = itemView.findViewById(R.id.txt_tag_name);
                    removeButton = itemView.findViewById(R.id.ib_remove);
                    cardView = itemView.findViewById(R.id.cardview_tags);
                    break;
            }
        }
    }

    public TagAdapter(Context context, Set<Tag> tags, ViewType type) {
        this.inflater = LayoutInflater.from(context);
        this.tags = tags;
        this.type = type;
        this.ctx = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = null;

        switch (type) {
            case DIALOG:
                v = inflater.inflate(R.layout.tag_recyclerview_dialog_row, parent, false);
                break;
            case ACTIVITY:
                v = inflater.inflate(R.layout.tag_recyclerview_row, parent, false);
                break;
        }

        return new ViewHolder(v, type);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final Tag[] tagsArray = tags.toArray(new Tag[tags.size()]);
        final Tag tag = tagsArray[position];

        holder.textView.setText(tag.getName().toUpperCase());
        holder.cardView.setCardBackgroundColor(tag.getColor());

        if (tag.getName().charAt(0) == '-') {
            holder.textView.setPaintFlags(holder.textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.textView.setPaintFlags(holder.textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        if (type == ViewType.ACTIVITY) {
            holder.removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tags.remove(tag);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, tags.size());
                }
            });
        } else {
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(ctx, MainActivity.class);

                    tags.clear();
                    tags.add(tag);
                    FileUtils.getInstance().saveTags(tags);
                    ctx.startActivity(i);

                    if (ctx.getClass().getName().equals("ImageActivity"))
                        ((ImageActivity)ctx).finish();
                    else
                        ((VideoActivity)ctx).finish();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }
}
