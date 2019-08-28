package eugene.com.todoer.data;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

public class SubTask {
    private final int parentTaskId;
    private final int id;
    private SimpleStringProperty taskName;
    private SimpleStringProperty description;
    private SimpleBooleanProperty isDone;

    public SubTask(String taskName, int id, int parentTaskId) {
        this.parentTaskId = parentTaskId;
        this.taskName = new SimpleStringProperty(taskName);
        this.id = id;
        this.isDone = new SimpleBooleanProperty(false);
        this.description = new SimpleStringProperty();

        description.set("none");
    }

    public SubTask(int id, int parentTaskId, String taskName, String description, boolean isDone) {
        this.id = id;
        this.parentTaskId = parentTaskId;
        this.taskName = new SimpleStringProperty(taskName);
        this.description = new SimpleStringProperty();
        this.description = new SimpleStringProperty(description);
        this.isDone = new SimpleBooleanProperty(isDone);
    }

    public String getTaskName() {
        return taskName.get();
    }

    public SimpleStringProperty taskNameProperty() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName.set(taskName);
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description.get();
    }

    public SimpleStringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public boolean isIsDone() {
        return isDone.get();
    }

    public SimpleBooleanProperty isDoneProperty() {
        return isDone;
    }

    public int getParentTaskId() {
        return parentTaskId;
    }
}
