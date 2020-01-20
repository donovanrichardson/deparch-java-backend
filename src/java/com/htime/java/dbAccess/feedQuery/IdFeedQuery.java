package com.htime.java.dbAccess.feedQuery;

import com.htime.java.dbAccess.gtfsController.AppController;

import java.io.IOException;

/**
 * Represents info sufficient to retrieve a singular GTFS feed, the most recent one with a specified feedId.
 */
public class IdFeedQuery extends SingleVersionQuery {

    /**
     *
     * @param feedId a single feed's id.
     */
    public IdFeedQuery(String feedId) {
        this.feedId = feedId;
    }

    @Override
    public void addSpecifiedFeeds(AppController ac, String apiKey) throws IOException {
        super.addSpecifiedFeeds(ac, apiKey);
        ac.addFeedVersion(this.feedJSON.getJSONObject("results").getJSONArray("versions").getJSONObject(0));
    }
}
