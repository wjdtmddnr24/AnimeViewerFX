package application;

import java.io.File;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.media.Track;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.text.Text;

public class Main {
	MediaView mediaView;
	MediaPlayer player;
	Duration duration;
	Slider progressBar, volumeBar;
	Text totaltime, totalVolume;
	Button play, stop;
	Stage primaryStage;
	String folderName;
	String fileName;
	String host;

	public void setupMedia(MediaView mediaView, String mediaURL) {
		Media media = new Media(mediaURL);
		mediaView.setPreserveRatio(true);
		DoubleProperty mvw = mediaView.fitWidthProperty();
		DoubleProperty mvh = mediaView.fitHeightProperty();
		mvw.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
		mvh.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));
		if (player != null && player.getStatus().compareTo(MediaPlayer.Status.DISPOSED) != 0) {
			player.dispose();
		}
		player = new MediaPlayer(media);
		player.play();

		duration = media.getDuration();
		primaryStage.setTitle(media.getSource());
		setProgress(progressBar);
		setVolume(volumeBar);
		play.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				toggleMediaPlayer();
				updateValues();
			}
		});

		stop.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				player.stop();
				updateValues();
			}
		});

		player.setOnReady(new Runnable() {
			public void run() {
				totaltime.setText(String.format("%d:%02.0f", (int) (player.getMedia().getDuration().toSeconds() % 59),
						player.getMedia().getDuration().toSeconds() / 59));
				duration = player.getMedia().getDuration();
				updateValues();
				System.out.println(media.getTracks());

			}
		});
		player.currentTimeProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				updateValues();
			}
		});
		player.setOnStopped(new Runnable() {
			@Override
			public void run() {
				totaltime.setText(String.format("%d:%02.0f", (int) (player.getMedia().getDuration().toSeconds() % 59),
						player.getMedia().getDuration().toSeconds() / 59));
				duration = player.getMedia().getDuration();
				updateValues();
			}
		});
		mediaView.setMediaPlayer(player);

	}

	public void toggleMediaPlayer() {
		if (player.getStatus().equals(Status.PLAYING)) {
			player.pause();
		} else {
			player.play();
			updateValues();
		}
	}

	public void setProgress(Slider progressBar) {
		progressBar.setMinWidth(50);
		progressBar.setMaxWidth(Double.MAX_VALUE);
		progressBar.valueProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				if (progressBar.isValueChanging() || progressBar.isPressed()) {
					player.seek(duration.multiply(progressBar.getValue() / 100.0));
				}

			}
		});
	}

	public void setVolume(Slider volumeBar) {
		volumeBar.setPrefWidth(100);
		volumeBar.setMaxWidth(Region.USE_PREF_SIZE);
		volumeBar.setMinWidth(30);
		volumeBar.valueProperty().addListener(new InvalidationListener() {
			public void invalidated(Observable observable) {
				if (volumeBar.isValueChanging() || volumeBar.isPressed()) {
					player.setVolume(volumeBar.getValue() / 100.0);
					totalVolume.setText(String.format("%.2f%%", volumeBar.getValue()));

				}
			}
		});
	}

	public Menu createMenu() {
		Menu menu = new Menu("파일");
		MenuItem openFile = new MenuItem("열기");
		MenuItem closeProject = new MenuItem("닫기");
		openFile.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Open Resource File");
				fileChooser.getExtensionFilters().add(new ExtensionFilter("Video 동영상", "*.mp4", "*.avi", "*.wmv"));
				File selectedFile = fileChooser.showOpenDialog(primaryStage);
				setupMedia(mediaView, selectedFile.toURI().toASCIIString());
			}
		});
		closeProject.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Platform.exit();
			}
		});
		menu.getItems().add(openFile);
		menu.getItems().add(closeProject);

		return menu;
	}

	public void start() {
		try {
			primaryStage = new Stage();
			Parent root = FXMLLoader.load(getClass().getResource("mediaviewer.fxml"));
			Scene scene = new Scene(root, 1280, 825);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			totaltime = (Text) scene.lookup("#totalTime");
			totalVolume = (Text) scene.lookup("#totalVolume");
			progressBar = (Slider) scene.lookup("#progress");
			volumeBar = (Slider) scene.lookup("#volume");
			mediaView = (MediaView) scene.lookup("#player");
			play = (Button) scene.lookup("#play");
			stop = (Button) scene.lookup("#stop");
			/* 오프라인 미디어 플레이어 사용시 초기 설정 */
			mediaView.setFitHeight(720);
			scene.lookup("#skin").setOnKeyPressed(new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent event) {
					if (event.getCode().equals(KeyCode.ENTER)) {
						if (primaryStage.isFullScreen()) {
							primaryStage.setFullScreen(false);
						} else {
							primaryStage.setFullScreen(true);
						}
					}
					if (event.getCode().equals(KeyCode.SPACE)) {
						toggleMediaPlayer();
						updateValues();
					}

				}

			});
			/*
			 * String url1 =
			 * "http://wjdtmddnr24.dyndns.org/%EC%95%A0%EB%8B%88/Gochuumon%20wa%20Usagi%20Desu%20ka%20S2/HorribleSubs%20Gochuumon%20wa%20Usagi%20Desu%20ka%20S2%20-%2001%201080p.mp4";
			 * String url2 =
			 * "http://wjdtmddnr24.dyndns.org/%EB%8F%99%EC%98%81%EC%83%81/%ED%8B%B0%EB%B9%84%ED%94%8C/TEKKEN%20SEE%20THIS.mp4";
			 * String url3 =
			 * "http://wjdtmddnr24.dyndns.org/%EB%8F%99%EC%98%81%EC%83%81/PRIMAL%C3%97HEARTS%20OP%20-%20primal%20(320kbps).mp4";
			 * String surl =
			 * "http://wjdtmddnr24.dyndns.org/%EC%B6%94%EA%B0%80%EC%A4%91%20%EC%95%A0%EB%8B%88/Subete%20ga%20F%20ni%20Naru/HorribleSubs%20Subete%20ga%20F%20ni%20Naru%20-%2004%201080p.mkv";
			 */

			if (host != null && folderName != null && fileName != null) {
				BorderPane background = (BorderPane) scene.lookup("#skin");
				String url = host + "/" + folderName + "/" + fileName;
				url = url.replaceAll(" ", "%20");
				background.setBackground(new Background(
						new BackgroundImage(new Image((host + "/" + folderName + "/poster.jpg").replaceAll(" ", "%20")),
								BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
								BackgroundSize.DEFAULT)));

				System.out.println(url.substring(54));

				setupMedia(mediaView, url);
			} else {
				// MediaPlayer player = new MediaPlayer(null);
				// this.player = player;
				// mediaView.setMediaPlayer(player);
			}

			MenuBar menuBar = (MenuBar) scene.lookup("#menu");
			menuBar.getMenus().add(createMenu());
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

				@Override
				public void handle(WindowEvent event) {
					System.out.println("closing...");
					player.dispose();

				}
			});
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void updateValues() {
		if (progressBar != null && volumeBar != null) {
			Platform.runLater(new Runnable() {
				public void run() {
					Duration currentTime = player.getCurrentTime();
					progressBar.setDisable(duration.isUnknown());
					if (!progressBar.isDisabled() && !progressBar.isPressed() && duration.greaterThan(Duration.ZERO)
							&& !progressBar.isValueChanging()) {
						progressBar.setValue(currentTime.divide(duration).toMillis() * 100.0);
						totaltime.setText(formatTime(player.getCurrentTime(), player.getMedia().getDuration()));
					}
					volumeBar.setDisable(duration.isUnknown());

					if (!volumeBar.isValueChanging() && !volumeBar.isPressed() && !volumeBar.isDisabled()) {
						volumeBar.setValue((int) Math.round(player.getVolume() * 100));
						totalVolume.setText(String.format("%.2f%%", volumeBar.getValue()));
					}
				}
			});
		}
	}

	private static String formatTime(Duration elapsed, Duration duration) {
		int intElapsed = (int) Math.floor(elapsed.toSeconds());
		int elapsedHours = intElapsed / (60 * 60);
		if (elapsedHours > 0) {
			intElapsed -= elapsedHours * 60 * 60;
		}
		int elapsedMinutes = intElapsed / 60;
		int elapsedSeconds = intElapsed - elapsedHours * 60 * 60 - elapsedMinutes * 60;

		if (duration.greaterThan(Duration.ZERO)) {
			int intDuration = (int) Math.floor(duration.toSeconds());
			int durationHours = intDuration / (60 * 60);
			if (durationHours > 0) {
				intDuration -= durationHours * 60 * 60;
			}
			int durationMinutes = intDuration / 60;
			int durationSeconds = intDuration - durationHours * 60 * 60 - durationMinutes * 60;
			if (durationHours > 0) {
				return String.format("%d:%02d:%02d/%d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds,
						durationHours, durationMinutes, durationSeconds);
			} else {
				return String.format("%02d:%02d/%02d:%02d", elapsedMinutes, elapsedSeconds, durationMinutes,
						durationSeconds);
			}
		} else {
			if (elapsedHours > 0) {
				return String.format("%d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds);
			} else {
				return String.format("%02d:%02d", elapsedMinutes, elapsedSeconds);
			}
		}
	}

	public Main() {
		start();
	}

	public Main(String host, String folderName, String fileName) {
		this.host = host;
		this.folderName = folderName;
		this.fileName = fileName;
		start();
	}

}
