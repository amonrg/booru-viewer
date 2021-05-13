package com.orin.booruviewer.util;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import com.orin.booruviewer.entity.Post;
import com.orin.booruviewer.entity.Tag;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Set;

import static android.content.Context.DOWNLOAD_SERVICE;

public class FileUtils {
    private static FileUtils instance;
    private Context context;

    private FileUtils(Context context) {
        this.context = context.getApplicationContext();
    }

    public static synchronized void init(Context context) {
        instance = new FileUtils(context);
    }

    public static FileUtils getInstance() {
        if (instance == null) {
            synchronized (FileUtils.class) {
                if (instance == null) {
                    throw new IllegalStateException(FileUtils.class.getSimpleName() +
                            " is not initialized, call init(...) first");
                }
                return instance;
            }
        }
        return instance;
    }

    public void readTags(Set<Tag> tags) {
        try (
            FileInputStream fis =
                new FileInputStream(context.getFilesDir() + "/tags.set");
            ObjectInputStream ois =
                new ObjectInputStream(fis);
        ) {
            tags.addAll((Collection<? extends Tag>) ois.readObject());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveTags(Set<Tag> tags) {
        String fileName = "tags.set";

        try (
            FileOutputStream fos =
                context.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream oos =
                new ObjectOutputStream(fos)
        ) {
            oos.writeObject(tags);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void downloadFile(Post post) {
        final String SITE_FOLDER = "/Gelbooru/";
        File pathFile = new File(Environment.getExternalStorageDirectory().getPath() + SITE_FOLDER);
        File file;

        if (pathFile.exists()) {
            System.out.println("Path already exists.");
        } else {
            pathFile.mkdirs();
        }

        file = new File(Environment.getExternalStorageDirectory().getPath() + SITE_FOLDER + post.getFilename());

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(post.getFileurl()))
                .setTitle(post.getFilename())
                .setDescription("Downloading")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                .setDestinationUri(Uri.fromFile(file))
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true);

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        assert downloadManager != null;
        downloadManager.enqueue(request);
    }

    public String getFileExtension(Post post) {
        return post.getFilename().split("\\.")[1];
    }

    public boolean isAnimated(Post post) {
        String ext = getFileExtension(post);

        switch (ext) {
            case "mp4":
            case "webm":
            case "gif":
                return true;
            default:
                return false;
        }
    }

    public String getFileType(Post post) {
        if (isAnimated(post))
            return "video/*";
        return "image/*";
    }

    public void copyToStorage(File src, File dst) throws IOException {
        try (InputStream in = new FileInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dst)) {
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }
}
