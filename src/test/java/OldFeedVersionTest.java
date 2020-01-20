

import com.htime.java.dbAccess.gtfsController.AppController;
import com.htime.java.dbAccess.feedQuery.FeedQuery;
import com.htime.java.dbAccess.gtfsController.GTFSController;
import com.htime.java.dbAccess.feedQuery.VersionFeedQuery;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

import static com.schema.tables.StopTime.STOP_TIME;
import static org.jooq.impl.DSL.count;
import static org.junit.jupiter.api.Assertions.fail;

public class OldFeedVersionTest{


//    String fv;
//    String fid;
    String username;
    File access = new File("access.txt");
    String password;
    String apiKey;
    DSLContext dsl;
    GTFSController c;


    void setup() throws FileNotFoundException, SQLException {
        Scanner s = new Scanner(access);
        username = s.nextLine();
        password = s.nextLine();
        apiKey = s.nextLine();
        java.sql.Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/gtfs?autoReconnect=true&useSSL=false&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", username, password);
        Configuration conf = new DefaultConfiguration().set(conn).set(SQLDialect.MYSQL_8_0);
        this.dsl = DSL.using(conf);
        this.c = new AppController(this.username, this.password, this.apiKey);
    }

    @Test
    public void addOldVersionTest(){
        String feedV = "mta/86/20191231";
        String feeID = "mta/86";
        FeedQuery old = new VersionFeedQuery(feedV);
        try {
            try {
                this.setup();
                Integer i =this.dsl.select(count()).from(STOP_TIME).where(STOP_TIME.FEED_VERSION.eq(feedV)).groupBy(STOP_TIME.FEED_VERSION).fetchOne(0, Integer.class);
//                assertEquals(0,i);
                this.c.addFeeds(old);
                Integer j =this.dsl.select(count()).from(STOP_TIME).where(STOP_TIME.FEED_VERSION.eq(feedV)).groupBy(STOP_TIME.FEED_VERSION).fetchOne(0, Integer.class);
                //todo j is larger than i.
                System.out.println(j);
                assert(j > 0);
            } catch (IOException | SQLException e) {
                fail();
                throw e;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        finally{
//            this.dsl.deleteFrom(FEED).where(FEED.ID.eq(fid)); todo this isn't working

        }

    }

}
