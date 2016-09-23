package com.starterkit.javafx.controller;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.starterkit.javafx.dataprovider.data.FileVO;
import com.starterkit.javafx.model.FileView;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.stage.DirectoryChooser;
import javafx.scene.control.ScrollPane;

public class ViewerController {

	private static final Logger LOG = Logger.getLogger(ViewerController.class);

	@FXML
	private TableView<FileVO> resultTable;

	@FXML
	private TableColumn<FileVO, String> nameColumn;

	@FXML
	private Button chooseDirectoryButton;

	@FXML
	private Button startSlideshowButton;

	@FXML
	private Button prevImageButton;

	@FXML
	private Button nextImageButton;

	@FXML
	private Button openImageButton;

	@FXML
	private ImageView imageView;

	private final FileView model = new FileView();

	private List<FileVO> imagesList = new ArrayList<FileVO>();

	private static final double ZOOM_FACTOR = 1.0014450997779993488675056142818;

	private final DoubleProperty zoomProperty = new SimpleDoubleProperty(1000);

	private final DoubleProperty mouseXProperty = new SimpleDoubleProperty();

	private final DoubleProperty mouseYProperty = new SimpleDoubleProperty();

	private static final long SLIDE_SHOW_DELAY = 1000L;

	@FXML
	private ScrollPane scrollPane;

	@FXML
	private void initialize() {

		initializeResultTable();

		resultTable.itemsProperty().bind(model.resultProperty());

		startSlideshowButton.disableProperty().bind(resultTable.getSelectionModel().selectedItemProperty().isNull());

		prevImageButton.disableProperty().bind(resultTable.getSelectionModel().selectedItemProperty().isNull());

		nextImageButton.disableProperty().bind(resultTable.getSelectionModel().selectedItemProperty().isNull());

		openImageButton.disableProperty().bind(resultTable.getSelectionModel().selectedItemProperty().isNull());
	}

	private void initializeResultTable() {

		nameColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getName()));
	}

	@FXML
	private void nextImageButtonAction(ActionEvent event) {
		LOG.debug("'next image' button clicked");

		resultTable.getSelectionModel().select(resultTable.getSelectionModel().getSelectedIndex() + 1);

		String imagePath = "file:///" + resultTable.getSelectionModel().selectedItemProperty().get().getDirectory();
		Image image = new Image(imagePath);
		imageView.setImage(image);

	}

	@FXML
	private void prevImageButtonAction(ActionEvent event) {
		LOG.debug("'prev image' button clicked");

		resultTable.getSelectionModel().select(resultTable.getSelectionModel().getSelectedIndex() - 1);

		String imagePath = "file:///" + resultTable.getSelectionModel().selectedItemProperty().get().getDirectory();
		Image image = new Image(imagePath);
		imageView.setImage(image);
	}

	@FXML
	private void startSlideshowButtonAction(ActionEvent event) {
		LOG.debug("'start slideshow' button clicked");
		Timer task = new Timer();
		TimerTask timer = new TimerTask() {

			@Override
			public void run() {
				boolean isSlideShowFinished = resultTable.getItems()
						.size() == resultTable.getSelectionModel().getSelectedIndex() + 1;
				nextImageButtonAction(new ActionEvent());

				if (isSlideShowFinished) {
					cancel();
				}
			}
		};

		task.scheduleAtFixedRate(timer, SLIDE_SHOW_DELAY, 1000);
	}

	@FXML
	private void chooseDirectoryButtonAction(ActionEvent event) {
		LOG.debug("'choose Directory' button clicked");
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Directory Chooser");
		File defaultDirectory = new File("C:\\Users\\Public\\Pictures\\Sample Pictures");
		directoryChooser.setInitialDirectory(defaultDirectory);

		File files = directoryChooser.showDialog(chooseDirectoryButton.getScene().getWindow());

		if (files != null) {
			imagesList.clear();
			for (File file : files.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".jpg") || name.endsWith(".bmp") || name.endsWith(".png")
							|| name.endsWith(".tga") || name.endsWith(".tiff");
				}
			})) {
				if (file.isFile()) {
					FileVO fileVO = new FileVO(file.getPath(), file.getName());
					imagesList.add(fileVO);
				}
			}
		}

		model.setResult(imagesList);
	}

	@FXML
	public void showImageButtonAction(ActionEvent event) {
		LOG.debug("'open Image' button clicked");

		String imagePath = "file:///" + resultTable.getSelectionModel().selectedItemProperty().get().getDirectory();
		Image image = new Image(imagePath);
		imageView.setImage(image);

		scrollPane.setContent(imageView);

		scrollPane.setPannable(true);

		setImageView(imageView, scrollPane);

		imageView.setOnMouseMoved(new EventHandler<MouseEvent>() {
			@Override
			public void handle(final MouseEvent event) {
				mouseXProperty.set(event.getX());
				mouseYProperty.set(event.getY());
			}
		});

		imageView.addEventFilter(ScrollEvent.ANY, new EventHandler<ScrollEvent>() {
			@Override
			public void handle(final ScrollEvent event) {

				// Original size of the image.
				double sourceWidth = zoomProperty.get() * imageView.getImage().getWidth();
				double sourceHeight = zoomProperty.get() * imageView.getImage().getHeight();

				zoomProperty.set(zoomProperty.get() * Math.pow(ZOOM_FACTOR, event.getDeltaY()));

				// Old values of the scrollbars.
				double oldHvalue = scrollPane.getHvalue();
				double oldVvalue = scrollPane.getVvalue();

				// Image pixels outside the visible area which need to be
				// scrolled.
				double preScrollXFactor = Math.max(0, sourceWidth - scrollPane.getWidth());
				double preScrollYFactor = Math.max(0, sourceHeight - scrollPane.getHeight());

				// Relative position of the mouse in the image.
				double mouseXPosition = (mouseXProperty.get() + preScrollXFactor * oldHvalue) / sourceWidth;
				double mouseYPosition = (mouseYProperty.get() + preScrollYFactor * oldVvalue) / sourceHeight;

				// Target size of the image.
				double targetWidth = zoomProperty.get() * imageView.getImage().getWidth();
				double targetHeight = zoomProperty.get() * imageView.getImage().getHeight();

				// Image pixels outside the visible area which need to be
				// scrolled.
				double postScrollXFactor = Math.max(0, targetWidth - scrollPane.getWidth());
				double postScrollYFactor = Math.max(0, targetHeight - scrollPane.getHeight());

				// Correction applied to compensate the vertical scrolling done
				// by ScrollPane
				double verticalCorrection = (postScrollYFactor / sourceHeight) * event.getDeltaY();

				// New scrollbar positions keeping the mouse position.
				double newHvalue = ((mouseXPosition * targetWidth) - mouseXProperty.get()) / postScrollXFactor;
				double newVvalue = ((mouseYPosition * targetHeight) - mouseYProperty.get() + verticalCorrection)
						/ postScrollYFactor;

				imageView.setFitWidth(targetWidth);
				imageView.setFitHeight(targetHeight);

				scrollPane.setHvalue(newHvalue);
				scrollPane.setVvalue(newVvalue);
			}
		});

		imageView.addEventFilter(ZoomEvent.ANY, new EventHandler<ZoomEvent>() {
			@Override
			public void handle(final ZoomEvent event) {
				zoomProperty.set(zoomProperty.get() * event.getZoomFactor());

				imageView.setFitWidth(zoomProperty.get() * imageView.getImage().getWidth());
				imageView.setFitHeight(zoomProperty.get() * imageView.getImage().getHeight());
			}
		});

	}

	/**
	 * Set the image view displayed by this class.
	 *
	 * @param imageView
	 *            The ImageView.
	 */
	public final void setImageView(final ImageView imageView, ScrollPane scrollPane) {
		scrollPane.setContent(imageView);
		zoomProperty.set(Math.min(imageView.getFitWidth() / imageView.getImage().getWidth(),
				imageView.getFitHeight() / imageView.getImage().getHeight()));
	}
}