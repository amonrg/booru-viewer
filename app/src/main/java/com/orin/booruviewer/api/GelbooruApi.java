package com.orin.booruviewer.api;

import android.text.Html;

import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
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

    public void fetchTagType(String name, final ApiCallback callback) {
        GelUrl url = new GelUrl.Builder(this.credentials).page("dapi").s("tag").q("index").name(name).json(true).build();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url.toString(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error_msg(error));
            }
        });
        System.out.println(url.toString());
        ApiRequest.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    public void fetchAutocompleteSuggestions(String term, final ApiCallback callback) {
        GelUrl url = new GelUrl.Builder(this.credentials).page("autocomplete2").term(term).type("tag_query").limit("10").build();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url.toString(), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error_msg(error));
            }
        });
        ApiRequest.getInstance().addToRequestQueue(jsonArrayRequest);
    }

    private JSONObject getJson(GelUrl gelUrl) throws IOException {
        URL url = new URL(gelUrl.toString());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        JSONObject jsonObject = new JSONObject();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             InputStream in = connection.getInputStream()) {

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
                throw new IOException(connection.getResponseMessage() + ": with " + url.toString());

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            jsonObject = new JSONObject(new String(out.toByteArray()));
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
        return jsonObject;
    }

    public List<Post> fetchPostsFromPage(int pid, Set<Tag> tags) {
        GelUrl gelUrl = new GelUrl.Builder(this.credentials)
                .page("dapi")
                .s("post")
                .q("index")
                .json(true)
                .pid(String.valueOf(pid))
                .tags(tags)
                .build();
        List<Post> posts = new ArrayList<>();

        try {
            JSONObject json = getJson(gelUrl);
            JSONArray jsonArray = json.getJSONArray("post");
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
        } catch (JSONException | IOException e) {
            e.printStackTrace();
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
            JSONObject json = getJson(url);
            int length = json.getJSONArray("tag").length();
            for (int i = 0; i < length; i++) {
                JSONObject jsonObject = json.getJSONArray("tag").getJSONObject(i);
                Tag tag = new Tag(jsonObject.getString("name"));

                tag.setType(jsonObject.getString("type"));
                post.addTag(tag);
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    private String error_msg(VolleyError error)
    {
        if (error instanceof NoConnectionError) {
            return "No Internet connection.";
        } else if (error instanceof ServerError) {
            return "The server could not be found.";
        } else if (error instanceof ParseError) {
            return error.getMessage();
        } else if (error instanceof TimeoutError) {
            return "Connection Timeout.";
        }
        return "";
    }
}
