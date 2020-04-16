package com.ninlgde.zenjson;

import com.ninlgde.zenjson.serialize.error.JsonDeserializeException;
import com.ninlgde.zenjson.serialize.error.JsonException;
import com.ninlgde.zenjson.serialize.error.JsonTypeException;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws JsonTypeException, JsonDeserializeException, UnsupportedEncodingException {
        String jsonstr = "{" +
//                "\"aaa\":23143141.23e100," +
//                "\"bbbb\":123," +
//                "\"cccc\": \"abc\","+
                "\"dddd\": [1,\"hello\",3]," +
                "\"eeeee\": {\"e1\":321,\"e2\":456}," +
                "\"bool\": {}}" +
                "}";
//        String jsonstr = "{\"bbbb\":{}}";//"{\"aa\":912345678900e100}";
        JSONObject jsonstrr = (JSONObject) Json.parse(jsonstr);
//        jsonstrr.remove("dddd");
//        jsonstrr.remove("eeeee");
        jsonstrr.remove("bool");

        System.out.println(jsonstrr.dump());

        jsonstrr.put("eee", 123);
        System.out.println(jsonstrr.dump());


        JSONObject json = new JSONObject();
//        json.put("aaa", 23143141.23e100);
//        json.put("bbb", 123);
//        JSONObject jsonObject = json.clone();
////        jsonObject.put("e1", "hhhh");
////        jsonObject.put("e1", "hhhh2");
//        json.put("ddd", jsonObject);
//        json.put("boo", true);

//        json.put("str", jsonstrr);
//        json.put("hanzi", "汉字");

        JSONArray array = jsonstrr.getJSONArray("dddd");
        array.add(4);
        array.add("xxxx");
        array.add("fffff");
        array.add(1, "world");

        json.remove("ddd");

        json.put("array", array);

        array.remove(0);

        System.out.println(json.dump());
        System.out.println(array.dump());

//        JSONObject jsonObject1 = jsonObject.clone();
//        System.out.println(json.containsValue(jsonObject1));

        List<Object> list = new ArrayList<>();
        list.add(1);
        list.add("你好");
        list.add(5);

        System.out.println(array.retainAll(list));
        System.out.println(array.dump());

        JSONArray array1 = new JSONArray(list);
        JSONObject obj = new JSONObject();
        obj.put("list", list);
//        ByteBuffer buffer = ByteBuffer.allocate(1024);
        String string = obj.dump();
//        byte[] bytes = new byte[buffer.position()];
//        buffer.position(0);
//        buffer.get(bytes);
        System.out.println(string);

        JSONObject a = (JSONObject) Json.parse(string);
        JSONArray ar = a.getJSONArray("list");
        System.out.println(ar.get(1));


//        list.add(3);
//        list.add(4);
//        list.add("xxxx");
//        list.add("fffff");
//        list.remove(3);
//        list.add(1, "world");
//
//        List<Object> sub = list.subList(2, 4);
//
//        list.forEach(System.out::println);
//
//        System.out.println(sub);

//        JSONObject j = json.getJSONObject("ddd");
//        j.put("e1", "xxxxxx");
//        System.out.println(j.dump());
//        System.out.println(json.dump(true));
    }
}
