package eugene.com.todoer;

import eugene.com.todoer.data.Task;
import eugene.com.todoer.data.TaskUiKeeper;
import javafx.application.Platform;

import java.time.LocalDate;
import java.time.Period;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class UiTimer {
    private final int UPDATE_FRAME_RATE = 1000;
    private final Timer timer = new Timer();

    public void setUiUpdateTimer(Storage storage, ConcurrentHashMap<Integer, TaskUiKeeper> taskKeeperList) {
        timer.schedule(updateTime(storage, taskKeeperList), 0L, UPDATE_FRAME_RATE);
    }

    public void stopTimer() {
        timer.cancel();
    }

    private TimerTask updateTime(Storage storage, ConcurrentHashMap<Integer, TaskUiKeeper> taskKeeperList) {
        return new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    ConcurrentHashMap<Integer, Task> tasksMap = storage.getTasksMap();

                    taskKeeperList.forEach((id, keeper) -> {
                        Task task = tasksMap.get(id);

                        int day = Period.between(task.getDate(), LocalDate.now()).getDays();
                        int endDay = Period.between(LocalDate.now(), task.getEndDate()).getDays();

                        if (endDay < 0) {
                            endDay = 0;
                        }

                        keeper.getControl().getBtnDays().setText(String.valueOf(day));
                        keeper.getControl().getBtnLeftDays().setText(String.valueOf(endDay));

                        task.setEndDateLeft(endDay);
                    });
                });
            }
        };
    }
}
