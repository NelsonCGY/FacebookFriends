package facebook;

import java.util.*;

/**
 * @author CNelson A class that represents a person and the list of his friends
 *         and their id. With ID, start/finish time, visit level, distance, pred
 *         BFS ID, pred DFS ID, color, and visit status.
 */
public class Man {
	public int ID;
	public int startT;
	public int finishT;
	public int level;
	public int distance;
	public int frontier;
	public int predBID;
	public int predDID;
	public int status; // status is 0 when not visited, 1 when visited and 2
						// when finished visiting.
	public ArrayList<Man> social;
	public ArrayList<Integer> idList;

	public Man(int ID) {
		this.ID = ID;
		social = new ArrayList<Man>();
		idList = new ArrayList<Integer>();
	}
}
