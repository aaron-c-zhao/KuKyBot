package util;

import entities.Command;

import java.util.Arrays;

public class CommandParser {

    public Command parse(String command, final String deliminator) {
        command = (command.replaceFirst(deliminator,"")).trim();
        String[] args;
        if (command.length() == 0)
            return new Command("InvalidCommand", 0, null);
        else args = command.split(" ");

        String name = args[0];
        args = Arrays.copyOfRange(args, 1, args.length);
        return new Command(name, args.length, args);
    }

}
