package project.connection;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.table.TableModel;

public class DBConnector {
	
	public static Connection conn = null;
	static String pathFile = null;
	
	public static Connection getConnection() {
		
		try {
			Class.forName("org.h2.Driver");
			conn = DriverManager.getConnection("jdbc:h2:tcp://localhost/" + pathFile, "sa","" );
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			return conn;
		}
		
	}
	
	public static void loadDatabase(){
		ArrayList<String> server = null ;
		try {
			Path toFile = Paths.get("src\\database\\files\\dataBaseFile.txt");
			server =(ArrayList<String>) Files.readAllLines(toFile);
			pathFile = server.get(0).replace("database=", "").replace(";", "");
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
}
