package eugene.com.todoer.db;

import eugene.com.todoer.data.SubTask;
import eugene.com.todoer.data.Task;
import eugene.com.todoer.data.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;

public class DbRequest {
    private static final Logger log = LoggerFactory.getLogger(DbRequest.class);
    private final String DB_URL = "jdbc:sqlite:todoer.db";
    private Connection conn;
    private PreparedStatement pstmt;
    private ResultSet rs;

    public DbRequest() throws SQLException {
        connect();
    }

    public void insertNewUser(int id, String name, String pass) throws SQLException {
        String sql = "INSERT INTO users(id,name,pass) VALUES(?,?,?)";

        pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, id);
        pstmt.setString(2, name);
        pstmt.setString(3, pass);

        pstmt.executeUpdate();

        log.debug("New user was insert to db");
    }

    public void addNewTask(Task task) throws SQLException {
        String sql = "INSERT INTO tasks(id,subTaskCounter,isDone,taskName,date,endDate,isDeleted,isGlobal,idUser) " +
                "VALUES(?,?,?,?,?,?,?,?,?)";

        int id = task.getId();

        pstmt = conn.prepareStatement(sql);

        pstmt.setInt(1, id);
        pstmt.setInt(2, task.getSubTaskCounter());
        pstmt.setBoolean(3, task.isIsDone());
        pstmt.setString(4, task.getTaskName());
        pstmt.setDate(5, Date.valueOf(task.getDate()));
        pstmt.setDate(6, Date.valueOf(task.getEndDate()));
        pstmt.setBoolean(7, task.isDeleted());
        pstmt.setBoolean(8, task.isGlobal());
        pstmt.setInt(9, task.getIdUser());

        pstmt.executeUpdate();

        log.debug("New task was insert to db with id: {}", id);
    }

    public void updateAll(User user, Task task) throws Exception {
        updateUser(user);
        updateTask(task);

        for (SubTask subTask : task.getSubTaskList()) {
            updateSubTask(subTask);
        }
    }

    public void updateTask(Task task) throws SQLException {
        String sql = "UPDATE tasks SET subTaskCounter=?,isDone=?,taskName=?,date=?,endDate=?,isDeleted=?,isGlobal=?," +
                "idUser=? WHERE id=?";

        int id = task.getId();

        pstmt = conn.prepareStatement(sql);

        pstmt.setInt(1, task.getSubTaskCounter());
        pstmt.setBoolean(2, task.isIsDone());
        pstmt.setString(3, task.getTaskName());
        pstmt.setDate(4, Date.valueOf(task.getDate()));
        pstmt.setDate(5, Date.valueOf(task.getEndDate()));
        pstmt.setBoolean(6, task.isDeleted());
        pstmt.setBoolean(7, task.isGlobal());
        pstmt.setInt(8, task.getIdUser());
        pstmt.setInt(9, id);

        pstmt.executeUpdate();

        log.debug("update db task id: {}", id);
    }

    public void addSubTask(SubTask subTask) throws SQLException {
        String sql = "INSERT INTO subTask(id,parentId,taskName,description,isDone) " +
                "VALUES(?,?,?,?,?)";

        int id = subTask.getId();
        int parentId = subTask.getParentTaskId();

        pstmt = conn.prepareStatement(sql);

        pstmt.setInt(1, id);
        pstmt.setInt(2, parentId);
        pstmt.setString(3, subTask.getTaskName());
        pstmt.setString(4, subTask.getDescription());
        pstmt.setBoolean(5, subTask.isIsDone());

        pstmt.executeUpdate();
        log.debug("New subtask was added to db with id: {}, parentId: {}", id, parentId);
    }

    public void updateSubTask(SubTask subTask) throws Exception {
        String sql = "UPDATE subTask SET taskName=?,description=?,isDone=? WHERE id=? AND parentId=?";

        int id = subTask.getId();
        int parentId = subTask.getParentTaskId();

        pstmt = conn.prepareStatement(sql);

        pstmt.setString(1, subTask.getTaskName());
        pstmt.setString(2, subTask.getDescription());
        pstmt.setBoolean(3, subTask.isIsDone());
        pstmt.setInt(4, id);
        pstmt.setInt(5, parentId);

        pstmt.executeUpdate();

        log.debug("update db subtask id: {}, parentId: {}", id, parentId);
    }

    public ArrayList<Task> getAllTasks() throws Exception {
        String sql = "SELECT id,subTaskCounter,isDone,taskName,date,endDate,isDeleted,isGlobal,idUser FROM tasks";

        rs = conn.prepareStatement(sql).executeQuery();

        ArrayList<Task> taskList = new ArrayList<>();
        while (rs.next()) {
            boolean isDeleted = rs.getBoolean("isDeleted");

            if (isDeleted) {
                continue;
            }

            int id = rs.getInt("id");
            int subTaskCounter = rs.getInt("subTaskCounter");
            int idUser = rs.getInt("idUser");
            boolean isDone = rs.getBoolean("isDone");
            boolean isGlobal = rs.getBoolean("isGlobal");
            String taskName = rs.getString("taskName");
            LocalDate date = convertDateToLocalDate(rs.getDate("date"));
            LocalDate endDate = convertDateToLocalDate(rs.getDate("endDate"));

            Task task = new Task(id, subTaskCounter, isDone, taskName, date, endDate, isDeleted, isGlobal, idUser);
            taskList.add(task);
        }

        return taskList;
    }

    public User getUser(String userName, String pass) throws Exception {
        String sql = "SELECT id,taskIdCounter FROM users WHERE name=? AND pass=?";

        pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, userName);
        pstmt.setString(2, pass);

        rs = pstmt.executeQuery();

        ArrayList<User> userList = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt("id");
            int taskIdCounter = rs.getInt("taskIdCounter");

            User user = new User(userName, pass);
            user.setTaskIdCounter(taskIdCounter);
            user.setIdUser(id);

            userList.add(user);
        }

        if (userList.size() == 1) {
            return userList.get(0);
        } else if (userList.size() == 0) {
            throw new Exception("There is no users with such name and pass: " + userName);
        } else {
            throw new Exception("In db more then 1 user with same name and pass: " + userName);
        }
    }

    public void updateUser(User user) throws Exception {
        String sql = "UPDATE users SET name=?,pass=?,taskIdCounter=? WHERE id=?";

        String name = user.getName();
        int userId = user.getIdUser();

        pstmt = conn.prepareStatement(sql);

        pstmt.setString(1, name);
        pstmt.setString(2, user.getPass());
        pstmt.setInt(3, user.getTaskIdCounter());
        pstmt.setInt(4, userId);

        pstmt.executeUpdate();

        log.debug("update user {} with id: {}", name, userId);
    }

    public ArrayList<SubTask> getAllSubTasks() throws Exception {
        String sql = "SELECT id,parentId,taskName,description,isDone FROM subTask";

        rs = conn.prepareStatement(sql).executeQuery();

        ArrayList<SubTask> taskList = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt("id");
            int parentId = rs.getInt("parentId");
            String taskName = rs.getString("taskName");
            String description = rs.getString("description");
            boolean isDone = rs.getBoolean("isDone");

            SubTask subTask = new SubTask(id, parentId, taskName, description, isDone);
            taskList.add(subTask);
        }

        return taskList;
    }

    public void disconnect() throws Exception {
        conn.close();
    }

    private void connect() throws SQLException {
        conn = DriverManager.getConnection(DB_URL);

    }

    private LocalDate convertDateToLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
