package com.mikhail.taskmanager.service;

import com.mikhail.taskmanager.model.Task;
import com.mikhail.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class TaskServiceTest {

    @Autowired
    private TaskService taskService;

    @MockBean
    private TaskRepository taskRepository;

    @Test
    public void testFindAll() {
        Task task = new Task();
        task.setTitle("Test Task");
        when(taskRepository.findAll()).thenReturn(List.of(task));

        List<Task> tasks = taskService.findAll();
        assertThat(tasks).hasSize(1);
        assertThat(tasks.get(0).getTitle()).isEqualTo("Test Task");
    }

    @Test
    public void testFindById() {
        Optional<Task> task = Optional.of(new Task());
        task.get().setTitle("Test Task");
        when(taskRepository.findById(anyLong())).thenReturn(task);

        Optional<Task> found = taskService.findById(1L);
        assertThat(found.isPresent()).isTrue();
        assertThat(found.get().getTitle()).isEqualTo("Test Task");
    }

    @Test
    public void testSave() {
        Task task = new Task();
        task.setTitle("New Task");
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task savedTask = taskService.save(task);
        assertThat(savedTask.getTitle()).isEqualTo("New Task");
    }

    @Test
    public void testDeleteById() {
        doNothing().when(taskRepository).deleteById(anyLong());
        taskService.deleteById(1L);
        verify(taskRepository, times(1)).deleteById(1L);
    }
}
