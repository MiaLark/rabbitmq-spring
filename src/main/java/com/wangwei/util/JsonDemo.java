package com.wangwei.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @author: wangwei
 * @date: 2019-09-15 17:37
 */
public class JsonDemo {
    public static void main(String[] args) {
        String path = JsonDemo.class.getClassLoader().getResource("bank.json").getPath();


        String bankName = BankUtil.getBankName("6226880172622938");

        String jsonFile = FileUtil.readJsonFile(path);

        JSONObject parse = (JSONObject) JSON.parse(jsonFile);

        System.out.println(parse.get(bankName));
    }
}
