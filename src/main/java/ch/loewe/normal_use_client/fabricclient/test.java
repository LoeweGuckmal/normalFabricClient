package ch.loewe.normal_use_client.fabricclient;

import java.util.Arrays;

public class test {
    public static void main(String[] args) {
        Integer[] integers = new Integer[]{2,3,4,5,6,7,8};
        Integer[] integers1 = new Integer[integers.length-1];
        System.arraycopy(integers, 0, integers1, 0, integers.length - 2);
        integers1[integers.length-2] = integers[integers.length-1];

        System.out.println(Arrays.toString(integers1));
    }
}
