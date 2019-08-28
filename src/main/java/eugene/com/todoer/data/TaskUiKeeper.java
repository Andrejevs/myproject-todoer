package eugene.com.todoer.data;

import eugene.com.todoer.controllers.TaskController;
import javafx.scene.Parent;

public class TaskUiKeeper {
    private final Parent parent;
    private final TaskController control;
    private final int id;

    public TaskUiKeeper(int id, Parent parent, TaskController control) {
        this.parent = parent;
        this.control = control;
        this.id = id;
    }

    public Parent getParent() {
        return parent;
    }

    public TaskController getControl() {
        return control;
    }
}
