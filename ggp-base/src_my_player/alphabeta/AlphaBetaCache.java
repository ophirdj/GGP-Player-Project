package alphabeta;

import java.util.HashMap;
import java.util.Map;

import state.MyState;

public class AlphaBetaCache<T extends AlphaBetaEntry> {

	private Map<MyState, T> cache = new HashMap<MyState, T>();

	public boolean containsKey(MyState state, int height) {
		return cache.containsKey(state) && cache.get(state) != null
				&& cache.get(state).getHeight() >= height;
	}

	public T get(MyState state) {
		return cache.get(state);
	}

	public void put(MyState state, T entry) {
		if (!cache.containsKey(state) || (cache.get(state) == null)
				|| (cache.get(state).getHeight() <= entry.getHeight())) {
			cache.put(state, entry);
		}
	}

}
