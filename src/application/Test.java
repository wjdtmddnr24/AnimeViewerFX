package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Test extends Application {

	@Override
	public void start(Stage primaryStage) {
		StackPane root = new StackPane();
		Scene scene = new Scene(root, 1280, 810);
		
		ScrollPane sp = new ScrollPane();
		sp.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		GridPane gridPane = new GridPane();

		
		try {
			List<Folder> animations = getAnimeList();
			for (int i = 0; i < 15; i++) {

				ImageView imageView = new ImageView(
						"http://wjdtmddnr24.dyndns.org/%EC%95%A0%EB%8B%88/Sekai%20Seifuku%20-%20Bouryaku%20no%20Zvezda/poster.jpg");
				System.out.println("http://wjdtmddnr24.dyndns.org/%EC%95%A0%EB%8B%88/" + animations.get(i).getDir() + "/poster.jpg");
				System.out.println("http://wjdtmddnr24.dyndns.org/%EC%95%A0%EB%8B%88/Sekai%20Seifuku%20-%20Bouryaku%20no%20Zvezda/poster.jpg");

				// Text text = new Text(animations.get(i).getDir());
				// BorderPane borderPane = new BorderPane(imageView);
				// borderPane.setBottom(text);
				gridPane.add(imageView, 0, i);
			}
		} catch (ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sp.setContent(gridPane);
		root.getChildren().add(gridPane);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
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
			// System.out.println(dir);
			for (int j = 0; j < dirList.size(); j++) {
				folder.addFile(dirList.get(j).toString());
				// System.out.println(dirList.get(j).toString());
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
