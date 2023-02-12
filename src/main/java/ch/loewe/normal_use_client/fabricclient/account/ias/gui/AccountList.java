package ch.loewe.normal_use_client.fabricclient.account.ias.gui;

import ch.loewe.normal_use_client.fabricclient.account.Config;
import ch.loewe.normal_use_client.fabricclient.account.account.Account;
import ch.loewe.normal_use_client.fabricclient.account.ias.IAS;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Locale;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class AccountList extends AlwaysSelectedEntryListWidget<AccountList.AccountEntry> {
    public AccountList(MinecraftClient mc, int width, int height) {
        super(mc, width, height, 32, height - 64, 14);
    }

    public void updateAccounts(String query) {
        this.clearEntries();
        Config.accounts.stream().filter((acc) -> {
            return query.trim().isEmpty() || acc.name().toLowerCase(Locale.ROOT).startsWith(query.toLowerCase(Locale.ROOT));
        }).forEach((acc) -> {
            this.addEntry(new AccountList.AccountEntry(acc));
        });
        this.setSelected(this.children().isEmpty() ? null : (AccountList.AccountEntry)this.getEntry(0));
    }

    public void swap(int first, int second) {
        Account account = (Account)Config.accounts.get(first);
        Config.accounts.set(first, (Account)Config.accounts.get(second));
        Config.accounts.set(second, account);
        Config.save(this.client.runDirectory.toPath());
        AccountList.AccountEntry entry = (AccountList.AccountEntry)this.children().get(first);
        this.children().set(first, (AccountList.AccountEntry)this.children().get(second));
        this.children().set(second, entry);
        this.setSelected(entry);
    }

    public class AccountEntry extends Entry<AccountList.AccountEntry> {
        private final Account account;
        private Identifier skin;
        private boolean slimSkin;

        public AccountEntry(Account account) {
            this.account = account;
            if (IAS.SKIN_CACHE.containsKey(account.uuid())) {
                this.skin = (Identifier)IAS.SKIN_CACHE.get(account.uuid());
            } else {
                this.skin = DefaultSkinHelper.getTexture(account.uuid());
                this.slimSkin = DefaultSkinHelper.getModel(account.uuid()).equalsIgnoreCase("slim");
                AccountList.this.client.getSkinProvider().loadSkin(new GameProfile(account.uuid(), account.name()), (type, loc, tex) -> {
                    if (type == Type.SKIN) {
                        this.skin = loc;
                        this.slimSkin = "slim".equalsIgnoreCase(tex.getMetadata("model"));
                        IAS.SKIN_CACHE.put(account.uuid(), loc);
                    }

                }, true);
            }
        }

        public Account account() {
            return this.account;
        }

        public Identifier skin() {
            return this.skin;
        }

        public boolean slimSkin() {
            return this.slimSkin;
        }

        public void render(MatrixStack ms, int i, int y, int x, int w, int h, int mx, int my, boolean hover, float delta) {
            int color = -1;
            if (AccountList.this.client.getSession().getUsername().equals(this.account.name())) {
                color = 65280;
            }

            DrawableHelper.drawStringWithShadow(ms, AccountList.this.client.textRenderer, this.account.name(), x + 10, y + 1, color);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, this.skin());
            Screen.drawTexture(ms, x, y + 1, 8.0F, 8.0F, 8, 8, 64, 64);
            if (AccountList.this.client.options.isPlayerModelPartEnabled(PlayerModelPart.HAT)) {
                Screen.drawTexture(ms, x, y + 1, 40.0F, 8.0F, 8, 8, 64, 64);
            }

            if (AccountList.this.getSelectedOrNull() == this) {
                RenderSystem.setShaderTexture(0, new Identifier("textures/gui/server_selection.png"));
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                boolean movableDown = i + 1 < AccountList.this.children().size();
                boolean movableUp = i > 0;
                boolean hoveredUp;
                if (movableDown) {
                    hoveredUp = mx > x + w - 16 && mx < x + w - 6 && hover;
                    Screen.drawTexture(ms, x + w - 35, y - 18, 48.0F, hoveredUp ? 32.0F : 0.0F, 32, 32, 256, 256);
                }

                if (movableUp) {
                    hoveredUp = mx > x + w - (movableDown ? 28 : 16) && mx < x + w - (movableDown ? 16 : 6) && hover;
                    Screen.drawTexture(ms, x + w - (movableDown ? 30 : 19), y - 3, 96.0F, hoveredUp ? 32.0F : 0.0F, 32, 32, 256, 256);
                }
            }

        }

        public boolean mouseClicked(double mx, double my, int button) {
            if (button == 0 && AccountList.this.getSelectedOrNull() == this) {
                int x = AccountList.this.getRowLeft();
                int w = AccountList.this.getRowWidth();
                int i = AccountList.this.children().indexOf(this);
                boolean movableDown = i + 1 < AccountList.this.children().size();
                boolean movableUp = i > 0;
                boolean hoveredUp;
                if (movableDown) {
                    hoveredUp = mx > (double)(x + w - 16) && mx < (double)(x + w - 6);
                    if (hoveredUp) {
                        AccountList.this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                        AccountList.this.swap(i, i + 1);
                    }
                }

                if (movableUp) {
                    hoveredUp = mx > (double)(x + w - (movableDown ? 28 : 16)) && mx < (double)(x + w - (movableDown ? 16 : 6));
                    if (hoveredUp) {
                        AccountList.this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                        AccountList.this.swap(i, i - 1);
                    }
                }

                return true;
            } else {
                AccountList.this.setSelected(this);
                return true;
            }
        }

        public Text getNarration() {
            return Text.literal(this.account.name());
        }
    }
}
