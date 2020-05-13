package controllers;


import entities.Command;
import entities.TodoList;
import exception.InvalidCommandException;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import util.YamlParser;

import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ToDoMessageHandler implements MessageHandler{

    private TodoList todoList;
    private static final String path = "todo.yml";
    private static final YamlParser yamlParser = new YamlParser();
    private static final long todoId = 706073676461899818L;

    public ToDoMessageHandler() throws IOException {
        Object obj;
        try{
            obj = yamlParser.read(path);
        } catch (IOException e) {
            todoList = new TodoList();
            yamlParser.write(todoList, path);
            return;
        }
        if (obj instanceof TodoList)
            todoList = (TodoList) obj;
    }

    @Override
    //TODO refactor this method with strategy pattern
    public void handle(Command command, MessageCreateEvent event) throws InvalidCommandException {
        List<TodoList.Todo> todos = todoList.getTodoList();
        if (command.getArgc() == 0) {
            MessageBuilder messageBuilder = new MessageBuilder();
            StringBuilder stringBuilder = new StringBuilder();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            todos.sort(TodoList.Todo::compareTo);
            for (int i = 0; i < todos.size(); i++) {
               stringBuilder.append("*").append(i + 1).append(".* ")
                       .append("**").append(todos.get(i).getContent()).append("**")
                       .append(" -- DueDate: ")
                       .append(formatter.format(todos.get(i).getDueDate()))
                       .append("\n");
            }
            messageBuilder.setEmbed(new EmbedBuilder()
                    .setTitle("TODO LIST")
                    .setColor(Color.blue)
                    .setDescription(stringBuilder.toString().trim()))
                    .send(event.getChannel());

        }
        else if (command.getArgs()[0].equalsIgnoreCase("new")) {
            TodoList.Todo todo = addRecord(command, event);
            getToDoMessage("TODO", Color.orange, todo).send(event.getChannel());
        }
        else if (command.getArgs()[0].equalsIgnoreCase("done")) {
            int index = -1;
            try{
                index = Integer.parseInt(command.getArgs()[1]);
            } catch (NumberFormatException e) {
                event.getChannel().sendMessage("Invalid command!");
                return;
            }
            TodoList.Todo todo = todos.get(index - 1);
            todoList.getTodoList().remove(todo);
            getToDoMessage("DONE", Color.GREEN, todo).send(event.getChannel());
            TextChannel todoChannel = event.getApi().getTextChannelById(todoId).orElse(null);
            if (todoChannel != null) {
                try {
                    Message message = todoChannel.getMessageById(todo.getMessageId()).get();
                    String content = message.getContent();
                    message.edit("~~" + content + "~~");
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            writeToYaml();
        }
        else if (command.getArgs()[0].equalsIgnoreCase("clear")) {
            if (event.getMessageAuthor().isBotOwner()) {
                todoList.getTodoList().clear();
                writeToYaml();
            }
            else event.getChannel().sendMessage("Danger! Bot owner only!");
        }
        else if (command.getArgs()[0].equalsIgnoreCase("help")) {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Help")
                    .setColor(Color.PINK)
                    .addField("Get todo list", "::todo")
                    .addField("Add new todo entry", "::todo content &duration(in days) @responsiblePerson")
                    .addField("Task done", "::todo done indexOfTask"));
        }

    }

    private void writeToYaml() {
        try {
            yamlParser.write(todoList, path);
        } catch (IOException e) {
            //TODO error handling
            e.printStackTrace();
        }
    }

    //TODO add permission checking
    //TODO refactor this method
    private TodoList.Todo addRecord(Command command, MessageCreateEvent event) throws InvalidCommandException {
        int i = 1;
        StringBuilder content = new StringBuilder(command.getArgs()[i++]);
        while (!command.getArgs()[i].startsWith("&") && i < command.getArgs().length) {
           content.append(" ").append(command.getArgs()[i++]);
        }
        if (i == command.getArgs().length)
            throw new InvalidCommandException("Invalid command: " + command.toString());
        int duration = Integer.parseInt(command.getArgs()[i++].replaceAll("&", ""));
        Date dueDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dueDate);
        c.add(Calendar.DAY_OF_MONTH, duration);
        dueDate = c.getTime();
        ArrayList<Long> users = new ArrayList<>();
        for (; i < command.getArgs().length; i++) {
            String idStr = command.getArgs()[i].replaceAll("[^\\d.]", "");
            Long id = Long.parseLong(idStr);
            users.add(id);
        }
        Long[] usersArray = new Long[users.size()];
        users.toArray(usersArray);
        TodoList.Todo todo = new TodoList.Todo(dueDate, content.toString(), usersArray);
        //TODO create a message service to replace scattered message sending code
        MessageBuilder messageBuilder = new MessageBuilder();
        CompletableFuture<Message> message = messageBuilder.append("**ToDo: ").append(todo.getContent()).append("**")
                .append(" -- DueDate: ").append(new SimpleDateFormat("dd/MM/yyyy").format(dueDate))
                .send(event.getApi().getTextChannelById(todoId).orElse(null));
        long messageId = -1L;
        try {
            messageId = message.get().getId();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        todo.setMessageId(messageId);
        todoList.insert(todo);
        //TODO replace it by logger
        System.out.println("new record: " + todo);
        writeToYaml();
        return todo;

    }

    private MessageBuilder getToDoMessage(String title,Color color, TodoList.Todo todo) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        MessageBuilder messageBuilder = new MessageBuilder();
        messageBuilder.setEmbed(new EmbedBuilder()
                .setTitle(title)
                .setColor(color)
                .addField("Content",todo.getContent())
                .addField("DueDate", formatter.format(todo.getDueDate())));
        return messageBuilder;
    }


}
