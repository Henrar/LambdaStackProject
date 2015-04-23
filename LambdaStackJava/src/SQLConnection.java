import java.sql.*;
import javax.sql.*;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
/**
 * Created by Henrar on 2015-04-23.
 */
public class SQLConnection {
    public static void testConnection(){
        MysqlDataSource ds = new MysqlDataSource();
        ds.setUser("spark");
        ds.setPassword("spark");
        ds.setServerName("172.17.84.79");
        ds.setDatabaseName("sCache");


        try(Connection c = ds.getConnection();
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery("select * from TWEET_COUNT")){


            while(rs.next()){
                System.out.println(rs.getString(1));
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }
    }
}
