package site.dwei.mapred;
import org.junit.Test;
import site.dwei.util.PropertiesParser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author weitu
 * @date 2020-04-19
 * @description
 */
public class Driver {

    public static void main(String[] args ) {

        System.out.println("正在解析配置文件...");
        System.out.println(Driver.class.getResource("").getPath());
        InputStream resource = Driver.class.getClassLoader().getResourceAsStream("mr.properties");
//        PropertiesParser.load("mr.properties");
        PropertiesParser.load(resource);
//        PropertiesParser.load("E:\\JavaWork\\MapReduceStandAlone\\resources\\mr.properties");
        System.out.println("配置文件解析完成");

        System.out.println("Map任务开始...");
        long start = System.currentTimeMillis();
        try {

            MapLaunch mapLaunch = (MapLaunch) Class.forName("site.dwei.mapred.MapLaunch").newInstance();
            System.out.println("准备输入文件...");
            mapLaunch.prepareFiles();
            System.out.println("切分文件");
            mapLaunch.splitFile();

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            System.out.println("Map过程失败...");
            e.printStackTrace();
        }

        System.out.println("Map阶段结束");
        System.out.println("耗时： "+(System.currentTimeMillis()-start)/1000);

        System.out.println("Reduce任务开始...");
        start = System.currentTimeMillis();

        try {
            ReduceLaunch reduceLaunch = (ReduceLaunch)Class.forName("site.dwei.mapred.ReduceLaunch").newInstance();
            reduceLaunch.calculate();
        } catch (InstantiationException | ClassNotFoundException | IllegalAccessException e) {
            System.out.println("Reduce过程失败...");
            e.printStackTrace();
        }
        System.out.println("Reduce任务结束");
        System.out.println("耗时： "+(System.currentTimeMillis()-start)/1000);


    }


    @Test
    public void test1() throws IOException {
        FileInputStream fis = new FileInputStream("E:\\JavaWork\\MapReduceMuiltThreads\\src\\main\\resources\\in.txt");
        int read = fis.read();
    }
}
