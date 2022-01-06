package com.orin.booruviewer.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.github.piasy.biv.loader.ImageLoader;
import com.github.piasy.biv.view.BigImageView;
import com.github.piasy.biv.view.GlideImageViewFactory;
import com.orin.booruviewer.R;
import com.orin.booruviewer.entity.Post;
import com.orin.booruviewer.ui.dialog.TagsDialog;
import com.orin.booruviewer.ui.progress.ProgressBarHorizontalIndicator;
import com.orin.booruviewer.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class ImageActivity extends AppCompatActivity {
    private Post post;
    private BigImageView bigImageView;
    private int notificationId;
    private ImageStatus imageStatus;

    private enum ImageStatus {
        DOWNLOADING,
        DOWNLOADED,
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        Intent intent = getIntent();

        initToolbar();
        bigImageView = findViewById(R.id.mBigImage);
        this.post = (Post) intent.getSerializableExtra("post");

        bigImageView.setImageLoaderCallback(new ImageLoader.Callback() {
            @Override
            public void onCacheHit(int imageType, File image) {

            }

            @Override
            public void onCacheMiss(int imageType, File image) {

            }

            @Override
            public void onStart() {

            }

            @Override
            public void onProgress(final int progress) {
                if (imageStatus == ImageStatus.DOWNLOADING) {
                    final NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "com.orin.booruviewer.ANDROID");
                    final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

                    builder.setContentTitle("Picture Download")
                            .setContentText("Download in progress")
                            .setSmallIcon(android.R.drawable.stat_sys_download)
                            .setPriority(NotificationCompat.PRIORITY_LOW);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            builder.setProgress(100, progress, false);
                            notificationManager.notify(notificationId, builder.build());
                        }
                    }).start();
                }
            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onSuccess(File image) {
                if (imageStatus == ImageStatus.DOWNLOADING) {
                    File file = new File(Environment.getExternalStorageDirectory().getPath() + "/Gelbooru/" + post.getFilename());
                    saveFile(file);
                    showNotification(file);
                    imageStatus = ImageStatus.DOWNLOADED;
                }
            }

            @Override
            public void onFail(Exception error) {

            }
        });

        if (this.post != null)
            showImage(this.post);
    }

    private void showImage(final Post post) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                bigImageView.setProgressIndicator(new ProgressBarHorizontalIndicator());
                bigImageView.setImageViewFactory(new GlideImageViewFactory());
                bigImageView.showImage(Uri.parse(post.getThumburl()), Uri.parse(post.getFileurl()));
            }
        }).start();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.image_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.app_bar_download:
                Toast.makeText(getApplicationContext(), "Downloading image", Toast.LENGTH_SHORT).show();

                notificationId = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

                if (bigImageView.getCurrentImageFile() != null) {
                    File file = new File(Environment.getExternalStorageDirectory().getPath() + "/Gelbooru/" + post.getFilename());
                    saveFile(file);
                    showNotification(file);
                } else {
                    imageStatus = ImageStatus.DOWNLOADING;
                }
                break;
            case R.id.app_bar_tags:
                DialogFragment dialogFragment = new TagsDialog(this, post.getTagsSet());
                dialogFragment.show(getSupportFragmentManager(), "dialog");
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showNotification(File file) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        Intent i = new Intent(Intent.ACTION_VIEW);
        PendingIntent pendingIntent;
        String type;
        Uri data = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".fileprovider", file);

        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        type = FileUtils.getInstance().getFileType(post);
        i.setDataAndType(data, type);

        pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, i, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "com.orin.booruviewer.ANDROID")
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setContentTitle("Download complete")
                .setContentText(post.getFilename())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(notificationId, builder.build());
    }

    private void saveFile(File file) {
        try {
            FileUtils.getInstance().copyToStorage(bigImageView.getCurrentImageFile(), file);

            MediaScannerConnection.scanFile(this,
                    new String[] { file.toString() }, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}