package ws.temple.conway.rules;

public interface Rules {
	
	/**
	 * Returns true iff a dead cell with the given number of neighbors
	 * should become active in the next generation.
	 * 
	 * @param neighbors
	 * @return
	 */
	boolean checkBirth(int neighbors);
	
	/**
	 * Returns true iff a living cell with the given number of neighbors should
	 * remain active in the next generation. 
	 * 
	 * @param neighbors
	 * @return
	 */
	boolean checkSurvival(int neighbors);

}
