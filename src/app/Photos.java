package app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.*;
import view.*;

/**
 * Controls startup of Photos application
 * @author Jason Mooney (jcm370)
 * @author Crystal Zhang (cz298)
 *
 */

public class Photos extends Application implements Serializable {
	
	/** serial ID for version */
	private static final long serialVersionUID = 1L;
	/** list of all the application's users */
	private ArrayList<User> users;
	/** temporary stage for scene changes */
	private static Stage tempStage;
	
	/**
	 * Constructor for Photos, generates an instance containing stock images
	 */
	public Photos() {
		User stock = new User("stock");
		Album stockAlbum = new Album("stock");
		
		stockAlbum.addPhoto(new Photo("data/stock1.jpg"));
		stockAlbum.addPhoto(new Photo("data/stock2.jpg"));
		stockAlbum.addPhoto(new Photo("data/stock3.jpg"));
		stockAlbum.addPhoto(new Photo("data/stock4.jpg"));
		stockAlbum.addPhoto(new Photo("data/stock5.jpg"));
		stock.albums.add(stockAlbum);
		
		users = new ArrayList<User>();
		users.add(stock);
	}
	/**
	 * Sets the stage to login scene, which is out root for the application
	 * @param primaryStage main stage for the application to load scenes on
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		tempStage = primaryStage;
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Photos.class.getResource("/view/login.fxml"));
		
		Parent root = (Parent) loader.load();
		LoginController loginController = loader.getController();
		loginController.start(primaryStage);
		
		Scene scene = new Scene(root, 900, 600);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Photos");
		primaryStage.setResizable(false);
		primaryStage.show();
	}
	
	/**
	 * Launches application
	 * @param args no command-line arguments for this application
	 */
	public static void main(String[] args) {
		launch(args);
	}
	
	/**
	 * Changes the scene based on given fxml file
	 * @param fxml filename of fxml file the scene is being changed to
	 * @param arg1 the name of the current user being loaded (when applicable).
	 * @param arg2 the name of the current album being loaded (when applicable).
	 * @throws Exception
	 */
	public void changeScene(String fxml, String arg1, String arg2) throws Exception {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Photos.class.getResource("/view/" + fxml));
		Parent pane = (Parent) loader.load();
		
		switch (fxml) {
		case "admin.fxml": 
			AdminController adminController = loader.getController();
			adminController.start(tempStage);
			break;
		case "albums.fxml":
			AlbumsController albumsController = loader.getController();
			albumsController.start(tempStage, arg1);
			break;
		case "login.fxml":
			LoginController loginController = loader.getController();
			loginController.start(tempStage);
			break;
		case "photos.fxml":
			PhotosController photosController = loader.getController();
			photosController.start(tempStage, arg1, arg2);
			break;
		case "search.fxml":
			SearchController searchController = loader.getController();
			searchController.start(tempStage, arg1);
			break;
		}
		
		tempStage.getScene().setRoot(pane);
		tempStage.show();
	}
	
	/**
	 * Reads the serialized version of the previous session, or creates a new file to store for future sessions
	 * @return an instance of Photos from the previous session
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Photos readApp() throws IOException, ClassNotFoundException {
		try {
			ObjectInputStream ois = new ObjectInputStream(
					new FileInputStream("src/model/users.dat"));
			Photos ph = (Photos) ois.readObject();
			return ph;
		} catch (Exception e) {
			File newFile = new File("src/model/users.dat");
			newFile.createNewFile();
			return new Photos();
		}
	}
	
	/**
	 * Writes the current session to the data file
	 * @param ph the current instance of the Photos application
	 * @throws IOException
	 */
	public static void writeApp(Photos ph) throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(
				new FileOutputStream("src/model/users.dat"));
		oos.writeObject(ph);
	}
	
	/**
	 * Checks if the given User is registered in the application
	 * @param str the name of the User being searched for
	 * @return whether the User exists in the application
	 */
	public boolean contains(String str) {
		for (User us : users) {
			if (us.name.equals(str)) return true;
		}
		return false;
	}
	
	/**
	 * Adds the given user to the application's User list
	 * @param newUser the new User
	 */
	public void addUser(User newUser) {
		users.add(newUser);
	}
	
	/**
	 * Removes the given user from the application's user list
	 * @param currUser the given User
	 */
	public void removeUser(User currUser) {
		users.remove(currUser);
	}
	
	/**
	 * Returns an instance of the User based off the given username
	 * @param str the username being searched for
	 * @return an instance of the User from the application's User list
	 */
	public User getUser(String str) {
		for (User us : users) {
			if (us.name.equals(str)) return us;
		}
		return null;
	}
	
	/**
	 * Returns the application's current User list
	 * @return the current User list
	 */
	public ArrayList<User> getUsers() {
		return users;
	}
}
