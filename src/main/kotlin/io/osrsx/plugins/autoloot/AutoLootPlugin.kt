package io.osrsx.plugins.autoloot

import io.osrsx.plugin.PluginSettings
import io.osrsx.plugin.isTrue
import io.osrsx.plugin.Plugin

/**
 * Picks up configured ground items within a radius. A minimal, fully SDK-authored plugin: the host's `PluginManager` discovers it via the generated
 * jar manifest (`Osrsx-Plugin-Main`), per-plugin settings via a
 * [PluginSettings], and the game API through the `Plugin` base accessors (`login`/`players`/`groundItems`).
 *
 * This is also a reference example of an **extracted osrsx plugin** — it started life as a built-in and
 * now lives in its own repo, built against the published `io.osrsx:osrsx-api` SDK with nothing but the
 * `io.osrsx.plugin` Gradle plugin. Inert until enabled from the Plugin Manager; runs concurrently with
 * any other plugin.
 */
class AutoLootPlugin : Plugin() {

    /** Per-plugin settings, persisted under config group "autoloot". */
    object Config : PluginSettings("autoloot") {
        var lootItems by itemListItem("items", "Item names", "Bones,Coins", "Ground items to pick up")
        // The pickup radius only applies while auto-pickup is on — hidden when it's off.
        var radius by intItem("radius", "Pickup radius (tiles)", 8, min = 1, max = 25, visibleIf = isTrue("pickup"))
        var pickup by boolItem("pickup", "Pick up automatically", true)
    }

    override fun settings() = Config

    override fun onLoop(): Long {
        if (!Config.pickup || !login.isLoggedIn()) return 600
        val wanted = Config.lootItems.split(",").map { it.trim().lowercase() }.filter { it.isNotEmpty() }
        if (wanted.isEmpty()) return 600
        val target = groundItems.query()
            .within(Config.radius)
            .keepIf { it.name()?.lowercase() in wanted }
            .nearest() ?: return 600
        target.interact("Take")
        return 800
    }
}
