package site.dwei.mapred;

import site.dwei.constant.PropertyKeys;
import site.dwei.util.FilePathUtil;
import site.dwei.util.KVListSerial;
import site.dwei.util.PartitionUtil;
import site.dwei.util.PropertiesParser;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author weitu
 * @date 2020-04-18
 * @description
 */
public class MapReduceContext extends Context{

    private static int numReduceTasks=Integer.parseInt(PropertiesParser.getValue(PropertyKeys.REDUCETASK.getName()));
    private static boolean isfinish;
    private static ArrayList<KVListSerial.KVList.Builder> kvList;
    private static File[] files;
    private static File[] finalFiles;
    private StringBuilder sb = new StringBuilder();
    private static String tmppath;
    private static String outputpath;
    private static AtomicInteger ai;
    private BufferedOutputStream finalbos = null;


    private int partitionFlag = 0;

    /**
     * 初始化
     */
    static {

        kvList = new ArrayList<>(numReduceTasks);
        files = new File[numReduceTasks];
        finalFiles=new File[numReduceTasks];
        ai = new AtomicInteger(0);
        isfinish = false;
        tmppath=PropertiesParser.getValue(PropertyKeys.TEMPPATH.getName());
        outputpath=PropertiesParser.getValue(PropertyKeys.OUTPUTPATH.getName());

        for (int i = 0; i < numReduceTasks; i++) {

            kvList.add(KVListSerial.KVList.newBuilder());
            FilePathUtil.createPath(tmppath);
            FilePathUtil.createPath(outputpath);

            files[i] = new File(tmppath+"/tmp"+i+".txt");
            finalFiles[i] = new File(outputpath+"/final"+i+".txt");

            try {
                files[i].createNewFile();
                finalFiles[i].createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    public void write(String key, long value){
        //如果还没结束
        if(!isfinish){

            //如果已经读到文件末尾，就将结束标识符置true
            if(MapLaunch.stateSignal&&Mapper.endFlag){
                isfinish=true;
            }

            this.partitionFlag= PartitionUtil.getHashPartition(key,numReduceTasks);

            //将KV对序列化到序列化数组
            kvList.get(this.partitionFlag).addKvtexts(KVListSerial.KVList.KVText.newBuilder().setKey(key).setValue(value));
            //如果该分区结束
            if(MapLaunch.splitSignal&&Mapper.endFlag){

                MapLaunch.splitSignal=false;
                Mapper.endFlag=false;

                //将序列化数组中的所有数据写入对应的临时文件中
                for (int i = 0; i < numReduceTasks; i++) {

                    BufferedOutputStream bos = null;
                    FileOutputStream fos = null;

                    try {
                        //以append的方式写入
                        fos=new FileOutputStream(files[i],true);
                        bos=new BufferedOutputStream(fos);
                        bos.write(kvList.get(i).build().toByteArray());
                        bos.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {
                        try {
                            bos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            Mapper.endFlag=false;

        }else {
            //reduce
            if (this.finalbos==null){
                try {
                    this.finalbos=new BufferedOutputStream(new FileOutputStream(finalFiles[ai.getAndIncrement()],true));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

            this.sb.append(key+" "+value+"\n");
            byte[] bs;


            //每1M写一下
            if(this.sb.length()>=1024){
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

            //结束了也要写一下
            if("final".equals(ReduceTaskWapper.taskLocal.get())){
                bs=this.sb.toString().getBytes();

                try {
                    this.finalbos.write(bs);
                    this.finalbos.flush();
                    this.sb.setLength(0);
                    //this.finalbos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }



        }
    }

}
