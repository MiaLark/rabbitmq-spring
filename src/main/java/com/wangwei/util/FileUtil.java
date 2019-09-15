package com.wangwei.util;

import java.io.*;

/**
 * @author: wangwei
 * @date: 2019-09-15 17:19
 */
public class FileUtil {

    public static String readJsonFile(String fileName) {

        StringBuilder sb = new StringBuilder();

        try(BufferedReader br = new BufferedReader(new FileReader(fileName))){
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return sb.toString();
    }
}
