package ch.loewe.normal_use_client.fabricclient;

public class test {
    public static void main(String [] args){
        int width = 64;
        int height = 32;
        int imageWidth = 120;
        int imageHeigt = 32;
        while (width < imageWidth || height < imageHeigt) {
            height *= 2;
            width *= 2;
        }
        System.out.println(height + ", " + width);
    }
}
