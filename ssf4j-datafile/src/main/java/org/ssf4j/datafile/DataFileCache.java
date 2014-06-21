package org.ssf4j.datafile;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.AbstractList;
import java.util.HashMap;
import java.util.Map;

public class DataFileCache<T> extends AbstractList<T> {

	protected DataFileDeserializer<T> wrapped;
	
	protected Map<Integer, Reference<? extends T>> cache;
	protected Map<Reference<? extends T>, Integer> reverse;
	protected ReferenceQueue<T> refq;
	
	protected int size;
	
	public DataFileCache(DataFileDeserializer<T> wrapped) {
		this.wrapped = wrapped;
		
		cache = new HashMap<Integer, Reference<? extends T>>();
		reverse = new HashMap<Reference<? extends T>, Integer>();
		refq = new ReferenceQueue<T>();
		
		size = wrapped.size();
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

	@Override
	public T set(int index, T element) {
		if(index < 0 || index >= size())
			throw new IndexOutOfBoundsException();
		T old = get(index);
		wrapped.set(index, element);
		reverse.remove(cache.remove(index));
		Reference<? extends T> ref = new SoftReference<T>(element, refq);
		cache.put(index, ref);
		reverse.put(ref, index);
		cleanup();
		return old;
	}

	@Override
	public void add(int index, T element) {
		wrapped.add(index, element);
		
		Map<Integer, Reference<? extends T>> nc = new HashMap<Integer, Reference<? extends T>>();
		Map<Reference<? extends T>, Integer> nr = new HashMap<Reference<? extends T>, Integer>();
		
		for(Map.Entry<Integer, Reference<? extends T>> e : cache.entrySet())
			nc.put(e.getKey() >= index ? e.getKey() + 1 : e.getKey(), e.getValue());
		for(Map.Entry<Reference<? extends T>, Integer> e : reverse.entrySet())
			nr.put(e.getKey(), e.getValue() >= index ? e.getValue() + 1 : e.getValue());
		
		cache = nc;
		reverse = nr;
		
		Reference<? extends T> ref = new SoftReference<T>(element, refq);
		cache.put(index, ref);
		reverse.put(ref, index);
		
		cleanup();
	}

	@Override
	public T remove(int index) {
		T old = get(index);
		wrapped.remove(index);
		
		Map<Integer, Reference<? extends T>> nc = new HashMap<Integer, Reference<? extends T>>();
		Map<Reference<? extends T>, Integer> nr = new HashMap<Reference<? extends T>, Integer>();
		
		for(Map.Entry<Integer, Reference<? extends T>> e : cache.entrySet())
			if(index != e.getKey())
				nc.put(e.getKey() > index ? e.getKey() - 1 : e.getKey(), e.getValue());
		for(Map.Entry<Reference<? extends T>, Integer> e : reverse.entrySet())
			if(index != e.getValue())
				nr.put(e.getKey(), e.getValue() > index ? e.getValue() - 1 : e.getValue());
		
		cache = nc;
		reverse = nr;
		
		cleanup();
		return old;
	}

	@Override
	public void clear() {
		wrapped.clear();
		cache.clear();
		reverse.clear();
		for(Reference<? extends T> ref = refq.poll(); ref != null; ref = refq.poll())
			;
		size = 0;
	}

}
