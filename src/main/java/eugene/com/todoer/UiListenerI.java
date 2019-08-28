package eugene.com.todoer;

import eugene.com.todoer.data.Task;

public interface UiListenerI {
    String getAndResetEnteredName();
    void createTaskUi(Task task);
    void pressGlobalCheckBox();
}
