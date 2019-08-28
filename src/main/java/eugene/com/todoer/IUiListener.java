package eugene.com.todoer;

import eugene.com.todoer.data.Task;

public interface IUiListener {
    String getAndResetEnteredName();
    void createTaskUi(Task task);
    void pressGlobalCheckBox();
    void closeStage();
}
