package view;

import java.io.IOException;
import java.util.List;
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
 * Controller class for admin scene
 * @author Jason Mooney (jcm370)
 * @author Crystal Zhang (cz298)
 * 
 */

public class AdminController {
	/** Instance of Photos application */
	private Photos ph;
	/** User list for the running application */
	private List<User> userList;
	/** ObservableList setup for ListView of Users */
	private static ObservableList<User> obsList;
	
	/** ListView setup for Users */
	@FXML private ListView<User> listView;
	
	/** TextField to name a new User */
	@FXML private TextField username;
	
	/**
	 * Sets up ListView of current Users
	 * @param mainStage main stage for the application to load scenes on
	 * @throws Exception
	 */
	public void start(Stage mainStage) throws Exception {
		ph = Photos.readApp();
		userList = ph.getUsers();
		obsList = FXCollections.observableArrayList(userList);
		
		listView.setItems(obsList);
		listView.getSelectionModel().select(0);
	}
	
	/**
	 * Adds new User to the application
	 * @throws IOException
	 */
	public void add() throws IOException {
		String user = username.getText().toString();
		if (user.length() == 0 || user.equals("admin")) {
			Alert violation = new Alert(AlertType.INFORMATION);
			violation.setTitle("Error");
			violation.setHeaderText("Add failed: Invalid username");
			violation.showAndWait();
			return;
		}
		if (ph.contains(user)) {
			Alert violation = new Alert(AlertType.INFORMATION);
			violation.setTitle("Error");
			violation.setHeaderText("Add failed: User already exists");
			violation.showAndWait();
			return;
		}
		
		User newUser = new User(user);
		
		ph.addUser(newUser);
		obsList.add(newUser);
		username.setText("");
		listView.getSelectionModel().select(newUser);
		
		Photos.writeApp(ph);
	}
	
	/**
	 * Removes existing User from the application
	 * @throws IOException
	 */
	public void delete() throws IOException {
		User curr = listView.getSelectionModel().getSelectedItem();
		
		Alert confirm = new Alert(AlertType.CONFIRMATION);
		confirm.setContentText("Do you want to delete this user? This action cannot be reversed");
		Optional<ButtonType> response = confirm.showAndWait();
		if (response.get() != ButtonType.OK) return;
		
		ph.removeUser(curr);
		obsList.remove(listView.getSelectionModel().getSelectedItem());
		
		Photos.writeApp(ph);
	}
	
	/**
	 * Calls changeScene back to login.fxml
	 * @throws Exception
	 */
	public void logout() throws Exception {
		ph.changeScene("login.fxml", null, null);
	}
}
