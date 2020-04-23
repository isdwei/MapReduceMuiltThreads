package site.dwei.mapred;

/**
 * @author weitu
 * @date 2020-04-18
 * @description
 */
public abstract class Reducer {
    public abstract void reduce(String key, Iterable value, Context context);
}
