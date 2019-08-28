package eugene.com.todoer.uiLogic;

import eugene.com.todoer.data.Task;
import eugene.com.todoer.data.TaskUiKeeper;
import eugene.com.todoer.controllers.TaskController;
import eugene.com.todoer.logic.MainLogic;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.util.converter.NumberStringConverter;

import java.util.concurrent.ConcurrentHashMap;

public class UiTaskPaneLogic {
    private final NumberStringConverter converterToString;

    public UiTaskPaneLogic(NumberStringConverter converterToString) {
        this.converterToString = converterToString;
    }

    public void initTaskPane(VBox vbox, TaskController taskController, Task task, Parent root,
                             TaskUiKeeper taskUiKeeper, UiTaskWindowLogic taskWindowLogic,
                             ConcurrentHashMap<Integer, TaskUiKeeper> taskKeeperList, MainLogic logic) {

        initBtnTaskName(taskController.getBtnTaskName(), task, taskUiKeeper, taskWindowLogic, logic);

        initCloseBtn(vbox, taskController, task, taskKeeperList, logic);

        Button checkBtn = initCheckBtn(taskController, task);

        task.isDoneProperty().addListener(event -> getIsDoneStyle(checkBtn, task.isIsDone()));

        vbox.getChildren().add(root);
    }

    private void initCloseBtn(VBox vbox, TaskController taskController, Task task,
                              ConcurrentHashMap<Integer, TaskUiKeeper> taskKeeperList, MainLogic logic) {
        taskController.getCloseBtn().setOnAction(event -> {
                    vbox.getChildren().remove(taskKeeperList.get(task.getId()).getParent());

                    task.setDeleted(true);

                    logic.updateTask(task);
                });
    }

    private Button initCheckBtn(TaskController taskController, Task task) {
        Button checkBtn = taskController.getBtnCheck();

        checkBtn.textProperty().bindBidirectional(task.subTaskCounterProperty(), converterToString);
        checkBtn.setOnAction(event -> onCheckBtn(task));

        return checkBtn;
    }

    private void initBtnTaskName(Button btnTaskName, Task task, TaskUiKeeper taskUiKeeper,
                                 UiTaskWindowLogic taskWindowLogic, MainLogic logic) {

        btnTaskName.setText(task.getTaskName());
        btnTaskName.setOnAction(event -> taskWindowLogic.initTaskWindowElements(task, taskUiKeeper, logic));
        btnTaskName.textProperty().bindBidirectional(task.taskNameProperty());
    }

    private void onCheckBtn(Task task) {
        task.setIsDone(!task.isIsDone());
    }

    private void getIsDoneStyle(Button btn, boolean done) {
        if (!done) {
            btn.setStyle("-fx-background-color:red");
        } else {
            btn.setStyle("-fx-background-color:#0fe06d");
        }
    }
}
