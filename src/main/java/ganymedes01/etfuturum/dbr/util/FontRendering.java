/*
 * Copied from GTNHLib (https://github.com/GTNewHorizons/GTNHLib, LGPL-3.0) for EtFuturumDbr,
 * so that Et Futurum Requiem does not require GTNHLib at runtime: GTNHLib crashes the
 * client together with OptiFine (TessellatorManager expects a static Tessellator field that
 * OptiFine turns into an instance field). Modified by Machitos (Dragon Block Resurrection).
 */
package ganymedes01.etfuturum.dbr.util;

/**
 * Copy of com.gtnewhorizon.gtnhlib.util.font.FontRendering#countVisibleChars. Without GTNHLib
 * there is no text preprocessor registered, so this is the plain version: skip every
 * section-sign formatting pair.
 */
public final class FontRendering {

	private static final char FORMATTING_CHAR = 167; // section sign

	private FontRendering() {
	}

	public static int countVisibleChars(String str) {
		if (str == null || str.isEmpty()) return 0;
		int count = 0;
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == FORMATTING_CHAR && i + 1 < str.length()) {
				i++;
			} else {
				count++;
			}
		}
		return count;
	}
}
