package io.osrsx.plugins.autoloot

import io.osrsx.plugin.ClientThreadPlugin
import io.osrsx.plugin.PluginSettings
import io.osrsx.plugin.isTrue

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
class AutoLootPlugin : ClientThreadPlugin() {

    /** Per-plugin settings, persisted under config group "autoloot". */
    object Config : PluginSettings("autoloot") {
        var lootItems by itemListItem("items", "Item names", "Bones,Coins", "Ground items to pick up")
        // The pickup radius only applies while auto-pickup is on — hidden when it's off.
        var radius by intItem("radius", "Pickup radius (tiles)", 8, min = 1, max = 25, visibleIf = isTrue("pickup"))
        var pickup by boolItem("pickup", "Pick up automatically", true)
    }

    override fun settings() = Config

    /** SENSE + DECIDE — client thread, per game tick: the ground-item query reads live, exact scene
     *  state (no snapshot, no hops). The blocking pickup click is offered as the intent and executes
     *  on the actuator drain thread; re-offers each tick keep targeting the CURRENT nearest item. */
    override fun onClientTick() {
        if (!Config.pickup || !login.isLoggedIn()) return
        val wanted = Config.lootItems.split(",").map { it.trim().lowercase() }.filter { it.isNotEmpty() }
        if (wanted.isEmpty()) return
        val target = groundItems.query()
            .within(Config.radius)
            .keepIf { it.name()?.lowercase() in wanted }
            .nearest() ?: return
        offer(0, "take ${target.name()}") { target.interact("Take") }
    }
}
