/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CONEXAO_BANCO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Analice
 */
public class Banco_dados {

    public Connection conexao = null;
    private final String driver = "com.mysql.cj.jdbc.Driver";
    private final String nomedobanco = "projeto_java";
    private final String local = "jdbc:mysql://localhost:3306/" + nomedobanco;
    private final String login = "root";
    private final String senha = "root";

    public boolean getConnection() {
        try {
            Class.forName(driver);
            conexao = DriverManager.getConnection(local, login, senha);
            return true;
        } catch (ClassNotFoundException erro) {
            System.out.println("Drive não encontrado" + erro.toString());
            return false;
        } catch (SQLException erro) {
             System.out.println("Falha na conexão" + erro.toString());
            return false;
        }
    }

}
