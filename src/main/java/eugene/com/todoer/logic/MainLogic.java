package eugene.com.todoer.logic;

import eugene.com.todoer.Storage;
import eugene.com.todoer.UiListenerI;
import eugene.com.todoer.data.SubTask;
import eugene.com.todoer.data.Task;
import eugene.com.todoer.db.DbRequest;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class MainLogic {
    private static final Logger log = LoggerFactory.getLogger(MainLogic.class);
    private final DbRequest dbReq;
    private final UiListenerI uiListener;
    private final Storage storage;
    private int taskIdCounter = 0;

    public MainLogic(UiListenerI uiListener) {
        this.storage = new Storage();
        this.uiListener = uiListener;
        this.dbReq = new DbRequest();
    }

    public Storage getStorage() {
        return storage;
    }

    public void closeApp() {
        dbReq.disconnect();
        log.debug("App was closed by user");

        System.exit(0);
    }

    public void addNewTask() {
        String name = uiListener.getAndResetEnteredName();

        Task task = initTask(name);

        uiListener.createTaskUi(task);

        dbReq.addNewTask(task);
    }

    public void addDbTask(Task task) {
        initTask(task);

        uiListener.createTaskUi(task);
    }

    public void onNewTextKeyPressed(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            addNewTask();
        }

        if (event.getCode().equals(KeyCode.ALT)) {
            uiListener.pressGlobalCheckBox();
        }
    }

    public void updateTask(Task task) {
        dbReq.updateTask(task);
    }

    public void addSubTaskToTask(Task task) {
        SubTask subTask = new SubTask(task.getTaskName(), task.increaseSubTaskCount().get(), task.getId());

        task.getSubTaskList().add(subTask);

        dbReq.addSubTask(subTask);
        dbReq.updateTask(task);
    }

    public void addSubTaskToTaskFromDb(Task task, SubTask subTask) {
        task.getSubTaskList().add(subTask);
    }

    public void loadDbTasks() {
        ArrayList<Task> taskList = dbReq.getAllTasks();

        if (taskList != null) {
            taskList.forEach(this::addDbTask);
        }

        dbReq.getAllSubTasks().forEach(subTask -> {
            addSubTaskToTaskFromDb(storage.getTasksMap().get(subTask.getParentTaskId()), subTask);
        });

    }

    private Task initTask(String name) {
        Task task = new Task(name, ++taskIdCounter);

        addSubTaskToTask(task);

        storage.addTaskToMap(taskIdCounter, task);

        return task;
    }

    private void initTask(Task task) {
        storage.addTaskToMap(task.getId(), task);
    }
}
