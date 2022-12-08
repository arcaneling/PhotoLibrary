package view;

import java.io.IOException;
import java.util.Optional;
import app.Photos;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import model.*;



/**
 * Controller class for user album subsystem	
 * @author Jason Mooney (jcm370)
 * @author Crystal Zhang (cz298)
 * 
 */

public class AlbumsController {
	/** Instance of Photos application */
	private Photos ph;
	/** Current User viewing their Albums */
	private User currUser;
	/** ObservableList setup for ListView of Albums */
	private ObservableList<Album> obsList;
	
	/** ListView setup for Albums */
	@FXML private ListView<Album> listView;
	
	/** TextField to make new Album */
	@FXML private TextField albumName;
	/** TextField to rename existing Album */
	@FXML private TextField renameAlbum;
	
	/**
	 * Sets up ListView of current Albums
	 * @param mainStage main stage for the application to load scenes on
	 * @param curr name of the current User
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public void start(Stage mainStage, String curr) throws ClassNotFoundException, IOException {
		ph = Photos.readApp();
		currUser = ph.getUser(curr);
		obsList = FXCollections.observableArrayList(currUser.getAlbums());
		
		listView.setItems(obsList);
		listView.getSelectionModel().select(0);
	}
	
	/**
	 * Opens the selected Album
	 * @throws Exception
	 */
	public void open() throws Exception {
		if (listView.getSelectionModel().getSelectedItem() == null) {
			Alert violation = new Alert(AlertType.INFORMATION);
			violation.setTitle("Error");
			violation.setHeaderText("Open failed: No album selected");
			violation.showAndWait();
			return;
		}
		
		Album currAlbum = listView.getSelectionModel().getSelectedItem();
		ph.changeScene("photos.fxml", currUser.name, currAlbum.name);
	}
	
	/**
	 * Adds a new empty Album to the User's list
	 * @throws IOException
	 */
	public void add() throws IOException {
		String alb = albumName.getText().toString();
		if (alb.length() == 0) {
			Alert violation = new Alert(AlertType.INFORMATION);
			violation.setTitle("Error");
			violation.setHeaderText("Add failed: Invalid album name");
			violation.showAndWait();
			return;
		}
		if (currUser.contains(alb)) {
			Alert violation = new Alert(AlertType.INFORMATION);
			violation.setTitle("Error");
			violation.setHeaderText("Add failed: Album already exists");
			violation.showAndWait();
			return;
		}
		
		Album newAlbum = new Album(alb); 
		currUser.addAlbum(newAlbum);
		obsList.add(newAlbum);
		listView.getSelectionModel().select(newAlbum);
		albumName.setText("");
		
		Photos.writeApp(ph);
	}
	
	/**
	 * Renames the selected Album
	 * @throws IOException
	 */
	public void rename() throws IOException {
		if (listView.getSelectionModel().getSelectedItem() == null) {
			Alert violation = new Alert(AlertType.INFORMATION);
			violation.setTitle("Error");
			violation.setHeaderText("Rename failed: No album selected");
			violation.showAndWait();
			return;
		}
		
		String newName = renameAlbum.getText().toString();
		Album curr = listView.getSelectionModel().getSelectedItem();
		int index = listView.getSelectionModel().getSelectedIndex();
		
		if (newName.length() == 0) {
			Alert violation = new Alert(AlertType.INFORMATION);
			violation.setTitle("Error");
			violation.setHeaderText("Rename failed: Invalid album name");
			violation.showAndWait();
			return;
		}
		if (currUser.contains(newName)) {
			Alert violation = new Alert(AlertType.INFORMATION);
			violation.setTitle("Error");
			violation.setHeaderText("Rename failed: Album already exists");
			violation.showAndWait();
			return;
		}
		
		currUser.renameAlbum(curr, newName);
		obsList.set(index, curr);
		renameAlbum.setText("");
		
		Photos.writeApp(ph);
	}
	
	/**
	 * Deletes the selected Album
	 * @throws IOException
	 */
	public void delete() throws IOException {
		if (listView.getSelectionModel().getSelectedItem() == null) {
			Alert violation = new Alert(AlertType.INFORMATION);
			violation.setTitle("Error");
			violation.setHeaderText("Delete failed: No album selected");
			violation.showAndWait();
			return;
		}
		Album curr = listView.getSelectionModel().getSelectedItem();
		
		Alert confirm = new Alert(AlertType.CONFIRMATION);
		confirm.setContentText("Do you want to delete this album? This action cannot be reversed");
		Optional<ButtonType> response = confirm.showAndWait();
		if (response.get() != ButtonType.OK) return;
		
		currUser.removeAlbum(curr);
		obsList.remove(listView.getSelectionModel().getSelectedItem());
		
		Photos.writeApp(ph);
	}
	
	/**
	 * Calls changeScene to search.fxml
	 * @throws Exception
	 */
	public void search() throws Exception {
		ph.changeScene("search.fxml", currUser.name, null);
	}
	
	/**
	 * Calls changeScene back to login.fxml
	 * @throws Exception
	 */
	public void logout() throws Exception {
		ph.changeScene("login.fxml", null, null);
	}
}
