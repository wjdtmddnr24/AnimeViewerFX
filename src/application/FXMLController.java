package application;

import java.io.File;
import java.net.URI;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class FXMLController {
	private MediaView mediaView;
	@FXML
	private Stage stage;

	@SuppressWarnings("restriction")
	public void handleOpen(ActionEvent e) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Video ÆÄÀÏ", "*.mp4", "*.avi", "*.wmv"));
		File selectedFile = fileChooser.showOpenDialog(stage);
		Media media = new Media(selectedFile.toURI().toASCIIString());
		MediaPlayer mediaPlayer = new MediaPlayer(media);
		mediaPlayer.setOnReady(mediaView.getMediaPlayer().getOnReady());
		mediaView.getMediaPlayer().dispose();
		mediaView.setMediaPlayer(new MediaPlayer(media));
		mediaView.getMediaPlayer().play();

	}

	public void setMediaView(MediaView mediaView) {
		this.mediaView = mediaView;
	}

	public void handleExit(ActionEvent e) {
		Platform.exit();
		System.exit(0);
	}

}
