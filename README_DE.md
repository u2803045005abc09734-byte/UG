# Strange Horizons — Minecraft Java 26.3 / Fabric

Dies ist eine **eigenständige, neu geschriebene experimentelle Worldgen-Mod**. Sie enthält keinen kopierten Java-Code, keine Klassen und keine Assets aus Unstable Generation. Sie ist ein erster, bewusst kleinerer Alternativ-Prototyp und kein identischer Port der Original-Mod.

## Enthaltene Regionen

Die Mod bearbeitet **nur neu generierte Overworld-Chunks** in diesen Bereichen. Bereits erzeugte Chunks werden absichtlich nicht neu überschrieben, damit Spielerbauten bei späteren Chunk-Ladevorgängen nicht kaputtgehen.

| Region | Mittelpunkt zum Teleportieren | Idee |
|---|---|---|
| High Plateau | `/tp @s 62000 160 0` | Hohe, wellige Stein- und Grasplateaus |
| Endless Water | `/tp @s -250000 100 0` | Breites Meer mit sandigem Meeresboden |
| Veiled Ridge | `/tp @s -100000 120 0` | Nebelartige Hügel und Felsrücken |
| Fracture Lands | `/tp @s 12550821 180 0` | Zackige, stark zerklüftete Höhen |
| Spoke Fields | `/tp @s 500000 120 0` | Radiale, terrassenartige Höhenzüge |

Die Bereiche sind vollständig neu gestaltet, nicht identisch mit Unstable Generation. Diese erste Version ändert die Oberflächenhöhe in neu generierten Chunks; sie registriert keine neuen Biome, Partikel-Nebel oder eine separate Unterwelt-Dimension.

## Bauen mit GitHub Actions

1. Lade alle Dateien aus diesem Projekt in ein neues Repository hoch, einschließlich `.github/workflows/build.yml`.
2. Öffne **Actions** und wähle **Build Strange Horizons for Minecraft 26.3**.
3. Wenn der Lauf erfolgreich ist, lade das Artefakt `strange-horizons-26.3` herunter und entpacke es.
4. Lege die enthaltene JAR in den `mods`-Ordner des Fabric-Servers.
5. Teste sie zuerst in einer neuen Testwelt und sichere bestehende Welten vorher.

## Hinweise

- Minecraft Java 26.3, Java 25, Fabric Loader 0.19.5+ und Fabric API 0.160.7+26.3.
- Nur serverseitige Installation nötig, wenn alle Spieler auf demselben Fabric-Server spielen.
- Bestehende Chunks werden nicht verändert. Für die Regionen neue Chunks generieren.
- Ungetesteter Prototyp: der GitHub-Build ist erforderlich, um API-/Compilerprobleme festzustellen.
