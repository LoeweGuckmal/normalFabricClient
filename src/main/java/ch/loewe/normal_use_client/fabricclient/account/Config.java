package ch.loewe.normal_use_client.fabricclient.account;

import ch.loewe.normal_use_client.fabricclient.account.account.Account;
import ch.loewe.normal_use_client.fabricclient.account.account.Auth;
import ch.loewe.normal_use_client.fabricclient.account.account.MicrosoftAccount;
import ch.loewe.normal_use_client.fabricclient.account.account.OfflineAccount;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Config {
    private static final int CONFIG_VERSION = 2;
    public static List<Account> accounts = new ArrayList();
    public static boolean titleScreenText = true;
    public static String titleScreenTextX;
    public static String titleScreenTextY;
    public static Config.Alignment titleScreenTextAlignment;
    public static boolean titleScreenButton;
    public static String titleScreenButtonX;
    public static String titleScreenButtonY;
    public static boolean multiplayerScreenButton;
    public static String multiplayerScreenButtonX;
    public static String multiplayerScreenButtonY;
    public static boolean experimentalJavaFXBrowser;

    public Config() {
    }

    public static void load(@NotNull Path gameDir) {
        try {
            Path p = gameDir.resolve("config").resolve("ias.json");
            if (!Files.isRegularFile(p, new LinkOption[0])) {
                return;
            }

            JsonObject jo = (JsonObject)SharedIAS.GSON.fromJson(new String(Files.readAllBytes(p), StandardCharsets.UTF_8), JsonObject.class);
            if (!jo.has("version")) {
                accounts = (List)(jo.has("accounts") ? loadAccounts(jo.getAsJsonArray("accounts"), 1) : new ArrayList());
                titleScreenTextX = jo.has("textX") ? jo.get("textX").getAsString() : null;
                titleScreenTextX = jo.has("textY") ? jo.get("textY").getAsString() : null;
                titleScreenButtonX = jo.has("btnX") ? jo.get("btnX").getAsString() : null;
                titleScreenButtonY = jo.has("btnY") ? jo.get("btnY").getAsString() : null;
                multiplayerScreenButton = jo.has("showOnMPScreen") && jo.get("showOnMPScreen").getAsBoolean();
                titleScreenButton = !jo.has("showOnTitleScreen") || jo.get("showOnTitleScreen").getAsBoolean();
                return;
            }

            int version = jo.get("version").getAsInt();
            if (version != 2) {
                throw new IllegalStateException("Unknown config version: " + version + ", content: " + jo);
            }

            accounts = (List)(jo.has("accounts") ? loadAccounts(jo.getAsJsonArray("accounts"), version) : new ArrayList());
            titleScreenText = !jo.has("titleScreenText") || jo.get("titleScreenText").getAsBoolean();
            titleScreenTextX = jo.has("titleScreenTextX") ? jo.get("titleScreenTextX").getAsString() : null;
            titleScreenTextY = jo.has("titleScreenTextY") ? jo.get("titleScreenTextY").getAsString() : null;
            titleScreenTextAlignment = jo.has("titleScreenTextAlignment") ? Config.Alignment.getOr(jo.get("titleScreenTextAlignment").getAsString(), Config.Alignment.CENTER) : Config.Alignment.CENTER;
            titleScreenButton = !jo.has("titleScreenButton") || jo.get("titleScreenButton").getAsBoolean();
            titleScreenButtonX = jo.has("titleScreenButtonX") ? jo.get("titleScreenButtonX").getAsString() : null;
            titleScreenButtonY = jo.has("titleScreenButtonY") ? jo.get("titleScreenButtonY").getAsString() : null;
            multiplayerScreenButton = jo.has("multiplayerScreenButton") && jo.get("multiplayerScreenButton").getAsBoolean();
            multiplayerScreenButtonX = jo.has("multiplayerScreenButtonX") ? jo.get("multiplayerScreenButtonX").getAsString() : null;
            multiplayerScreenButtonY = jo.has("multiplayerScreenButtonY") ? jo.get("multiplayerScreenButtonY").getAsString() : null;
            experimentalJavaFXBrowser = jo.has("experimentalJavaFXBrowser") && jo.get("experimentalJavaFXBrowser").getAsBoolean() && experimentalJavaFXBrowserAvailable();
        } catch (Throwable var4) {
            SharedIAS.LOG.error("Unable to load IAS config.", var4);
        }

    }

    @Contract(
            pure = true
    )
    @NotNull
    private static List<Account> loadAccounts(@NotNull JsonArray accounts, int version) {
        List<Account> accs = new ArrayList();
        Iterator var3 = accounts.iterator();

        while(var3.hasNext()) {
            JsonElement je = (JsonElement)var3.next();
            Account account = loadAccount(je.getAsJsonObject().get("type").getAsString(), version == 1 ? je.getAsJsonObject().getAsJsonObject("data") : je.getAsJsonObject(), version);
            if (account != null) {
                accs.add(account);
            }
        }

        return accs;
    }

    @Contract(
            pure = true
    )
    @Nullable
    private static Account loadAccount(@NotNull String type, @NotNull JsonObject json, int version) {
        if (!type.equalsIgnoreCase("ias:microsoft") && !type.equalsIgnoreCase("ru.vidtu.ias.account.MicrosoftAccount")) {
            if (!type.equalsIgnoreCase("ias:offline") && !type.equalsIgnoreCase("ru.vidtu.ias.account.OfflineAccount")) {
                return null;
            } else {
                String name = version == 1 ? json.get("username").getAsString() : json.get("name").getAsString();
                return new OfflineAccount(name, version == 1 ? Auth.resolveUUID(name) : UUID.fromString(json.get("uuid").getAsString()));
            }
        } else {
            return new MicrosoftAccount(version == 1 ? json.get("username").getAsString() : json.get("name").getAsString(), json.get("accessToken").getAsString(), json.get("refreshToken").getAsString(), UUID.fromString(json.get("uuid").getAsString()));
        }
    }

    public static void save(@NotNull Path gameDir) {
        try {
            Path p = gameDir.resolve("config").resolve("ias.json");
            Files.createDirectories(p.getParent());
            JsonObject jo = new JsonObject();
            jo.addProperty("version", 2);
            jo.add("accounts", saveAccounts(accounts));
            jo.addProperty("titleScreenText", titleScreenText);
            if (titleScreenTextX != null) {
                jo.addProperty("titleScreenTextX", titleScreenTextX);
            }

            if (titleScreenTextY != null) {
                jo.addProperty("titleScreenTextY", titleScreenTextY);
            }

            if (titleScreenTextAlignment != null) {
                jo.addProperty("titleScreenTextAlignment", titleScreenTextAlignment.name());
            }

            jo.addProperty("titleScreenButton", titleScreenButton);
            if (titleScreenButtonX != null) {
                jo.addProperty("titleScreenButtonX", titleScreenButtonX);
            }

            if (titleScreenButtonY != null) {
                jo.addProperty("titleScreenButtonY", titleScreenButtonY);
            }

            jo.addProperty("multiplayerScreenButton", multiplayerScreenButton);
            if (multiplayerScreenButtonX != null) {
                jo.addProperty("multiplayerScreenButtonX", multiplayerScreenButtonX);
            }

            if (multiplayerScreenButtonY != null) {
                jo.addProperty("multiplayerScreenButtonY", multiplayerScreenButtonY);
            }

            jo.addProperty("experimentalJavaFXBrowser", experimentalJavaFXBrowser);
            Files.write(p, jo.toString().getBytes(StandardCharsets.UTF_8), new OpenOption[0]);
        } catch (Throwable var3) {
            SharedIAS.LOG.error("Unable to save IAS config.", var3);
        }

    }

    @Contract(
            pure = true
    )
    @NotNull
    private static JsonArray saveAccounts(@NotNull List<Account> accounts) {
        JsonArray ja = new JsonArray();
        Iterator var2 = accounts.iterator();

        while(var2.hasNext()) {
            Account a = (Account)var2.next();
            JsonObject jo = saveAccount(a);
            if (jo != null) {
                ja.add(jo);
            }
        }

        return ja;
    }

    @Contract(
            pure = true
    )
    @Nullable
    private static JsonObject saveAccount(@NotNull Account account) {
        JsonObject jo;
        if (account instanceof MicrosoftAccount) {
            jo = new JsonObject();
            MicrosoftAccount ma = (MicrosoftAccount)account;
            jo.addProperty("type", "ias:microsoft");
            jo.addProperty("name", ma.name());
            jo.addProperty("accessToken", ma.accessToken());
            jo.addProperty("refreshToken", ma.refreshToken());
            jo.addProperty("uuid", ma.uuid().toString());
            return jo;
        } else if (account instanceof OfflineAccount) {
            jo = new JsonObject();
            jo.addProperty("type", "ias:offline");
            jo.addProperty("name", account.name());
            jo.addProperty("uuid", account.uuid().toString());
            return jo;
        } else {
            return null;
        }
    }

    public static boolean experimentalJavaFXBrowserAvailable() {
        try {
            Class.forName("javafx.scene.web.WebView");
            return true;
        } catch (Throwable var1) {
            return false;
        }
    }

    static {
        titleScreenTextAlignment = Config.Alignment.CENTER;
        titleScreenButton = true;
        multiplayerScreenButton = false;
        experimentalJavaFXBrowser = false;
    }

    public static enum Alignment {
        LEFT("ias.configGui.titleScreenText.alignment.left"),
        CENTER("ias.configGui.titleScreenText.alignment.center"),
        RIGHT("ias.configGui.titleScreenText.alignment.right");

        private final String key;

        private Alignment(@NotNull String key) {
            this.key = key;
        }

        @Contract(
                pure = true
        )
        @NotNull
        public String key() {
            return this.key;
        }

        @Contract(
                pure = true
        )
        @NotNull
        public static Config.Alignment getOr(@NotNull String name, @NotNull Config.Alignment fallback) {
            Config.Alignment[] var2 = values();
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                Config.Alignment v = var2[var4];
                if (v.name().equalsIgnoreCase(name)) {
                    return v;
                }
            }

            return fallback;
        }
    }
}
