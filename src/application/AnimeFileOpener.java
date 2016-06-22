package application;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class AnimeFileOpener {

	public AnimeFileOpener(String folderName, List<String> directory) {
		Collections.sort(directory, new Comparator<String>() {
			@Override
			public int compare(String arg0, String arg1) {
				return arg0.compareTo(arg1);
			}
		});
		Stage primaryStage = new Stage();
		StackPane root = new StackPane();

		BorderPane borderPane = new BorderPane();
		ImageView imageview = new ImageView(
				("http://wjdtmddnr24.dyndns.org/%EC%95%A0%EB%8B%88/" + folderName + "/poster.jpg").replaceAll(" ",
						"%20"));
		imageview.setFitHeight(600);
		imageview.setFitWidth(400);
		borderPane.setLeft(imageview);

		GridPane gridPane = new GridPane();

		ListView<String> list = new ListView<String>();
		ObservableList<String> items = FXCollections.observableArrayList();
		for (int i = 0; i < directory.size(); i++) {
			String dir = directory.get(i);
			String extension = dir.substring(dir.length() - 3);
			if (extension.equals("avi") || extension.equals("mp4") || extension.equals("mkv")) {
				items.add(directory.get(i));
			}

		}
		list.setItems(items);
		borderPane.setCenter(list);
		root.getChildren().add(borderPane);
		Scene scene = new Scene(root, 800, 600);

		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
