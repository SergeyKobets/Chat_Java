package com.company;

import org.junit.Test;

import static org.junit.Assert.*;

public class ConsoleHelperTest {
    @Test
    public void getNane() throws Exception {
        ConsoleHelper.setName("Sergey");
        String name = ConsoleHelper.getNane();
        assertEquals("Sergey", name);
    }


    @Test
    public void writeMessage() throws Exception {
        String hello = ConsoleHelper.writeMessage("Hello");
        assertEquals("Hello", hello);
    }

}