package site.dwei.mapred;

import org.junit.Test;
import site.dwei.constant.PropertyKeys;
import site.dwei.util.FilePathUtil;
import site.dwei.util.MRFactory;
import site.dwei.util.PropertiesParser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author weitu
 * @date 2020-04-18
 * @description
 */
public class MapLaunch {

    public static boolean splitSignal = false;
    public static boolean stateSignal = false;

    public List<String> filePaths;
    public BufferedReader reader;

    public void prepareFiles() {

        System.out.println("检查文件...");
        File tempFileDir = new File(PropertiesParser.getValue(PropertyKeys.TEMPPATH.getName()));
        FilePathUtil.deletePath(tempFileDir);
        filePaths = getFiles(PropertiesParser.getValue(PropertyKeys.INPUTPATH.getName()));

    }


    public static List<String> getFiles(String path) {
        List<String> files = new ArrayList<>();
        File file = new File(path);
        File[] tempList = file.listFiles();
        if (tempList == null) {
            return files;
        }
        for (File f : tempList) {
            if (f.isFile()) {
                files.add(f.toString());
            }
            if (f.isDirectory()) {
                files.addAll(getFiles(f.toString()));
            }
        }
        return files;
    }

    public void splitFile() {

        System.out.println("开始切分文件....");
        /**
         * 获取上下文对象
         * 获取任务
         */
        Mapper mapper = MRFactory.mapTask;
        Context context = MRFactory.getMapContext();
        //每读一行，存入tmp
        String tmp;


        for (String p : filePaths) {
            try {
                reader = new BufferedReader(new FileReader(p));
                //按行读，循环
                while ((tmp = reader.readLine()) != null) {
                    //当前读到的行数
                    long position = 0L;
                    mapper.map(position, tmp, context);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //所有文件读完刷写一次
        context.flush();
    }
}
