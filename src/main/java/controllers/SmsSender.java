
package controllers;


import okhttp3.*;

import java.io.IOException;

public class SmsSender {

    public static void sendSms(String phoneNumber, String message) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"messages\":[{\"destinations\":[{\"to\":\"21695841109\"}],\"from\":\"HACKIFY\",\"text\":\"" + message + "\"}]}");
        Request request = new Request.Builder()
                .url("https://qdvml3.api.infobip.com/sms/2/text/advanced")
                .method("POST", body)
                .addHeader("Authorization", "App 9f9ff5ff0a8d4c5bb2ff0d7a5b37568a-4f676d00-e055-4616-8322-6a26bf702bce")
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        System.out.println("Response code: " + response.code());
        System.out.println("Response body: " + response.body().string());
        System.out.println("sms sent");
    }
}
