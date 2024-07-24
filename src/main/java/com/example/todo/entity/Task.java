package com.example.todo.entity;

import com.example.todo.exceptions.MessageConstants;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.util.Date;
import java.util.Objects;

/**
 * Class defining the {@link Task} entity
 */
@Entity
public class Task {
    @Id
    @GeneratedValue
    private Long id;

    private String title;

    private Date dueDate;

    @Min(value = 1, message = MessageConstants.PRIORITY_RANGE_MESSAGE)
    @Max(value = 5, message = MessageConstants.PRIORITY_RANGE_MESSAGE)
    private Integer priority;

    private TaskStatus status = TaskStatus.NOT_DONE;

    public Task() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public void updateTask(Task otherTask) {
        if (otherTask.title != null) {
            this.title = otherTask.title;
        }
        if (otherTask.dueDate != null) {
            this.dueDate = otherTask.dueDate;
        }
        if (otherTask.priority != null) {
            this.priority = otherTask.priority;
        }
        if (otherTask.status != null) {
            this.status = otherTask.status;
        }
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (!(that instanceof Task thatTask)) {
            return false;
        }
        return Objects.equals(this.id, thatTask.id)
                && Objects.equals(this.title, thatTask.title)
                && Objects.equals(this.dueDate, thatTask.dueDate)
                && Objects.equals(this.priority, thatTask.priority);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id,
                title,
                dueDate,
                priority);
    }
}
