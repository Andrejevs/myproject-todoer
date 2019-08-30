package eugene.com.todoer.data;

import eugene.com.todoer.logic.MainLogic;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Task {
    private final int id;
    private final int idUser;
    private final LocalDate date;
    private final SimpleStringProperty taskName;
    private SimpleIntegerProperty subTaskCounter = new SimpleIntegerProperty(0);
    private SimpleIntegerProperty endDateLeft;
    private SimpleBooleanProperty isDone;
    private LocalDate endDate;
    private boolean isDeleted = false;
    private boolean isGlobal = false;
    private ObservableList<SubTask> subTaskList;

    public Task(String name, int id, boolean isGlobal, int idUser) {
        this.date = LocalDate.now();
        this.taskName = new SimpleStringProperty(name);
        this.id = id;
        this.subTaskList = FXCollections.observableArrayList();
        this.isDone = new SimpleBooleanProperty(false);
        this.endDate = LocalDate.now();
        this.endDateLeft = new SimpleIntegerProperty(0);
        this.isGlobal = isGlobal;
        this.idUser = idUser;
    }

    public Task(int id, int subTaskCounter, boolean isDone, String taskName, LocalDate date, LocalDate endDate,
                boolean isDeleted, boolean isGlobal, int idUser) {
        this.id = id;
        this.subTaskCounter.set(subTaskCounter);
        this.isDone = new SimpleBooleanProperty(isDone);
        this.taskName = new SimpleStringProperty(taskName);
        this.date = date;
        this.endDate = endDate;
        this.isDeleted = isDeleted;
        this.subTaskList = FXCollections.observableArrayList();
        this.endDateLeft = new SimpleIntegerProperty(0);
        this.isGlobal = isGlobal;
        this.idUser = idUser;
    }

    public String getTaskName() {
        return taskName.get();
    }

    public StringProperty taskNameProperty() {
        return taskName;
    }

    public int getId() {
        return id;
    }

    public ObservableList<SubTask> getSubTaskList() {
        return subTaskList;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getNextSubTaskCount() {
        return increaseSubTaskCount().get();
    }

    public SimpleIntegerProperty increaseSubTaskCount() {
        subTaskCounter.set(subTaskCounter.get() + 1);

        return subTaskCounter;
    }

    public int getSubTaskCounter() {
        return subTaskCounter.get();
    }

    public SimpleIntegerProperty subTaskCounterProperty() {
        return subTaskCounter;
    }

    public boolean isIsDone() {
        return isDone.get();
    }

    public SimpleBooleanProperty isDoneProperty() {
        return isDone;
    }

    public void setIsDone(boolean isDone) {
        this.isDone.set(isDone);
    }

    public void setSimpleBooleanProperty(SimpleBooleanProperty simBool) {
        isDone = simBool;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDateLeft(int endDateLeft) {
        this.endDateLeft.set(endDateLeft);
    }

    public int getEndDateLeft() {
        return endDateLeft.get();
    }

    public SimpleIntegerProperty endDateLeftProperty() {
        return endDateLeft;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    public int getIdUser() {
        return idUser;
    }
}
