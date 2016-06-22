package application;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.imageio.ImageIO;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class AnimeListingMain extends Application {
	public static List<Folder> animations;

	@Override
	public void start(Stage primaryStage) {
		try {
			System.out.print("start Window");
			Parent root = FXMLLoader.load(getClass().getResource("AnimeListingMain.fxml"));
			Scene scene = new Scene(root, 1280, 810);
	
			ScrollPane scrollPane = (ScrollPane) scene.lookup("#AnimeList");
			scrollPane.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
			scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
			scrollPane.prefWidthProperty().bind(scene.widthProperty());
			scrollPane.prefHeightProperty().bind(scene.heightProperty());
			Button button = (Button) scene.lookup("#LocalButton");
			button.setOnAction(new EventHandler<ActionEvent>() {
				
				@Override
				public void handle(ActionEvent event) {
					new Main();
				}
			});
			
			if (animations != null) {
				GridPane gridPane = new GridPane();
				gridPane.setVgap(15);
				gridPane.setAlignment(Pos.CENTER);

				for (int i = 0, j = 0; i < animations.size(); i++) {
					if (i != 0 && i % 3 == 0) {
						j++;
					}
					ImageView imageView = new ImageView(("http://wjdtmddnr24.dyndns.org/%EC%95%A0%EB%8B%88/"
							+ animations.get(i).getDir() + "/poster.jpg").replaceAll(" ", "%20"));
					imageView.setFitHeight(600);
					imageView.setFitWidth(400);
					System.out.println("http://wjdtmddnr24.dyndns.org/%EC%95%A0%EB%8B%88" + animations.get(i).getDir()
							+ "/poster.jpg");
					Text text = new Text(animations.get(i).getDir());
					text.setLayoutX(150);
					CustomBorderPane borderPane = new CustomBorderPane(imageView);
					borderPane.setBottom(text);
					borderPane.setDirectories(animations.get(i).getFiles());
					borderPane.setFolder(animations.get(i).getDir());
					borderPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent mouseEvent) {
							if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
								if (mouseEvent.getClickCount() == 2) {
									System.out.println("Selected");
									CustomBorderPane borderPane = (CustomBorderPane) mouseEvent.getSource();

									new AnimeFileOpener(borderPane.getFolder(), borderPane.getDirectories());

								}
							}
						}
					});
					gridPane.add(borderPane, i % 3, j);
					primaryStage.setScene(scene);
					primaryStage.show();
				}
				scrollPane.setContent(gridPane);
				gridPane.prefWidthProperty().bind(scrollPane.widthProperty());
				gridPane.prefHeightProperty().bind(scrollPane.heightProperty());
			}else{
				System.out.println("ASdf");
			//	primaryStage.close();
				new Main();
			}
		
		
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		String result;
		try {
			animations = getAnimeList();
			Collections.sort(animations, new Comparator<Folder>() {
				@Override
				public int compare(Folder o1, Folder o2) {
					return o1.getDir().compareTo(o2.getDir());
				}
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		launch(args);

	}

	public static List<Folder> getAnimeList() throws ParseException, UnknownHostException, IOException {
		String jsonQuery = getAnimeJSON();
		if (jsonQuery == null)
			return null;
		List<Folder> animeList = new ArrayList<Folder>();
		JSONParser parser = new JSONParser();
		JSONObject rootObject = (JSONObject) parser.parse(jsonQuery);
		JSONArray root = (JSONArray) rootObject.get("root");
		for (int i = 0; i < root.size(); i++) {
			JSONObject indexObject = (JSONObject) root.get(i);
			String dir = indexObject.get("dir").toString();
			JSONArray dirList = (JSONArray) indexObject.get("files");
			Folder folder = new Folder(dir);
			for (int j = 0; j < dirList.size(); j++) {
				folder.addFile(dirList.get(j).toString());
			}
			animeList.add(folder);
		}

		return animeList;
	}

	public static String getAnimeJSON() throws UnknownHostException, IOException {
		Socket socket = new Socket("teamlog.kr", 20125);
		System.out.println("connected");
		InputStreamReader reader = new InputStreamReader(socket.getInputStream());
		BufferedReader breader = new BufferedReader(reader);
		PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
		writer.println("wjdtmddnr24");
		String result = breader.readLine();
		breader.close();
		reader.close();
		writer.close();
		socket.close();
		return result;
	}
}
