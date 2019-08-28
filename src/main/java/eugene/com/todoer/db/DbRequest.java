package eugene.com.todoer.db;

import eugene.com.todoer.data.SubTask;
import eugene.com.todoer.data.Task;
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

    public DbRequest() {
        connect();
    }

    public void insertNewUser(int id, String name, String pass) {
        String sql = "INSERT INTO users(id,name,pass) VALUES(?,?,?)";

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.setString(2, name);
            pstmt.setString(3, pass);

            pstmt.executeUpdate();

            log.debug("New user was insert to db");
        } catch (SQLException e) {
            log.error("Error to insert user to db. Error: {}", e.getMessage(), e);
        }

    }

    public void addNewTask(Task task) {
        String sql = "INSERT INTO tasks(id,subTaskCounter,isDone,taskName,date,endDate,isDeleted) " +
                "VALUES(?,?,?,?,?,?,?)";

        try {
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, task.getId());
            pstmt.setInt(2, task.getSubTaskCounter());
            pstmt.setBoolean(3, task.isIsDone());
            pstmt.setString(4, task.getTaskName());
            pstmt.setDate(5,  Date.valueOf(task.getDate()));
            pstmt.setDate(6, Date.valueOf(task.getEndDate()));
            pstmt.setBoolean(7, task.isDeleted());

            pstmt.executeUpdate();
            log.debug("New task was insert to db with id: {}", task.getId());
        } catch (SQLException e) {
            log.error("Error to addNewTask to db with id: {}. Error: {}", task.getId(), e.getMessage(), e);
        }
    }

    public void updateTask(Task task) {
        String sql = "UPDATE tasks SET subTaskCounter=?,isDone=?,taskName=?,date=?,endDate=?,isDeleted=? WHERE id=?";

        try {
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, task.getSubTaskCounter());
            pstmt.setBoolean(2, task.isIsDone());
            pstmt.setString(3, task.getTaskName());
            pstmt.setDate(4,  Date.valueOf(task.getDate()));
            pstmt.setDate(5, Date.valueOf(task.getEndDate()));
            pstmt.setBoolean(6, task.isDeleted());
            pstmt.setInt(7, task.getId());

            pstmt.executeUpdate();

            log.debug("update db task id: {}", task.getId());
        } catch (SQLException e) {
            log.error("Error to update task in db with id: {}. Error: {}", task.getId(), e.getMessage(), e);
        }

        task.getSubTaskList().forEach(this::updateSubTask);
    }

    public void addSubTask(SubTask subTask) {
        String sql = "INSERT INTO subTask(id,parentId,taskName,description,isDone) " +
                "VALUES(?,?,?,?,?)";

        try {
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, subTask.getId());
            pstmt.setInt(2, subTask.getParentTaskId());
            pstmt.setString(3, subTask.getTaskName());
            pstmt.setString(4, subTask.getDescription());
            pstmt.setBoolean(5, subTask.isIsDone());

            pstmt.executeUpdate();
            log.debug("New subtask was added to db with id: {}, parentId: {}", subTask.getId(),
                    subTask.getParentTaskId());
        } catch (SQLException e) {
            log.error("Error to add new subtask to db with id: {}, parentId: {}. Error: {}", subTask.getId(),
                    subTask.getParentTaskId(), e.getMessage(), e);
        }
    }

    public void updateSubTask(SubTask subTask) {
        String sql = "UPDATE subTask SET taskName=?,description=?,isDone=? WHERE id=? AND parentId=?";

        try {
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, subTask.getTaskName());
            pstmt.setString(2, subTask.getDescription());
            pstmt.setBoolean(3, subTask.isIsDone());
            pstmt.setInt(4, subTask.getId());
            pstmt.setInt(5, subTask.getParentTaskId());

            pstmt.executeUpdate();

            log.debug("update db subtask id: {}, parentId: {}", subTask.getId(), subTask.getParentTaskId());
        } catch (SQLException e) {
            log.error("Error to update subtask in db with id: {}, parentId: {}. Error: {}", subTask.getId(),
                    subTask.getParentTaskId(), e.getMessage(), e);
        }
    }

    public ArrayList<Task> getAllTasks() {
        String sql = "SELECT id,subTaskCounter,isDone,taskName,date,endDate,isDeleted FROM tasks";

        try {
            rs = conn.prepareStatement(sql).executeQuery();

            ArrayList<Task> taskList = new ArrayList<>();
            while (rs.next()) {
                Boolean isDeleted = rs.getBoolean("isDeleted");

                if (isDeleted) {
                    continue;
                }

                int id = rs.getInt("id");
                int subTaskCounter = rs.getInt("subTaskCounter");
                boolean isDone = rs.getBoolean("isDone");
                String taskName = rs.getString("taskName");
                LocalDate date = convertDateToLocalDate(rs.getDate("date"));
                LocalDate endDate = convertDateToLocalDate(rs.getDate("endDate"));

                Task task = new Task(id, subTaskCounter, isDone, taskName, date, endDate, isDeleted);
                taskList.add(task);
            }

            return taskList;
        } catch (SQLException e) {
            log.error("Error to get all tasks from db. Error: {}", e.getMessage(), e);
        }

        return null;
    }

    public ArrayList<SubTask> getAllSubTasks() {
        String sql = "SELECT id,parentId,taskName,description,isDone FROM subTask";

        try {
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
        } catch (SQLException e) {
            log.error("Error to get all subtasks from db. Error: {}", e.getMessage(), e);
        }

        return null;
    }

    public void disconnect() {
        try {
            conn.close();
        } catch (SQLException e) {
            log.error("Error to close DB connection. Error: {}", e.getMessage(), e);
        }
    }

    private void connect() {
        try {
            conn = DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            log.error("Error to connect to DB. Error: {}", e.getMessage(), e);
        }
    }

    private LocalDate convertDateToLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
