package ch.loewe.normal_use_client.fabricclient.account.ias.gui;
//
import ch.loewe.normal_use_client.fabricclient.account.Config;
import ch.loewe.normal_use_client.fabricclient.account.SharedIAS;
import ch.loewe.normal_use_client.fabricclient.account.account.Account;
import ch.loewe.normal_use_client.fabricclient.account.ias.IAS;
import com.mojang.authlib.yggdrasil.ProfileResult;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class AccountList extends AlwaysSelectedEntryListWidget<AccountList.AccountEntry> {
    public AccountList(MinecraftClient mc, int width, int height) {
        super(mc, width, height, 32, height - 64, 14);
    }

    public void updateAccounts(String query) {
        clearEntries();
        Config.accounts.stream()
                .filter(acc -> query.trim().isEmpty() || acc.name().toLowerCase(Locale.ROOT)
                        .startsWith(query.toLowerCase(Locale.ROOT)))
                .forEach(acc -> addEntry(new AccountEntry(acc)));
        setSelected(children().isEmpty() ? null : getEntry(0));
    }

    public void swap(int first, int second) {
        Account account = Config.accounts.get(first);
        Config.accounts.set(first, Config.accounts.get(second));
        Config.accounts.set(second, account);
        Config.save(client.runDirectory.toPath());
        AccountEntry entry = children().get(first);
        children().set(first, children().get(second));
        children().set(second, entry);
        setSelected(entry);
    }

    public class AccountEntry extends Entry<AccountList.AccountEntry> {
        static final Identifier MOVE_UP_HIGHLIGHTED_SPRITE = new Identifier("transferable_list/move_up_highlighted");
        static final Identifier MOVE_UP_SPRITE = new Identifier("transferable_list/move_up");
        static final Identifier MOVE_DOWN_HIGHLIGHTED_SPRITE = new Identifier("transferable_list/move_down_highlighted");
        static final Identifier MOVE_DOWN_SPRITE = new Identifier("transferable_list/move_down");
        private final Account account;
        private SkinTextures skin;

        public AccountEntry(Account account) {
            this.account = account;
            if (IAS.SKIN_CACHE.containsKey(account.uuid())) {
                this.skin = IAS.SKIN_CACHE.get(account.uuid());
                return;
            }
            skin = DefaultSkinHelper.getSkinTextures(account.uuid());
            CompletableFuture.supplyAsync(() -> {
                ProfileResult result = client.getSessionService().fetchProfile(account.uuid(), false);
                if (result == null) return null;
                return result.profile();
            }, SharedIAS.EXECUTOR).thenComposeAsync(profile -> {
                if (profile == null) return CompletableFuture.completedFuture(DefaultSkinHelper.getSkinTextures(account.uuid()));
                return client.getSkinProvider().fetchSkinTextures(profile);
            }, client).thenAcceptAsync(skin -> {
                this.skin = skin;
                IAS.SKIN_CACHE.put(account.uuid(), skin);
            }, client);
        }

        public Account account() {
            return account;
        }

        public SkinTextures skin() {
            return skin;
        }

        @Override
        public void render(DrawContext ctx, int i, int y, int x, int w, int h, int mx, int my, boolean hover, float delta) {
            int color = -1;
            if (client.getSession().getUsername().equals(account.name())) color = 0x00FF00;
            ctx.drawTextWithShadow(client.textRenderer, account.name(), x + 10, y + 1, color);
            Identifier tex = skin.texture();
            ctx.drawTexture(tex, x, y + 1, 8, 8, 8, 8, 64, 64); // Head
            if (client.options.isPlayerModelPartEnabled(PlayerModelPart.HAT))
                ctx.drawTexture(tex, x, y + 1, 40, 8, 8, 8, 64, 64); // Head (Overlay)
            if (getSelectedOrNull() == this) {
                boolean movableDown = i + 1 < children().size();
                boolean movableUp = i > 0;
                if (movableDown) {
                    boolean hoveredDown = mx > x + w - 16 && mx < x + w - 6 && hover;
                    ctx.drawGuiTexture(hoveredDown ? MOVE_DOWN_HIGHLIGHTED_SPRITE : MOVE_DOWN_SPRITE, x + w - 35, y - 18, 32, 32);
                }
                if (movableUp) {
                    boolean hoveredUp = mx > x + w - (movableDown ? 28 : 16) && mx < x + w - (movableDown ? 16 : 6) && hover;
                    ctx.drawGuiTexture(hoveredUp ? MOVE_UP_HIGHLIGHTED_SPRITE : MOVE_UP_SPRITE, x + w - (movableDown ? 30 : 19) - 16, y - 3, 96, 32, 32);
                }
            }
        }

        @Override
        public boolean mouseClicked(double mx, double my, int button) {
            if (button == 0 && getSelectedOrNull() == this) {
                int x = getRowLeft();
                int w = getRowWidth();
                int i = children().indexOf(this);
                boolean movableDown = i + 1 < children().size();
                boolean movableUp = i > 0;
                if (movableDown) {
                    boolean hoveredDown = mx > x + w - 16 && mx < x + w - 6;
                    if (hoveredDown) {
                        client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1F));
                        swap(i, i + 1);
                    }
                }
                if (movableUp) {
                    boolean hoveredUp = mx > x + w - (movableDown ? 28 : 16) && mx < x + w - (movableDown ? 16 : 6);
                    if (hoveredUp) {
                        client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1F));
                        swap(i, i - 1);
                    }
                }
                return true;
            }
            setSelected(this);
            return true;
        }

        @Override
        public Text getNarration() {
            return Text.literal(account.name());
        }
    }
}
