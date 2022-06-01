package com.stream.myvoa.utils;

import android.util.Log;

import com.stream.myvoa.bean.LRCObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LRCLoader {

    /**
     * 通过LRC文件网络地址，获取LRC数据并解析为一个LRC对象列表
     * @param strUrl LRC文件网络地址
     * @return LRC对象列表
     */
    public static ArrayList<LRCObject> load(String strUrl){
        ArrayList<LRCObject> rowList, lrcList;
        rowList = getLRCList(strUrl);
        lrcList = LRCParser(rowList);
        return lrcList;
    }

    public static ArrayList<LRCObject> getLRCList(String strUrl) {
        ArrayList<LRCObject> rowList = new ArrayList<>();
        try {
            URL url = new URL(strUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setUseCaches(false);
            httpURLConnection.connect();
            //判断HTTP报文的状态码
            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Log.e("http","OK");
                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                //输入流
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                //加一层缓冲，就可以一行一行读
                String line = "";
                while (null != (line = bufferedReader.readLine())) {
                    rowList.add(new LRCObject(line));
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return rowList;
    }

    public static ArrayList<LRCObject> LRCParser(ArrayList<LRCObject> rowList){
        ArrayList<LRCObject> lrcList = new ArrayList<>();

        //先做一遍过滤，过滤无用行
        for(int i = 0;i< rowList.size();i++){
            String sentence = rowList.get(i).getLRCSentence();
            if(sentence.indexOf('[')!=0 || sentence.indexOf(']') !=9){
                // 在LRC文件中，合法的一行中'['必在第0位且']'必在第9位
                // 若不符合，则这一行无用，跳过
                continue;
            }
            else{
                lrcList.add(rowList.get(i));
            }
        }

        //将时间和句子拆开，并把时间转换为毫秒值
        for(int i = 0; i < lrcList.size(); i++){
            LRCObject lrcObject = lrcList.get(i);
            String sentence = lrcObject.getLRCSentence();
            String content = sentence.substring(10, sentence.length()); //第9位是"]"，往后就都是句子
            long startTime = timeConvert(sentence.substring(1, 9)); //去掉左右括号，将[02:32.43]转换成02:32.43
            lrcObject.setContent(content);
            lrcObject.setStartTime(startTime);
        }

        return lrcList;
    }

    /**
     * 将mm:ss.SS格式的时间字符串转为毫秒值
     * @param timeString
     * @return
     */
    private static long timeConvert(String timeString){
        timeString = timeString.replace('.', ':');
        String[] times = timeString.split(":");

        return (long) Integer.parseInt(times[0]) * 60 * 1000 +
                Integer.parseInt(times[1]) * 1000L +
                Integer.parseInt(times[2]);
    }
}
