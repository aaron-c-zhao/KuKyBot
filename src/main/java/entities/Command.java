package entities;

public class Command {
    private String name;
    private int argc;
    private String[] args;

    public Command(String name, int argc, String[] args) {
        this.name = name;
        this.argc = argc;
        this.args = args;
    }


    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Command: ").append(name).append("\n");
        for (int i = 0; i < argc; i++) {
            builder.append("arg ").append(i + 1).append(args[i]).append("\n");
        }
        return builder.toString();
    }

    public String getName() {
        return this.name;
    }

    public int getArgc() {
        return this.argc;
    }

    public String[] getArgs() {
        return this.args;
    }
}
