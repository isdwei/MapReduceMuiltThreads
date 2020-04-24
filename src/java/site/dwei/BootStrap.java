package site.dwei;

import java.lang.reflect.Method;

/**
 * @author weitu
 * @date 2020-04-18
 * @description
 */
public class BootStrap {
    public static void main(String[] args) throws Exception {
        Method main = Class.forName("site.dwei.mapred.Driver").getMethod("main", String[].class);


    }

}
