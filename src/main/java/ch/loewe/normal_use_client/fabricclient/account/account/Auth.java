package ch.loewe.normal_use_client.fabricclient.account.account;

import ch.loewe.normal_use_client.fabricclient.account.SharedIAS;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class Auth {
    private static final String CLIENT_ID = "54fd49e4-2103-4044-9603-2b028c814ec3";
    private static final String REDIRECT_URI = "http://localhost:59125";
    private static final boolean BLIND_SSL = Boolean.getBoolean("ias.blindSSL");
    private static final boolean NO_CUSTOM_SSL = Boolean.getBoolean("ias.noCustomSSL");
    private static final SSLContext FIXED_CONTEXT;

    public Auth() {
    }

    @NotNull
    public static Entry<String, String> codeToToken(@NotNull String code) throws Exception {
        HttpsURLConnection conn = (HttpsURLConnection)(new URL("https://login.live.com/oauth20_token.srf")).openConnection();
        if (FIXED_CONTEXT != null) {
            conn.setSSLSocketFactory(FIXED_CONTEXT.getSocketFactory());
        }

        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(15000);
        conn.setDoOutput(true);
        OutputStream out = conn.getOutputStream();

        SimpleImmutableEntry var5;
        try {
            String var10001 = URLEncoder.encode("55fca734-6e47-4719-ac3f-1fcdc5600732", StandardCharsets.UTF_8);
            out.write(("client_id=" + var10001 + "&code=" + URLEncoder.encode(code, StandardCharsets.UTF_8) + "&secret_id=a3c8bfb2-59fa-41bf-9d6a-357fa27b6f07&client_secret=NBU8Q~shSml-YG3EoVjgBtZallfDQXly-3T5wcud&grant_type=authorization_code&redirect_uri=" + URLEncoder.encode("http://localhost:59125", StandardCharsets.UTF_8) + "&scope=XboxLive.signin%20XboxLive.offline_access").getBytes(StandardCharsets.UTF_8));
            BufferedReader err;
            if (conn.getResponseCode() < 200 || conn.getResponseCode() > 299) {
                try {
                    err = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));

                    try {
                        int var10002 = conn.getResponseCode();
                        throw new IllegalArgumentException("codeToToken response: " + var10002 + ", data: " + (String)err.lines().collect(Collectors.joining("\n")));
                    } catch (Throwable var10) {
                        try {
                            err.close();
                        } catch (Throwable var8) {
                            var10.addSuppressed(var8);
                        }

                        throw var10;
                    }
                } catch (Throwable var11) {
                    throw new IllegalArgumentException("codeToToken response: " + conn.getResponseCode(), var11);
                }
            }

            err = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));

            try {
                JsonObject resp = (JsonObject) SharedIAS.GSON.fromJson((String)err.lines().collect(Collectors.joining("\n")), JsonObject.class);
                var5 = new SimpleImmutableEntry(resp.get("access_token").getAsString(), resp.get("refresh_token").getAsString());
            } catch (Throwable var9) {
                try {
                    err.close();
                } catch (Throwable var7) {
                    var9.addSuppressed(var7);
                }

                throw var9;
            }

            err.close();
        } catch (Throwable var12) {
            if (out != null) {
                try {
                    out.close();
                } catch (Throwable var6) {
                    var12.addSuppressed(var6);
                }
            }

            throw var12;
        }

        if (out != null) {
            out.close();
        }

        return var5;
    }

    @NotNull
    public static Entry<String, String> refreshToken(@NotNull String refreshToken) throws Exception {
        HttpsURLConnection conn = (HttpsURLConnection)(new URL("https://login.live.com/oauth20_token.srf")).openConnection();
        if (FIXED_CONTEXT != null) {
            conn.setSSLSocketFactory(FIXED_CONTEXT.getSocketFactory());
        }

        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(15000);
        conn.setDoOutput(true);
        OutputStream out = conn.getOutputStream();

        SimpleImmutableEntry var5;
        try {
            String var10001 = URLEncoder.encode("55fca734-6e47-4719-ac3f-1fcdc5600732", "UTF-8");
            out.write(("client_id=" + var10001 + "&refresh_token=" + URLEncoder.encode(refreshToken, "UTF-8") + "&grant_type=refresh_token&redirect_uri=" + URLEncoder.encode("http://localhost:59125", StandardCharsets.UTF_8) + "&scope=XboxLive.signin%20XboxLive.offline_access").getBytes(StandardCharsets.UTF_8));
            BufferedReader err;
            if (conn.getResponseCode() < 200 || conn.getResponseCode() > 299) {
                try {
                    err = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));

                    try {
                        int var10002 = conn.getResponseCode();
                        throw new IllegalArgumentException("refreshToken response: " + var10002 + ", data: " + (String)err.lines().collect(Collectors.joining("\n")));
                    } catch (Throwable var10) {
                        try {
                            err.close();
                        } catch (Throwable var8) {
                            var10.addSuppressed(var8);
                        }

                        throw var10;
                    }
                } catch (Throwable var11) {
                    throw new IllegalArgumentException("refreshToken response: " + conn.getResponseCode(), var11);
                }
            }

            err = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));

            try {
                JsonObject resp = (JsonObject)SharedIAS.GSON.fromJson((String)err.lines().collect(Collectors.joining("\n")), JsonObject.class);
                var5 = new SimpleImmutableEntry(resp.get("access_token").getAsString(), resp.get("refresh_token").getAsString());
            } catch (Throwable var9) {
                try {
                    err.close();
                } catch (Throwable var7) {
                    var9.addSuppressed(var7);
                }

                throw var9;
            }

            err.close();
        } catch (Throwable var12) {
            if (out != null) {
                try {
                    out.close();
                } catch (Throwable var6) {
                    var12.addSuppressed(var6);
                }
            }

            throw var12;
        }

        if (out != null) {
            out.close();
        }

        return var5;
    }

    @NotNull
    public static String authXBL(@NotNull String authToken) throws Exception {
        HttpsURLConnection conn = (HttpsURLConnection)(new URL("https://user.auth.xboxlive.com/user/authenticate")).openConnection();
        if (FIXED_CONTEXT != null) {
            conn.setSSLSocketFactory(FIXED_CONTEXT.getSocketFactory());
        }

        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(15000);
        conn.setDoOutput(true);
        OutputStream out = conn.getOutputStream();

        String var7;
        try {
            JsonObject req = new JsonObject();
            JsonObject reqProps = new JsonObject();
            reqProps.addProperty("AuthMethod", "RPS");
            reqProps.addProperty("SiteName", "user.auth.xboxlive.com");
            reqProps.addProperty("RpsTicket", "d=" + authToken);
            req.add("Properties", reqProps);
            req.addProperty("RelyingParty", "http://auth.xboxlive.com");
            req.addProperty("TokenType", "JWT");
            out.write(req.toString().getBytes(StandardCharsets.UTF_8));
            BufferedReader err;
            if (conn.getResponseCode() < 200 || conn.getResponseCode() > 299) {
                try {
                    err = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));

                    try {
                        int var10002 = conn.getResponseCode();
                        throw new IllegalArgumentException("authXBL response: " + var10002 + ", data: " + (String)err.lines().collect(Collectors.joining("\n")));
                    } catch (Throwable var12) {
                        try {
                            err.close();
                        } catch (Throwable var10) {
                            var12.addSuppressed(var10);
                        }

                        throw var12;
                    }
                } catch (Throwable var13) {
                    throw new IllegalArgumentException("authXBL response: " + conn.getResponseCode(), var13);
                }
            }

            err = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));

            try {
                JsonObject resp = (JsonObject)SharedIAS.GSON.fromJson((String)err.lines().collect(Collectors.joining("\n")), JsonObject.class);
                var7 = resp.get("Token").getAsString();
            } catch (Throwable var11) {
                try {
                    err.close();
                } catch (Throwable var9) {
                    var11.addSuppressed(var9);
                }

                throw var11;
            }

            err.close();
        } catch (Throwable var14) {
            if (out != null) {
                try {
                    out.close();
                } catch (Throwable var8) {
                    var14.addSuppressed(var8);
                }
            }

            throw var14;
        }

        if (out != null) {
            out.close();
        }

        return var7;
    }

    @NotNull
    public static Entry<String, String> authXSTS(@NotNull String xblToken) throws Exception {
        HttpsURLConnection conn = (HttpsURLConnection)(new URL("https://xsts.auth.xboxlive.com/xsts/authorize")).openConnection();
        if (FIXED_CONTEXT != null) {
            conn.setSSLSocketFactory(FIXED_CONTEXT.getSocketFactory());
        }

        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(15000);
        conn.setDoOutput(true);
        OutputStream out = conn.getOutputStream();

        SimpleImmutableEntry var8;
        try {
            JsonObject req = new JsonObject();
            JsonObject reqProps = new JsonObject();
            JsonArray userTokens = new JsonArray();
            userTokens.add(xblToken);
            reqProps.add("UserTokens", userTokens);
            reqProps.addProperty("SandboxId", "RETAIL");
            req.add("Properties", reqProps);
            req.addProperty("RelyingParty", "rp://api.minecraftservices.com/");
            req.addProperty("TokenType", "JWT");
            out.write(req.toString().getBytes(StandardCharsets.UTF_8));
            BufferedReader err;
            if (conn.getResponseCode() < 200 || conn.getResponseCode() > 299) {
                try {
                    err = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));

                    try {
                        int var10002 = conn.getResponseCode();
                        throw new IllegalArgumentException("authXSTS response: " + var10002 + ", data: " + (String)err.lines().collect(Collectors.joining("\n")));
                    } catch (Throwable var13) {
                        try {
                            err.close();
                        } catch (Throwable var11) {
                            var13.addSuppressed(var11);
                        }

                        throw var13;
                    }
                } catch (Throwable var14) {
                    throw new IllegalArgumentException("authXSTS response: " + conn.getResponseCode(), var14);
                }
            }

            err = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));

            try {
                JsonObject resp = (JsonObject)SharedIAS.GSON.fromJson((String)err.lines().collect(Collectors.joining("\n")), JsonObject.class);
                var8 = new SimpleImmutableEntry(resp.get("Token").getAsString(), resp.getAsJsonObject("DisplayClaims").getAsJsonArray("xui").get(0).getAsJsonObject().get("uhs").getAsString());
            } catch (Throwable var12) {
                try {
                    err.close();
                } catch (Throwable var10) {
                    var12.addSuppressed(var10);
                }

                throw var12;
            }

            err.close();
        } catch (Throwable var15) {
            if (out != null) {
                try {
                    out.close();
                } catch (Throwable var9) {
                    var15.addSuppressed(var9);
                }
            }

            throw var15;
        }

        if (out != null) {
            out.close();
        }

        return var8;
    }

    @NotNull
    public static String authMinecraft(@NotNull String userHash, @NotNull String xstsToken) throws Exception {
        HttpsURLConnection conn = (HttpsURLConnection)(new URL("https://api.minecraftservices.com/authentication/login_with_xbox")).openConnection();
        if (FIXED_CONTEXT != null) {
            conn.setSSLSocketFactory(FIXED_CONTEXT.getSocketFactory());
        }

        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(15000);
        conn.setDoOutput(true);
        OutputStream out = conn.getOutputStream();

        String var7;
        try {
            JsonObject req = new JsonObject();
            req.addProperty("identityToken", "XBL3.0 x=" + userHash + ";" + xstsToken);
            out.write(req.toString().getBytes(StandardCharsets.UTF_8));
            BufferedReader err;
            if (conn.getResponseCode() < 200 || conn.getResponseCode() > 299) {
                try {
                    err = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));

                    try {
                        int var10002 = conn.getResponseCode();
                        throw new IllegalArgumentException("authMinecraft response: " + var10002 + ", data: " + (String)err.lines().collect(Collectors.joining("\n")));
                    } catch (Throwable var12) {
                        try {
                            err.close();
                        } catch (Throwable var10) {
                            var12.addSuppressed(var10);
                        }

                        throw var12;
                    }
                } catch (Throwable var13) {
                    throw new IllegalArgumentException("authMinecraft response: " + conn.getResponseCode(), var13);
                }
            }

            err = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));

            try {
                JsonObject resp = (JsonObject)SharedIAS.GSON.fromJson((String)err.lines().collect(Collectors.joining("\n")), JsonObject.class);
                var7 = resp.get("access_token").getAsString();
            } catch (Throwable var11) {
                try {
                    err.close();
                } catch (Throwable var9) {
                    var11.addSuppressed(var9);
                }

                throw var11;
            }

            err.close();
        } catch (Throwable var14) {
            if (out != null) {
                try {
                    out.close();
                } catch (Throwable var8) {
                    var14.addSuppressed(var8);
                }
            }

            throw var14;
        }

        if (out != null) {
            out.close();
        }

        return var7;
    }

    @NotNull
    public static Entry<UUID, String> getProfile(@NotNull String accessToken) throws Exception {
        HttpURLConnection conn = (HttpURLConnection)(new URL("https://api.minecraftservices.com/minecraft/profile")).openConnection();
        conn.setRequestProperty("Authorization", "Bearer " + accessToken);
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(15000);
        BufferedReader err;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 299) {
            err = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));

            SimpleImmutableEntry var4;
            try {
                JsonObject resp = (JsonObject)SharedIAS.GSON.fromJson((String)err.lines().collect(Collectors.joining("\n")), JsonObject.class);
                var4 = new SimpleImmutableEntry(UUID.fromString(resp.get("id").getAsString().replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5")), resp.get("name").getAsString());
            } catch (Throwable var7) {
                try {
                    err.close();
                } catch (Throwable var5) {
                    var7.addSuppressed(var5);
                }

                throw var7;
            }

            err.close();
            return var4;
        } else {
            try {
                err = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));

                try {
                    int var10002 = conn.getResponseCode();
                    throw new IllegalArgumentException("getProfile response: " + var10002 + ", data: " + (String)err.lines().collect(Collectors.joining("\n")));
                } catch (Throwable var8) {
                    try {
                        err.close();
                    } catch (Throwable var6) {
                        var8.addSuppressed(var6);
                    }

                    throw var8;
                }
            } catch (Throwable var9) {
                throw new IllegalArgumentException("getProfile response: " + conn.getResponseCode(), var9);
            }
        }
    }

    @NotNull
    public static UUID resolveUUID(@NotNull String name) {
        UUID uuid;
        try {
            InputStreamReader in = new InputStreamReader((new URL("https://api.mojang.com/users/profiles/minecraft/" + name)).openStream(), StandardCharsets.UTF_8);

            UUID var3;
            try {
                uuid = UUID.fromString(((JsonObject)SharedIAS.GSON.fromJson(in, JsonObject.class)).get("id").getAsString().replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
                var3 = uuid;
            } catch (Throwable var5) {
                try {
                    in.close();
                } catch (Throwable var4) {
                    var5.addSuppressed(var4);
                }

                throw var5;
            }

            in.close();
            return var3;
        } catch (Throwable var6) {
            uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
            return uuid;
        }
    }

    static {
        SSLContext ctx = null;

        try {
            if (BLIND_SSL) {
                SharedIAS.LOG.warn("========== IAS: WARNING ==========");
                SharedIAS.LOG.warn("You've enabled 'ias.blindSSL' property.");
                SharedIAS.LOG.warn("(probably via JVM-argument '-Dias.blindSSL=true')");
                SharedIAS.LOG.warn("While this may fix some SSL problems, it's UNSAFE!");
                SharedIAS.LOG.warn("Do NOT use this option as a 'permanent solution to all problems',");
                SharedIAS.LOG.warn("nag the mod authors if any problems arrive:");
                SharedIAS.LOG.warn("https://github.com/The-Fireplace-Minecraft-Mods/In-Game-Account-Switcher/issues");
                SharedIAS.LOG.warn("========== IAS: WARNING ==========");
                TrustManager blindManager = new X509TrustManager() {
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                };
                ctx = SSLContext.getInstance("TLS");
                ctx.init((KeyManager[])null, new TrustManager[]{blindManager}, new SecureRandom());
                SharedIAS.LOG.warn("Blindly skipping SSL checks. (behavior: 'ias.blindSSL' property)");
            } else if (!NO_CUSTOM_SSL) {
                KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
                InputStream in = Auth.class.getResourceAsStream("/iasjavafix.jks");

                try {
                    ks.load(in, "iasjavafix".toCharArray());
                } catch (Throwable var7) {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (Throwable var6) {
                            var7.addSuppressed(var6);
                        }
                    }

                    throw var7;
                }

                if (in != null) {
                    in.close();
                }

                TrustManagerFactory customTmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                customTmf.init(ks);
                TrustManagerFactory defaultTmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                defaultTmf.init((KeyStore)null);
                final List<X509TrustManager> managers = new ArrayList<>();
                managers.addAll(Arrays.stream(customTmf.getTrustManagers()).filter((tm) -> tm instanceof X509TrustManager).map((tm) -> (X509TrustManager)tm).collect(Collectors.toList()));
                managers.addAll(Arrays.stream(defaultTmf.getTrustManagers()).filter((tm) -> tm instanceof X509TrustManager).map((tm) -> (X509TrustManager)tm).collect(Collectors.toList()));
                TrustManager multiManager = new X509TrustManager() {
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        CertificateException wrapper = new CertificateException("Unable to validate via any trust manager.");
                        Iterator var4 = managers.iterator();

                        while(var4.hasNext()) {
                            X509TrustManager manager = (X509TrustManager)var4.next();

                            try {
                                manager.checkClientTrusted(chain, authType);
                                return;
                            } catch (Throwable var7) {
                                wrapper.addSuppressed(var7);
                            }
                        }

                        throw wrapper;
                    }

                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        CertificateException wrapper = new CertificateException("Unable to validate via any trust manager.");
                        Iterator var4 = managers.iterator();

                        while(var4.hasNext()) {
                            X509TrustManager manager = (X509TrustManager)var4.next();

                            try {
                                manager.checkServerTrusted(chain, authType);
                                return;
                            } catch (Throwable var7) {
                                wrapper.addSuppressed(var7);
                            }
                        }

                        throw wrapper;
                    }

                    public X509Certificate[] getAcceptedIssuers() {
                        List<X509Certificate> certificates = new ArrayList();
                        Iterator var2 = managers.iterator();

                        while(var2.hasNext()) {
                            X509TrustManager manager = (X509TrustManager)var2.next();
                            certificates.addAll(Arrays.asList(manager.getAcceptedIssuers()));
                        }

                        return (X509Certificate[])certificates.toArray(new X509Certificate[0]);
                    }
                };
                ctx = SSLContext.getInstance("TLS");
                ctx.init((KeyManager[])null, new TrustManager[]{multiManager}, new SecureRandom());
                SharedIAS.LOG.info("Using shared SSL context. (behavior: default; custom + default certificates)");
            } else {
                SharedIAS.LOG.warn("Not editing SSL context. (behavior: 'ias.noCustomSSL' property)");
            }
        } catch (Throwable var8) {
            SharedIAS.LOG.error("Unable to init SSL context.", var8);
        }

        FIXED_CONTEXT = ctx;
    }
}
