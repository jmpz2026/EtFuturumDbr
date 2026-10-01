# EtFuturumDbr: changes from upstream

Base: Et Futurum Requiem `2.6.59-GTNH` (GTNewHorizons). License: LGPL-3.0, same as upstream
(see `LICENSE`). Modified by Machitos (Dragon Block Resurrection). Branch: `dbr`.

## 1. No GTNHLib
GTNHLib crashes the client together with OptiFine (`IncompatibleClassChangeError: Expected
static field Tessellator.field_78394_d` in `TessellatorManager`: OptiFine turns that static field
into an instance field). Et Futurum only used three small GTNHLib utilities, copied into
`ganymedes01.etfuturum.dbr.util` (GTNHLib is LGPL-3.0 as well, origin credited in each file):
`BFSLeafDecay`, `CoordinatePacker` (without the JOML overloads) and
`FontRendering.countVisibleChars`. The fastutil collections that came with GTNHLib are now
`java.util`. `required-after:gtnhlib` and the Gradle dependency are gone.

UniMixins 0.1.23 or newer is still required (one mixin uses MixinExtras 0.5 expressions).

## 2. Generation only in listed worlds, by name
New option `dbrGenerationWorlds` (`world.cfg`, category `generation`, default `tierra`):
Et Futurum generates nothing in any other world (deepslate, ores, geodes, fossils, new stones,
trees, flowers, bee nests on trees and on grown saplings). Matched by world FOLDER name, not
dimension ID: Crucible assigns the IDs of Multiverse worlds and they can change. Empty list =
every world (upstream behaviour). Code: `dbr/util/DbrWorlds`, checked at the top of both world
generators and in the two bee nest hooks of `WorldEventHandler`.

World height is never changed (Et Futurum only reads `getHeight()`).

## 3. Different defaults
The defaults of the `Config*` classes follow the server's selection (Construction, Stations,
World, Bees; no Nether, copper, new mobs except bees, render tweaks, combat or movement changes).
The full option-by-option table with the reason of each one is kept in the server's planning
repo (`EFR-OPCIONES.md`). Also `amethystOuterBlockID` = `etfuturum:tuff` (basalt belongs to the
Nether, which is off). An existing `.cfg` still wins over these defaults.

The End blocks (`enableChorusBlocks`: purpur, end bricks, end rod, chorus) and the prismarine
blocks (`enablePrismarine`, with the sea lantern) are on, but only as decoration. Nothing generates
them and nothing produces their ingredients: ocean monuments, `enablePrismarineRecipes` and the
End are all off, and the End is not in `dbrGenerationWorlds`. Players get them from the server.

## 4. Smithing table without GUI
Decorative only: without netherite it has no recipes, and its container never checks that the
block is still there (`canInteractWith` is always `true`). The GUI comes back with the server's
forge integration, with block and distance checks.

## 5. Modern vanilla food textures
90 modern textures of vanilla food items and crop/food blocks (renamed to the 1.7.10 names), moved
here from DbrFoodMod, in `resourcepacks/vanilla_overrides/assets/minecraft/textures/`. Active
with `enableNewTextures`, like the ones upstream already ships.

## Pulling upstream fixes
```bash
git fetch upstream --tags
git merge <new upstream tag>     # on the dbr branch; conflicts are usually in Config* defaults
```
After a merge, re-run the defaults script of the planning repo (`efr-staging/aplicar_defaults.py`)
and check that no new option was added without a decision.
