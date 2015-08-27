package org.ssf4j.datafile.hashfile;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MessageDigestUtil {
	public static final String SHA1_DIGEST_NAME = "SHA-1";
	public static final String MD5_DIGEST_NAME = "MD5";
	
	private String digestName;
	private int length;
	
	public MessageDigestUtil() {
		this(SHA1_DIGEST_NAME);
	}
	
	public MessageDigestUtil(String digestName) {
		this.digestName = digestName;
		try {
			this.length = MessageDigest.getInstance(digestName).digest().length;
		} catch(NoSuchAlgorithmException e) {
			throw new IllegalArgumentException("No such message digest: " + digestName, e);
		}
	}
	
	public String getDigestName() {
		return digestName;
	}
	
	public int getLength() {
		return length;
	}
	
	public MessageDigest createDigest() {
		try {
			return MessageDigest.getInstance(digestName);
		} catch(NoSuchAlgorithmException e) {
			throw new Error("Digest " + digestName + " has gone missing!", e);
		}
	}
}
