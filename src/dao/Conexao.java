package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {
    
   
    private static final String URL = "jdbc:mysql://localhost:3306/banco_digital?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    
    private static final String USER = "root"; 
    private static final String PASSWORD = "senha124578?"; //  USAR SUA SENHA

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao conectar ao banco de dados", e);
        }
    }
}