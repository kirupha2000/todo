package com.example.todo.controller;

import com.example.todo.entity.Task;
import com.example.todo.entity.TaskStatus;
import com.example.todo.exceptions.IdNotAllowedException;
import com.example.todo.exceptions.PriorityRequiredException;
import com.example.todo.exceptions.TaskNotFoundException;
import com.example.todo.repository.TaskRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Controller responsible for routing requests.
 */
@RestController
public class TaskController {
    private final TaskRepository repository;

    public TaskController(TaskRepository repository) {
        this.repository = repository;
    }

    @RequestMapping(value = "/tasks", method = RequestMethod.POST)
    public ResponseEntity<Task> addTask(@Valid @RequestBody Task newTask) {
        if (newTask.getId() != null) {
            throw new IdNotAllowedException();
        }
        if (newTask.getPriority() == null) {
            throw new PriorityRequiredException();
        }
        return new ResponseEntity<>(repository.save(newTask), HttpStatus.CREATED);
    }

    @GetMapping("/tasks")
    public List<Task> all(@RequestParam(defaultValue = "id") Optional<String> sortBy,
                          @RequestParam(defaultValue = "asc") Optional<String> orderBy,
                          @RequestParam(defaultValue = "false") Optional<Boolean> outdatedOnly,
                          @RequestParam Optional<Integer> priority) {
        Sort sortOptions = Sort.unsorted();
        if (sortBy.isPresent()) {
            sortOptions = Sort.by(sortBy.get());
        }
        if (orderBy.isPresent()) {
            if (orderBy.get().equalsIgnoreCase("desc")) {
                sortOptions = sortOptions.descending();
            } else {
                sortOptions = sortOptions.ascending();
            }
        }
        Predicate<Task> filter = getTaskPredicate(outdatedOnly, priority);
        // TODO: Filtering happens in-memory. Need to filter at the database.
        return repository.findAll(sortOptions).stream()
                .filter(filter)
                .toList();
    }

    private static Predicate<Task> getTaskPredicate(Optional<Boolean> outdatedOnly,
                                                    Optional<Integer> priorityFilter) {
        Predicate<Task> filter = (task -> true);
        if (outdatedOnly.isPresent() && outdatedOnly.get()) {
            Date now = new Date();
            filter = filter.and(task -> task.getDueDate() != null);
            filter = filter.and(task -> task.getStatus() == TaskStatus.NOT_DONE);
            filter = filter.and(task -> task.getDueDate().before(now));
        }
        if (priorityFilter.isPresent()) {
            int priority = priorityFilter.get();
            filter = filter.and(task -> task.getPriority() == priority);
        }
        return filter;
    }

    @GetMapping("/tasks/{id}")
    public Task findById(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    @PatchMapping("/tasks/{id}")
    public Task updateTask(@Valid @RequestBody Task updatedTask, @PathVariable Long id) {
        if (updatedTask.getId() != null) {
            throw new IdNotAllowedException();
        }
        Task existingTask = repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        existingTask.updateTask(updatedTask);
        return repository.save(existingTask);
    }
}
