package eugene.com.todoer.controllers;

import eugene.com.todoer.data.Task;
import eugene.com.todoer.IUiListener;
import eugene.com.todoer.data.User;
import eugene.com.todoer.db.DbRequest;
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

public class MainController implements IUiListener {
    private static final Logger log = LoggerFactory.getLogger(MainController.class);
    private static final String TASK_PANE_PATH = "/paneTask.fxml";
    private final MainLogic logic;
    private final UiLogic uiLogic;
    private final Stage primStage;

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

    public MainController(Stage taskWindowStage, TaskWindowController taskWindowController, Stage primaryStage,
                          User user) {
        this.uiLogic = new UiLogic(taskWindowStage, taskWindowController);
        this.primStage = primaryStage;
        logic = new MainLogic(this, user);
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
    public void createTaskUi(Task task, VBox vbox) {
        try {
            uiLogic.createTaskUi(TASK_PANE_PATH, task, vbox, logic);
        } catch (IOException e) {
            log.error("Error to create task UI keeper. Error: {}", e.getMessage(), e);
        }
    }

    @Override
    public void pressGlobalCheckBox() {
        uiLogic.pressGlobalCheckBox(checkboxGlob);
    }

    @Override
    public void closeStage() {
        uiLogic.closeStage(primStage);
    }

    @Override
    public boolean isGlobal() {
        return uiLogic.getGlobalCheckbox(checkboxGlob);
    }

    @Override
    public VBox getVboxBasedOnGlobal(Task task) {
        return uiLogic.getVboxBasedOnGlobal(task, vboxGlobal, vboxLocal);
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
