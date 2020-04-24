package site.dwei.myjob;

import site.dwei.mapred.Context;
import site.dwei.mapred.MapReduceContext;
import site.dwei.mapred.Reducer;

import java.util.Iterator;

/**
 * @author weitu
 * @date 2020-04-19
 * @description
 */
public class MyReduce extends Reducer {

    @Override
    public void reduce(String key, Iterable value, Context context) {
        long result=0L;
        Iterator iterator = value.iterator();
        while (iterator.hasNext()){
            Object next = iterator.next();
            result+=(long)next;
        }
        context.write(key,result);
    }
}
