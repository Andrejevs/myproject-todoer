package eugene.com.todoer.logic;

import eugene.com.todoer.Storage;
import eugene.com.todoer.IUiListener;
import eugene.com.todoer.data.SubTask;
import eugene.com.todoer.data.Task;
import eugene.com.todoer.db.DbRequest;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class MainLogic {
    private static final Logger log = LoggerFactory.getLogger(MainLogic.class);
    private final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    private final DbRequest dbReq;
    private final IUiListener uiListener;
    private final Storage storage;
    private int taskIdCounter = 0;

    public MainLogic(IUiListener uiListener) {
        this.storage = new Storage();
        this.uiListener = uiListener;
        this.dbReq = new DbRequest();
    }

    public Storage getStorage() {
        return storage;
    }

    public void closeApp() {
        storage.getTasksMap().forEach((id, task) -> updateTask(task));

        service.execute(() -> {
            dbReq.disconnect();

            service.shutdown();
        });

        log.debug("App was closed by user");
        uiListener.closeStage();
    }

    public void addNewTask() {
        String name = uiListener.getAndResetEnteredName();

        Task task = initTask(name);

        uiListener.createTaskUi(task);

        service.execute(() -> dbReq.addNewTask(task));

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
        service.execute(() -> dbReq.updateTask(task));
    }

    public void addSubTaskToTask(Task task) {
        SubTask subTask = new SubTask(task.getTaskName(), task.increaseSubTaskCount().get(), task.getId());

        task.getSubTaskList().add(subTask);

        service.execute(() -> {
            dbReq.addSubTask(subTask);
            dbReq.updateTask(task);
        });
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
            Task task = storage.getTasksMap().get(subTask.getParentTaskId());

            if (task == null) {
                log.error("Found subtask with no task. id: {}, parentId: {}",
                        subTask.getId(), subTask.getParentTaskId());

                closeApp();
            }

            addSubTaskToTaskFromDb(task, subTask);
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
