package site.dwei.util;

import site.dwei.mapred.MapReduceContext;
import site.dwei.constant.PropertyKeys;
import site.dwei.mapred.Mapper;
import site.dwei.mapred.Reducer;

/**
 * @author weitu
 * @date 2020-04-18
 * @description
 */
public class MRFactory {

    public static Mapper mapTask = null;
    public static MapReduceContext mrContext = null;

    private static Class mapClass = null;
    private static Class reduceClass = null;

    static {
        System.out.println("初始化任务...");
        try {
            mapClass = Class.forName(PropertiesParser.getValue(PropertyKeys.MAPTASKPATH.getName()));
            reduceClass = Class.forName(PropertiesParser.getValue(PropertyKeys.REDUCETASKPATH.getName()));
            mapTask = (Mapper) mapClass.newInstance();
            mrContext = new MapReduceContext();
        } catch (Exception e) {
            System.out.println("初始化任务出错");
            e.printStackTrace();
        }
    }

    public static Mapper getNewMapTask() {
        try {
            return (Mapper) mapClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("加载map类出错");

        }
    }

    public static Reducer getNewReduceTask() {
        try {
            return (Reducer) reduceClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("加载reduce类出错");

        }
    }
}