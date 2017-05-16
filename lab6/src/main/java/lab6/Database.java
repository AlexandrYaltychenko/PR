package lab6;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.sql.*;

/**
 * Created by alexandr on 14.05.17.
 */
public class Database {
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost/lab6";
    private static final String USER = "lab6";
    private static final String PASS = "labpass";
    public Database() throws Exception {
        Statement stmt = null;
        Class.forName("com.mysql.jdbc.Driver");
    }


    private Connection getConnection(){
        System.out.println("Connecting to database...");
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            return conn;
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }
    public boolean checkCredentials(String login, String password){
        Connection connection = getConnection();
        System.out.println("Creating statement...");
        try {
            Statement statement = connection.createStatement();
            String sql;
            sql = "SELECT 1 FROM users WHERE login ='"+login+"' AND password = '"+ md5Hash(password)+"'";
            ResultSet rs = statement.executeQuery(sql);
            rs.last();
            System.out.println("NUMBER OF ROWS = "+rs.getRow());
            if (rs.getRow()>0) {
                statement.close();
                return true;
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try {
                if (connection != null)
                connection.close();
            } catch (SQLException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    public String md5Hash(String str){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte[] digest = md.digest();
            String myHash = DatatypeConverter
                    .printHexBinary(digest).toUpperCase();
            return myHash;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public String find(String str){

    }
}
