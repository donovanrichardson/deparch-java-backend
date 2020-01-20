package com.htime.java.dbAccess.feedQuery;

import com.htime.java.dbAccess.gtfsController.AppController;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

/**
 * Represents information needed to a identify particular feed version as part of a feed.
 */
public class VersionFeedQuery extends SingleVersionQuery {

    public VersionFeedQuery(String fv) {
        this.versionId = fv;
        this.feedId = fv.replaceAll("^(.+)/([^/]+)$","$1");
    }


    @Override
    public void addSpecifiedFeeds(AppController ac, String apiKey) throws IOException {
        super.addSpecifiedFeeds(ac, apiKey);
        ac.addFeedVersion(this.getFeedVersion(apiKey));
        //todo see the analogous method in IdFeedQuery because these two methods put together are a mess
        //todo like i literally just copy pasted and so this should be abstracted and then the addig of a feed version should totally be up to a method that consumes a feed_version_id or whatever, and not a feed_id.
    }

    private JSONObject getFeedVersion(String apiKey) throws IOException {
        int page = 1;//todo if page is too big
        while (true) {
            String requestUrl = String.format("https://api.transitfeeds.com/v1/getFeedVersions?key=%s&feed=%s&page=%s&limit=100&err=1&warn=1", apiKey, this.feedId, page);
            Connection api = Jsoup.connect(requestUrl);
            String jString = api.ignoreContentType(true).get().body().text(); //Throws IOException
            JSONObject feedObject = new JSONObject(jString);
            int numLeft = feedObject.getJSONObject("results").getInt("total") - ((page - 1) * 100);
            int totalPages = feedObject.getJSONObject("results").getInt("numPages");
            int limit = (numLeft > 100) ? 100 : numLeft;
            JSONArray relevantJSON = feedObject.getJSONObject("results").getJSONArray("versions");

            for(int i = 0; i < limit; i++){
                JSONObject curObj = relevantJSON.getJSONObject(i);
                if (curObj.getString("id").equals(this.versionId)){
                    return curObj;
                }
            }
            page++;
            if (page > totalPages){
                throw new IllegalArgumentException("GTFS feed version not found");//todo maybe use a checked exception
            }
        }
    }
}
