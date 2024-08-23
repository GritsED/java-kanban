package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    void shouldBeEqualsEpics (){
        //prepare
        Epic epic1 = new Epic(1,"Epic1", "Description");
        Epic epic2 = new Epic(1,"Epic1", "Description");

        //check
        assertEquals(epic1,epic2);
    }



}