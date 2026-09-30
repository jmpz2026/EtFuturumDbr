/*
 * Copied from GTNHLib (https://github.com/GTNewHorizons/GTNHLib, LGPL-3.0) for EtFuturumDbr,
 * so that Et Futurum Requiem does not require GTNHLib at runtime: GTNHLib crashes the
 * client together with OptiFine (TessellatorManager expects a static Tessellator field that
 * OptiFine turns into an instance field). Modified by Machitos (Dragon Block Resurrection).
 */
package ganymedes01.etfuturum.dbr.util;

/** Copy of com.gtnewhorizon.gtnhlib.util.CoordinatePacker (without the JOML overloads). */
public final class CoordinatePacker {

	private static final int SIZE_BITS_X = 26;
	private static final int SIZE_BITS_Z = SIZE_BITS_X;
	private static final int SIZE_BITS_Y = 64 - SIZE_BITS_X - SIZE_BITS_Z;

	private static final long BITS_X = (1L << SIZE_BITS_X) - 1L;
	private static final long BITS_Y = (1L << SIZE_BITS_Y) - 1L;
	private static final long BITS_Z = (1L << SIZE_BITS_Z) - 1L;

	private static final int BIT_SHIFT_X = SIZE_BITS_Y + SIZE_BITS_Z;
	private static final int BIT_SHIFT_Z = SIZE_BITS_Y;
	private static final int BIT_SHIFT_Y = 0;

	private CoordinatePacker() {
	}

	public static long pack(int x, int y, int z) {
		long l = 0L;
		l |= ((long) x & BITS_X) << BIT_SHIFT_X;
		l |= ((long) y & BITS_Y) << BIT_SHIFT_Y;
		l |= ((long) z & BITS_Z) << BIT_SHIFT_Z;
		return l;
	}

	public static int unpackX(long packed) {
		return (int) (packed << 64 - BIT_SHIFT_X - SIZE_BITS_X >> 64 - SIZE_BITS_X);
	}

	public static int unpackY(long packed) {
		return (int) (packed << 64 - SIZE_BITS_Y >> 64 - SIZE_BITS_Y);
	}

	public static int unpackZ(long packed) {
		return (int) (packed << 64 - BIT_SHIFT_Z - SIZE_BITS_Z >> 64 - SIZE_BITS_Z);
	}
}
