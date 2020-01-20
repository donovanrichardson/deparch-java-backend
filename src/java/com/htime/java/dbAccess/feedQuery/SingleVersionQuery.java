package com.htime.java.dbAccess.feedQuery;

import com.htime.java.dbAccess.gtfsController.AppController;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

import static com.schema.Tables.FEED;

public abstract class SingleVersionQuery implements FeedQuery {

    String feedId;
    String versionId;
    JSONObject feedJSON;

    public void addSpecifiedFeeds(AppController ac, String apiKey) throws IOException {
        String requestUrl = String.format("https://api.transitfeeds.com/v1/getFeedVersions?key=%s&feed=%s&page=1&limit=10&err=1&warn=1",apiKey,this.feedId);
        Connection api = Jsoup.connect(requestUrl);
        String jString = api.ignoreContentType(true).get().body().text(); //Throws IOException
        this.feedJSON = new JSONObject(jString);
        JSONObject relevantJSON = this.feedJSON.getJSONObject("results").getJSONArray("versions").getJSONObject(0).getJSONObject("f");

        assert(this.feedId.equals(relevantJSON.getString("id")));
        String feedType = relevantJSON.getString("ty");
        String feedTitle = relevantJSON.getString("t");
        int location = relevantJSON.getJSONObject("l").getInt("id");

        if(!ac.dsl.fetchExists(ac.dsl.selectFrom(FEED).where(FEED.ID.eq(this.feedId)))){
            ac.dsl.insertInto(FEED, FEED.ID, FEED.TYPE, FEED.TITLE, FEED.LOCATION).values(this.feedId, feedType, feedTitle, location).execute();
        }
        //todo see the analogous method in IdFeedQuery because these two methods put together are a mess
        //todo like i literally just copy pasted and so this should be abstracted and then the addig of a feed version should totally be up to a method that consumes a feed_version_id or whatever, and not a feed_id.
    }


}
