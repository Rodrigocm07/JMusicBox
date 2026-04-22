package util;

import java.sql.Connection; // Conexão SQL para JAVA
import java.sql.DriverManager;
import java.sql.SQLException;

public class conexao {

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/JMusicBox", 
                    "root", 
                    "root" 
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
