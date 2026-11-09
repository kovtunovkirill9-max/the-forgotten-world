package com.forgottenworld.client;

import net.fabricmc.api.ClientModInitializer;

public class ForgottenWorldClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Portal and entities have server-side behavior registered.
        // Entity renderers for MC 1.21.11 render-state API should be added for full client visuals.
    }
}
