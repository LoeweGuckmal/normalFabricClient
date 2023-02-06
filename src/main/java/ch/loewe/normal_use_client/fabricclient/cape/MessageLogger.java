package ch.loewe.normal_use_client.fabricclient.cape;

import com.google.gson.JsonElement;
import net.minecraft.text.Text;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static ch.loewe.normal_use_client.fabricclient.client.FabricClientClient.mc;

public class MessageLogger {
    public void warn(String s){
        if (mc.player != null)
            mc.player.sendMessage(Text.of(s));
        LoggerFactory.getLogger("Loewe").warn(s);
    }
    public void warn(int i){
        warn(""+i);
    }
    public void warn(String s, String s2){
        warn(s+""+s2);
    }
    public void warn(String s, int i){
        warn(s+""+i);
    }
    public void warn(String s, String s2, JsonElement jsonElement){
        warn(s+""+s2+""+jsonElement);
    }
    public void info(String s){
        warn(s);
    }
    public void info(String s, String s2){
        warn(s+""+s2);
    }
    public void info(String s, UUID uuid){
        warn(s+""+uuid.toString());
    }
    public void error(String s, String s2){
        warn(s+""+s2);
    }
    public void error(String s){
        warn(s);
    }
}
