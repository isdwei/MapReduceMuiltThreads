package site.dwei.mapred;

import site.dwei.constant.PropertyKeys;
import site.dwei.util.FilePathUtil;
import site.dwei.util.MRFactory;
import site.dwei.util.PropertiesParser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author weitu
 * @date 2020-04-18
 * @description
 */
public class MapLaunch  {

    public static boolean splitSignal = false;
    public static boolean stateSignal = false;

    public File inputFile;
    public BufferedReader reader;

    public void prepareFiles() {

        System.out.println("开始读取输入文件...");
        long start = System.currentTimeMillis();

        File tempFileDir = new File(PropertiesParser.getValue(PropertyKeys.TEMPPATH.getName()));
        FilePathUtil.deletePath(tempFileDir);

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File(PropertiesParser.getValue(PropertyKeys.INPUTPATH.getName())));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        inputFile = new File(PropertiesParser.getValue(PropertyKeys.INPUTPATH.getName()));
        reader = new BufferedReader(new InputStreamReader(fis));
    }

    public void splitFile(){

        System.out.println("开始切分文件....");
        /**
         * 获取上下文对象
         * 获取任务
         */
        Mapper mapper = MRFactory.mapTask;
       // MapReduceContext mrContext = MRFactory.mrContext;
        Context context = MRFactory.getMapContext();

        //每读一行，存入tmp
        String tmp ;
        //当前读到的行数
        long position = 0L;
        //当前分区的字节长度
        long tmpLength = 0L;

        try {
            //得到输入文件位置
            Path path = Paths.get(inputFile.getPath());
            //总行数
            long lineCount = Files.lines(path).count();
            //按行读，循环
            while((tmp = reader.readLine()) != null){
                //行数加一
                ++position;
                //获取这一行的字节长度
                int length = tmp.getBytes().length;
                //加入当前分区
                tmpLength+=length;
                //到达文件末尾
                if(position==lineCount){
                    stateSignal = true;
                }
                //达到每片最大值或到达行尾,10M
                Long splitSize = 10485760L;
                if(tmpLength>= splitSize || position == lineCount){
                    splitSignal =true;
                    tmpLength=0L;
                }
                //mapper.map(position, tmp, mrContext);
                mapper.map(position,tmp,context);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
