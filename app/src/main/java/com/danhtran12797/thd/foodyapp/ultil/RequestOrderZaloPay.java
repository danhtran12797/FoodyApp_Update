package com.danhtran12797.thd.foodyapp.ultil;

import android.util.Log;

import com.danhtran12797.thd.foodyapp.model.Payment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

public class RequestOrderZaloPay {
    private static String mrefundid = "12797";

    private static Map<String, String> config = new HashMap<String, String>() {{
        put("appid", "553");
        put("key1", "9phuAOYhan4urywHTh0ndEXiV3pKHr5Q");
        put("key2", "Iyz2habzyr7AG8SgvoBCbKwKi3UzlLi3");
        put("endpoint", "https://sandbox.zalopay.com.vn/v001/tpe/createorder");
    }};

    public static String getCurrentTimeString(String format) {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+7"));
        SimpleDateFormat fmt = new SimpleDateFormat(format);
        fmt.setCalendar(cal);
        return fmt.format(cal.getTimeInMillis());
    }

    public static Map<String, Object> requestCreateOrder(long amount, String orderId) {
        final Map embeddata = new HashMap() {{
            put("merchantinfo", "THD Foody - Ứng dụng bán đồ ăn vặt");
        }};

        final Map[] item = {
                new HashMap() {{
                    put("itemid", "knb");
                    put("itemname", "kim nguyen bao");
                    put("itemprice", 198400);
                    put("itemquantity", 1);
                }}
        };

        Map<String, Object> order = new HashMap<String, Object>() {{
            put("appid", config.get("appid"));
            put("apptransid", getCurrentTimeString("yyMMdd") + "_" + orderId); // mã giao dich có định dạng yyMMdd_xxxx
            put("apptime", System.currentTimeMillis()); // miliseconds
            put("appuser", "demo");
            put("amount", amount);
            put("description", "THD Foody - thanh toán đơn hàng #" + orderId);
            put("bankcode", "zalopayapp");
            Gson gsonObject = new GsonBuilder().create();
            put("item", gsonObject.toJson(item));
            put("embeddata", new JSONObject(embeddata).toString());
        }};

        // appid +”|”+ apptransid +”|”+ appuser +”|”+ amount +"|" + apptime +”|”+ embeddata +"|" +item
        String data = order.get("appid") + "|" + order.get("apptransid") + "|" + order.get("appuser") + "|" + order.get("amount")
                + "|" + order.get("apptime") + "|" + order.get("embeddata") + "|" + order.get("item");
        order.put("mac", HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, config.get("key1"), data));

        return order;
    }

    public static Map<String, Object> requestRefundOrder(Payment payment) {
        String appid = config.get("appid");
        Random rand = new Random();
        long timestamp = System.currentTimeMillis(); // miliseconds
        String uid = timestamp + "" + (111 + rand.nextInt(888)); // unique id
        mrefundid = getCurrentTimeString("yyMMdd") + "_" + appid + "_" + uid;

        Log.d("AAA", "uid: " + uid);
        Log.d("AAA", "mrefundid: " + mrefundid);

        Map<String, Object> order = new HashMap<String, Object>() {{
            put("appid", appid);
            put("zptransid", payment.getTransId());
            put("mrefundid", mrefundid);
            put("timestamp", timestamp);
            put("amount", payment.getAmount());
            put("description", "THD Foody - hủy đơn hàng #" + payment.getOrderId());
        }};

        // appid|zptransid|amount|description|timestamp
        String data = order.get("appid") + "|" + order.get("zptransid") + "|" + order.get("amount")
                + "|" + order.get("description") + "|" + order.get("timestamp");
        order.put("mac", HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, config.get("key1"), data));

        return order;
    }

    public static Map<String, Object> getpartialrefundstatus() {
        String appid = config.get("appid");
        long timestamp = System.currentTimeMillis(); // miliseconds

        String data = config.get("appid") + "|" + mrefundid + "|" + timestamp; // appid|mrefundid|timestamp
        String mac = HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, config.get("key1"), data);

        Map<String, Object> order = new HashMap<String, Object>() {{
            put("appid", appid);
            put("mrefundid", mrefundid);
            put("timestamp", timestamp);
            put("mac", mac);
        }};

        return order;
    }
}
