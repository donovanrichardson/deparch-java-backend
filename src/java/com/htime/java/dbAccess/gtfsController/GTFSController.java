package com.htime.java.dbAccess.gtfsController;

import com.schema.tables.records.FeedRecord;
import com.schema.tables.records.FeedVersionRecord;
import com.htime.java.dbAccess.feedQuery.FeedQuery;
import org.jooq.Result;
import org.json.JSONObject;

import java.io.IOException;

public interface GTFSController {

    void addFeeds(FeedQuery q) throws IOException;

    Result<FeedRecord> getFeeds();

    void addFeedVersion(JSONObject feedVersion) throws IOException;

    Result<FeedVersionRecord> getFeedVersions();

    String getLatest(String feed);
}
