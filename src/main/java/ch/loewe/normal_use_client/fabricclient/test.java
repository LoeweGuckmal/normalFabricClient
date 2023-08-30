package ch.loewe.normal_use_client.fabricclient;

public class test {
    public static void main(String[] args) {
        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                System.out.println("1");
            }
        }).start();
        System.out.println("2");
    }
}
