package ganymedes01.etfuturum.configuration.configs;

import ganymedes01.etfuturum.configuration.ConfigBase;

import java.io.File;

public class ConfigEntities extends ConfigBase {

	public static boolean enableStray;
	public static boolean enableEndermite;
	public static boolean enableVillagerZombies;
    public static boolean enableModernWither;
	public static boolean enableVillagerTurnsIntoWitch;
	public static boolean enableHusk;
	public static boolean enableShulker;
	public static boolean enableBrownMooshroom;
	public static boolean enableBabyGrowthBoost;
	public static boolean enableRabbit;
	public static boolean enableDragonRespawn;
	public static boolean enableNetherEndermen;
	public static boolean enableShearableSnowGolems;
	public static boolean enableBees;
	public static boolean enableSquidInk;
	public static boolean enableFoxes;

	static final String catHostile = "hostile";
	static final String catNeutral = "neutral";
	static final String catPassive = "passive";
	static final String catPlayer = "player";
	static final String catMisc = "misc";

	public ConfigEntities(File file) {
		super(file);
		setCategoryComment(catHostile, "Hostile entities.");
		setCategoryComment(catNeutral, "Neutral entities.");
		setCategoryComment(catPassive, "Passive entities.");
		setCategoryComment(catPlayer, "These settings effect the player directly.");
		setCategoryComment(catMisc, "Entity settings that don't fit into any other category.");

		configCats.add(getCategory(catHostile));
		configCats.add(getCategory(catNeutral));
		configCats.add(getCategory(catPassive));
		configCats.add(getCategory(catMisc));
		configCats.add(getCategory(catPlayer));
	}

	@Override
	protected void syncConfigOptions() {
		//passive
		enableRabbit = getBoolean("enableRabbits", catPassive, false, "");
		enableBrownMooshroom = getBoolean("enableBrownMooshroom", catPassive, false, "Brown mooshroom variant, the red mooshrooms turn into then when they are hit by lightning.");
		enableFoxes = getBoolean("enableFoxes", catPassive, false, "");

		//neutral
		enableBees = getBoolean("enableBees", catNeutral, true, "");

		//hostile
		enableEndermite = getBoolean("enableEndermite", catHostile, false, "Rarely spawns when the player lands from Ender Pearl throws");
		enableHusk = getBoolean("enableHusks", catHostile, false, "Desert zombie variant");
		enableStray = getBoolean("enableStrays", catHostile, false, "Tundra skeleton variant");
		enableShulker = getBoolean("enableShulker", catHostile, false, "Shell-lurking mobs from the End.");
		enableVillagerZombies = getBoolean("enableZombieVillager", catHostile, false, "");
        enableModernWither = getBoolean("enableModernWither", catHostile, false, "Introduces the modern behavior of the wither on spawning.");

		//function
		enableShearableSnowGolems = getBoolean("enableShearableSnowGolems", catMisc, false, "");
		enableBabyGrowthBoost = getBoolean("enableBabyGrowthBoost", catMisc, false, "");
		enableVillagerTurnsIntoWitch = getBoolean("enableVillagerTurnsIntoWitch", catMisc, false, "Villagers turn into Witches when struck by lightning");
		enableDragonRespawn = getBoolean("enableDragonRespawn", catMisc, false, "Crude implementation of respawning the dragon using four End crystals.");
		enableNetherEndermen = getBoolean("enableNetherEndermen", catMisc, false, "Allow endermen to rarely spawn in the Nether");
		enableSquidInk = getBoolean("enableSquidInk", catMisc, false, "Squid now produce a cloud of floating black ink particles when attacked.");
	}

}
