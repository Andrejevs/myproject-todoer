package eugene.com.todoer;

import eugene.com.todoer.data.Task;
import javafx.scene.layout.VBox;

public interface IUiListener {
    String getAndResetEnteredName();
    VBox getVboxBasedOnGlobal(Task task);
    boolean isGlobal();
    void createTaskUi(Task task, VBox vbox);
    void pressGlobalCheckBox();
    void closeStage();
}
