package update_database.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    public static String getPropValues(String param, String filePath){
        Properties prop = new Properties();
        try (InputStream inputStream = new FileInputStream(filePath);){
            if (inputStream != null) {
                prop.load(inputStream);
            } 
            return prop.getProperty(param);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}

