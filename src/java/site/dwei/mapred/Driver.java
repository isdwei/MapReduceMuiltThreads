package site.dwei.mapred;


import org.junit.Test;
import site.dwei.util.PropertiesParser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * @author weitu
 * @date 2020-04-19
 * @description
 */
public class Driver {

    public static void main(String[] args) {

        System.out.println("正在解析配置文件...");
        InputStream resource = Driver.class.getClassLoader().getResourceAsStream("mr.properties");
        PropertiesParser.load(resource);
        System.out.println("配置文件解析完成");

        System.out.println("Map任务开始...");
        long start = System.currentTimeMillis();
        MapLaunch mapLaunch = new MapLaunch();
        System.out.println("准备输入文件...");
        mapLaunch.prepareFiles();
        System.out.println("切分文件");
        mapLaunch.splitFile();
        System.out.println("Map阶段结束");
        System.out.println("耗时： " + (System.currentTimeMillis() - start) + "ms");

        System.out.println("Reduce任务开始...");
        start = System.currentTimeMillis();

        ReduceLaunch reduceLaunch = new ReduceLaunch();
        reduceLaunch.calculate();

        System.out.println("Reduce任务结束");
        System.out.println("耗时： " + (System.currentTimeMillis() - start) + "ms");


    }

}
