package controllers;

import entities.Command;
import exception.InvalidCommandException;
import org.javacord.api.event.message.MessageCreateEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MessageHandlerManager {
    private static final Map<String, MessageHandler> handlerMap = new HashMap<>();

    static {
        try {
            handlerMap.put("todo", new ToDoMessageHandler());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleCommand(Command command, MessageCreateEvent event) {
        System.out.println(command.getName());
        try {
            handlerMap.get(command.getName()).handle(command, event);
        } catch (InvalidCommandException e) {
            // TODO error handling
            e.printStackTrace();
        }
    }
}
