

import com.htime.java.dbAccess.table.FeedTable;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.fail;

public class TimetableTest extends OldFeedVersionTest {

    @Test
    public void feedTableTest(){//todo this test requires addOldVersionTest to be run first
        int year = 2020;
        int month = 2;
        int date = 10;
        try{
            super.setup();
//            Calendar cal = new Calendar.Builder().setDate(year,month - 1,date).build();
//            System.out.println(cal.get(Calendar.YEAR));
//            System.out.println(String.format("%02d", 12));
            FeedTable roamFT = new FeedTable("mta/86/20191231", this.dsl);
            /*StopTime subq = STOP_TIME.as("sq");
            System.out.println(dsl.selectDistinct(STOP_TIME.STOP_ID, STOP.STOP_NAME).from(STOP_TIME.leftJoin(STOP).on(STOP.STOP_ID.eq(STOP_TIME.STOP_ID))).whereExists(dsl.selectFrom(subq).where(subq.STOP_ID.eq("2428685").and(STOP_TIME.STOP_SEQUENCE.greaterThan(subq.STOP_SEQUENCE)).and(STOP_TIME.TRIP_ID.eq(subq.TRIP_ID)))).orderBy(STOP.STOP_NAME.asc()).fetch().toString());*/
//            roamFT.getRoutes();
//            roamFT.setRoute(routeId);
//            roamFT.getStops();
            System.out.println(roamFT.getStops("ff"));
            roamFT.setOrigin("91");
            System.out.println(roamFT.getDestinations());
            roamFT.setDestination("237");
            roamFT.setDate(year, month, date);
            String roamJSON = roamFT.getTimetable();
            System.out.println(roamJSON);
        }catch (IOException | SQLException e) {
            fail();
        }
    }

}
