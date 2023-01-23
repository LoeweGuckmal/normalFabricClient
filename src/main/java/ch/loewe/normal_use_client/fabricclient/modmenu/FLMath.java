package ch.loewe.normal_use_client.fabricclient.modmenu;

import java.util.function.Supplier;
import net.minecraft.client.MinecraftClient;

public class FLMath {
    private static final double PI = 3.141592653589793D;
    private static final Supplier<Double> RENDER_DISTANCE = () -> {
        return MinecraftClient.getInstance().worldRenderer != null ? Math.min(MinecraftClient.getInstance().worldRenderer.getViewDistance(), (double)getRadiusBoundMax()) : (double)getRadiusBoundMax();
    };

    public FLMath() {
    }

    public static int getChunkTryLimit() {
        return parseMinMax(FLConfig.getChunkTryLimit(), DefaultConfig.getTryLimitBound());
    }

    public static Boolean getDebug() {
        return FLConfig.getRawDebug();
    }

    public static int getRadiusBoundMax() {
        return DefaultConfig.getRawRadiusBound().max();
    }

    public static MinMaxHolder getRadiusBound() {
        return DefaultConfig.getRawRadiusBound();
    }

    public static MinMaxHolder getChunkTryLimitBound() {
        return DefaultConfig.getTryLimitBound();
    }

    private static int getRenderDistance() {
        return RENDER_DISTANCE.get().intValue();
    }

    protected static int parseMinMax(int toProcess, int max, int min) {
        return Math.max(Math.min(toProcess, max), min);
    }

    protected static int parseMinMax(int toProcess, MinMaxHolder maxMin) {
        return Math.max(Math.min(toProcess, maxMin.max()), maxMin.min());
    }

    private static int getSquareArea(boolean worldProgressTracker, int toCalc, boolean raw) {
        int i = toCalc * 2;
        if (!raw) {
            ++i;
        }

        if (worldProgressTracker) {
            ++i;
            ++i;
        }

        if (i == 0) {
            i = 1;
        }

        return i * i;
    }

    public static Double getCircleArea(int radius) {
        return 3.141592653589793D * (double)radius * (double)radius;
    }

    public static Integer getPreRenderRadius() {
        return parseMinMax(FLConfig.getRawPreRenderRadius(), Math.min(getRenderDistance(), getRadiusBoundMax()), 0);
    }

    public static Integer getPreRenderRadius(boolean raw) {
        return raw ? Math.max(FLConfig.getRawPreRenderRadius(), getRadiusBound().min()) : getPreRenderRadius();
    }

    public static int getPregenRadius(boolean raw) {
        return raw ? parseMinMax(FLConfig.getRawChunkPregenRadius(), getRadiusBound()) : parseMinMax(FLConfig.getRawChunkPregenRadius(), getRadiusBound()) + 1;
    }

    public static int getPregenRadius() {
        return getPregenRadius(true);
    }

    public static int getPregenArea() {
        return getSquareArea(false, parseMinMax(getPregenRadius(), getRadiusBound().max(), getRadiusBound().min()), false);
    }

    public static int getProgressArea() {
        return getSquareArea(true, parseMinMax(getPregenRadius(), getRadiusBound().max(), getRadiusBound().min()), false);
    }

    public static Integer getPreRenderArea() {
        return getCircleArea(getPreRenderRadius()).intValue();
    }

    public static Boolean getCloseUnsafe() {
        return FLConfig.getCloseLoadingScreenUnsafely();
    }

    public static Boolean getCloseSafe() {
        return getPreRenderRadius() > 0;
    }

    public static Boolean getForceBuild() {
        return getChunkTryLimit() >= 1000;
    }
}
