package com.mikhail.taskmanager;

import com.mikhail.taskmanager.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationIntegrationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private ResponseEntity<Task> response;

    @BeforeEach
    public void setUp() {
        Task task = new Task("Sample Task", "This is a sample task", LocalDate.now(), false);
        response = restTemplate.postForEntity(getBaseUrl() + "/tasks", task, Task.class);
    }

    private String getBaseUrl() {
        return "http://localhost:" + port;
    }

    @Test
    public void testTaskCreation() {

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Sample Task");
    }

    @Test
    public void testTaskDeletion() {

        Task createdTask = response.getBody();
        restTemplate.delete(getBaseUrl() + "/tasks/" + createdTask.getId());

        // Проверяем удаление
        ResponseEntity<Task> response = restTemplate.getForEntity(getBaseUrl()
                + "/tasks/" + createdTask.getId(), Task.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testTaskRead() {
        // Создаем новую задачу
        Task newTask = new Task("Integration Test Task", "Test Description",
                LocalDate.now(), false);
        ResponseEntity<Task> responseEntity = restTemplate.postForEntity("http://localhost:"
                + port + "/tasks", newTask, Task.class);

        // Проверяем, что задача была успешно создана
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Получаем ID созданной задачи
        Long createdTaskId = Objects.requireNonNull(responseEntity.getBody()).getId();

        // Проверяем получение задачи по ID
        Task getResult = restTemplate.getForObject("http://localhost:" + port + "/tasks/"
                + createdTaskId, Task.class);
        assertThat(getResult.getTitle()).isEqualTo("Integration Test Task");
        assertThat(getResult.getDescription()).isEqualTo("Test Description");
    }
}
