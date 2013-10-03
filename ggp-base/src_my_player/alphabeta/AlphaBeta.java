package alphabeta;

import minmax.MinMax;

public interface AlphaBeta extends MinMax {

	
	class AlphaBetaException extends MinMaxException {
		private static final long serialVersionUID = -8084183763156956934L;
		
		public AlphaBetaException() {
			super();
		}
		
		public AlphaBetaException(String message) {
			super(message);
		}
		
	}
}
