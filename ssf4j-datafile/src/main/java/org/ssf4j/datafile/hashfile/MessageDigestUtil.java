package org.ssf4j.datafile.hashfile;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for creating {@link MessageDigest} instances
 * @author robin
 *
 */
public class MessageDigestUtil {
	/**
	 * {@link MessageDigest} name for SHA-1 hashes
	 */
	public static final String SHA1_DIGEST_NAME = "SHA-1";
	/**
	 * {@link MessageDigest} name for MD5 hashes
	 */
	public static final String MD5_DIGEST_NAME = "MD5";
	
	/**
	 * The name of the {@link MessageDigest} to use
	 */
	private String digestName;
	/**
	 * The length of a hash produced by the named {@link MessageDigest}
	 */
	private int length;
	
	/**
	 * Create a {@link MessageDigestUtil} for the given {@link MessageDigest} name
	 * @param digestName The name of the {@link MessageDigest}
	 */
	public MessageDigestUtil(String digestName) {
		this.digestName = digestName;
		try {
			this.length = MessageDigest.getInstance(digestName).digest().length;
		} catch(NoSuchAlgorithmException e) {
			throw new IllegalArgumentException("No such message digest: " + digestName, e);
		}
	}
	
	/**
	 * Return the name of the {@link MessageDigest}
	 * @return The name
	 */
	public String getDigestName() {
		return digestName;
	}
	
	/**
	 * Return the length of a hash produced by the named {@link MessageDigest}
	 * @return The length
	 */
	public int getLength() {
		return length;
	}
	
	/**
	 * Create a new {@link MessageDigest} instance for the given name
	 * @return A new {@link MessageDigest}
	 */
	public MessageDigest createDigest() {
		try {
			return MessageDigest.getInstance(digestName);
		} catch(NoSuchAlgorithmException e) {
			throw new Error("Digest " + digestName + " has gone missing!", e);
		}
	}
}
