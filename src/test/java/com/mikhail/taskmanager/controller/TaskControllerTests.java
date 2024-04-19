package com.mikhail.taskmanager.controller;

import com.mikhail.taskmanager.model.Task;
import com.mikhail.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.BDDMockito.*;

@WebMvcTest(TaskController.class)
public class TaskControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskRepository taskRepository;

    @Test
    public void testGetAllTasks() throws Exception {
        Task task = new Task("Test Title", "Test Description", LocalDate.now(), false);
        given(taskRepository.findAll()).willReturn(Collections.singletonList(task));

        mockMvc.perform(MockMvcRequestBuilders.get("/tasks")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Title"));
    }

    @Test
    public void testGetTaskById() throws Exception {
        Task task = new Task("Test Title", "Test Description", LocalDate.now(), false);
        given(taskRepository.findById(1L)).willReturn(Optional.of(task));

        mockMvc.perform(MockMvcRequestBuilders.get("/tasks/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Title"));
    }

    @Test
    public void testCreateTask() throws Exception {
        Task task = new Task("Create Title", "Create Description", LocalDate.now(), false);
        given(taskRepository.save(any(Task.class))).willReturn(task);

        mockMvc.perform(MockMvcRequestBuilders.post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Create Title\"," +
                        "\"description\":\"Create Description\",\"dueDate\":\"2023-04-01\",\"completed\":false}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Create Title"));
    }

    @Test
    public void testDeleteTask() throws Exception {
        Task task = new Task("Delete Title", "Delete Description", LocalDate.now(), true);
        given(taskRepository.findById(1L)).willReturn(Optional.of(task));
        doNothing().when(taskRepository).delete(task);

        mockMvc.perform(MockMvcRequestBuilders.delete("/tasks/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
