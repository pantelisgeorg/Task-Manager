package com.example.taskmanager.service;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class TaskService {
    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public Page<Task> searchTasks(String title, String status, Pageable pageable) {
        String cleanTitle = (title != null && !title.trim().isEmpty()) ? title.trim() : null;
        String cleanStatus = (status != null && !status.isEmpty() && !status.equals("ALL")) ? status : null;
        return repository.searchTasks(cleanTitle, cleanStatus, pageable);
    }

    public Optional<Task> findById(Long id) {
        return repository.findById(id);
    }

    public Task save(Task task) {
        return repository.save(task);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
