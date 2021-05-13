package com.orin.booruviewer.api;

import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.orin.booruviewer.entity.Tag;

import org.json.JSONArray;
import java.util.HashMap;
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

    public void fetchPostsFromPage(int pid, Set<Tag> tags, final ApiCallback callback) {
        GelUrl url = new GelUrl.Builder(this.credentials)
                .page("dapi")
                .s("post")
                .q("index")
                .json(true)
                .pid(String.valueOf(pid))
                .tags(tags)
                .build();

        sendRequest(url, callback);
        System.out.println(url.toString());
    }

    public void fetchTagType(String name, final ApiCallback callback) {
        GelUrl url = new GelUrl.Builder(this.credentials).page("dapi").s("tag").q("index").name(name).json(true).build();
        sendRequest(url, callback);
    }

    public void fetchTagsType(String names, final ApiCallback callback) {
        GelUrl url = new GelUrl.Builder(this.credentials).page("dapi").s("tag").q("index").names(names).order("asc").orderby("name").json(true).build();
        sendRequest(url, callback);
    }

    public void fetchAutocompleteSuggestions(String term, final ApiCallback callback) {
        GelUrl url = new GelUrl.Builder(this.credentials).page("autocomplete2").term(term).type("tag_query").limit("10").build();
        sendRequest(url, callback);
    }

    private static void sendRequest(final GelUrl url, final ApiCallback callback) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url.toString(), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String msg = "";

                if (error instanceof NoConnectionError) {
                    msg = "No Internet connection.";
                } else if (error instanceof ServerError) {
                    msg = "The server could not be found.";
                } else if (error instanceof ParseError) {
                    //msg = "Parsing error.";
                    msg = error.getMessage();
                } else if (error instanceof TimeoutError) {
                    msg = "Connection Timeout.";
                }

                callback.onError(msg);
            }
        });

        ApiRequest.getInstance().addToRequestQueue(jsonArrayRequest);
    }
}
