import com.schema.tables.records.FeedRecord;
import com.htime.java.dbAccess.gtfsController.AppController;
import com.htime.java.dbAccess.feedQuery.FeedQuery;
import com.htime.java.dbAccess.gtfsController.GTFSController;
import com.htime.java.dbAccess.feedQuery.IdFeedQuery;
import org.jooq.Result;

import java.io.FileNotFoundException;
import java.sql.SQLException;

public class FeedMembersTest extends AbstractTest{

    private String wstring = "bus-it-waikato/1226";
    private String matchString = ".*bus-it-waikato/1226.*";
    private FeedQuery waikato;
    private GTFSController c;
    private Result<FeedRecord> r1;
    private Result<FeedRecord> r2;

    @Override
    void setup() throws FileNotFoundException, SQLException {
        super.setup();
        waikato = new IdFeedQuery(wstring);
        c = new AppController(this.username, this.password, this.apiKey);
    }


    public FeedMembersTest() throws SQLException {
    }

//    void addFeed() throws IOException { //maybe should not be a test
//        this.c.addFeeds(this.waikato);
//        this.r1 = c.getFeeds();
//    }
//
//    void addFeedVersion() throws IOException {
//        for (FeedRecord f : r1){
//            c.addFeedVersion(f);
//        }
//   }
//
//    @Test
//    public void fmTest(){
//        try{
//            this.setup();
//        } catch (IOException | SQLException e){
//            e.printStackTrace();
//            fail();
//        }
//        try {
//            this.addFeed();
//            try{
//                this.addFeed();
//            }catch(Exception e){
//                assertEquals(DataAccessException.class, e.getClass());
//            }
//            this.addFeedVersion();
//            Result<FeedVersionRecord> versions = c.getFeedVersions();
//            System.out.print(versions.toString());
//            assert(this.matchesLine(versions.toString(), matchString));
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            fail();
//        } catch(Exception e){
//            throw e;
//        }finally{
//            this.dsl.delete(FEED).where(FEED.ID.eq("bus-it-waikato/1226")).execute(); // lol it isn't working THAtS BecAuse You didNt put eXecUTE aT tHE enD
//        }
//
//
//
//
//
//    }

}
