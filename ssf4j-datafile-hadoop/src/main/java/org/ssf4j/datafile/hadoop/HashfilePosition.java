package org.ssf4j.datafile.hadoop;

import java.util.Arrays;
import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.Schema.Type;
import org.apache.avro.specific.SpecificRecordBase;
import org.ssf4j.datafile.hashfile.MessageDigestUtil;

public class HashfilePosition extends SpecificRecordBase {
	public static final Schema SCHEMA$;
	static {
		String name = HashfilePosition.class.getSimpleName();
		String space = HashfilePosition.class.getPackage().getName();
		
		SCHEMA$ = Schema.createRecord(name, "", space, false);
		List<Field> fields = Arrays.asList(
				new Field("values_path", Schema.create(Type.STRING), "", null),
				new Field("keys_path", Schema.create(Type.STRING), "", null),
				new Field("key_hash", Schema.createFixed(name + "_Hash", "", space, MessageDigestUtil.SHA1.getLength()), "", null)
				);
		SCHEMA$.setFields(fields);
	}
	
	public static Schema getClassSchema() {
		return SCHEMA$;
	}
	
	public CharSequence values_path;
	public CharSequence keys_path;
	public byte[] key_hash;
	
	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}

	@Override
	public Object get(int field) {
		switch(field) {
		case 0: return values_path;
		case 1: return keys_path;
		case 2: return key_hash;
		default: throw new IllegalArgumentException();
		}
	}

	@Override
	public void put(int field, Object value) {
		switch(field) {
		case 0: values_path = (CharSequence) value; break;
		case 1: keys_path = (CharSequence) value; break;
		case 2: key_hash = (byte[]) value; break;
		default: throw new IllegalArgumentException();
		}
	}

	public CharSequence getValuesPath() {
		return values_path;
	}

	public void setValuesPath(CharSequence valuesPath) {
		this.values_path = valuesPath;
	}

	public CharSequence getKeysPath() {
		return keys_path;
	}

	public void setKeysPath(CharSequence keysPath) {
		this.keys_path = keysPath;
	}

	public byte[] getKeyHash() {
		return key_hash;
	}

	public void setKeyHash(byte[] keyHash) {
		this.key_hash = keyHash;
	}

}
