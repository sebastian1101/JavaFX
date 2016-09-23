package com.starterkit.javafx.controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.starterkit.javafx.dataprovider.DataProvider;
import com.starterkit.javafx.dataprovider.data.PlayerVO;
import com.starterkit.javafx.model.UserProfileEdit;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UserProfileEditController {
	
	private static final Logger LOG = Logger.getLogger(UserProfilesSearchController.class);
	
	/**
	 * Resource bundle loaded with this controller. JavaFX injects a resource
	 * bundle specified in {@link FXMLLoader#load(URL, ResourceBundle)} call.
	 * <p>
	 * NOTE: The variable name must be {@code resources}.
	 * </p>
	 */
	@FXML
	private ResourceBundle resources;
	
	@FXML
	private TextField firstNameField;
	
	@FXML
	private TextField lastNameField;
	
	@FXML
	private TextField emailField;
	
	@FXML
	private TextField passwordField;
	
	@FXML
	private Button saveProfileButton;
	
	@FXML
	private Button cancelButton;
	
	@FXML
	private TextArea aboutMeArea;
	
	@FXML
	private TextArea lifeMottoArea;
	
	@FXML
	private Label userIdLabel;
	
	private final UserProfileEdit model = new UserProfileEdit();
	
	private final DataProvider dataProvider = DataProvider.INSTANCE;
	
	private final PlayerVO playerToSave = new PlayerVO();
	
	public UserProfileEditController() {
	}



	@FXML
	private void initialize() {
		
		firstNameField.textProperty().bindBidirectional(model.firstNameProperty());
		lastNameField.textProperty().bindBidirectional(model.lastNameProperty());
		emailField.textProperty().bindBidirectional(model.emailProperty());
		passwordField.textProperty().bindBidirectional(model.passwordProperty());
		lifeMottoArea.textProperty().bindBidirectional(model.LifeMottoProperty());
		aboutMeArea.textProperty().bindBidirectional(model.aboutMeProperty());

		saveProfileButton.disableProperty().bind(firstNameField.textProperty().isEmpty()
				.and(lastNameField.textProperty().isEmpty()).and(emailField.textProperty().isEmpty()));
		
	}
	
	public void init(PlayerVO user) {
		
		model.setAboutMe(user.getAboutMe());
		model.setEmail(user.getEmail());
		model.setFirstName(user.getName());
		model.setLastName(user.getSurname());
		model.setLifeMotto(user.getLifeMotto());
		model.setPassword(user.getPassword());
		model.setLogin(user.getLogin());
		model.setId(user.getId());
		
		userIdLabel.setText(model.getLogin());
	}
	
	@FXML
	private void saveProfileButtonAction() {
		LOG.debug("'Save' button clicked");
		
		playerToSave.setAboutMe(model.getAboutMe());
		playerToSave.setEmail(model.getEmail());
		playerToSave.setLifeMotto(model.getLifeMotto());
		playerToSave.setLogin(model.getLogin());
		playerToSave.setName(model.getFirstName());
		playerToSave.setPassword(model.getPassword());
		playerToSave.setSurname(model.getLastName());
		playerToSave.setId(model.getId());
		
		saveProfile();
		
	}
	
	private void saveProfile() {
		
		Runnable backgroundTask = new Runnable() {

			/**
			 * This method will be executed in a background thread.
			 */
			@Override
			public void run() {
				LOG.debug("backgroundTask.run() called");

				/*
				 * Update data.
				 */
				dataProvider.savePlayer(playerToSave);
				Platform.runLater(new Runnable() {

					/**
					 * This method will be executed in the JavaFX Application
					 * Thread.
					 */
					@Override
					public void run() {
						Stage stage = (Stage) cancelButton.getScene().getWindow();
						stage.close();

					}
				});
			}
		};

		/*
		 * Start the background task. In real life projects some framework
		 * manages threads. You should never create a thread on your own.
		 */
		new Thread(backgroundTask).start();
		
	}
	
	@FXML
	private void cancelAction(ActionEvent event) {

		Stage stage = (Stage) cancelButton.getScene().getWindow();
	    stage.close();
		
	}
}
