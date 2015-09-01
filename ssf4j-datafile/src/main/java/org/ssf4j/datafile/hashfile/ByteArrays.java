package org.ssf4j.datafile.hashfile;

import java.util.Comparator;

/**
 * Utility methods for dealing with byte arrays
 * @author robin
 *
 */
public class ByteArrays {
	/**
	 * Comparator for unsigned ordering of byte arrays
	 */
	public static final Comparator<byte[]> BYTE_ARRAY_ORDER = new Comparator<byte[]>() {
		@Override
		public int compare(byte[] o1, byte[] o2) {
			int len = Math.min(o1.length, o2.length);
			for(int i = 0; i < len; i++) {
				int i1 = o1[i] & 0xFF;
				int i2 = o2[i] & 0xFF;
				if(i1 <  i2)
					return -1;
				else if(i1 > i2)
					return 1;
			}
			if(o1.length > len)
				return 1;
			else if(o2.length > len)
				return -1;
			else
				return 0;
		}
	};
	
	/**
	 * The length of a {@code long} in bytes
	 */
	public static final int LENGTH_LONG = 8;

	/**
	 * Write a {@code long} to a {@code byte[]} at an offset
	 * @param b The byte array
	 * @param off The offset
	 * @param v The {@code long} to write
	 */
	public static void toBytes(byte[] b, int off, long v) {
		b[off + 0] = (byte) (v >>> 56);
		b[off + 1] = (byte) (v >>> 48);
		b[off + 2] = (byte) (v >>> 40);
		b[off + 3] = (byte) (v >>> 32);
		b[off + 4] = (byte) (v >>> 24);
		b[off + 5] = (byte) (v >>> 16);
		b[off + 6] = (byte) (v >>> 8);
		b[off + 7] = (byte) (v >>> 0);
	}

	/**
	 * Read a {@code long} from a {@code byte[]} at an offset
	 * @param b The byte array
	 * @param off The offset
	 * @return The read {@code long}
	 */
	public static long toLong(byte[] b, int off) {
		long v = 0;
		v |= (b[off + 0] & 0xFFL) << 56;
		v |= (b[off + 1] & 0xFFL) << 48;
		v |= (b[off + 2] & 0xFFL) << 40;
		v |= (b[off + 3] & 0xFFL) << 32;
		v |= (b[off + 4] & 0xFFL) << 24;
		v |= (b[off + 5] & 0xFFL) << 16;
		v |= (b[off + 6] & 0xFFL) << 8;
		v |= (b[off + 7] & 0xFFL) << 0;
		return v;
	}
	
	private ByteArrays() {}
}
