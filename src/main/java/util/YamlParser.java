package util;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Map;

public class YamlParser {
    private static final Yaml YAML = new Yaml();
    private static final String dir = System.getProperty("user.dir");


    public Map<String, Object> readConfig(String fileName) {
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream(fileName);
        return YAML.load(inputStream);
    }

    public Object read(String fileName) throws IOException {
        InputStream inputStream = new FileInputStream(dir + File.separator + fileName);
        return YAML.load(inputStream);
    }

    public void write(Object obj, String fileName) throws IOException {
        FileWriter writer = new FileWriter(dir + File.separator + fileName);;
        YAML.dump(obj, writer);
    }
}
