package site.dwei.mapred;

import site.dwei.constant.PropertyKeys;
import site.dwei.util.FilePathUtil;
import site.dwei.util.MRFactory;
import site.dwei.util.PropertiesParser;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author weitu
 * @date 2020-04-19
 * @description
 */
public class ReduceLaunch {


    public void calculate(){


        //ReduceTask
        Integer num = Integer.valueOf(PropertiesParser.getValue(PropertyKeys.REDUCETASK.getName()));
        ExecutorService pool = Executors.newFixedThreadPool(num);

        for (int i = 0; i < num; i++) {
            pool.submit(new ReduceTaskWapper(i));

        }

       pool.shutdown();
//        try {
//            while (pool.awaitTermination(1L, TimeUnit.SECONDS)){
//                System.out.println("正在运行...");
//            }
//        }catch (InterruptedException e){
//            e.printStackTrace();
//        }

        File tempFileDir = new File(PropertiesParser.getValue(PropertyKeys.TEMPPATH.getName()));
        FilePathUtil.deletePath(tempFileDir);

    }
}
