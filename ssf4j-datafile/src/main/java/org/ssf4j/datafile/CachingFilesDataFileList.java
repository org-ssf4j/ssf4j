package org.ssf4j.datafile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.ssf4j.Serialization;

/**
 * {@link FilesDataFileList} that wraps its returned lists in {@link ImmutableListCache}
 * @author robin
 *
 * @param <T>
 */
public class CachingFilesDataFileList<T> extends FilesDataFileList<T> {

	/**
	 * The list caches
	 */
	protected Map<Integer, ImmutableListCache<T>> cachingLists;
	
	/**
	 * Create a new {@link CachingFilesDataFileList}
	 * @param cache
	 * @param index
	 * @param serde
	 * @param type
	 * @throws IOException
	 * @see {@link FilesDataFileList#FilesDataFileList(File, File, Serialization, Class)}
	 */
	public CachingFilesDataFileList(File cache, File index, Serialization serde, Class<T> type) throws IOException {
		super(cache, index, serde, type);
		if(cachingLists == null)
			cachingLists = new ConcurrentHashMap<Integer, ImmutableListCache<T>>();
	}

	@Override
	protected void readIndex() throws IOException {
		super.readIndex();
		cachingLists = new ConcurrentHashMap<Integer, ImmutableListCache<T>>();
		// Add a caching list for each read DFD
		for(DataFileDeserializer<T> des : super.desers)
			cachingLists.put(cachingLists.size(), new ImmutableListCache<T>(des));
	}

	@Override
	public int append(List<T> list) throws IOException {
		int index = super.append(list);
		// Add a caching list for the new DFD
		cachingLists.put(index, new ImmutableListCache<T>(super.get(index)));
		return index;
	}

	@Override
	public List<T> get(int index) {
		// return the caching list
		return cachingLists.get(index);
	}
	
}
