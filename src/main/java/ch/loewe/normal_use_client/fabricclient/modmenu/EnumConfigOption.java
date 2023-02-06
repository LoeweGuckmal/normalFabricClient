package ch.loewe.normal_use_client.fabricclient.modmenu;

import ch.loewe.normal_use_client.fabricclient.client.FabricClientClient;
import com.mojang.serialization.Codec;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.option.SimpleOption.PotentialValuesBasedCallbacks;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.Locale;

import static ch.loewe.normal_use_client.fabricclient.modmenu.ModMenuButtons.LB;
import static ch.loewe.normal_use_client.fabricclient.modmenu.ModMenuButtons.addressStorage;

public class EnumConfigOption<E extends Enum<E>> {

    public SimpleOption<E> getNewEnumButton(String type, E def){
        addressStorage.add(type);
        return new SimpleOption<>(
                LB + type,
                (value) -> Tooltip.of(getValueText(LB + type + ".tooltip", value)),
                (text, value) -> getValueText(LB + type, value),
                new PotentialValuesBasedCallbacks<>(Arrays.asList(def.getDeclaringClass().getEnumConstants()),
                        Codec.STRING.xmap((string) -> Arrays.stream(def.getDeclaringClass().getEnumConstants()).filter(
                                        (e) -> e.name().toLowerCase().equals(string)).findAny().orElse(null),
                                (newValue) -> newValue.name().toLowerCase())),
                def,
                (value) -> {
                    Config.storeProperty(type, value.name().toLowerCase(Locale.ROOT));
                    FabricClientClient.doCustom(type);
                    Config.write();
                }
        );
    }

    private static <E extends Enum<E>> Text getValueText(String translationKey, E value) {
        return Text.translatable(translationKey + "." + value.name().toLowerCase(Locale.ROOT));
    }
}
