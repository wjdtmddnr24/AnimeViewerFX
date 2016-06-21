package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javafx.application.Application;
import javafx.stage.Stage;

public class AnimeListingMain extends Application {

	@Override
	public void start(Stage primaryStage) {

	}

	public static void main(String[] args) {
		//launch(args);
		try {
			Socket socket = new Socket("teamlog.kr", 20125);
			System.out.println("connected");
			InputStreamReader reader = new InputStreamReader(socket.getInputStream());
			BufferedReader breader = new BufferedReader(reader);
			PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
			writer.println("wjdtmddnr24");
			System.out.println(breader.readLine());
			breader.close();
			reader.close();
			writer.close();
			socket.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
