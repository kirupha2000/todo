package com.example.todo;

import com.example.todo.entity.Task;
import com.example.todo.entity.TaskStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Instant;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
class TodoApplicationTests {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    // Test helper methods
    private List<Task> convertResponseToTasks(String response) throws JsonProcessingException {
        return MAPPER.readValue(response, new TypeReference<>() {
        });
    }

    private Task getSingleTaskFromResponse(String response) throws JsonProcessingException {
        return MAPPER.readValue(response, Task.class);
    }

    @Test
    void testGetAllOk() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/tasks"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }

    @Test
    void testFilterPriority() throws Exception {
        int priorityToFilter = 4;
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/tasks?priority=" + priorityToFilter))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        List<Task> filteredTasks = convertResponseToTasks(result.getResponse().getContentAsString());
        assert !filteredTasks.isEmpty();
        filteredTasks.forEach(task -> {
            assert task.getPriority() == priorityToFilter;
        });
    }

    @Test
    void testOverdueTasks() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/tasks?outdatedOnly=true"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        List<Task> overdueTasks = convertResponseToTasks(result.getResponse().getContentAsString());
        assert !overdueTasks.isEmpty();
        overdueTasks.forEach(task -> {
            assert task.getDueDate().before(new Date());
            assert task.getStatus() == TaskStatus.NOT_DONE;
        });
    }

    @Test
    void testPrioritySort() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/tasks?sortBy=priority"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        List<Task> prioritySortedTasks = convertResponseToTasks(result.getResponse().getContentAsString());
        assert !prioritySortedTasks.isEmpty();
        Assertions.assertThat(prioritySortedTasks)
                .isSortedAccordingTo(Comparator.comparingInt(Task::getPriority));
    }

    @Test
    void testReversePrioritySort() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/tasks?sortBy=priority&orderBy=desc"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        List<Task> prioritySortedTasks = convertResponseToTasks(result.getResponse().getContentAsString());
        assert !prioritySortedTasks.isEmpty();
        Assertions.assertThat(prioritySortedTasks)
                .isSortedAccordingTo(Comparator.comparingInt(Task::getPriority).reversed());
    }

    @Test
    void testDueDateSort() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/tasks?sortBy=dueDate"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        List<Task> dueDateSortedTasks = convertResponseToTasks(result.getResponse().getContentAsString());
        assert !dueDateSortedTasks.isEmpty();
        List<Task> nonNullDates = dueDateSortedTasks.stream().filter(task -> task.getDueDate() != null).toList();
        Assertions.assertThat(nonNullDates)
                .isSortedAccordingTo(Comparator.comparing(Task::getDueDate));
    }

    @Test
    void testReverseDueDateSort() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/tasks?sortBy=dueDate&orderBy=desc"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        List<Task> dueDateSortedTasks = convertResponseToTasks(result.getResponse().getContentAsString());
        assert !dueDateSortedTasks.isEmpty();
        List<Task> nonNullDates = dueDateSortedTasks.stream().filter(task -> task.getDueDate() != null).toList();
        Assertions.assertThat(nonNullDates)
                .isSortedAccordingTo(Comparator.comparing(Task::getDueDate).reversed());
    }

    @Test
    void testComplexRequest() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/tasks?sortBy=dueDate&orderBy=desc&priority=3" +
                        "&outdatedOnly=true"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        List<Task> tasks = convertResponseToTasks(result.getResponse().getContentAsString());
        List<Task> nonNullDates = tasks.stream().filter(task -> task.getDueDate() != null).toList();
        // Test date order
        Assertions.assertThat(nonNullDates)
                .isSortedAccordingTo(Comparator.comparing(Task::getDueDate).reversed());
        // Test priority filter
        tasks.forEach(task -> {
            assert task.getPriority() == 3;
        });
        // Test overdue tasks
        tasks.forEach(task -> {
            assert task.getDueDate().before(new Date());
            assert task.getStatus() == TaskStatus.NOT_DONE;
        });
    }

    @Test
    void testGetByIdOk() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/tasks/1"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        Task task = getSingleTaskFromResponse(result.getResponse().getContentAsString());
        assert task.getId() == 1L;
    }

    @Test
    void testGetByIdTaskNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/tasks/200"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();
    }

    @Test
    void testPostCreated() throws Exception {
        ObjectNode requestNode = MAPPER.createObjectNode();
        requestNode.put("title", "New task");
        requestNode.put("priority", 1);
        requestNode.put("dueDate", Instant.ofEpochMilli(System.currentTimeMillis()).toString());
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestNode.toString()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        Task createdTask = getSingleTaskFromResponse(result.getResponse().getContentAsString());
        assert createdTask.getTitle().equals(requestNode.get("title").asText());
        assert createdTask.getPriority().equals(requestNode.get("priority").asInt());
        assert createdTask.getDueDate().toInstant()
                .equals(Instant.parse(requestNode.get("dueDate").asText()));
    }

    @Test
    void testPostIdNotAllowed() throws Exception {
        ObjectNode requestNode = MAPPER.createObjectNode();
        requestNode.put("id", 1);
        requestNode.put("title", "New task");
        requestNode.put("priority", 1);
        requestNode.put("dueDate", Instant.ofEpochMilli(System.currentTimeMillis()).toString());
        mockMvc.perform(MockMvcRequestBuilders.post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestNode.toString()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
    }

    @Test
    void testPostNoPriority() throws Exception {
        ObjectNode requestNode = MAPPER.createObjectNode();
        requestNode.put("title", "New task");
        requestNode.put("dueDate", Instant.ofEpochMilli(System.currentTimeMillis()).toString());
        mockMvc.perform(MockMvcRequestBuilders.post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestNode.toString()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
    }

    @Test
    void testPostInvalidPriority() throws Exception {
        ObjectNode requestNode = MAPPER.createObjectNode();
        requestNode.put("title", "New task");
        requestNode.put("dueDate", Instant.ofEpochMilli(System.currentTimeMillis()).toString());
        requestNode.put("priority", 100);
        mockMvc.perform(MockMvcRequestBuilders.post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestNode.toString()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
    }

    @Test
    void testPatchOk() throws Exception {
        ObjectNode requestNode = MAPPER.createObjectNode();
        requestNode.put("title", "Updated Task");
        requestNode.put("priority", 5);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.patch("/tasks/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestNode.toString()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        Task updatedTask = getSingleTaskFromResponse(result.getResponse().getContentAsString());
        assert updatedTask.getTitle().equals(requestNode.get("title").asText());
        assert updatedTask.getPriority().equals(requestNode.get("priority").asInt());
    }

    @Test
    void testPatchMarkTaskDone() throws Exception {
        ObjectNode requestNode = MAPPER.createObjectNode();
        requestNode.put("status", "done");
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.patch("/tasks/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestNode.toString()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        Task updatedTask = getSingleTaskFromResponse(result.getResponse().getContentAsString());
        assert updatedTask.getStatus() == TaskStatus.DONE;
    }

    @Test
    void testPatchCancelTask() throws Exception {
        ObjectNode requestNode = MAPPER.createObjectNode();
        requestNode.put("status", "canceled");
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.patch("/tasks/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestNode.toString()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        Task updatedTask = getSingleTaskFromResponse(result.getResponse().getContentAsString());
        assert updatedTask.getStatus() == TaskStatus.CANCELED;
    }

    @Test
    void testPatchTaskNotFound() throws Exception {
        ObjectNode requestNode = MAPPER.createObjectNode();
        requestNode.put("title", "Updated Task");
        requestNode.put("priority", 5);
        mockMvc.perform(MockMvcRequestBuilders.patch("/tasks/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestNode.toString()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();
    }

    @Test
    void testPatchIdNotAllowed() throws Exception {
        ObjectNode requestNode = MAPPER.createObjectNode();
        requestNode.put("id", "2");
        requestNode.put("title", "Updated Task");
        requestNode.put("priority", 5);
        mockMvc.perform(MockMvcRequestBuilders.patch("/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestNode.toString()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
    }

    @Test
    void testPatchInvalidPriority() throws Exception {
        ObjectNode requestNode = MAPPER.createObjectNode();
        requestNode.put("title", "Updated Task");
        requestNode.put("priority", 500);
        mockMvc.perform(MockMvcRequestBuilders.patch("/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestNode.toString()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
    }
}
