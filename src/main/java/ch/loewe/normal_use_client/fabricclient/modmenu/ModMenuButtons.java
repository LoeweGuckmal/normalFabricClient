package ch.loewe.normal_use_client.fabricclient.modmenu;

import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.text.Text;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.option.SimpleOption.ValidatingIntSliderCallbacks;

import static ch.loewe.normal_use_client.fabricclient.modmenu.DefaultConfig.propertyKeys;

public class ModMenuButtons {
    private static final ArrayList<String> addressStorage = new ArrayList();
    private static final String FLB = "Fastload".toLowerCase() + ".button.";
    protected static final SimpleOption<?>[] buttons = new SimpleOption[]{getNewBoolButton(propertyKeys.debug(), FLMath.getDebug()), getNewBoolButton(propertyKeys.unsafeClose(), FLMath.getCloseUnsafe()), getNewSlider(propertyKeys.render(), FLMath.getRadiusBound(), FLMath.getPreRenderRadius()), getNewSlider(propertyKeys.pregen(), FLMath.getRadiusBound(), FLMath.getPregenRadius(true)), getNewSlider(propertyKeys.tryLimit(), FLMath.getChunkTryLimitBound(), FLMath.getChunkTryLimit())};

    public ModMenuButtons() {
    }

    public static String getButtonAddresses(int i) {
        return addressStorage.get(i);
    }

    public static SimpleOption<Boolean> getNewBoolButton(String type, boolean getConfig) {
        addressStorage.add(type);
        return SimpleOption.ofBoolean(FLB + type, SimpleOption.constantTooltip(Text.translatable(FLB + type + ".tooltip")), getConfig);
    }

    public static SimpleOption<Integer> getNewSlider(String type, MinMaxHolder minMaxHolder, int defVal) {
        addressStorage.add(type);
        int max = minMaxHolder.max();
        int min = minMaxHolder.min();
        return new SimpleOption<>(FLB + type, SimpleOption.constantTooltip(Text.translatable(FLB + type + ".tooltip")), (optionText, value) -> {
            if (value == min) {
                return GameOptions.getGenericValueText(optionText, Text.translatable(FLB + type + ".min"));
            } else {
                return value.equals(max) ? GameOptions.getGenericValueText(optionText, Text.translatable(FLB + type + ".max")) : GameOptions.getGenericValueText(optionText, value);
            }
        }, new ValidatingIntSliderCallbacks(min, max), Codec.DOUBLE.xmap((value) -> {
            return max;
        }, (value) -> {
            return (double) value - (double) max;
        }), defVal, (value) -> {
        });
    }

    public static SimpleOption<?>[] asOptions() {
        ArrayList<SimpleOption<?>> options = new ArrayList(Arrays.asList(buttons));
        return (SimpleOption<?>[])options.toArray(SimpleOption[]::new);
    }
}
