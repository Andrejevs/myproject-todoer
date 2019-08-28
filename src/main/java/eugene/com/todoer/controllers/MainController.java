package eugene.com.todoer.controllers;

import eugene.com.todoer.data.Task;
import eugene.com.todoer.UiListenerI;
import eugene.com.todoer.logic.MainLogic;
import eugene.com.todoer.uiLogic.UiLogic;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class MainController implements UiListenerI {
    private static final Logger log = LoggerFactory.getLogger(MainController.class);
    private static final String TASK_PANE_PATH = "/paneTask.fxml";
    private final MainLogic logic = new MainLogic(this);
    private final UiLogic uiLogic;

    @FXML
    private AnchorPane paneMain;
    @FXML
    private VBox vboxGlobal;
    @FXML
    private VBox vboxLocal;
    @FXML
    private Button btnClose;
    @FXML
    private Button btcAddTask;
    @FXML
    private CheckBox checkboxGlob;
    @FXML
    private TextField textNewTaskName;

    public MainController(Stage taskWindowStage, TaskWindowController taskWindowController) {
        uiLogic = new UiLogic(taskWindowStage, taskWindowController);
    }

    @FXML
    public void initialize() {
        btnClose.setOnAction(event -> logic.closeApp());
        btcAddTask.setOnAction(event -> logic.addNewTask());
        textNewTaskName.setOnKeyPressed(this::onNewTextKeyPressed);

        setTimerUiUpdate();
        logic.loadDbTasks();
    }

    @Override
    public String getAndResetEnteredName() {
        return uiLogic.getAndResetEnteredName(textNewTaskName);
    }

    @Override
    public void createTaskUi(Task task) {
        try {
            uiLogic.createTaskUi(TASK_PANE_PATH, task, getVBoxBasedOnGlob(), logic);
        } catch (IOException e) {
            log.error("Error to create task UI keeper. Error: {}", e.getMessage(), e);
        }
    }

    @Override
    public void pressGlobalCheckBox() {
        uiLogic.checkGlobalCheckBox(checkboxGlob);
    }

    private VBox getVBoxBasedOnGlob() {
        return uiLogic.getVBoxBasedOnGlob(checkboxGlob, vboxGlobal, vboxLocal);
    }

    private void setTimerUiUpdate() {
        uiLogic.setTimerUiUpdate(logic.getStorage());
    }

    private void onNewTextKeyPressed(KeyEvent event) {
        logic.onNewTextKeyPressed(event);
    }

}
