# osrsx-auto-loot

An auto-loot plugin for [osrsx](https://github.com/osrsx/osrsx-client). Picks up configured ground items
within a radius of your player.

This is also a reference example of an **extracted osrsx plugin**: it started life as a built-in and now
lives in its own repo, built against the published `io.osrsx:osrsx-api` SDK with nothing but the
`io.osrsx.plugin` Gradle plugin.

## What it does

Each loop it scans for the nearest ground item whose name matches your configured list and, if it's within
the pickup radius, walks the interaction and takes it. Inert until enabled from the in-game Plugin Manager,
and runs concurrently with any other plugin.

Config:

- **Item names** — comma-separated list of ground-item names to pick up (e.g. `Bones,Coins`).
- **Pickup radius (tiles)** — only take items within this Chebyshev distance of your player (1–25).
- **Pick up automatically** — master on/off for the pickup behaviour.

## Install (in-game marketplace)

Open the **Marketplace** panel in the client, search **Auto Loot**, and click **Install**. The client only
offers versions whose SDK range (`Osrsx-Api-Range`) supports your build.

## Build it yourself

```
./gradlew build          # produces build/libs/osrsx-auto-loot-<version>.jar
./gradlew installPlugin  # copies it into ~/.osrsx/plugins (the client hot-reloads it)
```

The entire build is `apply plugin: 'io.osrsx.plugin'` + the `osrsxPlugin { }` block in
[`build.gradle`](build.gradle). Applying the plugin pins the JDK-11 toolchain (auto-provisioned by foojay —
you don't need JDK 11 installed), wires the anonymous osrsx-maven SDK repo, stamps the jar manifest, and
generates `plugin.yaml` from that block.

## Dev loop (edit → save → live reload)

1. Launch the client once (from an osrsx checkout: `./gradlew :osrsx-core:runClient`).
2. Here: `./gradlew -t installPlugin` — rebuilds + reinstalls on every save; the client's directory watcher
   hot-reloads it live. Enable it from the in-game Plugin Manager.

## Publish an update

```
./gradlew publishPlugin
```

Collects the version + changelog, pushes/tags the repo, and opens the submission issue on
`osrsx/osrsx-central`. The registry CI builds and publishes it; the new version then appears in the
marketplace with its changelog.

## License

GPL-3.0 (matching the osrsx client).
