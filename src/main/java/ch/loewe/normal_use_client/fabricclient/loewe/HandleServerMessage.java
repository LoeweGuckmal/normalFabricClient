package ch.loewe.normal_use_client.fabricclient.loewe;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import static ch.loewe.normal_use_client.fabricclient.client.FabricClientClient.*;

public class HandleServerMessage {

    //TODO
    public static void onReceiveMessage(CustomPayload payload, ClientPlayNetworking.Context context) {
        logger.info(payload.toString());
        if (payload instanceof StringPayload stringPayload)
            switch (stringPayload.message) {
                case "monopoly" -> lastAddress = new ServerAddress("loewe-monopoly.feathermc.gg", 25565);
                case "op" -> isOpOnMonopoly = true;
                default -> logger.info(stringPayload.message);
            }
    }

    public static void sendMessage(String message) {
        ClientPlayNetworking.send(new StringPayload(message));
    }

    public record StringPayload(String message) implements CustomPayload {
        public static final CustomPayload.Id<StringPayload> ID = new CustomPayload.Id<>(Identifier.of("monopoly:loewe"));
        public static final PacketCodec<RegistryByteBuf, StringPayload> CODEC = PacketCodec.tuple(PACKET_CODEC, StringPayload::message, StringPayload::new);

        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    public static final PacketCodec<RegistryByteBuf, String> PACKET_CODEC = new PacketCodec<>() {
        public String decode(RegistryByteBuf byteBuf) {
            byte[] returnBytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(returnBytes);
            return new String(returnBytes);
        }

        public void encode(RegistryByteBuf byteBuf, String string) {
            PacketByteBuf.writeByteArray(byteBuf, string.getBytes());
        }
    };
}