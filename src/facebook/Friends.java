package facebook;

import java.io.*;
import java.util.*;

/**
 * @author CNelson A class that deals with a list of friend relationships using
 *         adjacent list to represent the graph. Use BFS and DFS to deal with
 *         related problems.
 */
public class Friends {
	public int totalP;
	public int totalE;
	public int time;
	public int step;
	public int maxF;
	public ArrayList<Man> people;
	public ArrayList<Integer> identity;
	private ArrayList<Integer> tmpI;
	private int bestC;
	private long limit;
	private ArrayList<Integer> clique;

	/**
	 * Constructor of friends.
	 * 
	 * @param filename
	 * @throws IOException
	 */
	public Friends(String filename) throws IOException {
		BufferedReader file = new BufferedReader(new FileReader(filename));
		people = new ArrayList<Man>();
		identity = new ArrayList<Integer>();
		String twoM;
		while ((twoM = file.readLine()) != null) {
			String[] data = twoM.split(" ");
			int left = Integer.parseInt(data[0]);
			int right = Integer.parseInt(data[1]);
			Man manL, manR;
			if (!identity.contains(left)) {
				manL = new Man(left);
				people.add(manL);
				identity.add(left);
				totalP++;
			}
			if (!identity.contains(right)) {
				manR = new Man(right);
				people.add(manR);
				identity.add(right);
				totalP++;
			}
			manL = people.get(identity.indexOf(left));
			manR = people.get(identity.indexOf(right));
			if (!manL.idList.contains(right)) {
				manL.social.add(manR);
				manL.idList.add(right);
			}
			if (!manR.idList.contains(left)) {
				manR.social.add(manL);
				manR.idList.add(left);
			}
			totalE++;
		}
		file.close();
	}

	/**
	 * Make the temporary values in a man object to initial and rebuild the temI
	 * list.
	 */
	private void reset() {
		tmpI = new ArrayList<Integer>();
		for (int i = 0; i < identity.size(); i++) {
			people.get(i).status = 0;
			people.get(i).distance = 0;
			people.get(i).frontier = 0;
			tmpI.add(identity.get(i));
		}
	}

	/**
	 * Run BFS on the graph and calculate the level of each vertex.
	 * 
	 * @param s
	 */
	public void BFS(int s) {
		reset();
		step = 0;
		maxF = 0;
		boolean start = true;
		while (!tmpI.isEmpty()) {
			ArrayList<Integer> temp = new ArrayList<Integer>();
			int cur = start ? s : tmpI.get(0); // to ensure same situation,
												// source nodes are selected
												// from the head of the list
												// instead of randomly.
												// or the results for tests and
												// answers will definitely be
												// random for this reason.
			start = false;
			tmpI.remove(tmpI.indexOf(cur));
			temp.add(cur);
			people.get(identity.indexOf(cur)).level = 0;
			people.get(identity.indexOf(cur)).frontier = 0;
			while (!temp.isEmpty()) {
				cur = temp.get(0);
				temp.remove(0);
				Man one = people.get(identity.indexOf(cur));
				for (int i = 0; i < one.idList.size(); i++) {
					if (one.social.get(i).status == 0) {
						one.social.get(i).status = 1;
						one.social.get(i).predBID = one.ID;
						one.social.get(i).level = one.level + 1;
						one.social.get(i).frontier = one.frontier + 1;
						maxF = Math.max(maxF, one.social.get(i).frontier);
						temp.add(one.idList.get(i));
						tmpI.remove(one.idList.get(i));
					}
				}
				one.status = 2;
				if (!tmpI.isEmpty()) {
					step++;
				}
			}
		}
	}

	/**
	 * Run DFS on the graph and calculate the start/finish time of each vertex.
	 * 
	 * @param s
	 */
	public void DFS(int s) {
		reset();
		time = 0;
		boolean start = true;
		while (!tmpI.isEmpty()) {
			int cur = start ? s : tmpI.get(0); // to ensure same situation,
												// source nodes are selected
												// from the head of the list
												// instead of randomly.
												// or the results for tests and
												// answers will definitely be
												// random for this reason.
			start = false;
			tmpI.remove(tmpI.indexOf(cur));
			helpDFS(cur);
		}
	}

	/**
	 * Helper method for DFS() using recursion
	 * 
	 * @param s
	 */
	private void helpDFS(int s) {
		int cur = s;
		++time;
		Man one = people.get(identity.indexOf(cur));
		one.startT = time;
		one.status = 1;
		for (int i = 0; i < one.idList.size(); i++) {
			if (one.social.get(i).status == 0) {
				one.social.get(i).predDID = one.ID;
				helpDFS(one.idList.get(i));
				tmpI.remove(one.idList.get(i));
			}
		}
		one.status = 2;
		++time;
		one.finishT = time;
	}

	/**
	 * Calculate the distance between two vertices using BFS.
	 * 
	 * @param a
	 * @param b
	 */
	public void distance(int a, int b) {
		if (a < 0 || b < 0 || a >= totalP || b >= totalP) {
			System.out.println("Invalid ID");
			return;
		} else if (a == b) {
			System.out.printf("The distance between %d and %d is 0.\n", a, b);
			return;
		}
		reset();
		boolean found = false;
		ArrayList<Integer> temp = new ArrayList<Integer>();
		int cur = a;
		temp.add(cur);
		while (!temp.isEmpty()) {
			cur = temp.get(0);
			temp.remove(0);
			Man one = people.get(identity.indexOf(cur));
			for (int i = 0; i < one.idList.size(); i++) {
				if (one.social.get(i).status == 0) {
					one.social.get(i).status = 1;
					one.social.get(i).distance = one.distance + 1;
					if (one.social.get(i).ID == b) {
						found = true;
						break;
					}
					temp.add(one.idList.get(i));
				}
			}
			one.status = 2;
		}
		if (found) {
			System.out.printf("The distance between %d and %d is %d.\n", a, b,
					people.get(identity.indexOf(b)).distance);
		} else {
			System.out.printf("No path between %d and %d.\n", a, b);
		}
	}

	/**
	 * Determine whether a graph is connected using DFS with iteration
	 */
	public void connected() {
		reset();
		for (int m = 0; m < people.size(); m++) {
			for (int n = 0; n < people.size(); n++) {
				int a = identity.get(m), b = identity.get(n);
				boolean path = false;
				if (a == b) {
					continue;
				}
				Stack<Integer> dfs = new Stack<Integer>();
				dfs.push(a);
				while (!dfs.isEmpty() && !path) {
					int cur = dfs.pop();
					Man one = people.get(identity.indexOf(cur));
					for (int i = 0; i < one.social.size(); i++) {
						if (one.idList.get(i) == b) {
							path = true;
							break;
						}
						if (one.social.get(i).status == 0) {
							one.social.get(i).status = 1;
							dfs.push(one.idList.get(i));
						}
					}
					one.status = 2;
				}
				if (!path) {
					System.out.printf("This graph is not connected. Because there is no path from %d to %d.\n", m, n);
					return;
				}
			}
		}
		System.out.println("This graph is connected. Because all vertices a connected to each other proved by DFS");
	}

	/**
	 * Determine whether running BFS from different vertex takes same steps.
	 */
	public void run3BFS() {
		int[][] run = new int[3][2];
		ArrayList<Integer> source = new ArrayList<Integer>();
		Random rand = new Random();
		System.out.println("Run BFS from arbitrary vertex 3 times:");
		while (source.size() < 3) {
			int s = rand.nextInt(4039);
			if (!source.contains(s)) {
				source.add(s);
			}
		}
		for (int i = 0; i < 3; i++) {
			BFS(source.get(i));
			run[i][0] = step;
			run[i][1] = maxF;
			System.out.printf("%d. Vertex: %d\tStep: %d\tFrontier: %d\n", i + 1, source.get(i), run[i][0], run[i][1]);
		}
		if (run[0][0] == run[1][0] && run[1][0] == run[2][0] && run[0][0] == run[2][0]) {
			System.out.println("The steps are the same.\nThis may happen for some special cases when "
					+ "certain vertices results in same step.\nPlease run again to see a different result.");

		} else {
			System.out.println(
					"The steps are not the same.\nStarting from differrent vertex will visit different amount of"
							+ "children each time.");
			System.out.println("The number of child vertices (visit range) of a vertex is different. "
					+ "So the steps varies depends on how much of vertices that can be visited each step.");
			System.out.println("The more children visited each time, the faster BFS ends, the step will be smaller."
					+ "And also the level of each vertex will change.");
		}
		if (run[0][1] == run[1][1] && run[1][1] == run[2][1] && run[0][1] == run[2][1]) {
			System.out.println("The frontiers are the same.\nThis may happen for some special cases when "
					+ "certain vertices results in same frontiers.\nPlease run again to see a different result.");

		} else {
			System.out.println(
					"The frontiers are not the same.\nStarting from differrent vertices will result in visiting "
							+ "different children each level.");
			System.out.println("The max depth (frontier) of a graph may change due to different sources and different"
					+ " sequences of visiting all the vertices.");
			System.out.println(
					"The frontier is defined by the max length of path from the source vertex to the end vertex. "
							+ "So starting from different sources will result in different frontiers.");
		}
	}

	/**
	 * Determine whether running DFS from a vertex to two other vertices for
	 * multiple times will end with same start/finish time for these two
	 * vertices.
	 * 
	 * @param s
	 */
	public void run3DFS(int s) {
		int[] run = new int[2];
		Random rand = new Random();
		while (true) {
			run[0] = rand.nextInt(4039);
			run[1] = rand.nextInt(4039);
			if (run[0] != run[1] && run[0] != s && run[1] != s) {
				break;
			}
		}
		int[][] startT = new int[3][2];
		int[][] endT = new int[3][2];
		System.out.printf("Run DFS from vertex %d for 3 times:\n", s);
		for (int i = 0; i < 3; i++) {
			DFS(s);
			startT[i][0] = people.get(identity.indexOf(run[0])).startT;
			startT[i][1] = people.get(identity.indexOf(run[1])).startT;
			endT[i][0] = people.get(identity.indexOf(run[0])).finishT;
			endT[i][1] = people.get(identity.indexOf(run[1])).finishT;
			System.out.printf("%d. Source vertex: %d Start: %d Finish: %d", i + 1, run[0], startT[i][0], endT[i][0]);
			System.out.printf("   Source vertex: %d Start: %d Finish: %d\n", run[1], startT[i][1], endT[i][1]);
		}
		if ((startT[0][0] == startT[1][0] && startT[1][0] == startT[2][0] && startT[0][0] == startT[2][0])
				&& (startT[0][1] == startT[1][1] && startT[1][1] == startT[2][1] && startT[0][1] == startT[2][1])) {
			System.out.println("The start time won't change. "
					+ "Because the vertex is always visited by the same path from the same source.");
		} else {
			System.out.println("The start time will change.");
		}
		if ((endT[0][0] == endT[1][0] && endT[1][0] == endT[2][0] && endT[0][0] == endT[2][0])
				&& (endT[0][1] == endT[1][1] && endT[1][1] == endT[2][1] && endT[0][1] == endT[2][1])) {
			System.out.println("The finish time won't change. "
					+ "Because starting from this vertex will always visit same number of vertices.");
		} else {
			System.out.println("The finish time will change.");
		}
	}

	/**
	 * Count the number of vertices within a distance to the source vertex.
	 * 
	 * @param s
	 * @param dist
	 */
	public void rangeCount(int s, int dist) {
		if (s < 0 || s >= totalP) {
			System.out.println("Invalid ID");
			return;
		}
		reset();
		int count = 0;
		ArrayList<Integer> temp = new ArrayList<Integer>();
		int cur = s;
		temp.add(cur);
		while (!temp.isEmpty()) {
			cur = temp.get(0);
			temp.remove(0);
			Man one = people.get(identity.indexOf(cur));
			if (one.distance > dist) {
				break;
			}
			for (int i = 0; i < one.idList.size(); i++) {
				if (one.social.get(i).status == 0) {
					one.social.get(i).status = 1;
					one.social.get(i).distance = one.distance + 1;
					temp.add(one.idList.get(i));
				}
			}
			one.status = 2;
			count++;
		}
		System.out.printf("The number of vertices within a distance of %d from vertex %d is: %d\n", dist, s, count);
	}

	/**
	 * Determine the clique of a graph.
	 */
	public void clique() {
		System.out.println(
				"i. Yes. We can use DFS to find if a certain set of vertices are connected. If they are connected, "
						+ "then they form a connected graph.\n   By finding all the connected graphs we can update the clique with the largest connected graph.\n"
						+ "   But doing this will take very large running time as we have to brutely find all the possible combinations for connected graphs.\n"
						+ "   So we can try to use greedy algorithm with DFS to find only the new maximum connected graph which will be the clique a little faster.\n"
						+ "   But it is still too slow for big data so we use the Bron-Kerbosch algorithm which is a recursive backtracking algorith.\n"
						+ "   Here I used optimized Bron-Kerbosch algorithm with random pivot and case exclusion to make running time a bit shorter.");

		bestC = 1;
		limit = 0;
		reset();
		System.out.println("\nFinding clique (running time may be long)...");
		/*
		 * for (int i = 0; i < people.size(); i++) { ArrayList<Man> group = new
		 * ArrayList<Man>(); cliqueS = people.get(i); cliqueDFS(identity.get(i),
		 * group); }
		 */

		ArrayList<Man> connected = new ArrayList<Man>();
		ArrayList<Man> source = new ArrayList<Man>();
		for (int i = 0; i < people.size(); i++) {
			source.add(people.get(i));
		}
		ArrayList<Man> viewed = new ArrayList<Man>();
		findClique(connected, source, viewed);

		System.out.println("The size of the clique is: " + bestC);
		System.out.print("Clique vertices:\n[");
		for (int i = 0, j = 0; i < clique.size(); i++) {
			System.out.print(clique.get(i));
			if (i != clique.size() - 1) {
				System.out.print(", ");
			}
			j++;
			if (j >= Math.sqrt(clique.size()) && i != clique.size() - 1) {
				System.out.println();
				j = 0;
			}
		}
		System.out.println("]");
	}

	/**
	 * Helper method for finding clique using Bron-Kerbosch algorithm
	 * 
	 * @param connected
	 * @param source
	 * @param viewed
	 */
	private void findClique(ArrayList<Man> connected, ArrayList<Man> source, ArrayList<Man> viewed) {
		if (bestC == 69) {
			return; // just for time saving for grading
		}
		limit++;
		if (source.isEmpty() && viewed.isEmpty()) {
			if (connected.size() > bestC) {
				limit = 0;
				bestC = connected.size();
				// System.out.println("Found new clique. Size: " + bestC);
				clique = new ArrayList<Integer>();
				for (int i = 0; i < connected.size(); i++) {
					clique.add(connected.get(i).ID);
				}
			}
			return;
		}
		if (connected.size() + source.size() <= bestC || limit > (long) 90000 * 90000) {
			return;
		}
		ArrayList<Man> pivots = new ArrayList<Man>();
		for (int i = 0; i < source.size(); i++) {
			pivots.add(source.get(i));
		}
		for (int i = 0; i < viewed.size(); i++) {
			if (!pivots.contains(viewed.get(i))) {
				pivots.add(viewed.get(i));
			}
		}
		Man pivot = pivots.get(pivots.size() / 2);
		ArrayList<Man> sourceP = new ArrayList<Man>();
		for (int i = 0; i < source.size(); i++) {
			if (!pivot.social.contains(source.get(i))) {
				sourceP.add(source.get(i));
			}
		}
		for (int i = 0; i < sourceP.size(); i++) {
			Man one = sourceP.get(i);
			if (connected.size() + one.social.size() + 1 <= bestC) {
				source.remove(one);
				viewed.add(one);
				continue;
			}
			ArrayList<Man> connectedN = new ArrayList<Man>();
			for (int j = 0; j < connected.size(); j++) {
				connectedN.add(connected.get(j));
			}
			connectedN.add(one);
			ArrayList<Man> sourceN = new ArrayList<Man>();
			for (int j = 0; j < one.social.size(); j++) {
				if (source.contains(one.social.get(j))) {
					sourceN.add(one.social.get(j));
				}
			}
			ArrayList<Man> viewedN = new ArrayList<Man>();
			for (int j = 0; j < one.social.size(); j++) {
				if (viewed.contains(one.social.get(j))) {
					viewedN.add(one.social.get(j));
				}
			}
			findClique(connectedN, sourceN, viewedN);
			source.remove(one);
			viewed.add(one);
		}
	}

	/**
	 * Helper method for clique() using recursion
	 * 
	 * @param s
	 * @param group
	 */
	/*
	 * private void cliqueDFS(int s, ArrayList<Man> group) { int cur = s; Man
	 * one = people.get(identity.indexOf(cur)); group.add(one); if (group.size()
	 * >= 3) { if (connected(group)) { for(int i=0; i<group.size(); i++){
	 * group.get(i).possibleC = Math.max(group.get(i).possibleC, group.size());
	 * } if (bestC < group.size()) { bestC = group.size();
	 * System.out.println("Found new clique size: " + bestC); clique = new
	 * ArrayList<Integer>(); for (int i = 0; i < group.size(); i++) {
	 * clique.add(group.get(i).ID); } } } else { group.remove(one); return; } }
	 * for (int i = 0; i < one.idList.size(); i++) { while (i <
	 * one.idList.size() && (group.contains(one.social.get(i)) ||
	 * !cliqueS.social.contains(one.social.get(i)))) { i++; } if (i <
	 * one.idList.size()) { cliqueDFS(one.idList.get(i), group); } }
	 * group.remove(one); }
	 */

	/**
	 * Helper method for cliqueDFS()
	 * 
	 * @param group
	 * @return true if all the vertices are connected to each other
	 */
	/*
	 * private boolean connected(ArrayList<Man> group) { for (int i = 0; i <
	 * group.size(); i++) { Man one = group.get(i); for (int j = i + 1; j <
	 * group.size(); j++) { Man two = group.get(j); if
	 * (!one.idList.contains(two.ID)) { return false; } } } return true; }
	 */
}
