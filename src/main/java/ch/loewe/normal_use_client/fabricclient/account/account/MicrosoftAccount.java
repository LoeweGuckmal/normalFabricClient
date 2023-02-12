package ch.loewe.normal_use_client.fabricclient.account.account;

import ch.loewe.normal_use_client.fabricclient.account.SharedIAS;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class MicrosoftAccount implements Account {
    private String name;
    private String accessToken;
    private String refreshToken;
    private UUID uuid;

    public MicrosoftAccount(@NotNull String name, @NotNull String accessToken, @NotNull String refreshToken, @NotNull UUID uuid) {
        this.name = name;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.uuid = uuid;
    }

    @NotNull
    public UUID uuid() {
        return this.uuid;
    }

    @NotNull
    public String name() {
        return this.name;
    }

    @Contract(
            pure = true
    )
    @NotNull
    public String accessToken() {
        return this.accessToken;
    }

    @Contract(
            pure = true
    )
    public String refreshToken() {
        return this.refreshToken;
    }

    @NotNull
    public CompletableFuture<AuthData> login(@NotNull BiConsumer<String, Object[]> progressHandler) {
        CompletableFuture<AuthData> cf = new CompletableFuture();
        SharedIAS.EXECUTOR.execute(() -> {
            try {
                this.refresh(progressHandler);
                cf.complete(new AuthData(this.name, this.uuid, this.accessToken, "msa"));
            } catch (Throwable var4) {
                SharedIAS.LOG.error("Unable to login/refresh Microsoft account.", var4);
                cf.completeExceptionally(var4);
            }

        });
        return cf;
    }

    private void refresh(@NotNull BiConsumer<String, Object[]> progressHandler) throws Exception {
        try {
            SharedIAS.LOG.info("Refreshing...");
            progressHandler.accept("ias.loginGui.microsoft.progress", new Object[]{"getProfile"});
            Entry<UUID, String> profile = Auth.getProfile(this.accessToken);
            SharedIAS.LOG.info("Access token is valid.");
            this.uuid = (UUID)profile.getKey();
            this.name = (String)profile.getValue();
        } catch (Exception var10) {
            try {
                SharedIAS.LOG.info("Step: refreshToken.");
                progressHandler.accept("ias.loginGui.microsoft.progress", new Object[]{"refreshToken"});
                Entry<String, String> authRefreshTokens = Auth.refreshToken(this.refreshToken);
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
                SharedIAS.LOG.info("Refreshed.");
                this.uuid = (UUID)profile.getKey();
                this.name = (String)profile.getValue();
                this.accessToken = accessToken;
                this.refreshToken = refreshToken;
            } catch (Exception var9) {
                var9.addSuppressed(var10);
                throw var9;
            }
        }

    }
}
