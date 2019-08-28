package eugene.com.todoer;

import eugene.com.todoer.controllers.MainController;
import eugene.com.todoer.controllers.TaskWindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main extends Application {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static final String MAIN_UI_PATH = "/main.fxml";
    private static final String TASK_UI_PATH = "/windowTask.fxml";
    private static final String STYLE_PATH = "style.css";
    private static final String TITLE_NAME = "TODOER";
    private double offsetX = 0;
    private double offsetY = 0;

    public Main() {

    }

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            log.error("Error: {}", e.getMessage(), e);
        });

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(MAIN_UI_PATH));

        TaskWindowController taskWindowController = new TaskWindowController();

        MainController mainController = new MainController(
                prepareTaskWindowStage(taskWindowController), taskWindowController);

        fxmlLoader.setController(mainController);

        Scene scene = new Scene(fxmlLoader.load());

        handlersRegistration(primaryStage, scene.getRoot());
        showStageWithSettings(primaryStage, scene);
    }

    private Stage prepareTaskWindowStage(TaskWindowController taskWindowController) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(TASK_UI_PATH));
        fxmlLoader.setController(taskWindowController);

        Parent parent = fxmlLoader.load();

        Stage stage = new Stage();
        stage.setScene(new Scene(parent));

        return stage;
    }

    private void showStageWithSettings(Stage primaryStage, Scene scene) {
        scene.getStylesheets().add(STYLE_PATH);
        scene.setFill(Color.TRANSPARENT);

        primaryStage.setScene(scene);
        primaryStage.setTitle(TITLE_NAME);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.show();
    }

    private void handlersRegistration(Stage primaryStage, Parent root) {
        root.setOnMousePressed(event -> {
            offsetX = event.getSceneX();
            offsetY = event.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - offsetX);
            primaryStage.setY(event.getScreenY() - offsetY);
        });
    }
}