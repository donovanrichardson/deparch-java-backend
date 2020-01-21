package com.htime.application;

import com.htime.java.dbAccess.gtfsController.AppController;
import com.htime.java.dbAccess.gtfsController.GTFSController;
import com.htime.java.dbAccess.table.FeedTable;
import com.htime.java.dbAccess.feedQuery.VersionFeedQuery;
import org.jooq.DSLContext;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

import static com.schema.tables.FeedVersion.FEED_VERSION;
import static org.jooq.impl.DSL.count;

public class Main {

    public static void main(String[] args){
        Scanner s = new Scanner(System.in);
        System.out.println("provide db username");
        String user = s.nextLine();
        System.out.println("provide db password");//todo what if incorrect credentials
        String pass = s.nextLine();
        DSLContext dsl = null;
        try{
            GTFSController ac = new AppController(user, pass, "f700defc-6fcc-4c3f-9045-5ac5e91d7623");
            dsl = ((AppController) ac).dsl; //todo lol this casting is wild, just make a method
            while(true){
                System.out.println("press n for new feed, t for timetable, or q for quit");
                String option = s.nextLine();
                if (option.equals("n")){
                    System.out.println("what is the feed version id for the new feed?");
                    String id = s.nextLine();
                    if(dsl.select(count()).from(FEED_VERSION).where(FEED_VERSION.ID.eq(id)).fetchOne(0, Integer.class)<1){
                        ac.addFeeds(new VersionFeedQuery(id));
                    }else{
                        System.out.println("that feed version has already been added");
                    }
                } else if (option.equals("t")){
                    System.out.println("what is the feed version id for the feed you'd like to query?");
                    String ver = s.nextLine();
                    FeedTable ft = new FeedTable(ver, dsl);
                    System.out.println("what is the origin?");
                    String ori = s.nextLine();
                    ft.setOrigin(ori);
                    System.out.println("what is the destination?");
                    String dest = s.nextLine();
                    ft.setDestination(dest);
                    System.out.println("what is the year?");
                    int year = Integer.valueOf(s.nextLine());
                    System.out.println("what is the month?");
                    int mon = Integer.valueOf(s.nextLine());
                    System.out.println("what is the date?");
                    int dat = Integer.valueOf(s.nextLine());
                    ft.setDate(year,mon,dat);
                    System.out.print(ft.getTimetable());
                } else if (option.equals("q")){
                    System.out.println("bye-bye!");
                    break;
                }else{
                    continue; //superfluous code
                }
            }
        }catch (SQLException | IOException e){
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            s.close();
            if (dsl != null){
                dsl.close();
            }
        }

    }

}
