package ch.loewe.normal_use_client.fabricclient.loewe;

import ch.loewe.normal_use_client.fabricclient.client.FabricClientClient;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.util.Identifier;

import java.util.Random;

public class HandleServerMessage {
    private static String code = null;
    public static void onReceiveMessage(MinecraftClient client, ClientPlayNetworkHandler handler, String message, PacketSender responseSender){
        if (message.length() > 0 && !Character.isLetter(message.charAt(0)))
            message = message.substring(1);
        switch (message) {
            case "send request" -> {
                code = randomString();
                ClientPlayNetworking.registerReceiver(new Identifier("monopoly", code), (client2, handler2, buf2, responseSender2) ->
                        HandleServerMessage.onReceiveMessage(client2, handler2, new String(buf2.getWrittenBytes()), responseSender2));
                responseSender.sendPacket(new Identifier("monopoly", "loewe"), PacketByteBufs.create().writeString("request_code " + code));
            }
            case "secure channel" -> {}
            case "settings allowed" -> {}//mc.setScreen(new IASConfigScreen(mc.currentScreen)); set screen here
            default -> FabricClientClient.logger.info(message);
        }
    }

    public static void requestSettings(){
        if (code == null) {
            ClientPlayNetworking.send(new Identifier("monopoly", "loewe"), PacketByteBufs.create().writeString("ask request"));
            if (code != null){
                ClientPlayNetworking.send(new Identifier("monopoly", code), PacketByteBufs.create().writeString("request settings"));
            }
        } else
            ClientPlayNetworking.send(new Identifier("monopoly", code), PacketByteBufs.create().writeString("request settings"));
    }

    private static String randomString(){
        String alphabet = "abcdefghijklmnopqrstuvwxyz";

        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for(int i = 0; i < 10; i++) {
            int index = random.nextInt(alphabet.length());
            char randomChar = alphabet.charAt(index);
            sb.append(randomChar);
        }

        return sb.toString();
    }
}
