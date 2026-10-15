import java.sql.*;

public class Database {
    private Connection connection;
    private Statement statement;

    public Database() {
        try {
            // Registrasi Driver MySQL secara eksplisit
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Koneksi ke Database
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/db_people",
                    "root",
                    ""
            );
            statement = connection.createStatement();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver MySQL Connector tidak ditemukan! Pastikan file JAR sudah dimasukkan ke lib/Dependencies.", e);
        } catch (SQLException e) {
            throw new RuntimeException("Koneksi Database Gagal: " + e.getMessage(), e);
        }
    }

    public ResultSet selectQuery(String sql) {
        try {
            statement.executeQuery(sql);
            return statement.getResultSet();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int insertUpdateDeleteQuery(String sql) {
        try {
            return statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Statement getStatement() {
        return statement;
    }
}