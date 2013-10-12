package minmax;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import states.MyState;

public class MinMaxCache<T> {


	public static class CacheEntry<T> {
		
		public static final int TERMINAL_STATE_DEPTH = 200;

		private final T entry;
		private final int height;
		private int ttl;

		public CacheEntry(T entry, int height) {
			this.entry = entry;
			this.height = height;
			this.ttl = height;
		}

		public int getHeight() {
			return height;
		}

		public T getEntry() {
			return entry;
		}

		public boolean reduce() {
			return ttl-- > 0;
		}

		public void reset() {
			ttl = height;
		}
	}

	private Map<MyState, CacheEntry<T>> cache;

	public MinMaxCache() {
		this.cache = new HashMap<MyState, CacheEntry<T>>();
	}

	public T get(MyState state) {
		CacheEntry<T> entry = cache.get(state);
		entry.reset();
		return entry.getEntry();
	}

	public void put(MyState state, CacheEntry<T> entry) {
		if (!contains(state, entry.getHeight())) {
			cache.put(state, entry);
		}
	}

	public boolean contains(MyState state, int height) {
		if (!cache.containsKey(state)) {
			return false;
		}
		return cache.get(state).getHeight() >= height;
	}
	
	public CacheEntry<T> remove(MyState state) {
		return cache.remove(state);
	}

	public int size() {
		return cache.size();
	}

	public void clear() {
		cache.clear();
	}

	public void prune() {
		for (Iterator<Map.Entry<MyState, CacheEntry<T>>> i = cache.entrySet()
				.iterator(); i.hasNext();) {
			Map.Entry<MyState, CacheEntry<T>> entry = i.next();
			if (entry.getValue().reduce()) {
				i.remove();
			}
		}
	}
}
