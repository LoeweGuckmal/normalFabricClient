package ch.loewe.normal_use_client.fabricclient.loewe;

import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import static ch.loewe.normal_use_client.fabricclient.client.FabricClientClient.*;

public class HandleServerMessage {
    private static final Logger log = LoggerFactory.getLogger(HandleServerMessage.class);

    public static void onReceiveMessage(MinecraftClient client, ClientPlayNetworkHandler handler, String message, PacketSender responseSender) {
        switch (message) {
            case "monopoly" -> lastAddress = new ServerAddress("loewe-monopoly.feathermc.gg", 25565);
            case "op" -> isOpOnMonopoly = true;
            default -> logger.info(message);
        }
    }

    //TODO
    public static void onReceiveMessage(CustomPayload payload, ClientPlayNetworking.Context player) {
        logger.info("onReceiveMessage");
        switch (payload.toString()) {
            case "monopoly" -> lastAddress = new ServerAddress("loewe-monopoly.feathermc.gg", 25565);
            case "op" -> isOpOnMonopoly = true;
            default -> logger.info(payload.toString());
        }
    }
    //deprecated
    public static void sendMessage(String namespace, String message) {
        //ClientPlayNetworking.send(Identifier.of(namespace), PacketByteBufs.create().writeString(message));
    }

    public static void sendMessage(String message) {
        ClientPlayNetworking.send(new StringPayload(message));
        //ClientPlayNetworking.send(Identifier.of("monopoly:loewe"), PacketByteBufs.create().writeString(message));
    }

    public static byte[] removeZerosFromEnd(byte[] array) {
        int lastIndex = array.length - 1;
        while (lastIndex >= 0 && array[lastIndex] == 0) {
            lastIndex--;
        }
        byte[] result = new byte[lastIndex + 1];
        System.arraycopy(array, 0, result, 0, lastIndex + 1);
        return result;
    }

    public record StringPayload(String string) implements CustomPayload {
        public static final CustomPayload.Id<StringPayload> ID = new CustomPayload.Id<>(Identifier.of("monopoly:loewe"));
        public static final PacketCodec<RegistryByteBuf, StringPayload> CODEC = PacketCodec.tuple(PACKET_CODEC, StringPayload::string, StringPayload::new);

        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() {
            return ID;
        }
    }
    public static final PacketCodec<ByteBuf, String> PACKET_CODEC = new PacketCodec<>() {
        public String decode(ByteBuf byteBuf) {
            ArrayList<Byte> list = new ArrayList<>();
            try {
                for (int i = 0; i <= byteBuf.writerIndex(); i++)
                    if (Integer.parseInt(String.valueOf(byteBuf.getByte(i))) >= 40 && Integer.parseInt(String.valueOf(byteBuf.getByte(i))) <= 126)
                        list.add(byteBuf.getByte(i));
            } catch (Exception ignored) {}
            byte[] returnBytes = new byte[list.size()];
            for (int i = 0; i < list.size(); i++)
                returnBytes[i] = list.get(i);
            logger.info(new String(returnBytes));
            return new String(returnBytes);
        }

        public void encode(ByteBuf byteBuf, String string) {
            PacketByteBuf.writeByteArray(byteBuf, string.getBytes());
        }
    };
}

            /*case "send request" -> {
                FabricClientClient.isOnMonopoly = true;
                code = randomString();
                ClientPlayNetworking.registerReceiver(Identifier.of("monopoly", code), (client2, handler2, buf2, responseSender2) ->
                        HandleServerMessage.onReceiveMessage(client2, handler2, new String(buf2.getWrittenBytes()), responseSender2));
                responseSender.sendPacket(Identifier.of("monopoly", "loewe"), PacketByteBufs.create().writeString("request_code " + code));
            }
            case "secure channel" -> {}
            case "settings allowed" -> {}//mc.setScreen(new IASConfigScreen(mc.currentScreen)); set screen here*/

    /*public static void requestSettings(){
        if (code == null) {
            ClientPlayNetworking.send(Identifier.of("monopoly", "loewe"), PacketByteBufs.create().writeString("ask request"));
            if (code != null){
                ClientPlayNetworking.send(Identifier.of("monopoly", code), PacketByteBufs.create().writeString("request settings"));
            }
        } else
            ClientPlayNetworking.send(Identifier.of("monopoly", code), PacketByteBufs.create().writeString("request settings"));
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
    }*/