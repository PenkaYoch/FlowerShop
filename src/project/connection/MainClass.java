package project.connection;

public class MainClass {

	/**
	 * 
	 * @author Penka Yochkova
	 * @author Dida Negasa
	 * 
	 */
	public static void main(String[] args) {		
		DBConnector.loadDatabase();
		MyFrame myFrame = new MyFrame();
		myFrame.setTitle("Магазин за цветя");
	}

}
