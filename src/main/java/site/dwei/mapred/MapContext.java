package site.dwei.mapred;

import site.dwei.constant.PropertyKeys;
import site.dwei.util.FilePathUtil;
import site.dwei.util.KVListSerial;
import site.dwei.util.PartitionUtil;
import site.dwei.util.PropertiesParser;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author weitu
 * @date 2020-04-23
 * @description
 */
public class MapContext extends Context {

    private static int numReduceTasks = Integer.parseInt(PropertiesParser.getValue(PropertyKeys.REDUCETASK.getName()));
    private static ArrayList<KVListSerial.KVList.Builder> kvList;
    private static File[] files;

    /**
     * 初始化
     */
    static {

        kvList = new ArrayList<>(numReduceTasks);
        files = new File[numReduceTasks];
        String tmppath = PropertiesParser.getValue(PropertyKeys.TEMPPATH.getName());

        for (int i = 0; i < numReduceTasks; i++) {

            kvList.add(KVListSerial.KVList.newBuilder());
            FilePathUtil.createPath(tmppath);
            files[i] = new File(tmppath + "/tmp" + i + ".txt");
            try {
                files[i].createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void write(String key, long value) {

        int partitionFlag = PartitionUtil.getHashPartition(key, numReduceTasks);

        //将KV对序列化到序列化数组
        kvList.get(partitionFlag).addKvtexts(KVListSerial.KVList.KVText.newBuilder().setKey(key).setValue(value));
        //如果该分区结束
        if (MapLaunch.splitSignal && Mapper.endFlag) {

            MapLaunch.splitSignal = false;
            Mapper.endFlag = false;

            //将序列化数组中的所有数据写入对应的临时文件中
            for (int i = 0; i < numReduceTasks; i++) {

                BufferedOutputStream bos = null;
                FileOutputStream fos = null;

                try {
                    //以append的方式写入
                    fos = new FileOutputStream(files[i], true);
                    bos = new BufferedOutputStream(fos);
                    bos.write(kvList.get(i).build().toByteArray());
                    bos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (bos != null) {
                        try {
                            bos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        Mapper.endFlag = false;
    }

}
