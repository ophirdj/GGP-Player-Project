package debugging;

public class Verbose {
	public static final long HEURISTIC_GENERATOR_VERBOSE = 1 << 0;
	public static final long CURRENT_SIMULATION_VERBOSE = 1 << 1;
	public static final long MIN_MAX_VERBOSE = 1 << 2;
	public static final long UNEXPECTED_VALUE = 1 << 3;
	public static final long SIMULATOR_MIN_MAX = 1 << 4;
	
	public static final long verbose = 0;

	public static void printVerbose(String message, long verboseType){
		if ((verboseType & verbose) != 0) System.out.println(message);
	}
	
	public static void printVerboseError(String message, long verboseType){
		System.err.println(message);
	}
}
