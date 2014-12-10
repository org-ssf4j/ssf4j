package org.ssf4j.datafile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
	protected List<ImmutableListCache<T>> cachingLists;
	
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
		cachingLists = new ArrayList<ImmutableListCache<T>>();
	}

	@Override
	protected void readIndex() throws IOException {
		super.readIndex();
		// Add a caching list for each read DFD
		for(DataFileDeserializer<T> des : super.desers)
			cachingLists.add(new ImmutableListCache<T>(des));
	}

	@Override
	public synchronized int append(List<T> list) throws IOException {
		int index = super.append(list);
		// Add a caching list for the new DFD
		cachingLists.add(new ImmutableListCache<T>(super.get(index)));
		return index;
	}

	@Override
	public synchronized List<T> get(int index) {
		// return the caching list
		return cachingLists.get(index);
	}
	
}
