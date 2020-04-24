package site.dwei.mapred;


/**
 * @author weitu
 * @date 2020-04-23
 * @description
 */
public abstract class Context {


    public abstract void write(String key, long value);

    public abstract void flush();

    public abstract void close();
}
