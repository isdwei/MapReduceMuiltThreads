package site.dwei.util;

import java.io.File;

/**
 * @author weitu
 * @date 2020-04-18
 * @description
 */
public class FilePathUtil {

    public FilePathUtil() {
    }

    /**
     * 清空，考虑是文件还是文件夹
     * @param dirFile
     * @return
     */
    public static boolean deletePath(File dirFile){
        if(!dirFile.exists()){
            return true;
        }
        if(dirFile.isFile()){
            return dirFile.delete();
        }else {
            File[] files = dirFile.listFiles();
            for(File file:files){
                deletePath(file);
            }

            return dirFile.delete();
        }
    }

    public static boolean createPath(String filePath){
        File file =new File(filePath);
        deletePath(file);
        if(!file.exists()){
            return file.mkdir();
        }
        return true;
    }
}
