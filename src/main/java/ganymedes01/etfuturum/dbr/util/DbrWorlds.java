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

	/** Worlds already logged, so the answer for each world is written to the log only once. */
	private static final java.util.Set<String> LOGGED = java.util.Collections.newSetFromMap(new java.util.concurrent.ConcurrentHashMap<String, Boolean>());

	public static boolean generatesIn(World world) {
		String[] allowed = ConfigWorld.dbrGenerationWorlds;
		if (allowed == null || allowed.length == 0) return true;
		String name = name(world);
		boolean yes = false;
		if (name != null) {
			for (String s : allowed) {
				if (s != null && s.trim().equalsIgnoreCase(name)) {
					yes = true;
					break;
				}
			}
		}
		String key = name + "#" + world.provider.dimensionId;
		if (LOGGED.add(key)) {
			ganymedes01.etfuturum.core.utils.Logger.info("EtFuturumDbr: world '" + name + "' (dim "
					+ world.provider.dimensionId + ") -> generation " + (yes ? "ON" : "OFF"));
		}
		return yes;
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
