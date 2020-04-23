package site.dwei.SingleThread;

import site.dwei.mapred.Driver;
import site.dwei.mapred.MapLaunch;
import site.dwei.mapred.ReduceLaunch;
import site.dwei.util.PropertiesParser;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author weitu
 * @date 2020-04-23
 * @description
 */
public class Test {

    public static void main(String[] args) {

        long start = System.currentTimeMillis();

        HashMap<String,Long> map = new HashMap<>();

        System.out.println("正在解析配置文件...");
        System.out.println(Driver.class.getResource("").getPath());
        InputStream resource = Driver.class.getClassLoader().getResourceAsStream("mr.properties");
        PropertiesParser.load(resource);
        System.out.println("配置文件解析完成");

        System.out.println("任务开始...");

        try {
            FileInputStream fileInputStream = new FileInputStream("E:\\JavaWork\\MapReduceMuiltThreads\\src\\main\\resources\\in.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
            String line;
            while ((line = reader.readLine())!=null){
                String[] ss = line.split(" ");
                for(String s : ss){
                    if(!map.containsKey(s)){
                        map.put(s,0L);
                    }
                    map.put(s,map.get(s)+1L);
                }
            }

            File file = new File("out.txt");
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            StringBuffer stringBuffer = new StringBuffer();
            Iterator<Map.Entry<String, Long>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry<String, Long> next = iterator.next();
                stringBuffer.append(next.getKey()+","+next.getValue()+"\n");
                byte[] bytes = stringBuffer.toString().getBytes();
                bos.write(bytes);
                bos.flush();
                stringBuffer.setLength(0);
            }

            bos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Reduce任务结束");
        System.out.println("耗时： " + (System.currentTimeMillis() - start) + "ms");
    }
}
