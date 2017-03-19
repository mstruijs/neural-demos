import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Created by Ziad on 9-3-2017.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("resources/main.fxml"));
        primaryStage.setTitle("Neural Network examples");
        primaryStage.setScene(new Scene(root, 1280, 960));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
