package ch.loewe.normal_use_client.fabricclient;

import ch.loewe.normal_use_client.fabricclient.loewe.DataFromUrl;

public class test {
    public static void main(String[] args) {
        System.out.println(DataFromUrl.getData("http://192.168.100.168:8881/sdk?mode=getCurrentColor").subSequence(3, 9));
    }
}
