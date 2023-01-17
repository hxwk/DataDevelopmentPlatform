package utils;

import com.dfssi.dataplatform.datasync.common.utils.JSONUtil;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HSF on 2017/12/15.
 */
public class JsonUtilTest {

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        String jsonStr =JSONUtil.toJson(list);
        System.out.println(jsonStr);
        List<String> listfromJson = JSONUtil.fromJson(jsonStr,new TypeToken<List<String>>(){});
        System.out.println("listfromJson = " + listfromJson);
    }

}
