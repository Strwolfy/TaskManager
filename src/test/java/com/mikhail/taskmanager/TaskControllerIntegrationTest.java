package com.mikhail.taskmanager;

import com.mikhail.taskmanager.model.Task;
import com.mikhail.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TaskControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getRootUrl() {
        return "http://localhost:" + port + "/tasks";
    }

    @Test
    public void testGetAllTasks() {

        ResponseEntity<Task[]> response = restTemplate.getForEntity(getRootUrl(), Task[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isNotNull();
        System.out.println("response.getBody()" + Arrays.toString(response.getBody()));

    }

    @Test
    public void testGetTaskById() {
        Task task = new Task("Specific Task", "Description for Specific Task", LocalDate.now(), false);
        ResponseEntity<Task> postResponse = restTemplate.postForEntity(getRootUrl(), task, Task.class);
        Task createdTask = postResponse.getBody();

        ResponseEntity<Task> response = restTemplate.getForEntity(getRootUrl() + "/" + createdTask.getId(), Task.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).getTitle()).isEqualTo("Specific Task");
    }

    @Test
    public void testCreateTask() {
        Task task = new Task("New Task", "Description for New Task", LocalDate.now(), false);
        ResponseEntity<Task> response = restTemplate.postForEntity(getRootUrl(), task, Task.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(Objects.requireNonNull(response.getBody()).getTitle()).isEqualTo("New Task");
    }

    @Test
    public void testUpdateTask() {
        Task task = new Task("Updatable Task", "Initial Description", LocalDate.now(), false);
        ResponseEntity<Task> postResponse = restTemplate.postForEntity(getRootUrl(), task, Task.class);
        Task createdTask = postResponse.getBody();

        assert createdTask != null;
        createdTask.setTitle("Updated Task");
        createdTask.setDescription("Updated Description");
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Task> entity = new HttpEntity<>(createdTask, headers);

        ResponseEntity<Task> response = restTemplate.exchange(getRootUrl() + "/" + createdTask.getId(),
                HttpMethod.PUT, entity, Task.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).getTitle()).isEqualTo("Updated Task");
        assertThat(response.getBody().getDescription()).isEqualTo("Updated Description");
    }

    @Test
    public void testDeleteTask() {
        Task task = new Task("Task to Delete", "Delete this task", LocalDate.now(), false);
        ResponseEntity<Task> postResponse = restTemplate.postForEntity(getRootUrl(), task, Task.class);
        Task createdTask = postResponse.getBody();

        assert createdTask != null;
        ResponseEntity<Void> response = restTemplate.exchange(getRootUrl() + "/" + createdTask.getId(),
                HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
