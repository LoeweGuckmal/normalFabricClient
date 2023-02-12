package ch.loewe.normal_use_client.fabricclient.account.account;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class OfflineAccount implements Account {
    private final String name;
    private final UUID uuid;

    public OfflineAccount(@NotNull String name, @NotNull UUID uuid) {
        this.name = name;
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

    @NotNull
    public CompletableFuture<AuthData> login(@NotNull BiConsumer<String, Object[]> progressHandler) {
        return CompletableFuture.completedFuture(new AuthData(this.name(), this.uuid(), "0", "legacy"));
    }
}
