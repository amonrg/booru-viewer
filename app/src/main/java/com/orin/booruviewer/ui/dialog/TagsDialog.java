package com.orin.booruviewer.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.orin.booruviewer.R;
import com.orin.booruviewer.entity.Tag;
import com.orin.booruviewer.ui.adapter.TagAdapter;

import java.util.Set;

public class TagsDialog extends DialogFragment {
    private Set<Tag> tags;
    private Context context;

    public TagsDialog(Context ctx, Set<Tag> tags) {
        this.tags = tags;
        this.context = ctx;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        RecyclerView recyclerView = (RecyclerView) LayoutInflater.from(context).inflate(R.layout.tags_recycler_dialog, null);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        TagAdapter adapter = new TagAdapter(context, tags, TagAdapter.ViewType.DIALOG);
        recyclerView.setAdapter(adapter);

        builder.setTitle("Tags")
               .setView(recyclerView)
               .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        TagsDialog.this.getDialog().cancel();
                    }
               });
        return builder.create();
    }
}
