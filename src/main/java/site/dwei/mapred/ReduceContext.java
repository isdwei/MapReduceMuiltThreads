package site.dwei.mapred;

import com.google.protobuf.Message;
import site.dwei.constant.PropertyKeys;
import site.dwei.util.FilePathUtil;
import site.dwei.util.KVListSerial;
import site.dwei.util.PropertiesParser;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author weitu
 * @date 2020-04-23
 * @description
 */
public class ReduceContext extends Context  {

    private static int numReduceTasks=Integer.parseInt(PropertiesParser.getValue(PropertyKeys.REDUCETASK.getName()));

    private static File[] finalFiles;
    private StringBuilder sb = new StringBuilder();
    private static String outputpath;
    private static AtomicInteger ai;
    private BufferedOutputStream finalbos = null;



    /**
     * 初始化
     */
    static {

        finalFiles=new File[numReduceTasks];
        ai = new AtomicInteger(0);
        outputpath=PropertiesParser.getValue(PropertyKeys.OUTPUTPATH.getName());

        for (int i = 0; i < numReduceTasks; i++) {

            FilePathUtil.createPath(outputpath);
            finalFiles[i] = new File(outputpath+"/final"+i+".txt");
            try {
                finalFiles[i].createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void write(String  key, long value) {
        //reduce
        if (this.finalbos==null){
            try {
                this.finalbos=new BufferedOutputStream(new FileOutputStream(finalFiles[ai.getAndIncrement()],true));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        this.sb.append(key).append(" ").append(value).append("\n");
        byte[] bs;

        //每1M写一下
        if(this.sb.length()>=1024||"final".equals(ReduceTaskWapper.taskLocal.get())){
            bs=this.sb.toString().getBytes();
            try {
                this.finalbos.write(bs);
                this.finalbos.flush();
                this.sb.setLength(0);
                if("final".equals(ReduceTaskWapper.taskLocal.get())){
                    this.finalbos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//        //结束了也要写一下
//        if("final".equals(ReduceTaskWapper.taskLocal.get())){
//            bs=this.sb.toString().getBytes();
//
//            try {
//                this.finalbos.write(bs);
//                this.finalbos.flush();
//                this.sb.setLength(0);
//                //this.finalbos.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }
}
