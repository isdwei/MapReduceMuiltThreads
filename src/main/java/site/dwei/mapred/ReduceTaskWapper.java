package site.dwei.mapred;

import com.google.protobuf.InvalidProtocolBufferException;
import site.dwei.constant.PropertyKeys;
import site.dwei.util.KVListSerial;
import site.dwei.util.MRFactory;
import site.dwei.util.PropertiesParser;

import java.io.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * @author weitu
 * @date 2020-04-18
 * @description
 */
public class ReduceTaskWapper implements Runnable {

    private Long numOfTasks;
    private Map<String, List<Object>> tm = null;
    private int index;
    private CountDownLatch countDownLatch;
    /**
     * the length of this file, measured in bytes.
     */
    private Long fileSize = 0L;
    private String filePath = null;
    public static ThreadLocal<String> taskLocal = ThreadLocal.withInitial(() -> "init value");

    public ReduceTaskWapper(int index) {
        this.index = index;
        this.tm = new HashMap<>();
        this.filePath = PropertiesParser.getValue(PropertyKeys.TEMPPATH.getName()) + "\\tmp" + index + ".txt";


        try {
            this.fileSize = new RandomAccessFile(new File(this.filePath), "r").length();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        System.out.println(Thread.currentThread()+this.filePath);
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
            fis=new FileInputStream(new File(this.filePath));
            bis = new BufferedInputStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        byte[] bigbuffer = new byte[this.fileSize.intValue()+100];
        byte[] shortbuffer = null;

        try {
            //读取临时文件的数据
            int tmp;
            try {
                while ((tmp=bis.read(bigbuffer))!=-1){
                    shortbuffer=new byte[tmp];
                    System.arraycopy(bigbuffer,0,shortbuffer,0,tmp);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }finally {
            try {
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {

            KVListSerial.KVList serialKV = KVListSerial.KVList.parseFrom(shortbuffer);
            List<KVListSerial.KVList.KVText> kvTexts = serialKV.getKvtextsList();
            Iterator<KVListSerial.KVList.KVText> iterator = kvTexts.iterator();

            while (iterator.hasNext()){
                KVListSerial.KVList.KVText next = iterator.next();
                String key=next.getKey();
                List<Object> value;
                if(!tm.containsKey(key)) {
                    value = new ArrayList<>();
                }else {
                    value = tm.get(key);
                }
                value.add(next.getValue());
                tm.put(key,value);
            }

            Reducer reducer = MRFactory.getNewReduceTask();
            MapReduceContext mrContext = MRFactory.mrContext;

            int count = 0;
            int size = tm.size();

            Iterator<Map.Entry<String, List<Object>>> entryIterator = tm.entrySet().iterator();
            while (entryIterator.hasNext()) {
                Map.Entry<String, List<Object>> next = entryIterator.next();
                ++count;
                if (count == size) {
                    taskLocal.set("final");
                }
                reducer.reduce(next.getKey(), next.getValue(), mrContext);
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }


    }
}
