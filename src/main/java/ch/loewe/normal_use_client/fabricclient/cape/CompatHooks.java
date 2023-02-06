package ch.loewe.normal_use_client.fabricclient.cape;

import com.google.common.collect.Lists;
import java.util.List;

public class CompatHooks {
    protected static final List<ICompatHooks> hooks = Lists.newArrayList();

    public CompatHooks() {
    }

    public static void addHook(ICompatHooks compatHooks) {
        hooks.add(compatHooks);
    }

    public static List<ICompatHooks> getHooks() {
        return hooks;
    }
}

