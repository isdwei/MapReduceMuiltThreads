package site.dwei.mapred;

/**
 * @author weitu
 * @date 2020-04-18
 * @description
 */
public abstract class Mapper {

    public static boolean endFlag = false;

    public abstract void map(long position, String tmp, MapReduceContext mrContext) ;
}
