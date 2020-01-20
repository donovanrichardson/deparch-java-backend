import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.schema.tables.Agency.AGENCY;
import static org.junit.jupiter.api.Assertions.fail;


public class ApiAndDbAccessTest extends AbstractTest {

    @Test
    public void accessExistsTest(){
        try{
            setup();
            assert(username.length() > 0);
            assert(password.length() > 0);
            assert(apiKey.length() > 0);
        }catch (Exception e){
            fail();
        }
    }

    @Test
    public void apiJSONTest(){
        try{
            setup();
        }catch(Exception e){
            fail();
        }
        Connection api = Jsoup.connect("https://api.transitfeeds.com/v1/getFeeds?key=f700defc-6fcc-4c3f-9045-5ac5e91d7623&location=93&descendants=1&page=1&limit=20");

        try {
            String jString = api.ignoreContentType(true).get().body().text();
//            System.out.println(jString);
            JSONObject jSon = new JSONObject(jString);
            System.out.println(jSon.toString());
            JSONArray feeds = jSon.getJSONObject("results").getJSONArray("feeds");
            for(int i = 0; i < feeds.length(); i++){
                JSONObject feed = feeds.getJSONObject(i);
                System.out.println(feed.optString("t"));
            }
            assert(feeds.length() > 11);
        }catch(IOException | JSONException e){
            fail();
        }
    }

    @Test
    public void localZipTest() throws IOException{
        File ruby = new File("ruby.zip");
        FileInputStream rfis = new FileInputStream(ruby);
        ZipInputStream zis = new ZipInputStream(rfis);
        List<InputStream> files = new ArrayList<>();
        int count = 0;

        while(true){
            ZipEntry entry;
            byte[] b = new byte[64];
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int l;
            try {
                entry = zis.getNextEntry(); //use in another method to name the files
            }catch(IOException e){
                break;
            }
            if(entry == null){
                break;
            }
            System.out.println(entry.getName());
            while((l = zis.read(b))>0){
                out.write(b,0,l);
                count++;
//                System.out.println(count);
            }
            System.out.println(out.toString());
            files.add(new ByteArrayInputStream(out.toByteArray()));
        }

        for(InputStream f : files){
//            Scanner s = new Scanner(f);
            System.out.println(f);
        }
    }

    @Test
    public void apiZipTest(){
        try{
            setup();
        }catch(Exception e){
            fail();
        }
        Connection api = Jsoup.connect("https://api.transitfeeds.com/v1/getLatestFeedVersion?key=f700defc-6fcc-4c3f-9045-5ac5e91d7623&feed=mbta%2F64");

        Connection.Response thedata = null;
        try {
            thedata = api.ignoreContentType(true).execute();
        } catch (IOException e) {
            fail();
        }
        assert(thedata != null);
        ZipInputStream zis = new ZipInputStream(thedata.bodyStream());
        List<InputStream> files = new ArrayList<>();

        while (true) {
            ZipEntry entry;
            byte[] b = new byte[1024];
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int l;

            try {
                entry = zis.getNextEntry();
            } catch (IOException e) {
                break;
            }

            if (entry == null) {
                break;
            }

            System.out.println(entry.getName());
            try {
                while ((l = zis.read(b)) > 0) {
                    out.write(b, 0, l);
                }
            }catch(EOFException e){
                e.printStackTrace();
            }
            catch (IOException i) {
                System.out.println("there was an ioexception");
                i.printStackTrace();
                fail();
            }
            files.add(new ByteArrayInputStream(out.toByteArray()));
        }

        for(InputStream f : files){
            System.out.println(f);
//            Scanner s = new Scanner(f);
//            System.out.println(s.nextLine());
        }
        try{
            java.sql.Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/lexicon?autoReconnect=true&useSSL=false&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", username, password);
            Configuration conf = new DefaultConfiguration().set(conn).set(SQLDialect.MYSQL_8_0);
            DSLContext dsl = DSL.using(conf);
            InputStream agency = files.get(0);
            List<Field<?>> csvFields = new ArrayList<>();
            csvFields.add(AGENCY.AGENCY_ID);
            csvFields.add(AGENCY.AGENCY_NAME);
            csvFields.add(AGENCY.AGENCY_URL);
            csvFields.add(AGENCY.AGENCY_TIMEZONE);
            csvFields.add(null);
            csvFields.add(null);
//            Scanner scansion = new Scanner(agency);
//            while(scansion.hasNextLine()){
//                System.out.println(scansion.nextLine()+"the_Line");
//            }
            dsl.loadInto(AGENCY).loadCSV(new FileInputStream(new File("/Users/donovanrichardson/Downloads/gtfs-1/agency.txt"))).fields(AGENCY.AGENCY_ID, AGENCY.AGENCY_NAME,AGENCY.AGENCY_URL,AGENCY.AGENCY_TIMEZONE,null,null).execute();
            System.out.println(dsl.selectFrom(AGENCY).fetch());
        }catch(SQLException sql){
            sql.printStackTrace();
        }catch(IOException ioexc){
            ioexc.printStackTrace();
        }


//        try{
//            Connection.Response theData = api.ignoreContentType(true).execute();
////            System.out.println(theData.body());
//            ZipInputStream theZip = new ZipInputStream(theData.bodyStream());
//            List<ZipEntry> entries = new ArrayList();
//            while(true){
//                ZipEntry entry;
//                int num = 0;
//                try{
//                    entry = theZip.getNextEntry();
//                }catch(EOFException e){
////                    i was thinking that EOF occurred because a zip entry's size was zero. now i'm thinking it's because the zip file is all read through but the .getNextEntry method won't reutrn null.
//                    break;
//                }
//                if(entry != null){
//                    entries.add(entry);
//                }else {
//                    break;
//                }
//            }
//            for(ZipEntry e : entries){
//                System.out.println(e.getName());
//            }
//            Scanner sc = new Scanner(theZip);
//            System.out.println(sc.nextLine());
//            System.out.println(sc.nextLine());
//            System.out.println(sc.nextLine());
//            theZip.close();
//        }catch(IOException e){
//            e.printStackTrace();
//            fail();
//        }




    }


}
