#!/usr/bin/env python3
"""Bundle the modern sounds Et Futurum needs into the mod jar.

Upstream downloads ~280 sound events from Mojang on first launch (AssetDirector). The DBR build
ships only the sounds of the new content enabled by the server defaults, so players download
nothing. This script fetches those sounds from Mojang's asset index and writes:

    src/main/resources/assets/<DOMAIN>/sounds.json   (1.7.10 format)
    src/main/resources/assets/<DOMAIN>/sounds/**.ogg

DOMAIN must match Reference.MCAssetVer. Event references ("type": "event") are expanded into
plain files. Run it again after editing EVENTS; it deletes and rewrites the whole domain folder.

    python tools/bundle_sounds.py
"""
import hashlib
import json
import shutil
import sys
import urllib.request
from pathlib import Path

MC_VERSION = "1.21.10"
DOMAIN = "minecraft_" + MC_VERSION

# New blocks enabled in DBR: (sound name, has its own place sound, has its own step sound).
# Mirrors ModSounds.CustomSound: without a place sound the break sound is used to place.
BLOCK_SOUNDS = [
    ("deepslate", True, True),
    ("deepslate_bricks", False, True),
    ("deepslate_tiles", False, True),
    ("tuff", False, True),
    ("polished_tuff", False, True),
    ("tuff_bricks", False, True),
    ("calcite", True, True),
    ("amethyst_block", True, True),
    ("small_amethyst_bud", True, False),
    ("medium_amethyst_bud", True, False),
    ("large_amethyst_bud", True, False),
    ("amethyst_cluster", True, True),
    ("bone_block", False, True),
    ("sweet_berry_bush", True, False),
    ("cherry_wood", False, True),
    ("cherry_leaves", False, True),
    ("cherry_sapling", False, True),
    ("pink_petals", False, True),
    ("bamboo_wood", False, True),
    ("bamboo", False, True),
    ("bamboo_sapling", False, False),
    ("lantern", True, True),
    ("chain", False, True),
    ("honey_block", False, True),
    ("coral_block", False, True),  # honeycomb block
]

# Other sounds of new content enabled in DBR: event -> 1.7.10 category.
EVENTS = {
    # Amethyst
    "block.amethyst_block.hit": "block",
    "block.amethyst_block.chime": "block",
    # Sweet berry bush
    "block.sweet_berry_bush.pick_berries": "player",
    # Composter, smoker, blast furnace
    "block.composter.empty": "block",
    "block.composter.fill": "block",
    "block.composter.fill_success": "block",
    "block.composter.ready": "block",
    "block.smoker.smoke": "block",
    "block.blastfurnace.fire_crackle": "block",
    # Chorus (decorative)
    "block.chorus_flower.grow": "block",
    "block.chorus_flower.death": "block",
    "item.chorus_fruit.teleport": "player",
    # Bees, hives, honey
    "entity.bee.loop": "neutral",
    "entity.bee.loop_aggressive": "neutral",
    "entity.bee.hurt": "neutral",
    "entity.bee.death": "neutral",
    "entity.bee.pollinate": "neutral",
    "entity.bee.sting": "neutral",
    "block.beehive.drip": "block",
    "block.beehive.enter": "neutral",
    "block.beehive.exit": "neutral",
    "block.beehive.work": "neutral",
    "block.beehive.shear": "player",
    "item.bottle.fill": "player",
    "item.honey_bottle.drink": "player",
    "block.honey_block.slide": "neutral",
    # Dirt path
    "item.shovel.flatten": "player",
}

ROOT = Path(__file__).resolve().parent.parent
OUT = ROOT / "src" / "main" / "resources" / "assets" / DOMAIN
MANIFEST = "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json"
OBJECTS = "https://resources.download.minecraft.net/{}/{}"


def fetch(url):
    with urllib.request.urlopen(url) as r:
        return r.read()


def all_events():
    events = dict(EVENTS)
    for name, place, step in BLOCK_SOUNDS:
        events["block.%s.break" % name] = "block"
        if place:
            events["block.%s.place" % name] = "block"
        if step:
            events["block.%s.step" % name] = "neutral"
    return events


def strip_ns(name):
    return name.split(":", 1)[1] if ":" in name else name


def expand(modern, event, seen=()):
    """Modern event -> list of 1.7.10 file entries, event references inlined."""
    if event in seen:
        sys.exit("event loop at " + event)
    out = []
    for s in modern[event]["sounds"]:
        if isinstance(s, str):
            s = {"name": s}
        name = strip_ns(s["name"])
        if s.get("type") == "event":
            for sub in expand(modern, name, seen + (event,)):
                for key in ("volume", "pitch"):
                    if key in s:
                        sub[key] = sub.get(key, 1.0) * s[key]
                if "weight" in s:
                    sub["weight"] = sub.get("weight", 1) * s["weight"]
                out.append(sub)
            continue
        entry = {"name": name}
        if s.get("volume", 1.0) != 1.0:
            entry["volume"] = min(max(s["volume"], 0.0), 1.0)
        if s.get("pitch", 1.0) != 1.0:
            entry["pitch"] = s["pitch"]
        if s.get("weight", 1) != 1:
            entry["weight"] = s["weight"]
        if s.get("stream"):
            entry["stream"] = True
        out.append(entry)
    return out


def main():
    manifest = json.loads(fetch(MANIFEST))
    version_url = next(v["url"] for v in manifest["versions"] if v["id"] == MC_VERSION)
    index_url = json.loads(fetch(version_url))["assetIndex"]["url"]
    objects = json.loads(fetch(index_url))["objects"]

    def get_object(path):
        h = objects[path]["hash"]
        data = fetch(OBJECTS.format(h[:2], h))
        if hashlib.sha1(data).hexdigest() != h:
            sys.exit("bad hash for " + path)
        return data

    modern = json.loads(get_object("minecraft/sounds.json"))

    if OUT.exists():
        shutil.rmtree(OUT)
    sounds_json = {}
    files = set()
    for event, category in sorted(all_events().items()):
        if event not in modern:
            sys.exit("not in %s sounds.json: %s" % (MC_VERSION, event))
        entries = expand(modern, event)
        sounds_json[event] = {"category": category, "sounds": entries}
        files.update(e["name"] for e in entries)

    total = 0
    for name in sorted(files):
        data = get_object("minecraft/sounds/%s.ogg" % name)
        dest = OUT / "sounds" / (name + ".ogg")
        dest.parent.mkdir(parents=True, exist_ok=True)
        dest.write_bytes(data)
        total += len(data)

    (OUT / "sounds.json").write_text(json.dumps(sounds_json, indent=2) + "\n", encoding="utf-8")
    print("%d events, %d files, %.1f KiB in %s" % (len(sounds_json), len(files), total / 1024, OUT.relative_to(ROOT)))


if __name__ == "__main__":
    main()
