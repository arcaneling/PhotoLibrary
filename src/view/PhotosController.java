package view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import app.Photos;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.*;

/**
 * Controller class for search tool	
 * @author Jason Mooney (jcm370)
 * @author Crystal Zhang (cz298)
 * 
 */

public class PhotosController {
	/** Instance of Photos application */
	private Photos ph;
	/** Current User viewing their Photos */
	private User currUser;
	/** Current Album being accessed by the User */
	private Album currAlbum;
	
	/** ObservableList setup for ListView of Photos */
	private ObservableList<Photo> obsListPhotos;
	/** ObservableList setup for ListView of Tags */
	private ObservableList<Tag> obsListTags;
	
	/** ListView setup for Photos */
	@FXML private ListView<Photo> listViewPhotos;
	/** ListView setup for Tags */
	@FXML private ListView<Tag> listViewTags;
	/* Display for selected Photo */
	@FXML private ImageView img;
	
	/** TextField to caption/recaption */
	@FXML private TextField caption;
	/** TextField to display date (uneditable) */
	@FXML private TextField date;
	
	/**
	 * steps up ListView for photos by creating PhotoCells
	 * @param mainStage main stage for the application to load scenes on
	 * @param currU name of current User
	 * @param currA name of current Album
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public void start(Stage mainStage, String currU, String currA) throws ClassNotFoundException, IOException {
		ph = Photos.readApp();
		currUser = ph.getUser(currU);
		currAlbum = currUser.getAlbum(currA);
	
		obsListPhotos = FXCollections.observableArrayList(currAlbum.getPhotos());

		listViewPhotos.setCellFactory(new Callback<ListView<Photo>, ListCell<Photo>>() {
			@Override
			public ListCell<Photo> call(ListView<Photo> arg0) {
				return new PhotoCell();
			}
		});
		
		listViewPhotos.setItems(obsListPhotos);
		listViewPhotos.getSelectionModel().
		selectedIndexProperty().
		addListener((obs, oldVal, newVal) -> displayDetails());
		
		listViewPhotos.getSelectionModel().select(0);
	}
	
	/**
	 * Internal supporting class to create thumbnail previews
	 * @author Jason Mooney (jcm370)
	 * @author Crystal Zhang (cz298)
	 *
	 */
	static class PhotoCell extends ListCell<Photo> {
		private ImageView imageView = new ImageView();
		@Override
		protected void updateItem(Photo curr, boolean empty) {
			super.updateItem(curr, empty);
			if (curr != null) {
				InputStream stream;
				try {
					stream = new FileInputStream(curr.getAddress());
				} catch (FileNotFoundException e) { return; }
				Image img = new Image(stream);
				imageView.setImage(img);
				imageView.setPreserveRatio(true);
				if (img.getHeight() > img.getWidth()) {
					imageView.setFitHeight(116);
				} else imageView.setFitWidth(116); 
				
				setGraphic(imageView);
				setText(curr.getCaption());
			}
		}
	}
	
	/**
	 * Prompts user for file address, adds Photo to the Album if accepted
	 * @throws IOException
	 */
	public void addPh() throws IOException {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("New Photo");
		dialog.setHeaderText("Add Photo");
		dialog.setContentText("Enter full photo address: ");
		
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			TextField input = dialog.getEditor();
			String newAddress = input.getText().toString();
			if (newAddress.length() == 0) {
				Alert violation = new Alert(AlertType.INFORMATION);
				violation.setTitle("Error");
				violation.setHeaderText("Add failed: Invalid address");
				violation.showAndWait();
				return;
			}
			if (currAlbum.contains(newAddress)) {
				Alert violation = new Alert(AlertType.INFORMATION);
				violation.setTitle("Error");
				violation.setHeaderText("Add failed: Photo already exists in album");
				violation.showAndWait();
				return;
			}
			
			File filepath = new File(newAddress);
			if (!filepath.exists()) {
				Alert violation = new Alert(AlertType.INFORMATION);
				violation.setTitle("Error");
				violation.setHeaderText("Add failed: Invalid address");
				violation.showAndWait();
				return;
			}
			
			if (notImg(newAddress)) {
				Alert violation = new Alert(AlertType.INFORMATION);
				violation.setTitle("Error");
				violation.setHeaderText("Add failed: Please make sure your file is of format BMP, GIF, JPEG/JPG, or PNG");
				violation.showAndWait();
				return;
			}
			
			Photo newPhoto = new Photo(newAddress);
			for (Album ab : currUser.albums) {
				if (ab.contains(newAddress)) {
					newPhoto = ab.getPhoto(newAddress);
					break;
				}
			}
			
			currAlbum.addPhoto(newPhoto);
			obsListPhotos.add(newPhoto);
			listViewPhotos.getSelectionModel().select(newPhoto);
				
			Photos.writeApp(ph);
			displayDetails();
		}
	}
	
	/**
	 * Determines whether the given file is of acceptable filetype
	 * @param filepath address of a file
	 * @return whether the given file is of format PNG, JPEG, JPG, GIF, or BMP
	 */
	private boolean notImg(String filepath) {
		String extension = "";
		int i = filepath.lastIndexOf('.');
		if (i >= 0) { extension = filepath.substring(i+1); }
		else return true;
		
		if (extension.equals("png") || extension.equals("jpeg") || extension.equals("jpg") || extension.equals("gif") || extension.equals("bmp")) return false;
		return true;
	}
	
	/**
	 * Removes selected Photo from the current Album
	 * @throws IOException
	 */
	public void removePh() throws IOException {
		if (listViewPhotos.getSelectionModel().getSelectedItem() == null) {
			Alert violation = new Alert(AlertType.INFORMATION);
			violation.setTitle("Error");
			violation.setHeaderText("Remove failed: No photo selected");
			violation.showAndWait();
			return;
		}
		
		Photo curr = listViewPhotos.getSelectionModel().getSelectedItem();
		int index = listViewPhotos.getSelectionModel().getSelectedIndex();
		
		Alert confirm = new Alert(AlertType.CONFIRMATION);
		confirm.setContentText("Do you want to remove this photo? This action cannot be reversed");
		Optional<ButtonType> response = confirm.showAndWait();
		if (response.get() != ButtonType.OK) return;
		
		currAlbum.removePhoto(curr);
		obsListPhotos.remove(listViewPhotos.getSelectionModel().getSelectedItem());
		listViewPhotos.getSelectionModel().select(index == obsListPhotos.size() ? index - 1 : index);
		
		Photos.writeApp(ph);
		displayDetails();
	}
	
	/**
	 * Moves the ListView selection to the next Photo, if it exists
	 */
	public void nextPh() {
		int index = listViewPhotos.getSelectionModel().getSelectedIndex();
		if (index == obsListPhotos.size() - 1) return;
		
		listViewPhotos.getSelectionModel().select(index + 1);
	}
	
	/**
	 * Moves the ListView selection to the previous Photo, if it exists
	 */
	public void prevPh() {
		int index = listViewPhotos.getSelectionModel().getSelectedIndex();
		if (index == 0) return;
		
		listViewPhotos.getSelectionModel().select(index - 1);
	}
	
	/**
	 * Captions/recaptions the selected Photo
	 * @throws IOException
	 */
	public void updateCap() throws IOException {
		if (listViewPhotos.getSelectionModel().getSelectedItem() == null) {
			Alert violation = new Alert(AlertType.INFORMATION);
			violation.setTitle("Error");
			violation.setHeaderText("Update failed: No photo selected");
			violation.showAndWait();
			return;
		}
		
		String cap = caption.getText().toString();
		Photo curr = listViewPhotos.getSelectionModel().getSelectedItem();
		int index = listViewPhotos.getSelectionModel().getSelectedIndex();
		
		curr.caption = cap;
		obsListPhotos.set(index, curr);
		listViewPhotos.getSelectionModel().select(index);
		
		Photos.writeApp(ph);
	}
	
	/**
	 * Prompts User for tag:value pairing, adds Tag to Photo if accepted
	 * @throws IOException
	 */
	public void addTg() throws IOException {
		if (listViewPhotos.getSelectionModel().getSelectedItem() == null) {
			Alert violation = new Alert(AlertType.INFORMATION);
			violation.setTitle("Error");
			violation.setHeaderText("Add failed: No photo selected");
			violation.showAndWait();
			return;
		}

		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("New Tag");
		dialog.setHeaderText("Add Tag");
		dialog.setContentText("Use name:value format: ");
		
		Photo currPhoto = listViewPhotos.getSelectionModel().getSelectedItem();
		
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			TextField input = dialog.getEditor();
			String newAddress = input.getText().toString();
			String[] dict = newAddress.split(":", 2);
			
			if (dict[0].length() == 0 || dict[1].length() == 0) {
				Alert violation = new Alert(AlertType.INFORMATION);
				violation.setTitle("Error");
				violation.setHeaderText("Add failed: Invalid length");
				violation.showAndWait();
				return;
			}
			if (currPhoto.contains(dict)) {
				Alert violation = new Alert(AlertType.INFORMATION);
				violation.setTitle("Error");
				violation.setHeaderText("Add failed: Duplicate tags");
				violation.showAndWait();
				return;
			}
			
			Tag newTag = new Tag(dict[0], dict[1]);
			currPhoto.addTag(newTag);
			obsListTags.add(newTag);
			listViewTags.getSelectionModel().select(newTag);
			
			Photos.writeApp(ph);
		}
	}
	
	/**
	 * Deletes selected Tag from the selected Photo
	 * @throws IOException
	 */
	public void delTg() throws IOException {
		if (listViewTags.getSelectionModel().getSelectedItem() == null) {
			Alert violation = new Alert(AlertType.INFORMATION);
			violation.setTitle("Error");
			violation.setHeaderText("Delete failed: No tag selected");
			violation.showAndWait();
			return;
		}

		Tag curr = listViewTags.getSelectionModel().getSelectedItem();
		Photo currPhoto = listViewPhotos.getSelectionModel().getSelectedItem();
		int index = listViewPhotos.getSelectionModel().getSelectedIndex();
		
		Alert confirm = new Alert(AlertType.CONFIRMATION);
		confirm.setContentText("Do you want to delete this tag? This action cannot be reversed");
		Optional<ButtonType> response = confirm.showAndWait();
		if (response.get() != ButtonType.OK) return;
		
		currPhoto.deleteTag(curr);
		obsListTags.remove(listViewTags.getSelectionModel().getSelectedItem());
		listViewTags.getSelectionModel().select(index == obsListTags.size() ? index - 1 : index);
		Photos.writeApp(ph);
	}
	
	/**
	 * Copies selected Photo to a destination Album
	 * @throws IOException
	 */
	public void copyPh() throws IOException {
		if (listViewPhotos.getSelectionModel().getSelectedItem() == null) {
			Alert violation = new Alert(AlertType.INFORMATION);
			violation.setTitle("Error");
			violation.setHeaderText("Copy failed: No photo selected");
			violation.showAndWait();
			return;
		}
		
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Copy Photo");
		dialog.setHeaderText("Copy Photo");
		dialog.setContentText("Enter album name: ");
		
		Photo currPhoto = listViewPhotos.getSelectionModel().getSelectedItem();
		
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			TextField input = dialog.getEditor();
			String toAlbum = input.getText().toString();
			if (!currUser.contains(toAlbum)) {
				Alert violation = new Alert(AlertType.INFORMATION);
				violation.setTitle("Error");
				violation.setHeaderText("Copy failed: Album does not exist");
				violation.showAndWait();
				return;
			}
			Album destination = currUser.getAlbum(toAlbum);
			if (destination.contains(currPhoto.address)) {
				Alert violation = new Alert(AlertType.INFORMATION);
				violation.setTitle("Error");
				violation.setHeaderText("Copy failed: Album already contains photo");
				violation.showAndWait();
				return;
			}
			
			currAlbum.copyPhoto(currPhoto, destination);
			Photos.writeApp(ph);
		}
	}
	
	/**
	 * Moves selected Photo from source Album to destination Album
	 * @throws IOException
	 */
	public void movePh() throws IOException {
		if (listViewPhotos.getSelectionModel().getSelectedItem() == null) {
			Alert violation = new Alert(AlertType.INFORMATION);
			violation.setTitle("Error");
			violation.setHeaderText("Move failed: No photo selected");
			violation.showAndWait();
			return;
		}
		
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Move Photo");
		dialog.setHeaderText("Move Photo");
		dialog.setContentText("Enter album name: ");
		
		Photo currPhoto = listViewPhotos.getSelectionModel().getSelectedItem();
		int index = listViewPhotos.getSelectionModel().getSelectedIndex();
		
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			TextField input = dialog.getEditor();
			String toAlbum = input.getText().toString();
			if (!currUser.contains(toAlbum)) {
				Alert violation = new Alert(AlertType.INFORMATION);
				violation.setTitle("Error");
				violation.setHeaderText("Move failed: Album does not exist");
				violation.showAndWait();
				return;
			}
			Album destination = currUser.getAlbum(toAlbum);
			if (destination.contains(currPhoto.address)) {
				Alert violation = new Alert(AlertType.INFORMATION);
				violation.setTitle("Error");
				violation.setHeaderText("Move failed: Album already contains photo");
				violation.showAndWait();
				return;
			}
			
			currAlbum.movePhoto(currPhoto, destination);
			obsListPhotos.remove(listViewPhotos.getSelectionModel().getSelectedItem());
			listViewPhotos.getSelectionModel().select(index == obsListPhotos.size() ? index - 1 : index);
			
			Photos.writeApp(ph);
			displayDetails();
		}
	}
	
	/**
	 * Calls changeScene back to albums.fxml
	 * @throws Exception
	 */
	public void returnToAlbums() throws Exception {
		Photos.writeApp(ph);
		ph.changeScene("albums.fxml", currUser.name, null);
	}
	
	/**
	 * Wipes detailed display, and then updates the display with selected Photo
	 */
	public void displayDetails() {
		resetDisplay();
		Photo curr = listViewPhotos.getSelectionModel().getSelectedItem();
		
		if (curr != null) {
			InputStream stream;
			try {
				stream = new FileInputStream(curr.getAddress());
			} catch (FileNotFoundException e) { return; }
			img.setImage(new Image(stream));
			caption.setText(curr.getCaption());
			date.setText(curr.getDate());
			obsListTags = FXCollections.observableArrayList(curr.tags);
			listViewTags.setItems(obsListTags);
			
		}
	}
	
	/**
	 * Wipes detailed display and ListView of Photos
	 */
	public void resetDisplay() {
		listViewPhotos.setCellFactory(new Callback<ListView<Photo>, ListCell<Photo>>() {
			@Override
			public ListCell<Photo> call(ListView<Photo> arg0) {
				return new PhotoCell();
			}
		});
		img.setImage(null);
		caption.setText("");
		date.setText("");
		obsListTags = FXCollections.observableArrayList();
		listViewTags.setItems(obsListTags);
	}
}
