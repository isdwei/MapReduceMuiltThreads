package site.dwei.constant;

/**
 * @author weitu
 * @date 2020-04-18
 * @description
 */
public enum PropertyKeys {


    /**
     *
     */
    REDUCETASK("num"),
    /**
     *
     */
    MAPTASKPATH("map"),
    /**
     *
     */
    REDUCETASKPATH("reduce"),
    /**
     *
     */
    TEMPPATH("tmp"),
    /**
     *
     */
    INPUTPATH("input"),
    /**
     *
     */
    OUTPUTPATH("output");

    private String name;

    /**
     *
     * @param name
     */
    PropertyKeys(String name) {
        this.name=name;
    }

    public String getName() {
        return name;
    }}
