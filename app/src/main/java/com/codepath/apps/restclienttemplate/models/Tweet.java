package com.codepath.apps.restclienttemplate.models;

import android.media.Image;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Parcel
public class Tweet {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public long id;
    public String body;
    public String createdAt;
    public User user;
    public String imageURL;

    public Tweet() {}

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();

        if(jsonObject.has("full_text")) {
            tweet.body = jsonObject.getString("full_text");
        } else {
            tweet.body = jsonObject.getString("text");
        }

        jsonObject.getJSONObject("entities");

        tweet.id = jsonObject.getLong("id");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));

        if (jsonObject.getJSONObject("entities").has("media")) {
            JSONArray mediaArray = jsonObject.getJSONObject("entities").getJSONArray("media");
            if(mediaArray.length() != 0) {
                JSONObject media = mediaArray.getJSONObject(0);
                tweet.imageURL = media.getString("media_url_https");
            }
        }
        return tweet;
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); ++i) {
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }

    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        try {
            long time = sf.parse(rawJsonDate).getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "⋅ just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "⋅ a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return "⋅ " + diff / MINUTE_MILLIS + " m";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "⋅ an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return "⋅ " + diff / HOUR_MILLIS + " h";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "⋅ yesterday";
            } else {
                return "⋅ " + diff / DAY_MILLIS + " d";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }
}