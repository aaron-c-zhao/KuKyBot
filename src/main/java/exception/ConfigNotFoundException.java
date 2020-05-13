package exception;

public class ConfigNotFoundException extends RuntimeException {
    public ConfigNotFoundException(String config) {
        super("Can not find config" + config);
    }
}
