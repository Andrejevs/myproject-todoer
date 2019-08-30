package eugene.com.todoer.logic;

import eugene.com.todoer.IUiListener;
import eugene.com.todoer.Storage;
import eugene.com.todoer.data.SubTask;
import eugene.com.todoer.data.Task;
import eugene.com.todoer.data.User;
import eugene.com.todoer.db.DbRequest;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class MainLogic {
    private static final Logger log = LoggerFactory.getLogger(MainLogic.class);
    private final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    private final DbRequest dbReq;
    private final IUiListener uiListener;
    private final Storage storage;
    private final User user;

    public MainLogic(IUiListener uiListener, User user) {
        this.storage = new Storage();
        this.uiListener = uiListener;
        this.dbReq = connectNewDb();
        this.user = getUserPropDb(user, dbReq);
    }

    public Storage getStorage() {
        return storage;
    }

    public void closeApp() {
        storage.getTasksMap().forEach((id, task) -> updateTask(task));

        service.execute(() -> {
            try {
                dbReq.disconnect();
            } catch (Exception e) {
                log.error("Error to close DB connection. Error: {}", e.getMessage(), e);
            } finally {
                service.shutdown();
            }

            service.shutdown();
        });

        log.debug("App was closed by user");
        uiListener.closeStage();
    }

    public void addNewTask() {
        String name = uiListener.getAndResetEnteredName();
        boolean isGlobal = uiListener.isGlobal();

        Task task = initTask(name, isGlobal);

        uiListener.createTaskUi(task, uiListener.getVboxBasedOnGlobal(task));

        addNewTaskDb(task);
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
        updateAllDb(task);
    }

    public void addSubTaskToTask(Task task) {
        SubTask subTask = new SubTask(task.getTaskName(), task.increaseSubTaskCount().get(), task.getId());

        task.getSubTaskList().add(subTask);

        addSubTaskDb(subTask);
        updateAllDb(task);
    }

    public void loadDbTasks() {
        ArrayList<Task> taskList = getAllTasksDb();

        if (taskList != null) {
            taskList.forEach(this::addDbTask);
        }

        Objects.requireNonNull(getAllSubTasksDb()).forEach(this::onGetAllSubTasksDb);
    }

    private void onGetAllSubTasksDb(SubTask subTask) {
        Task task = storage.getTasksMap().get(subTask.getParentTaskId());

        //TODO if there is no deleted task, subtasks load anyway
        if (task == null) {
            log.error("Found subtask with no task. id: {}, parentId: {}",
                    subTask.getId(), subTask.getParentTaskId());

            closeApp();
        }

        assert task != null;
        addSubTaskToTaskFromDb(task, subTask);
    }

    private ArrayList<SubTask> getAllSubTasksDb() {
        try {
            return dbReq.getAllSubTasks();
        } catch (Exception e) {
            log.error("Error to get all subtasks from db. Error: {}", e.getMessage(), e);
            closeApp();
        }

        return null;
    }

    private ArrayList<Task> getAllTasksDb() {
        try {
            return dbReq.getAllTasks();
        } catch (Exception e) {
            log.error("Error to get all tasks from db. Error: {}", e.getMessage(), e);
            closeApp();
        }

        return null;
    }

    private DbRequest connectNewDb() {
        try {
            return new DbRequest();
        } catch (SQLException e) {
            log.error("Error to connect to DB. Error: {}", e.getMessage(), e);
            closeApp();
        }

        return null;
    }

    private void addSubTaskDb(SubTask subTask) {
        service.execute(() -> {
            try {
                dbReq.addSubTask(subTask);
            } catch (SQLException e) {
                log.error("Error to add new subtask to db with id: {}, parentId: {}. Error: {}", subTask.getId(),
                        subTask.getParentTaskId(), e.getMessage(), e);
                closeApp();
            }
        });
    }

    private void updateAllDb(Task task) {
        service.execute(() -> {
            try {
                dbReq.updateAll(user, task);
            } catch (Exception e) {
                log.error("Error to update db with task id: {}. Error: {}", task.getId(), e.getMessage(), e);
                closeApp();
            }
        });
    }

    private void addNewTaskDb(Task task) {
        service.execute(() -> {
            try {
                dbReq.addNewTask(task);
            } catch (SQLException e) {
                log.error("Error to addNewTask to db with id: {}. Error: {}", task.getId(), e.getMessage(), e);
                closeApp();
            }
        });
    }

    private void addDbTask(Task task) {
        initTask(task);

        uiListener.createTaskUi(task, uiListener.getVboxBasedOnGlobal(task));
    }

    private void addSubTaskToTaskFromDb(Task task, SubTask subTask) {
        task.getSubTaskList().add(subTask);
    }

    private Task initTask(String name, boolean isGlobal) {
        Task task = new Task(name, user.getNewTaskId(), isGlobal, user.getIdUser());

        addSubTaskToTask(task);

        storage.addTaskToMap(user.getTaskIdCounter(), task);

        return task;
    }

    private void initTask(Task task) {
        storage.addTaskToMap(task.getId(), task);
    }

    private User getUserPropDb(User user, DbRequest dbReq) {
        try {
            return dbReq.getUser(user.getName(), user.getPass());
        } catch (Exception e) {
            log.error("Error to get user from db. Error: {}", e.getMessage(), e);
            closeApp();
        }

        return null;
    }
}
