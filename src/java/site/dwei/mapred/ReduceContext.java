package site.dwei.mapred;

import site.dwei.constant.PropertyKeys;
import site.dwei.util.FilePathUtil;
import site.dwei.util.PropertiesParser;

import java.io.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author weitu
 * @date 2020-04-23
 * @description
 */
public class ReduceContext extends Context {

    private static int numReduceTasks = Integer.parseInt(PropertiesParser.getValue(PropertyKeys.REDUCETASK.getName()));

    private static File[] files;
    private StringBuilder sb = new StringBuilder();
    private static String outputpath;

    private BufferedWriter bw = null ;


    /**
     * 初始化
     */
    static {

        files = new File[numReduceTasks];

        outputpath = PropertiesParser.getValue(PropertyKeys.OUTPUTPATH.getName());

        for (int i = 0; i < numReduceTasks; i++) {

            FilePathUtil.createPath(outputpath);
            files[i] = new File(outputpath + "/final" + i + ".txt");
            try {
                files[i].createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public ReduceContext(int index) {
        try {
            bw=new BufferedWriter(new FileWriter(files[index],true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write(String key, long value) {

        this.sb.append(key).append(" ").append(value).append("\n");
        if (this.sb.length() >= 1024 ) {
            flush();
            sb.setLength(0);
        }
    }

    @Override
    public void flush() {
        try {
            bw.write(sb.toString());
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void close() {
        try {
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
