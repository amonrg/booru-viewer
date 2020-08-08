package com.orin.booruviewer.util;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.FileProvider;

import com.orin.booruviewer.entity.Post;

import java.io.File;
import java.util.Date;

public class Notification extends BroadcastReceiver {
    private Post post;

    public Notification() { }
    public Notification(Post post) {
        this.post = post;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/Gelbooru/" + post.getFilename());
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        Intent i = new Intent(Intent.ACTION_VIEW);
        PendingIntent pendingIntent;
        String type;
        Uri data = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".fileprovider", file);
        int id = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        type = FileUtils.getInstance().getFileType(post);

        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        i.setDataAndType(data, type);

        pendingIntent = PendingIntent.getActivity(context, 0, i, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "com.orin.booruviewer.ANDROID")
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setContentTitle("Download complete")
                .setContentText(post.getFilename())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(id, builder.build());
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
