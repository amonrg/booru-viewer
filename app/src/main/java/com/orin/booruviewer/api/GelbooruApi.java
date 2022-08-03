package com.orin.booruviewer.api;

import android.text.Html;

import com.orin.booruviewer.entity.Post;
import com.orin.booruviewer.entity.Tag;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class GelbooruApi {
    private HashMap<String, String> credentials;
    private static GelbooruApi instance;

    private GelbooruApi() {
    }

    public synchronized void init(HashMap<String, String> credentials) {
        this.credentials = credentials;
    }

    public static GelbooruApi getInstance() {
        if (instance == null) {
            synchronized (GelbooruApi.class) {
                if (instance == null) {
                    instance = new GelbooruApi();
                }
            }
        }
        return instance;
    }

    private String getJson(GelUrl gelUrl) throws IOException {
        URL url = new URL(gelUrl.toString());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InputStream in = connection.getInputStream();

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
            throw new IOException(connection.getResponseMessage() + ": with " + url.toString());

        int bytesRead = 0;
        byte[] buffer = new byte[1024];
        while ((bytesRead = in.read(buffer)) > 0) {
            out.write(buffer, 0, bytesRead);
        }

        return new String(out.toByteArray());
    }

    public List<Post> fetchPostsFromPage(int pid, Set<Tag> tags) throws IOException, JSONException {
        GelUrl gelUrl = new GelUrl.Builder(this.credentials)
                .page("dapi")
                .s("post")
                .q("index")
                .json(true)
                .pid(String.valueOf(pid))
                .tags(tags)
                .build();
        List<Post> posts = new ArrayList<>();

        String json = getJson(gelUrl);
        JSONArray jsonArray = new JSONObject(json).getJSONArray("post");
        int length = jsonArray.length();
        for (int i = 0; i < length; i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            StringBuilder thumburl = new StringBuilder();
            Post post = new Post();

            post.setFilename(jsonObject.getString("image"));
            post.setFileurl(jsonObject.getString("file_url"));
            post.setId(jsonObject.getInt("id"));
            post.setTags(jsonObject.getString("tags"));

            thumburl.append("https://gelbooru.com/thumbnails/").
                    append(jsonObject.getString("directory")).
                    append("/thumbnail_").
                    append(jsonObject.getString("md5")).
                    append(".jpg");

            post.setThumburl(thumburl.toString());
            posts.add(post);
        }
        return posts;
    }

    public void insertTagsType(Post post) {
        GelUrl url = new GelUrl.Builder(this.credentials)
                .page("dapi")
                .s("tag")
                .q("index")
                .names(Html.fromHtml(post.getTags()).toString())
                .order("asc")
                .orderby("name")
                .json(true)
                .build();

        try {
            String json = getJson(url);
            JSONArray jsonArray = new JSONObject(json).getJSONArray("tag");
            int length = jsonArray.length();
            for (int i = 0; i < length; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Tag tag = new Tag(jsonObject.getString("name"));

                tag.setType(jsonObject.getString("type"));
                post.addTag(tag);
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    public String fetchTagType(String name) {
        GelUrl url = new GelUrl.Builder(this.credentials)
                .page("dapi")
                .s("tag")
                .q("index")
                .name(name)
                .json(true)
                .build();
        try {
            String json = getJson(url);
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.getJSONObject("@attributes").getInt("count") > 0)
                return jsonObject.getJSONArray("tag").getJSONObject(0).getString("type");
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return Tag.Type.INVALID.toString();
    }

    public String[] fetchAutocompleteSuggestions(String term) {
        GelUrl url = new GelUrl.Builder(this.credentials)
                .page("autocomplete2")
                .term(term)
                .type("tag_query")
                .limit("10")
                .build();
        String[] autotag = new String[]{"","","","","","","","","",""};
        try {
            String json = getJson(url);
            JSONArray jsonArray = new JSONArray(json);

            int length = jsonArray.length();
            for (int i = 0; i < length; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                autotag[i] = jsonObject.getString("value");
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return autotag;
    }
}
