package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root1 = FXMLLoader.load(getClass().getResource("menu.fxml"));
        Parent root2 = FXMLLoader.load(getClass().getResource("sample.fxml"));
        Button btn = (Button) root1.lookup("#btn_start");
        btn.setOnAction(event -> {
            primaryStage.setScene(new Scene(root2, 800, 800));
        });
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root1, 800, 800));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
