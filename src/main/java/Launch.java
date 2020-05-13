import controllers.MessageHandlerManager;
import entities.Command;
import exception.ConfigNotFoundException;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import util.CommandParser;
import util.YamlParser;

import java.util.Map;

public class Launch {
    //TODO configure log4j
    private static final YamlParser yamlParser = new YamlParser();
    private static final String config = "config.yml";

    public static void main(String[] args) {
        Map<String, Object> obj = yamlParser.readConfig(config);
        String token = "" ;
        if (obj.containsKey("token"))
            token = (String) obj.get("token");
        else throw new ConfigNotFoundException(config);
        DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();

        final String deliminator = (String) obj.get("deliminator");
        createListeners(api, deliminator);
    }

    private static void createListeners(DiscordApi api, final String deliminator) {
        api.addMessageCreateListener(event -> {
            String command = event.getMessageContent();
            System.out.println(command);
            if (command.startsWith(deliminator)) {
                CommandParser commandParser = new CommandParser();
                Command commandObj = commandParser.parse(command, deliminator);
                MessageHandlerManager messageHandlerManager = new MessageHandlerManager();
                messageHandlerManager.handleCommand(commandObj, event);
            }
        });


    }

}
