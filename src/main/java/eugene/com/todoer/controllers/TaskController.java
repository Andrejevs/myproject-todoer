package eugene.com.todoer.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class TaskController {
    @FXML
    private Button btnClose;
    @FXML
    private Button btnCheck;
    @FXML
    private Button btnDays;
    @FXML
    private Button btnTaskName;
    @FXML
    private Button btnLeftDays;
    @FXML
    private TextField textTaskName;

    @FXML
    public void initialize() {

    }

    public Button getCloseBtn() {
        return btnClose;
    }

    public Button getBtnCheck() {
        return btnCheck;
    }

    public Button getBtnDays() {
        return btnDays;
    }

    public Button getBtnTaskName() {
        return btnTaskName;
    }

    public Button getBtnLeftDays() {
        return btnLeftDays;
    }
}
