/**
 * @author JianKang
 * @date 2018/5/22
 * @description
 */
import java.util.Iterator;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class TestJson {


    public static String getKeys(JSONObject test) throws JSONException{

        String result = null;
        Iterator keys = test.keys();
        while(keys.hasNext()){
            try{
                String key = keys.next().toString();
                String value = test.optString(key);
                int i = testIsArrayORObject(value);
                if(result == null || result.equals("")){
                    if(i == 0){
                        result = key + ",";
                        System.out.println("i=0 | key="+key+"| result="+result);
                    }else if( i == 1){
                        result = key + ",";
                        System.out.println("i=1 | key="+key+"| result="+result);
                        result = getKeys(new JSONObject(value))+",";
                    }else if( i == 2){
                        result = key + ",";
                        System.out.println("i=2 | key="+key+"| result="+result);
                        JSONArray arrays = new JSONArray(value);
                        for(int k =0;k<arrays.length();k++){
                            JSONObject array = new JSONObject(arrays.get(k));
                            result = getKeys(array) + ",";
                        }
                    }
                }else{
                    if(i == 0){
                        result = result + key + ",";
                        System.out.println("i=0 | key="+key+"| result="+result);
                    }else if( i == 1){
                        result = result + key + ",";
                        System.out.println("i=1 | key="+key+"| result="+result);
                        result = result + getKeys(new JSONObject(value));
                    }else if( i == 2){
                        result = result + key + ",";
                        System.out.println("i=2 | key="+key+"| result="+result);
                        JSONArray arrays = new JSONArray(value);
                        for(int k =0;k<arrays.length();k++){
                            JSONObject array = new JSONObject(arrays.get(k));
                            result = result + getKeys(array) + ",";
                        }
                    }
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
        return result;
    }

    public static int testIsArrayORObject(String sJSON){
    /*
     * return 0:既不是array也不是object
     * return 1：是object
     * return 2 ：是Array
     */
        try {
            JSONArray array = new JSONArray(sJSON);
            return 2;
        } catch (JSONException e) {// 抛错 说明JSON字符不是数组或根本就不是JSON
            try {
                JSONObject object = new JSONObject(sJSON);
                return 1;
            } catch (JSONException e2) {// 抛错 说明JSON字符根本就不是JSON
                System.out.println("非法的JSON字符串");
                return 0;
            }
        }

    }

    public static void main(String args[]) throws org.json.JSONException{
        JSONObject test = new JSONObject();
        JSONObject test1 = new JSONObject();
        try{
            test1.put("A", "1");
            test1.put("B", "2");

            test.put("a", "1");
            test.put("c", test1);
            test.put("b", "2");

            System.out.println(test.toString());

        }catch(JSONException e){
            e.printStackTrace();
        }
        String s = getKeys(test);
        System.out.println(s);
    }
}