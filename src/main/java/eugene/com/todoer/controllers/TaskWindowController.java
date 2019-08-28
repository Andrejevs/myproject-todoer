package eugene.com.todoer.controllers;

import eugene.com.todoer.data.SubTask;
import eugene.com.todoer.data.TaskUiKeeper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TaskWindowController {
    @FXML
    private AnchorPane paneUpperMenu;
    @FXML
    private TextField textGlobalTaskName;
    @FXML
    private Button btnIsDone;
    @FXML
    private Button btnNextTask;
    @FXML
    private Button btnLeftDays;
    @FXML
    private TextField textLocalTaskName;
    @FXML
    private TextArea textAreaTaskDetails;
    @FXML
    private DatePicker dateEndDate;
    @FXML
    private TableView<SubTask> tableTasks;
    @FXML
    private TableColumn<SubTask, String> tableColumnV;
    @FXML
    private TableColumn<SubTask, String> tableColumnTasks;
    @FXML
    private TableColumn<SubTask, String> tableColumnDetails;

    @FXML
    public void initialize() {
        setEndDateFormat();
    }

    public void setControllerFields(TaskUiKeeper taskUiKeeper) {
        textGlobalTaskName.setText(taskUiKeeper.getControl().getBtnTaskName().getText());
    }

    public void setTableData(ObservableList<SubTask> subTaskObs,
                             Callback<TableColumn<SubTask, String>, TableCell<SubTask, String>> cellFactory) {
        tableColumnV.setCellValueFactory(new PropertyValueFactory<>("BtnIsDone"));
        tableColumnTasks.setCellValueFactory(new PropertyValueFactory<>("TaskName"));
        tableColumnDetails.setCellValueFactory(new PropertyValueFactory<>("Description"));

        tableColumnV.setCellFactory(cellFactory);

        tableTasks.setItems(subTaskObs);
    }

    public Button getBtnNextTask() {
        return btnNextTask;
    }

    public TextField getTextLocalTaskName() {
        return textLocalTaskName;
    }

    public TextArea getTextAreaTaskDetails() {
        return textAreaTaskDetails;
    }

    public TextField getTextGlobalTaskName() {
        return textGlobalTaskName;
    }

    public TableView<SubTask> getTableTasks() {
        return tableTasks;
    }

    public Button getBtnIsDone() {
        return btnIsDone;
    }

    public DatePicker getDateEndDate() {
        return dateEndDate;
    }

    public Button getBtnLeftDays() {
        return btnLeftDays;
    }

    private void setEndDateFormat() {
        String pattern = "dd (MM.yy)";

        dateEndDate.setConverter(new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });
    }
}
