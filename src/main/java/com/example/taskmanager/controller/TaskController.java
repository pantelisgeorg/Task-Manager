package com.example.taskmanager.controller;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/")
public class TaskController {
    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @GetMapping
    public String listTasks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false, defaultValue = "ALL") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.asc("dueDate").nullsLast()));
        Page<Task> taskPage = service.searchTasks(title, status, pageable);
        
        model.addAttribute("tasks", taskPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", taskPage.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("currentTitle", title);
        model.addAttribute("currentStatus", status);
        return "index";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("task", new Task());
        return "task-form";
    }

    @PostMapping
    public String saveTask(@Valid Task task, BindingResult result, RedirectAttributes redirectAttrs) {
        if (result.hasErrors()) return "task-form";
        service.save(task);
        redirectAttrs.addFlashAttribute("flashMessage", "Task created successfully!");
        redirectAttrs.addFlashAttribute("flashType", "success");
        return "redirect:/";
    }

    @GetMapping("/task/{id}")
    public String viewTask(@PathVariable Long id, Model model) {
        Task task = service.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
        model.addAttribute("task", task);
        return "task-detail";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Task task = service.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
        model.addAttribute("task", task);
        return "task-form";
    }

    @PostMapping("/update/{id}")
    public String updateTask(@PathVariable Long id, @Valid Task task, BindingResult result, RedirectAttributes redirectAttrs) {
        if (result.hasErrors()) return "task-form";
        Task existing = service.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
        existing.setTitle(task.getTitle());
        existing.setDescription(task.getDescription());
        existing.setStatus(task.getStatus());
        existing.setDueDate(task.getDueDate());
        service.save(existing);
        redirectAttrs.addFlashAttribute("flashMessage", "Task updated successfully!");
        redirectAttrs.addFlashAttribute("flashType", "success");
        return "redirect:/";
    }

    @PostMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        service.deleteById(id);
        redirectAttrs.addFlashAttribute("flashMessage", "Task deleted successfully!");
        redirectAttrs.addFlashAttribute("flashType", "warning");
        return "redirect:/";
    }

    @PostMapping("/toggle-status/{id}")
    public String toggleStatus(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        Task task = service.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
        task.setStatus(task.getStatus().equals("DONE") ? "TODO" : "DONE");
        service.save(task);
        redirectAttrs.addFlashAttribute("flashMessage", "Task status updated!");
        redirectAttrs.addFlashAttribute("flashType", "info");
        return "redirect:/";
    }
}
