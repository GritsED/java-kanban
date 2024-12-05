package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void shouldBeEqualsTasks() {
        //prepare
        Task task1 = new Task(1, "Task", "Description");
        Task task2 = new Task(1, "Task", "Description");

        //check
        assertEquals(task1, task2);
    }

    @Test
    void shouldBeEqualsNameTask() {
        //prepare
        Task task1 = new Task(1, "Task1", "Description");
        Task task2 = new Task(2, "Task2", "Description");

        //check
        assertEquals("Task1", task1.getName());
        assertEquals("Task2", task2.getName());


    }

}