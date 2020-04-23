package site.dwei.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author weitu
 * @date 2020-04-18
 * @description
 */
public class PropertiesParser {

    private static Properties properties = new Properties();
    private static Map<String,String> map = new HashMap<>();

    public PropertiesParser() {
    }

    public static void load(String path){
        InputStream in = Class.class.getClassLoader().getResourceAsStream(path);
        load(in);
    }

    public static void load(InputStream in){
        try {
            properties.load(in);
            Iterator<Object> it = properties.keySet().iterator();
            while (it.hasNext()){
                String key = (String) it.next();
                String value = properties.getProperty(key);
                map.put(key,value);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getValue(String key){
        return map.get(key);
    }

    public static Set<String> keySet(){
        return map.keySet();
    }
}
