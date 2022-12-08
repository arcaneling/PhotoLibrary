package view;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import app.Photos;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import model.*;

/**
 * Controller class for search tool	
 * @author Jason Mooney (jcm370)
 * @author Crystal Zhang (cz298)
 * 
 */

public class SearchController {
	/** Instance of Photos application */
	private Photos ph;
	/** Current User searching through their Albums */
	private User currUser;
	
	/** TextField for the lower bound of date search */
	@FXML private TextField earliestDate;
	/** TextField for the upper bound of date search */
	@FXML private TextField latestDate;
	/** TextField for the first tag of tag search */
	@FXML private TextField tag1;
	/** TextField for the second tag of tag search */
	@FXML private TextField tag2;
	
	/** ComboTag to specify type of pair searching */
	@FXML private ComboBox<String> comboTag;
	
	/**
	 * Sets up ComboTag for pair searching
	 * @param mainStage main stage for the application to load scenes on
	 * @param curr name of the current User
	 * @throws Exception
	 */
	public void start(Stage mainStage, String curr) throws Exception {
		ph = Photos.readApp();
		currUser = ph.getUser(curr);
		
		comboTag.getItems().add("AND");
		comboTag.getItems().add("OR");
	}
	
	/**
	 * Adds new Album to current User based on given date bounds
	 * @throws Exception
	 */
	public void saveDate() throws Exception {
		String earliest = earliestDate.getText().toString();
		String latest = latestDate.getText().toString();
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		if (earliest.length() == 0) {
			start.setTime(new Date(Long.MIN_VALUE));
		}
		else {
			try {
				SimpleDateFormat format1 = new SimpleDateFormat("MM/dd/yyyy");
				start.setTime(format1.parse(earliest));
			} catch (Exception e) {
				Alert violation = new Alert(AlertType.INFORMATION);
				violation.setTitle("Error");
				violation.setHeaderText("Invalid date format");
				violation.showAndWait();
				return;
			}
		}
		
		if (latest.length() == 0) {
			end.setTime(new Date(Long.MAX_VALUE));
		}
		else {
			try {
				SimpleDateFormat format1 = new SimpleDateFormat("MM/dd/yyyy");
				end.setTime(format1.parse(latest));
			} catch (Exception e) {
				Alert violation = new Alert(AlertType.INFORMATION);
				violation.setTitle("Error");
				violation.setHeaderText("Invalid date format");
				violation.showAndWait();
				return;
			}
		}
		
		Album newAlbum = new Album("Album #" + (currUser.getAlbums().size() + 1));
		for (Album ab : currUser.getAlbums()) {
			Album res = ab.searchDate(start, end);
			for (Photo pho : res.photos) {
				if (!newAlbum.contains(pho.address)) newAlbum.addPhoto(pho);
			}
		}
		
		currUser.addAlbum(newAlbum);
		Photos.writeApp(ph);
		returnToAlbums();
	}
	
	/**
	 * Adds new Album to current User based on give tag (single and pair) values 
	 * @throws Exception
	 */
	public void saveTags() throws Exception {
		String t1 = tag1.getText().toString();
		String t2 = tag2.getText().toString();
		
		if (t1.length() == 0 && t2.length() == 0) {
			Alert violation = new Alert(AlertType.INFORMATION);
			violation.setTitle("Error");
			violation.setHeaderText("Please enter tags");
			violation.showAndWait();
			return;
		}
		
		if (!t1.contains("=") || (t2.length() > 0 && !t2.contains("="))) {
			Alert violation = new Alert(AlertType.INFORMATION);
			violation.setTitle("Error");
			violation.setHeaderText("Invalid tag format");
			violation.showAndWait();
			return;
		}
		
		Album newAlbum = new Album("Album #" + (currUser.getAlbums().size() + 1));
		String[] dict1 = t1.split("=", 2);
		Tag temp1 = new Tag(dict1[0], dict1[1]);
		
		if (t2.length() == 0) {
			for (Album ab : currUser.getAlbums()) {
				Album res = ab.searchTag(temp1);
				for (Photo pho : res.photos) {
					if (!newAlbum.contains(pho.address)) newAlbum.addPhoto(pho);
				}
			}
		}
		else {
			String[] dict2 = t2.split("=", 2);
			Tag temp2 = new Tag(dict2[0], dict2[1]);
			
			if (comboTag.getValue() == null) {
				Alert violation = new Alert(AlertType.INFORMATION);
				violation.setTitle("Error");
				violation.setHeaderText("Select a pairing");
				violation.showAndWait();
				return;
			}
			if (comboTag.getValue().equals("AND")) {
				for (Album ab : currUser.getAlbums()) {
					Album res = ab.searchConjunctive(temp1, temp2);
					for (Photo pho : res.photos) {
						if (!newAlbum.contains(pho.address)) newAlbum.addPhoto(pho);
					}
				}
			}
			else if (comboTag.getValue().equals("OR")) {
				for (Album ab : currUser.getAlbums()) {
					Album res = ab.searchDisjunctive(temp1, temp2);
					for (Photo pho : res.photos) {
						if (!newAlbum.contains(pho.address)) newAlbum.addPhoto(pho);
					}
				}
			}
		}
		
		currUser.addAlbum(newAlbum);
		Photos.writeApp(ph);
		returnToAlbums();
	}
	
	/**
	 * Calls changeScene back to albums.fxml
	 * @throws Exception
	 */
	public void returnToAlbums() throws Exception {
		Photos.writeApp(ph);
		ph.changeScene("albums.fxml", currUser.name, null);
	}
}
