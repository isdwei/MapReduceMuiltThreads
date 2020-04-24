package site.dwei.myjob;

import site.dwei.mapred.Context;
import site.dwei.mapred.MapReduceContext;
import site.dwei.mapred.Mapper;

/**
 * @author weitu
 * @date 2020-04-18
 * @description
 */
public class MyMap extends Mapper {

    @Override
    public void map(long position, String line, Context context) {
        String[] splits = line.split(" ");
        for (String split : splits) {
            context.write(split, 1L);
        }
    }
}
