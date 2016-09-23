package com.starterkit.javafx.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.starterkit.javafx.dataprovider.DataProvider;
import com.starterkit.javafx.dataprovider.data.PlayerVO;
import com.starterkit.javafx.dataprovider.data.SexVO;
import com.starterkit.javafx.model.UserProfileSearch;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class UserProfilesSearchController {

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

	/**
	 * JavaFX injects an object defined in FXML with the same "fx:id" as the
	 * variable name.
	 */

	@FXML
	private TextField userIdField;

	@FXML
	private TextField firstNameField;

	@FXML
	private TextField lastNameField;

	@FXML
	private Button searchProfileButton;

	@FXML
	private Button deleteProfileButton;

	@FXML
	private Button editProfileButton;

	@FXML
	private TableView<PlayerVO> resultTable;

	@FXML
	private TableColumn<PlayerVO, String> userIdColumn;

	@FXML
	private TableColumn<PlayerVO, String> firstNameColumn;

	@FXML
	private TableColumn<PlayerVO, String> lastNameColumn;

	@FXML
	private TableColumn<PlayerVO, String> emailColumn;

	private final DataProvider dataProvider = DataProvider.INSTANCE;

	private final UserProfileSearch model = new UserProfileSearch();

	/**
	 * The JavaFX runtime instantiates this controller.
	 * <p>
	 * The @FXML annotated fields are not yet initialized at this point.
	 * </p>
	 */
	public UserProfilesSearchController() {
	}

	/**
	 * The JavaFX runtime calls this method after loading the FXML file.
	 * <p>
	 * The @FXML annotated fields are initialized at this point.
	 * </p>
	 * <p>
	 * NOTE: The method name must be {@code initialize}.
	 * </p>
	 */
	@FXML
	private void initialize() {
		// LOG.debug("initialize(): nameField = " + nameField);

		initializeResultTable();

		/*
		 * Bind controls properties to model properties.
		 */
		firstNameField.textProperty().bindBidirectional(model.firstNameProperty());
		lastNameField.textProperty().bindBidirectional(model.lastNameProperty());
		userIdField.textProperty().bindBidirectional(model.userIdProperty());
		resultTable.itemsProperty().bind(model.resultProperty());

		/*
		 * Make the all buttons inactive when the fields or selected item are
		 * empty.
		 */
		searchProfileButton.disableProperty().bind(firstNameField.textProperty().isEmpty()
				.and(lastNameField.textProperty().isEmpty()).and(userIdField.textProperty().isEmpty()));

		editProfileButton.disableProperty().bind(resultTable.getSelectionModel().selectedItemProperty().isNull());
		deleteProfileButton.disableProperty().bind(resultTable.getSelectionModel().selectedItemProperty().isNull());
	}

	private void initializeResultTable() {
		/*
		 * Define what properties of PlayerVO will be displayed in different
		 * columns.
		 */
		firstNameColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getName()));

		userIdColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getLogin()));

		lastNameColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getSurname()));

		emailColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getEmail()));

		/*
		 * Show specific text for an empty table. This can also be done in FXML.
		 */
		resultTable.setPlaceholder(new Label(resources.getString("table.emptyText")));

	}

	/**
	 * The JavaFX runtime calls this method when the <b>Search</b> button is
	 * clicked.
	 *
	 * @param event
	 *            {@link ActionEvent} holding information about this event
	 */
	@FXML
	private void searchButtonAction() {
		LOG.debug("'Search' button clicked");

		searchButtonActionVersion2();
		// searchButtonActionVersion3();
	}

	@FXML
	private void deleteProfileButtonAction() {

		LOG.debug("'Delete' button clicked");

		deleteButtonAction();
	}

	private void deleteButtonAction() {

		/*
		 * Use runnable to execute the potentially long running call in
		 * background (separate thread), so that the JavaFX Application Thread
		 * is not blocked.
		 */
		Runnable backgroundTask = new Runnable() {

			/**
			 * This method will be executed in a background thread.
			 */
			@Override
			public void run() {
				LOG.debug("backgroundTask.run() called");

				/*
				 * Delete data.
				 */

				long id = resultTable.getSelectionModel().selectedItemProperty().getValue().getId();
				dataProvider.deletePlayer(id);
			}
		};

		/*
		 * Start the background task. In real life projects some framework
		 * manages threads. You should never create a thread on your own.
		 */
		new Thread(backgroundTask).start();
	}

	@FXML
	private void editProfileButtonAction() throws Exception {
		LOG.debug("'Edit' button clicked");
		Stage secondaryStage = new Stage();

		PlayerVO user = resultTable.getSelectionModel().getSelectedItem();

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/starterkit/javafx/view/profiles-edit.fxml"),
				ResourceBundle.getBundle("com/starterkit/javafx/bundle/base"));
		Parent root1 = (Parent) fxmlLoader.load();

		UserProfileEditController editController = fxmlLoader.<UserProfileEditController> getController();
		editController.init(user);

		secondaryStage.setTitle("StarterKit-JavaFX");

		/*
		 * Load screen from FXML file with specific language bundle (derived
		 * from default locale).
		 */
		// Parent tree =
		// FXMLLoader.load(getClass().getResource("/com/starterkit/javafx/view/userProfile-edit.fxml"),
		// //
		// ResourceBundle.getBundle("com/starterkit/javafx/bundle/base"));

		Scene scene = new Scene(root1);

		/*
		 * Set the style sheet(s) for application.
		 */
		scene.getStylesheets().add(getClass().getResource("/com/starterkit/javafx/css/standard.css").toExternalForm());

		secondaryStage.setScene(scene);

		secondaryStage.show();

		secondaryStage.setOnHiding(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				searchButtonAction();

			}
		});

	}

	/**
	 * This implementation is correct.
	 * <p>
	 * The {@link DataProvider#findPersons(String, SexVO)} call is executed in a
	 * background thread.
	 * </p>
	 */
	private void searchButtonActionVersion2() {
		/*
		 * Use task to execute the potentially long running call in background
		 * (separate thread), so that the JavaFX Application Thread is not
		 * blocked.
		 */
		Task<Collection<PlayerVO>> backgroundTask = new Task<Collection<PlayerVO>>() {

			/**
			 * This method will be executed in a background thread.
			 */
			@Override
			protected Collection<PlayerVO> call() throws Exception {

				/*
				 * Get the data.
				 */
				Collection<PlayerVO> result = dataProvider.findPlayer( //
						model.getUserId(), //
						model.getFirstName(), model.getLastName());

				/*
				 * Value returned from this method is stored as a result of task
				 * execution.
				 */
				return result;
			}

			/**
			 * This method will be executed in the JavaFX Application Thread
			 * when the task finishes.
			 */
			@Override
			protected void succeeded() {
				LOG.debug("succeeded() called");

				/*
				 * Get result of the task execution.
				 */
				Collection<PlayerVO> result = getValue();

				/*
				 * Copy the result to model.
				 */
				model.setResult(new ArrayList<PlayerVO>(result));

				/*
				 * Reset sorting in the result table.
				 */
				resultTable.getSortOrder().clear();
			}
		};

		/*
		 * Start the background task. In real life projects some framework
		 * manages background tasks. You should never create a thread on your
		 * own.
		 */
		new Thread(backgroundTask).start();
	}

	/**
	 * This implementation is correct.
	 * <p>
	 * The {@link DataProvider#findPersons(String, SexVO)} call is executed in a
	 * background thread.
	 * </p>
	 */
	@SuppressWarnings("unused")
	private void searchButtonActionVersion3() {
		/*
		 * Use runnable to execute the potentially long running call in
		 * background (separate thread), so that the JavaFX Application Thread
		 * is not blocked.
		 */
		Runnable backgroundTask = new Runnable() {

			/**
			 * This method will be executed in a background thread.
			 */
			@Override
			public void run() {
				LOG.debug("backgroundTask.run() called");

				/*
				 * Get the data.
				 */
				Collection<PlayerVO> result = dataProvider.findPlayer( //
						model.getUserId(), //
						model.getFirstName(), model.getLastName());

				/*
				 * Add an event(runnable) to the event queue.
				 */
				Platform.runLater(new Runnable() {

					/**
					 * This method will be executed in the JavaFX Application
					 * Thread.
					 */
					@Override
					public void run() {
						LOG.debug("Platform.runLater(Runnable.run()) called");

						/*
						 * Copy the result to model.
						 */
						model.setResult(new ArrayList<PlayerVO>(result));

						/*
						 * Reset sorting in the result table.
						 */
						resultTable.getSortOrder().clear();
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

}
