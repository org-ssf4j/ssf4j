package org.ssf4j.datafile;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListCache<T> extends ImmutableListCache<T> {

	public ListCache(List<T> wrapped) {
		super(wrapped);
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
		
		size++;
		
		cleanup();
	}

	@Override
	public T remove(int index) {
		wrapped.remove(index);

		T old = get(index);
		
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
		
		size--;
		
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
