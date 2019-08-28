package eugene.com.todoer.uiLogic;

import eugene.com.todoer.Storage;
import eugene.com.todoer.UiTimer;
import eugene.com.todoer.controllers.TaskController;
import eugene.com.todoer.controllers.TaskWindowController;
import eugene.com.todoer.data.Task;
import eugene.com.todoer.data.TaskUiKeeper;
import eugene.com.todoer.logic.MainLogic;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class UiLogic {
    private final ConcurrentHashMap<Integer, TaskUiKeeper> taskKeeperList;
    private final UiTaskWindowLogic taskWindowLogic;
    private final UiTaskPaneLogic taskPaneLogic;
    private final UiTimer uiTimer;

    public UiLogic(Stage taskWindowStage, TaskWindowController taskWindowController) {
        NumberStringConverter converterToString = new NumberStringConverter();

        this.taskWindowLogic = new UiTaskWindowLogic(taskWindowController, taskWindowStage, converterToString);
        this.taskKeeperList = new ConcurrentHashMap();
        this.taskPaneLogic = new UiTaskPaneLogic(converterToString);
        this.uiTimer = new UiTimer();
    }

    public String getAndResetEnteredName(TextField textNewTaskName) {
        String text = textNewTaskName.getText();

        Platform.runLater(() -> textNewTaskName.setText(""));

        return text;
    }

    public void createTaskUi(String taskPanePath, Task task, VBox vbox, MainLogic logic) throws IOException {
        TaskController taskController = new TaskController();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(taskPanePath));
        fxmlLoader.setController(taskController);

        Parent root = fxmlLoader.load();

        int id = task.getId();

        Platform.runLater(() -> {
            TaskUiKeeper taskUiKeeper = createNewTaskKeeper(taskController, root, id);

            taskPaneLogic.initTaskPane(vbox, taskController, task, root, taskUiKeeper, taskWindowLogic, taskKeeperList,
                    logic);
        });
    }

    public VBox getVBoxBasedOnGlob(CheckBox checkboxGlob, VBox globBox, VBox dayBox) {
        if (checkboxGlob.isSelected()) {
            return globBox;
        }

        return dayBox;
    }

    public void checkGlobalCheckBox(CheckBox checkboxGlob) {
        Platform.runLater(() -> checkboxGlob.setSelected(!checkboxGlob.isSelected()));
    }

    public void setTimerUiUpdate(Storage storage) {
        uiTimer.setUiUpdateTimer(storage, taskKeeperList);
    }

    public void closeStage(Stage primStage) {
        Platform.runLater(() -> {
            uiTimer.stopTimer();
            primStage.close();
        });
    }

    private TaskUiKeeper createNewTaskKeeper(TaskController taskController, Parent root, int id) {
        TaskUiKeeper taskUiKeeper = new TaskUiKeeper(id, root, taskController);
        taskKeeperList.put(id, taskUiKeeper);

        return taskUiKeeper;
    }
}
