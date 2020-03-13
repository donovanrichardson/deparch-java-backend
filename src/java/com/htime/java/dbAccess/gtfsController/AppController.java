package com.htime.java.dbAccess.gtfsController;

import com.mysql.cj.jdbc.ConnectionImpl;
import com.schema.tables.FeedVersion;
import com.schema.tables.Stop;
import com.schema.tables.records.FeedRecord;
import com.schema.tables.records.FeedVersionRecord;
import com.htime.java.dbAccess.feedQuery.FeedQuery;
import com.htime.java.dbAccess.importer.GtfsImporter;
import com.htime.java.dbAccess.wrapper.exception.OptionalTableException;
import com.schema.tables.records.RouteRecord;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.types.UByte;
import org.jooq.types.ULong;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;


import java.io.*;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.schema.Tables.FEED;
import static com.schema.Tables.FEED_VERSION;
import static com.schema.tables.Route.ROUTE;
import static com.schema.tables.Stop.STOP;
import static org.junit.jupiter.api.Assertions.fail;

public class AppController implements GTFSController {

    String dbuser;
    String dbpass;
    String apiKey;
    public DSLContext dsl;
    private static final String[] order = {"agency.txt", "stops.txt", "routes.txt", "calendar.txt", "calendar_dates.txt", "shapes.txt", "trips.txt", "frequencies.txt", "stop_times.txt"};

    public AppController(String dbuser, String dbpass, String apiKey) throws SQLException {
        this.dbuser = dbuser;
        this.dbpass = dbpass;
        this.apiKey = apiKey;
        String host = "database-1.c2skpltdp2me.us-east-2.rds.amazonaws.com";
//        String host = "localhost";
//        System.out.println(this.dbuser);
//        System.out.println(this.dbpass);
//        System.out.println(this.apiKey);
        java.sql.Connection conn = DriverManager.getConnection("jdbc:mysql://"+host+":3306/gtfs?autoReconnect=true&useSSL=false&useUnicode=true&useLegacyDatetimeCode=false&autoCommit=false&relaxAutoCommit=true", this.dbuser, this.dbpass); //In earlier version I was causing the time zones to be switched without warrant.
        Configuration conf = new DefaultConfiguration().set(conn).set(SQLDialect.MYSQL_8_0);
        ConnectionImpl cImpl = (ConnectionImpl)conf.connectionProvider().acquire();
        cImpl.getSession().getServerSession().setAutoCommit(false);
        Configuration conf2 = new DefaultConfiguration().set(cImpl).set(SQLDialect.MYSQL_8_0);
        this.dsl = DSL.using(conf2);
    }

    @Override
    public void addFeeds(FeedQuery q) throws IOException {

        q.addSpecifiedFeeds(this, this.apiKey);

    }

    private JSONObject  getFeedJSON(String feedId) throws IOException {
        String requestUrl = String.format("https://api.transitfeeds.com/v1/getFeedVersions?key=%s&feed=%s&page=1&limit=10&err=1&warn=1",this.apiKey,feedId);
        Connection api = Jsoup.connect(requestUrl);
        String jString = api.ignoreContentType(true).get().body().text(); //Throws IOException
        return new JSONObject(jString);
    }

    @Override
    public Result<FeedRecord> getFeeds() {
        return dsl.selectFrom(FEED).fetch();
    }

    @Override
    public void addFeedVersion(JSONObject feedVersion) throws IOException {
        FeedVersion fv = FEED_VERSION;
        String feedId = feedVersion.getJSONObject("f").getString("id");
        String versionId = feedVersion.getString("id");
        ULong timestamp = ULong.valueOf(feedVersion.getLong("ts"));
        ULong size = ULong.valueOf(feedVersion.getLong("size"));
        String downloadUrl = feedVersion.getString("url");
        String start = feedVersion.getJSONObject("d").getString("s");//these two will need to be cast to unsigned to be at all useful
        String finish = feedVersion.getJSONObject("d").getString("f");

        dsl.insertInto(fv, fv.ID, fv.FEED, fv.TIMESTAMP, fv.SIZE, fv.URL, fv.START, fv.FINISH).values(versionId, feedId, timestamp, size, downloadUrl, start, finish).execute();

        try(ZipInputStream verZip = this.getGtfsZip(versionId)){ //IOException may be thrown on this line
            GtfsImporter imp = new GtfsImporter(this.dsl);
            Map<String, InputStream> zipMap;
            try{
                zipMap = this.unzip(verZip);
            } catch (IOException e){
                throw e;
            } finally{
                verZip.close();
            }
            try {
                for (String txt : order) {
                    try{
                        imp.addTxt(txt, zipMap.get(txt)); //IOException may be thrown on this line
                    } catch (OptionalTableException o){
                        continue;
                    }
                }
                UpdateConditionStep routeUpdate = this.dsl.update(ROUTE).set(ROUTE.DEFAULT_NAME, ROUTE.ROUTE_LONG_NAME).where(ROUTE.DEFAULT_NAME.isNull());
                routeUpdate.execute();
                UpdateConditionStep stopUpdate = this.dsl.update(STOP).set(STOP.STOP_NAME, STOP.STOP_ID).where(STOP.STOP_NAME.isNull());
                stopUpdate.execute();
                UpdateConditionStep feedUpdate = this.dsl.update(FEED).set(FEED.LATEST, versionId).where(FEED.ID.eq(feedId));
                feedUpdate.execute();
//                UPDATE `stop`
//SET stop.parent_station = stop.stop_id
//WHERE stop.parent_station is null;
                UpdateConditionStep nullToSelf = this.dsl.update(STOP).set(STOP.PARENT_STATION, STOP.STOP_ID).where(STOP.PARENT_STATION.isNull());
                nullToSelf.execute();

                //UPDATE `stop`
                //left join `stop` as parent_stop ON stop.parent_station = parent_stop.stop_id
                //SET stop.parent_station = parent_stop.parent_station
                //WHERE stop.location_type = 4;
                Stop parent = STOP.as("parent");
                UpdateConditionStep boardingAreaToStation = this.dsl.update(STOP.leftJoin(parent).on(STOP.PARENT_STATION.eq(parent.STOP_ID))).set(STOP.PARENT_STATION, parent.PARENT_STATION).where(STOP.LOCATION_TYPE.eq(UByte.valueOf(4)));
                boardingAreaToStation.execute();

            } catch (IOException e){
                this.dsl.deleteFrom(FEED_VERSION).where(FEED_VERSION.ID.eq(versionId)).execute();
                e.printStackTrace();
                System.out.println(String.format("All records associated with %s have been removed to avoid incomplete data import", versionId));
            }
        }catch (IOException io1){
            System.out.println("Could not retrieve the GTFS zip file.");
            throw io1;
        }finally{
            this.dsl.close();
        }

    }

    @Override
    public Result<FeedVersionRecord> getFeedVersions() {
        return dsl.selectFrom(FEED_VERSION).fetch();
    }

    /**
     * gets the datebase's latest feed version id from a feed
     * @param feed feed id
     * @return
     */
    @Override
    public String getLatest(String feed) {
        return this.dsl.select(FEED_VERSION.ID).from(FEED_VERSION).where(FEED_VERSION.FEED.eq(feed)).orderBy(FEED_VERSION.TIMESTAMP.desc()).fetchOne(FEED_VERSION.ID);
    }

//    @Override
//    public void importFeeds() {
//
//        List<String> importVersions = this.dsl.select(FEED_VERSION.ID, AGENCY.KEY).from(FEED_VERSION.leftJoin(AGENCY).on(AGENCY.FEED_VERSION.eq(FEED_VERSION.ID))).where(AGENCY.FEED_VERSION.isNull()).fetch().getValues(FEED_VERSION.ID);
//
//        for(String v : importVersions){
//            try(ZipInputStream verZip = this.getGtfsZip(v)){
//                GtfsImporter imp = new GtfsImporter(this.dsl);
//                Map<String, InputStream> zipMap = this.unzip(verZip);
//                for(String txt : order){
//                    imp.addTxt(txt, zipMap.get(txt));
//                }
//            }
//        }
//
//    }

    private Map<String, InputStream> unzip(ZipInputStream verZip) throws IOException {

        Map<String, InputStream> result = new HashMap<>();

        while (true) {
            ZipEntry entry;
            byte[] b = new byte[1024];
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int l;

            entry = verZip.getNextEntry();//Might throw IOException

            if (entry == null) {
                break;
            }

            try {
                while ((l = verZip.read(b)) > 0) {
                    out.write(b, 0, l);
                }
                out.flush();
            }catch(EOFException e){
                e.printStackTrace();
            }
            catch (IOException i) {
                System.out.println("there was an ioexception");
                i.printStackTrace();
                fail();
            }
            result.put(entry.getName(), new ByteArrayInputStream(out.toByteArray()));
        }
        return result;
    }

    private ZipInputStream getGtfsZip(String v) throws IOException {
        String apiUrl = this.dsl.select(FEED_VERSION.URL).from(FEED_VERSION).where(FEED_VERSION.ID.eq(v)).fetchOne(FEED_VERSION.URL);
        Connection zipConnect = Jsoup.connect(apiUrl);
        Connection notEnoughBytes = zipConnect.ignoreContentType(true);
        Connection.Request requestSize = notEnoughBytes.request();
        requestSize.maxBodySize(1073741824);
        Connection.Response almostZipStream = notEnoughBytes.request(requestSize).execute();
        ZipInputStream zis = new ZipInputStream(almostZipStream.bodyStream());
        return zis;
    }
}
