package in.thunk.quartz.test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;

public class MainClass {
  public static void main(String[] args) {
    Connection connection = null;
    Statement statement = null;
    try {
      Class.forName("org.hsqldb.jdbcDriver").newInstance();
      String url = "jdbc:hsqldb:hsql://localhost/quartz-test-db";
      connection = DriverManager.getConnection(url, "SA", "");
      connection.setAutoCommit(false);

      statement = connection.createStatement();

      ResultSet result = statement.executeQuery("SELECT * FROM PUBLIC.QRTZ_LOCKS");
      while(result.next()) {
    	  System.out.println(result.getString(1));
      }
      
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (statement != null) {
        try {
          statement.close();
        } catch (SQLException e) {
        } // nothing we can do
      }
      if (connection != null) {
        try {
          connection.close();
        } catch (SQLException e) {
        } // nothing we can do
      }
    }
  }
}
