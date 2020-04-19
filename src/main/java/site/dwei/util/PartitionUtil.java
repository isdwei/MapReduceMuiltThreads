package site.dwei.util;

/**
 * @author weitu
 * @date 2020-04-19
 * @description
 */
public class PartitionUtil {
    public static int getHashPartition(Object key, int num){
        return (key.toString().hashCode()&Integer.MAX_VALUE)%num;
    }
}
