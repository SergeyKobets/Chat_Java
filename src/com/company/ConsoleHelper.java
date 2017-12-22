package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleHelper {
    private static String name;

    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static String  writeMessage(String message) {
        System.out.println(message);
        return message;
    }

    public static String readString() {
        System.out.println("start readSt");
        String message = null;
        while (message == null) {
            try {
                message = reader.readLine();
            } catch (IOException e) {
                System.out.println("Произошла ошибка при попытке ввода текста. Попробуйте еще раз.");
            }
        }
        return message;
    }

    public static int readInt() {
        int intRead;
        while (true) {
            try {
                intRead = Integer.parseInt(readString());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Произошла ошибка при попытке ввода числа. Попробуйте еще раз.");
            }
        }
        return intRead;
    }

    public static String getNane() {
        return name;
    }
    public static void setName(String name) {
        ConsoleHelper.name = name;
    }
}

