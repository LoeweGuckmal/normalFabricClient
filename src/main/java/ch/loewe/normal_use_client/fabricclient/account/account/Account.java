package ch.loewe.normal_use_client.fabricclient.account.account;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface Account {
    @NotNull
    UUID uuid();

    @NotNull
    String name();

    @NotNull
    CompletableFuture<Account.AuthData> login(@NotNull BiConsumer<String, Object[]> var1);

    public static class AuthData {
        public static final String MSA = "msa";
        public static final String MOJANG = "mojang";
        public static final String LEGACY = "legacy";
        private final String name;
        private final UUID uuid;
        private final String accessToken;
        private final String userType;

        public AuthData(@NotNull String name, @NotNull UUID uuid, @NotNull String accessToken, @NotNull String userType) {
            this.name = name;
            this.uuid = uuid;
            this.accessToken = accessToken;
            this.userType = userType;
        }

        @Contract(
                pure = true
        )
        @NotNull
        public String name() {
            return this.name;
        }

        @Contract(
                pure = true
        )
        @NotNull
        public UUID uuid() {
            return this.uuid;
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
        @NotNull
        public String userType() {
            return this.userType;
        }
    }
}
