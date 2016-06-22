package application;

import java.util.List;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

public class CustomBorderPane extends BorderPane {

	private String folder;
	private List<String> directories;
	public CustomBorderPane(Node e) {
		super(e);
	}
	public String getFolder() {
		return folder;
	}
	public void setFolder(String folder) {
		this.folder = folder;
	}
	public List<String> getDirectories() {
		return directories;
	}
	public void setDirectories(List<String> directories) {
		this.directories = directories;
	}
	
}
