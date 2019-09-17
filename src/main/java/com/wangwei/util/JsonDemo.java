package com.wangwei.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @author: wangwei
 * @date: 2019-09-15 17:37
 */
public class JsonDemo {
    public static void main(String[] args) {
        String bankFile = JsonDemo.class.getClassLoader().getResource("bank.json").getPath();

        String bankName = BankUtil.getBankName("6235010100800291413");

        String bankFileStr = FileUtil.readJsonFile(bankFile);

        JSONObject parse = (JSONObject) JSON.parse(bankFileStr);

        String bname = (String) parse.get(bankName);



        String bankNoFile = JsonDemo.class.getClassLoader().getResource("bankNo.json").getPath();

        String bankNoFileStr = FileUtil.readJsonFile(bankNoFile);

        final JSONObject parse1 = (JSONObject) JSON.parse(bankNoFileStr);

        final String bankNumber = (String) parse1.get(bname);

        System.out.println(bname + " : " + bankNumber);
    }
}
