package com.example.todo.config;

import com.example.todo.entity.Task;
import com.example.todo.entity.TaskStatus;
import com.example.todo.repository.TaskRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Configuration
public class LoadDatabase {
    @Bean
    CommandLineRunner initDatabase(TaskRepository repository) {
        return args -> {
            Task task = new Task();
            task.setId(1L);
            task.setTitle("Simple task");
            task.setDueDate(new Date());
            task.setPriority(3);
            repository.save(task);

            Task otherTask = new Task();
            otherTask.setId(2L);
            otherTask.setTitle("Long overdue task");
            otherTask.setDueDate(new Date(0L));
            otherTask.setPriority(4);
            repository.save(otherTask);

            Task dueAfterOneHour = new Task();
            dueAfterOneHour.setId(3L);
            dueAfterOneHour.setTitle("One hour task");
            dueAfterOneHour.setDueDate(new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1)));
            dueAfterOneHour.setPriority(3);
            repository.save(dueAfterOneHour);

            Task doneTask = new Task();
            doneTask.setId(4L);
            doneTask.setTitle("Completed");
            doneTask.setDueDate(new Date());
            doneTask.setPriority(2);
            doneTask.setStatus(TaskStatus.DONE);
            repository.save(doneTask);

            Task nullDate = new Task();
            nullDate.setId(5L);
            nullDate.setTitle("No date");
            nullDate.setPriority(3);
            repository.save(nullDate);
        };
    }
}
