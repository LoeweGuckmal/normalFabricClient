package ch.loewe.normal_use_client.fabricclient;

import ch.loewe.normal_use_client.fabricclient.cape.DownloadManager;
import ch.loewe.normal_use_client.fabricclient.modmenu.Config;
import com.google.gson.Gson;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Base64;

import static ch.loewe.normal_use_client.fabricclient.client.FabricClientClient.logger;

public class test {
    public static void main(String[] args) {
        int rgba = RGBA(12, 0, 168, 255);
        System.out.println(rgba + ", r: " + NativeImage.getRed(rgba) + ", g: " + NativeImage.getGreen(rgba) + ", b: " + NativeImage.getBlue(rgba) + ", a: " + NativeImage.getAlpha(rgba));
        int px = RGBA(12, 0, 168, 255);
        int a = (px >> 24)& 0xff;
        int r = (px >> 16)& 0xff;
        int g = (px >> 8)& 0xff;
        int b = (px)& 0xff;
        System.out.println(a + ", " + r + ", " + g + ", " + b);
    }
    public static int RGBA(int r, int g, int b, int a) {
        return (a << 24) | ((r & 255) << 16) | ((g & 255) << 8) | (b & 255);
    }
}
