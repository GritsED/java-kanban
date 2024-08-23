package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    @Test
    void shouldBeEqualsSubtasks () {
        Subtask subtask1 = new Subtask(1, "Subtask", "Description");
        Subtask subtask2 = new Subtask(1, "Subtask", "Description");

        //check
        assertEquals(subtask1, subtask2);
    }

}