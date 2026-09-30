/*
 * EtFuturumDbr (Dragon Block Resurrection), LGPL-3.0 like Et Futurum Requiem.
 */
package ganymedes01.etfuturum.dbr.util;

import ganymedes01.etfuturum.configuration.configs.ConfigWorld;
import net.minecraft.world.World;

/**
 * Which worlds Et Futurum may generate in, by world folder name (the Multiverse world name),
 * from {@link ConfigWorld#dbrGenerationWorlds}. Dimension IDs are not used on purpose: on
 * Crucible they are assigned to Multiverse worlds and can change when a world is recreated.
 */
public final class DbrWorlds {

	private DbrWorlds() {
	}

	public static boolean generatesIn(World world) {
		String[] allowed = ConfigWorld.dbrGenerationWorlds;
		if (allowed == null || allowed.length == 0) return true;
		String name = name(world);
		if (name == null) return false;
		for (String s : allowed) {
			if (s != null && s.trim().equalsIgnoreCase(name)) return true;
		}
		return false;
	}

	/** Folder name of the world; falls back to the level name. */
	public static String name(World world) {
		try {
			if (world.getSaveHandler() != null && world.getSaveHandler().getWorldDirectoryName() != null) {
				return world.getSaveHandler().getWorldDirectoryName();
			}
		} catch (Throwable ignored) {
			// some mod worlds have odd save handlers: fall back
		}
		return world.getWorldInfo() == null ? null : world.getWorldInfo().getWorldName();
	}
}
