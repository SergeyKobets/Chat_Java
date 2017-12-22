package com.company.client;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ClientGuiModel {

    private final Set<String> allUserNames = new HashSet<>();

    private String newMessage;


    /** methods **/
    // добавлять имя участника во множество, хранящее всех участников
    public void addUser(String newUserName) {

        allUserNames.add(newUserName);
    }


    // будет удалять имя участника из множества
    public void deleteUser(String userName) {

        allUserNames.remove(userName);
    }


    /** getters and setters **/
    // запретив модифицировать возвращенное множество.
    public Set<String> getAllUserNames() {

        return Collections.unmodifiableSet(allUserNames);
    }

    public String getNewMessage() {
        return newMessage;
    }

    public void setNewMessage(String newMessage) {
        this.newMessage = newMessage;
    }
}
