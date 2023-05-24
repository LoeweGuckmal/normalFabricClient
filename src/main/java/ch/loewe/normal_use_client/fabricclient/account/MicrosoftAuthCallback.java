package ch.loewe.normal_use_client.fabricclient.account;

import ch.loewe.normal_use_client.fabricclient.account.account.Auth;
import ch.loewe.normal_use_client.fabricclient.account.account.MicrosoftAccount;
import com.sun.net.httpserver.HttpServer;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MicrosoftAuthCallback implements Closeable {
    public static final String MICROSOFT_AUTH_URL = "https://login.live.com/oauth20_authorize.srf?client_id=54fd49e4-2103-4044-9603-2b028c814ec3&response_type=code&scope=XboxLive.signin%20XboxLive.offline_access&redirect_uri=http://localhost:59125&prompt=select_account";
    private HttpServer server;

    public MicrosoftAuthCallback() {
    }

    @NotNull
    public CompletableFuture<MicrosoftAccount> start(@NotNull BiConsumer<String, Object[]> progressHandler, @NotNull String done) {
        CompletableFuture cf = new CompletableFuture();

        try {
            this.server = HttpServer.create(new InetSocketAddress("localhost", 59125), 0);
            this.server.createContext("/", (ex) -> {
                SharedIAS.LOG.info("Microsoft authentication callback request: " + ex.getRemoteAddress());

                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(MicrosoftAuthCallback.class.getResourceAsStream("/authPage.html"), StandardCharsets.UTF_8));

                    try {
                        progressHandler.accept("ias.loginGui.microsoft.progress", new Object[]{"preparing"});
                        byte[] b = in.lines().collect(Collectors.joining("\n")).replace("%message%", done).getBytes(StandardCharsets.UTF_8);
                        ex.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
                        ex.sendResponseHeaders(307, b.length);
                        OutputStream os = ex.getResponseBody();

                        try {
                            os.write(b);
                        } catch (Throwable var12) {
                            if (os != null) {
                                try {
                                    os.close();
                                } catch (Throwable var11) {
                                    var12.addSuppressed(var11);
                                }
                            }

                            throw var12;
                        }

                        if (os != null) {
                            os.close();
                        }

                        this.close();
                        SharedIAS.EXECUTOR.execute(() -> {
                            try {
                                cf.complete(this.auth(progressHandler, ex.getRequestURI().getQuery()));
                            } catch (Throwable var5) {
                                SharedIAS.LOG.error("Unable to authenticate via Microsoft.", var5);
                                cf.completeExceptionally(var5);
                            }

                        });
                    } catch (Throwable var13) {
                        try {
                            in.close();
                        } catch (Throwable var10) {
                            var13.addSuppressed(var10);
                        }

                        throw var13;
                    }

                    in.close();
                } catch (Throwable var14) {
                    SharedIAS.LOG.error("Unable to process request on Microsoft authentication callback server.", var14);
                    this.close();
                    cf.completeExceptionally(var14);
                }

            });
            this.server.start();
            SharedIAS.LOG.info("Started Microsoft authentication callback server.");
        } catch (Throwable var5) {
            SharedIAS.LOG.error("Unable to run the Microsoft authentication callback server.", var5);
            this.close();
            cf.completeExceptionally(var5);
        }

        return cf;
    }

    @Nullable
    private MicrosoftAccount auth(@NotNull BiConsumer<String, Object[]> progressHandler, @Nullable String query) throws Exception {
        SharedIAS.LOG.info("Authenticating...");
        if (query == null) {
            throw new NullPointerException("query=null");
        } else if (query.equals("error=access_denied&error_description=The user has denied access to the scope requested by the client application.")) {
            return null;
        } else if (!query.startsWith("code=")) {
            throw new IllegalStateException("query=" + query);
        } else {
            SharedIAS.LOG.info("Step: codeToToken.");
            progressHandler.accept("ias.loginGui.microsoft.progress", new Object[]{"codeToToken"});
            Entry<String, String> authRefreshTokens = Auth.codeToToken(query.replace("code=", ""));
            String refreshToken = (String)authRefreshTokens.getValue();
            SharedIAS.LOG.info("Step: authXBL.");
            progressHandler.accept("ias.loginGui.microsoft.progress", new Object[]{"authXBL"});
            String xblToken = Auth.authXBL((String)authRefreshTokens.getKey());
            SharedIAS.LOG.info("Step: authXSTS.");
            progressHandler.accept("ias.loginGui.microsoft.progress", new Object[]{"authXSTS"});
            Entry<String, String> xstsTokenUserhash = Auth.authXSTS(xblToken);
            SharedIAS.LOG.info("Step: authMinecraft.");
            progressHandler.accept("ias.loginGui.microsoft.progress", new Object[]{"authMinecraft"});
            String accessToken = Auth.authMinecraft((String)xstsTokenUserhash.getValue(), (String)xstsTokenUserhash.getKey());
            SharedIAS.LOG.info("Step: getProfile.");
            progressHandler.accept("ias.loginGui.microsoft.progress", new Object[]{"getProfile"});
            Entry<UUID, String> profile = Auth.getProfile(accessToken);
            SharedIAS.LOG.info("Authenticated.");
            return new MicrosoftAccount((String)profile.getValue(), accessToken, refreshToken, (UUID)profile.getKey());
        }
    }

    public void close() {
        try {
            if (this.server != null) {
                this.server.stop(0);
                SharedIAS.LOG.info("Stopped Microsoft authentication callback server.");
            }
        } catch (Throwable var2) {
            SharedIAS.LOG.error("Unable to stop the Microsoft authentication callback server.", var2);
        }

    }
}
