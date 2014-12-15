package org.ssf4j.datafile;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.AbstractList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImmutableListCache<T> extends AbstractList<T> {

	protected List<T> wrapped;
	
	protected Map<Integer, Reference<? extends T>> cache;
	protected Map<Reference<? extends T>, Integer> reverse;
	protected ReferenceQueue<T> refq;
	
	protected int size;
	
	public ImmutableListCache(List<T> wrapped) {
		this.wrapped = wrapped;
		
		cache = new HashMap<Integer, Reference<? extends T>>();
		reverse = new HashMap<Reference<? extends T>, Integer>();
		refq = new ReferenceQueue<T>();
		
		size = wrapped.size();
	}
	
	public List<T> getWrapped() {
		return wrapped;
	}

	protected void cleanup() {
		for(Reference<? extends T> ref = refq.poll(); ref != null; ref = refq.poll()) {
			Integer index = reverse.remove(ref);
			cache.remove(index);
		}
	}
	
	@Override
	public T get(int index) {
		if(index < 0 || index >= size())
			throw new IndexOutOfBoundsException();
		Reference<? extends T> ref = cache.get(index);
		T val;
		if(ref == null || (val = ref.get()) == null) {
			val = wrapped.get(index);
			ref = new SoftReference<T>(val, refq);
			cache.put(index, ref);
			reverse.put(ref, index);
		}
		cleanup();
		return val;
	}

	@Override
	public int size() {
		return size;
	}


}
