package com.my.jokeswidget.httpclient;

import android.content.Context;
import android.util.Pair;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;

public class JokesClient {

    private Pair<String, String> joke;

    public void fetchJokeFromServer(Context context, final VolleyCallback callback) {
        String url = "https://official-joke-api.appspot.com/jokes/random";
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        String setup = (response.getString("setup"));
                        String punchline = (response.getString("punchline"));
                        joke = new Pair<>(setup, punchline);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    callback.onSuccess(true);
                },
                error -> {
                    System.out.println(error);
                    callback.onSuccess(false);
        });

        VolleyRQSingleton.getInstance(context).addToRequestQueue(jsonRequest);
    }

    public Pair<String, String> joke(){
        return  joke;
    }
}

