package eugene.com.todoer.uiLogic;

import eugene.com.todoer.controllers.TaskWindowController;
import eugene.com.todoer.data.SubTask;
import eugene.com.todoer.data.Task;
import eugene.com.todoer.data.TaskUiKeeper;
import eugene.com.todoer.logic.MainLogic;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.BooleanStringConverter;
import javafx.util.converter.NumberStringConverter;

public class UiTaskWindowLogic {
    private final TaskWindowController taskWindowController;
    private final NumberStringConverter converterToString;
    private final BooleanStringConverter converterToBool;
    private final Stage taskWindowStage;
    private SimpleIntegerProperty lastSubTaskCount;

    public UiTaskWindowLogic(TaskWindowController taskWindowController, Stage taskWindowStage,
                             NumberStringConverter converterToString) {
        this.taskWindowController = taskWindowController;
        this.lastSubTaskCount = new SimpleIntegerProperty();
        this.converterToString = converterToString;
        this.taskWindowStage = taskWindowStage;
        this.converterToBool = new BooleanStringConverter();
    }

    public void initTaskWindowElements(Task task, TaskUiKeeper taskUiKeeper, MainLogic logic) {
        initEndDate(task);
        initIsDoneBtn(task);

        taskWindowController.getBtnLeftDays().setText(String.valueOf(task.getEndDateLeft()));
        task.endDateLeftProperty().addListener(event -> taskWindowController.getBtnLeftDays().setText(
                String.valueOf(task.getEndDateLeft())));

        taskWindowController.setTableData(task.getSubTaskList(), getCellFactory());

        TextField globalTaskName = taskWindowController.getTextGlobalTaskName();

        taskWindowController.getBtnNextTask().setOnAction(event -> logic.addSubTaskToTask(task));

        TextField localTaskName = taskWindowController.getTextLocalTaskName();

        TextArea areaTaskDetails = taskWindowController.getTextAreaTaskDetails();

        TableView<SubTask> tableView = taskWindowController.getTableTasks();

        localTaskName.setText("");

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) ->
                onTableViewListener(localTaskName, areaTaskDetails, newSelection));

        globalTaskName.setOnKeyPressed(event -> task.taskNameProperty().setValue(
                globalTaskName.getText() + event.getText()));

        localTaskName.setOnKeyPressed(event -> onLocalTaskNameKeyPress(tableView, localTaskName, event));

        areaTaskDetails.setOnKeyPressed(event -> onAreaTaskDetailKeyPress(tableView, areaTaskDetails, event));

        taskWindowController.setControllerFields(taskUiKeeper);

        taskWindowStage.setTitle(task.getTaskName());
        taskWindowStage.show();
    }

    private void initEndDate(Task task) {
        DatePicker endDate = taskWindowController.getDateEndDate();

        endDate.setOnAction(event -> task.setEndDate(endDate.getValue()));

        endDate.setValue(task.getEndDate());
    }

    private void initIsDoneBtn(Task task) {
        Button btc = taskWindowController.getBtnIsDone();

        StringProperty btnIsDoneProperty = btc.textProperty();

        btnIsDoneProperty.unbindBidirectional(lastSubTaskCount);

        lastSubTaskCount = task.subTaskCounterProperty();

        btnIsDoneProperty.bindBidirectional(lastSubTaskCount, converterToString);

        getIsDoneStyle(btc, task.isIsDone());
    }

    private void onTableViewListener(TextField localTaskName, TextArea areaTaskDetails, SubTask newSelection) {
        if (newSelection != null) {
            localTaskName.setText(newSelection.getTaskName());
            areaTaskDetails.setText(newSelection.getDescription());
        }
    }

    private void onLocalTaskNameKeyPress(TableView<SubTask> tableView, TextField localTaskName, KeyEvent event) {
        tableView.getSelectionModel().getSelectedItem().setTaskName(localTaskName.getText() + event.getText());
        tableView.refresh();
    }

    private void onAreaTaskDetailKeyPress(TableView<SubTask> tableView, TextArea areaTaskDetails, KeyEvent event) {
        tableView.getSelectionModel().getSelectedItem().setDescription(areaTaskDetails.getText() + event.getText());
        tableView.refresh();
    }

    private Callback<TableColumn<SubTask, String>, TableCell<SubTask, String>> getCellFactory() {
        Callback<TableColumn<SubTask, String>, TableCell<SubTask, String>> cellFactory
                = //
                new Callback<TableColumn<SubTask, String>, TableCell<SubTask, String>>() {
                    @Override
                    public TableCell call(final TableColumn<SubTask, String> param) {
                        final TableCell<SubTask, String> cell = new TableCell<SubTask, String>() {

                            final Button btn = addNewIsDoneSubTaskBtn();

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    SubTask subTask = getTableView().getItems().get(getIndex());

                                    getIsDoneStyle(btn, subTask.isIsDone());

                                    btn.setOnAction(event -> onSubTaskIsDoneBtn(subTask));

                                    InvalidationListener listener = getSubTaskIsDoneBtnListener(btn, subTask);

                                    //if method will be called more then once
                                    subTask.isDoneProperty().removeListener(listener);
                                    subTask.isDoneProperty().addListener(listener);

                                    setGraphic(btn);
                                    setText(null);
                                }
                            }
                        };

                        return cell;
                    }
                };

        return cellFactory;
    }

    private Button addNewIsDoneSubTaskBtn() {
        Button btn = new Button();

        btn.setMaxSize(20, 20);
        btn.setMinSize(20, 20);
        btn.setPrefSize(20, 20);

        return btn;
    }

    private void onSubTaskIsDoneBtn(SubTask subTask) {
        subTask.isDoneProperty().setValue(!subTask.isIsDone());
    }

    private InvalidationListener getSubTaskIsDoneBtnListener(Button btn, SubTask subTask) {
        return listener -> {
            getIsDoneStyle(btn, subTask.isIsDone());
        };
    }

    private void getIsDoneStyle(Button btn, boolean isDone) {
        if (!isDone) {
            btn.setStyle("-fx-background-color:red");

            return;
        }

        btn.setStyle("-fx-background-color:#0fe06d");
    }
}
