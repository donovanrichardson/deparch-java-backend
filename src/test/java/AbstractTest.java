import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class AbstractTest {

    File access = new File("access.txt");
    String username;
    String password;
    String apiKey;
    DSLContext dsl;

    void setup() throws FileNotFoundException, SQLException {
        Scanner s = new Scanner(access);
        username = s.nextLine();
        password = s.nextLine();
        apiKey = s.nextLine();
        java.sql.Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/gtfs?autoReconnect=true&useSSL=false&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", username, password);
        Configuration conf = new DefaultConfiguration().set(conn).set(SQLDialect.MYSQL_8_0);
        this.dsl = DSL.using(conf);
    }

    boolean matchesLine(String multiLine, String regex){
        Scanner s = new Scanner(multiLine);
        boolean match = false;
        while(!match && s.hasNextLine()){
            match = s.nextLine().matches(regex);
        }
        return match;

    }
}
