package controllers;

import entities.Command;
import exception.InvalidCommandException;
import org.javacord.api.event.message.MessageCreateEvent;

public interface MessageHandler {
    /**
     * Reply the message.
     * @param command
     * @param event
     */
    void handle(Command command, MessageCreateEvent event) throws InvalidCommandException;

}
