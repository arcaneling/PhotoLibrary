package view;

import app.Photos;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

/**
 * Controller class for login scene
 * @author Jason Mooney (jcm370)
 * @author Crystal Zhang (cz298)
 * 
 */

public class LoginController {
	/** Instance of Photos application */
	private Photos ph;
	
	/** User input inside text box */
	@FXML private TextField username;
	/** Login screen button */
	@FXML private Button btnLogin;
	
	/**
	 * Starting method for login screen, no setup was required
	 * @param mainStage main stage for the application to load scenes on
	 */
	public void start(Stage mainStage) { }
	
	/**
	 * Reads user input, directs scene change or identifies incorrect login details
	 * @throws Exception 
	 */
	public void attemptLogin() throws Exception {
		ph = Photos.readApp();
		String user = username.getText().toString();
		if (user.equals("admin")) {
			ph.changeScene("admin.fxml", null, null);
		}
		else if (ph.contains(user)) {
			ph.changeScene("albums.fxml", user, null);
		} 
		else {
			Alert violation = new Alert(AlertType.INFORMATION);
			violation.setTitle("Error");
			violation.setHeaderText("Login failed: Invalid username");
			violation.showAndWait();
			return;
		}
		
	}
	
}
