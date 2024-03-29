package ch.loewe.normal_use_client.fabricclient.modmenu;

import ch.loewe.normal_use_client.fabricclient.client.FabricClientClient;
import ch.loewe.normal_use_client.fabricclient.modmenu.DefaultConfig.propertyKeys;
import com.mojang.serialization.Codec;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.option.SimpleOption.ValidatingIntSliderCallbacks;
import net.minecraft.text.Text;

import java.util.ArrayList;

public class ModMenuButtons {
    public static final ArrayList<String> addressStorage = new ArrayList<>();
    public static final String LB = "Loewe".toLowerCase() + ".button.";
    public static final SimpleOption<?>[] buttons = new SimpleOption[]{
            getNewBoolButton(propertyKeys.doRgb(), Config.getDoRgb()),
            getNewEnumButton(propertyKeys.standardColor(), StandardColor.valueOf(Config.getStandardColor().toUpperCase())),

            getNewBoolButton(propertyKeys.showFps(), Config.getShowFps()),
            getNewBoolButton(propertyKeys.showCords(), Config.getShowCords()),

            getNewIntSlider(propertyKeys.standardZoom(), DefaultConfig.getStandardZoom(), Config.getStandardZoom()),

            getNewBoolButton(propertyKeys.hasCapeGlint(), Config.getHasCapeGlint()),
            getNewBoolButton(propertyKeys.capeFromFile(), Config.getCapeFromFile()),
            getNewEnumButton(propertyKeys.reloadCape(), ReloadCape.valueOf(Config.getReloadCape().toUpperCase())),

            getNewBoolButton(propertyKeys.skipFrontView(), Config.getSkipFrontView()),

            getNewEnumButton(propertyKeys.openAccountSwitcher(), OpenAccountSwitcher.valueOf(Config.getOpenAccountSwitcher().toUpperCase())),

            getNewEnumButton(propertyKeys.requestServerAccess(), RequestServerAccess.valueOf(Config.getRequestServerAccess().toUpperCase())),

            getNewBoolButton(propertyKeys.debug(), Config.getDebug())
    };
    public static final SimpleOption<?>[] monopolyButtons = new SimpleOption[]{
            getNewEnumButton(propertyKeys.startGame(), StartGame.valueOf(Config.getStartGame().toUpperCase())),
            getNewEnumButton(propertyKeys.stopGame(), StartGame.valueOf(Config.getStopGame().toUpperCase()))
    };

    public ModMenuButtons() {}

    //buttons
    public static SimpleOption<Boolean> getNewBoolButton(String type, boolean def) {
        addressStorage.add(type);
        return SimpleOption.ofBoolean(LB + type, SimpleOption.constantTooltip(Text.translatable(LB + type + ".tooltip")), def,
                (value) -> {
            Config.storeProperty(type, String.valueOf(value));
            FabricClientClient.doCustom(type);
            Config.write();
        });
    }

    public static SimpleOption<Integer> getNewIntSlider(String type, MinMaxDefHolder minMaxDefHolder, int current) {
        addressStorage.add(type);
        int max = minMaxDefHolder.max();
        int min = minMaxDefHolder.min();
        return new SimpleOption<>(LB + type,
                (value) -> {
                    if (value == min) return Tooltip.of(Text.translatable(LB + type + ".tooltip.min"));
                    else if (value == max) return Tooltip.of(Text.translatable(LB + type + ".tooltip.max"));
                    else return Tooltip.of(Text.translatable(LB + type + ".tooltip"));
                },
                (optionText, value) -> {
                    if (value == min) {
                        return GameOptions.getGenericValueText(optionText, Text.translatable(LB + type + ".min"));
                    } else {
                        return value.equals(max) ? GameOptions.getGenericValueText(optionText, Text.translatable(LB + type + ".max")) : GameOptions.getGenericValueText(optionText, value);
                    }
                },
                new ValidatingIntSliderCallbacks(min, max),
                Codec.DOUBLE.xmap((value) -> max, (value) -> (double) value - (double) max),
                current,
                (value) -> {
                    Config.storeProperty(type, String.valueOf(value));
                    FabricClientClient.doCustom(type);
                    Config.write();
                }
        );
    }

    public static <E extends Enum<E>> SimpleOption<E> getNewEnumButton(String type, E def) {
        return new EnumConfigOption<E>().getNewEnumButton(type, def);
    }


    //other
    public static SimpleOption<?>[] asOptions() {
        SimpleOption<?>[] newArr;
        if (FabricClientClient.isOnMonopoly() && FabricClientClient.isOpOnMonopoly) {
            newArr = buttons.clone();
        } else {
            newArr = new SimpleOption<?>[buttons.length - 1];
            System.arraycopy(buttons, 0, newArr, 0, buttons.length - 2);
            newArr[buttons.length-2] = buttons[buttons.length-1];
        }
        return newArr.clone();
    }
    public static SimpleOption<?>[] asOptionsMonopoly() {
        return monopolyButtons.clone();
    }

    public static String getButtonAddresses(int i) {
        return addressStorage.get(i);
    }


    //Enums (loewe)
    public enum StandardColor {
        YELLOW,
        BLUEGREEN,
        CURRENT
    }
    public enum ReloadCape {
        RELOAD,
        RELOAD2
    }
    public enum OpenAccountSwitcher {
        ACCOUNT,
        ACCOUNT2
    }
    public enum RequestServerAccess {
        ACCESS,
        ACCESS2
    }

    //Enums (monopoly)
    public enum StartGame {
        START,
        START2
    }
}
