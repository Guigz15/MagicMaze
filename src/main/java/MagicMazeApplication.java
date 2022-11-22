import controller.MainWindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.Getter;

import java.io.IOException;
import java.util.Objects;

public class MagicMazeApplication extends Application {

    @Getter
    private MainWindowController mainWindowController;

    public static void main(String[] args) {
        launch();
    }

    /**
     * This method is called by default when the application is launched
     * @param stage the stage to display the application
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(getClass().getClassLoader().getResource("views/main_window.fxml")));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        mainWindowController = fxmlLoader.getController();
        stage.setTitle("MagicMaze");
        stage.setScene(scene);
        stage.getIcons().add(new Image("images/maze.png"));
        stage.show();
    }
}
