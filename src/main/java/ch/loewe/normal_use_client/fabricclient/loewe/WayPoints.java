package ch.loewe.normal_use_client.fabricclient.loewe;

import ch.loewe.normal_use_client.fabricclient.modmenu.Config;
import net.minecraft.util.WorldSavePath;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;
import java.util.UUID;

import static ch.loewe.normal_use_client.fabricclient.client.FabricClientClient.*;
import static ch.loewe.normal_use_client.fabricclient.client.FabricClientClient.mc;
import static ch.loewe.normal_use_client.fabricclient.modmenu.Config.properties;

public class WayPoints {
    public static final HashMap<String, double[]> wayPointsMap = new HashMap<>();
    public static final HashMap<String, Integer> indexMap = new HashMap<>();
    public static final Properties loeweUUID = new Properties();
    public static Path path = null;


    public static int toggle(){
        try {
            if (wpToggled == null)
                wpToggled = Config.parseBoolean(properties.getProperty("wpToggled"));
            else wpToggled = !Config.parseBoolean(properties.getProperty("wpToggled"));
        } catch (Exception ignored) {wpToggled = false;}
        properties.setProperty("wpToggled", String.valueOf(wpToggled));
        Config.write();
        return 1;
    }

    public static HashMap<String, double[]> getWayPoints(){
        String key = "wayPoints_" + getUUID();
        HashMap<String, double[]> returnV = new HashMap<>();
        try {
            String s = properties.getProperty(key);
            if (s != null) {
                properties.setProperty(key, s);
                Config.write();
                Arrays.stream(s.split(";")).toList().forEach(wayPoint -> returnV.put(wayPoint.split("_")[0],
                        new double[]{Double.parseDouble(wayPoint.split("_")[1]), Double.parseDouble(wayPoint.split("_")[2]),
                                Double.parseDouble(wayPoint.split("_")[3])}));
                wayPointsMap.clear();
                wayPointsMap.putAll(returnV);
                rearrangeIndex();
                return returnV;
            }
        } catch (Exception ignored) {}
        properties.setProperty(key, "");
        Config.write();
        wayPointsMap.clear();
        rearrangeIndex();
        return returnV;
    }

    public static void addWayPoint(String s, double[] i){
        HashMap<String, double[]> map = new HashMap<>();
        map.put(s, i);
        addWayPoints(map);
    }

    public static void addWayPoints(HashMap<String, double[]> wayPoints){
        HashMap<String, double[]> newW = getWayPoints();
        newW.putAll(wayPoints);
        setWayPoints(newW);
    }

    public static void removeWayPoint(String name){
        try {
            HashMap<String, double[]> newW = getWayPoints();
            newW.remove(name);
            setWayPoints(newW);
        } catch (Exception ignored){}
    }

    public static void setWayPoints(HashMap<String, double[]> wayPoints){
        try { //waypoints = name-x-y-z;name-x-y-z
            StringBuilder builder = new StringBuilder();
            wayPoints.forEach(
                    (String name, double[] doubles) -> builder.append(name).append("_").
                    append(doubles[0]).append("_").append(doubles[1]).append("_").append(doubles[2]).append(";")
            );
            properties.setProperty("wayPoints_" + getUUID(), builder.toString());
            Config.write();
            wayPointsMap.clear();
            wayPointsMap.putAll(wayPoints);
            rearrangeIndex();
            return;
        } catch (Exception ignored){}
        logger.warn("Couldn't set waypoints!");
    }

    public static void rearrangeIndex(){
        indexMap.clear();
        final int[] index = {3};
        wayPointsMap.forEach((name, i) -> indexMap.put(name, index[0]++));
    }

    public static String getUUID() {
        if (path())
            return loeweUUID.getProperty("uuid");
        return null;
    }

    public static boolean path() {
        if (mc.isInSingleplayer() && mc.getServer() != null) {
            if (mc.getServer().getSavePath(WorldSavePath.ROOT).resolve("Loewe_UUID".toLowerCase() + ".dat") == path)
                return true;
            path = mc.getServer().getSavePath(WorldSavePath.ROOT).resolve("Loewe_UUID".toLowerCase() + ".dat");
            loeweUUID.clear();
            if (Files.isRegularFile(path)) {
                try {
                    InputStream in = Files.newInputStream(path, StandardOpenOption.CREATE);
                    try {
                        loeweUUID.load(in);
                    } catch (IOException ignored) {}

                    in.close();
                } catch (IOException ignored) {}
            }
            if (loeweUUID.getProperty("uuid") == null)
                loeweUUID.setProperty("uuid", UUID.randomUUID().toString());
            try {
                OutputStream out = Files.newOutputStream(path, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
                try {
                    loeweUUID.store(out, "Loewe UUID File");
                } catch (Throwable ignored) {}
                if (out != null) out.close();
            } catch (IOException ignored) {}
            return true;
        } else if (isConnectedToServer) {
            loeweUUID.clear();
            if (loeweUUID.getProperty("uuid") == null)
                loeweUUID.setProperty("uuid", lastAddress.getAddress());
            return true;
        } else return false;
    }
}
