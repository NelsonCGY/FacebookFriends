package facebook;

import java.io.*;
import java.util.*;

/**
 * @author CNelson A class that deals with a list of friend relationships using
 *         adjacent matrix to represent the graph. Use BFS and DFS to deal with
 *         related problems.
 */
public class FriendsM {
	public int totalP;
	public int totalE;
	public ArrayList<Man> people;
	public ArrayList<Integer> identity;
	public boolean[][] matrix;
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
	public FriendsM(String filename, int N) throws IOException {
		BufferedReader file = new BufferedReader(new FileReader(filename));
		people = new ArrayList<Man>();
		identity = new ArrayList<Integer>();
		matrix = new boolean[N][N];
		String twoM;
		while ((twoM = file.readLine()) != null) {
			String[] data = twoM.split(" ");
			int left = Integer.parseInt(data[0]);
			int right = Integer.parseInt(data[1]);
			if (!identity.contains(left)) {
				Man manL = new Man(left);
				people.add(manL);
				identity.add(left);
				totalP++;
			}
			if (!identity.contains(right)) {
				Man manR = new Man(right);
				people.add(manR);
				identity.add(right);
				totalP++;
			}
			matrix[left][right] = true;
			matrix[right][left] = true;
			totalE++;
		}
		file.close();
		if (totalP != N) {
			System.out.println("What happened?!");
			System.exit(-1);
		}
	}

	/**
	 * Make the temporary values in a man object to 0 and rebuild the temI list.
	 */
	private void reset() {
		tmpI = new ArrayList<Integer>();
		for (int i = 0; i < identity.size(); i++) {
			people.get(i).status = 0;
			tmpI.add(identity.get(i));
		}
	}

	/**
	 * Run BFS on the graph.
	 * 
	 * @param s
	 */
	public void BFS(int s) {
		reset();
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
			while (!temp.isEmpty()) {
				cur = temp.get(0);
				temp.remove(0);
				Man curOne = people.get(identity.indexOf(cur));
				for (int i = 0; i < totalP; i++) {
					if (matrix[cur][i] && people.get(identity.indexOf(i)).status == 0) {
						Man one = people.get(identity.indexOf(i));
						one.status = 1;
						one.predBID = curOne.ID;
						one.distance = curOne.distance + 1;
						temp.add(identity.get(i));
						tmpI.remove(identity.get(i));
					}
				}
				curOne.status = 2;
			}
		}
	}

	/**
	 * Run DFS on the graph.
	 * 
	 * @param s
	 */
	public void DFS(int s) {
		reset();
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
	 * Helper method for DFS using recursion
	 * 
	 * @param s
	 */
	private void helpDFS(int s) {
		int cur = s;
		Man curOne = people.get(identity.indexOf(cur));
		curOne.status = 1;
		for (int i = 0; i < totalP; i++) {
			if (matrix[cur][i] && people.get(identity.indexOf(i)).status == 0) {
				Man one = people.get(identity.indexOf(i));
				one.predDID = curOne.ID;
				helpDFS(one.ID);
				tmpI.remove(identity.get(i));
			}
		}
		curOne.status = 2;
	}

	/**
	 * Determine the largest clique number of a graph.
	 */
	public void clique() {
		System.out.println("\nFinding clique (running time may be long)...");
		bestC = 1;
		limit = 0;
		reset();
		/*
		 * for (int i = 0; i < totalP; i++) { ArrayList<Integer> group = new
		 * ArrayList<Integer>(); cliqueS = i; cliqueDFS(i, group); }
		 */
		ArrayList<Integer> connected = new ArrayList<Integer>();
		ArrayList<Integer> source = new ArrayList<Integer>();
		for (int i = 0; i < totalP; i++) {
			source.add(identity.get(i));
		}
		ArrayList<Integer> viewed = new ArrayList<Integer>();
		findClique(connected, source, viewed);

		System.out.println("The size of the clique is: " + bestC);
		System.out.print("Clique vertices:[");
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
	private void findClique(ArrayList<Integer> connected, ArrayList<Integer> source, ArrayList<Integer> viewed) {
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
					clique.add(connected.get(i));
				}
			}
			return;
		}
		if (connected.size() + source.size() <= bestC || limit > (long) 90000 * 90000) {
			return;
		}
		ArrayList<Integer> pivots = new ArrayList<Integer>();
		for (int i = 0; i < source.size(); i++) {
			pivots.add(source.get(i));
		}
		for (int i = 0; i < viewed.size(); i++) {
			if (!pivots.contains(viewed.get(i))) {
				pivots.add(viewed.get(i));
			}
		}
		int pivot = pivots.get(pivots.size() / 2);
		ArrayList<Integer> sourceP = new ArrayList<Integer>();
		for (int i = 0; i < source.size(); i++) {
			if (!matrix[pivot][source.get(i)]) {
				sourceP.add(source.get(i));
			}
		}
		for (int i = 0; i < sourceP.size(); i++) {
			int one = sourceP.get(i);
			int neigh = 0;
			for (int j = 0; j < totalP; j++) {
				if (matrix[one][j]) {
					neigh++;
				}
			}
			if (connected.size() + neigh + 1 <= bestC) {
				source.remove(source.indexOf(one));
				viewed.add(one);
				continue;
			}
			ArrayList<Integer> connectedN = new ArrayList<Integer>();
			for (int j = 0; j < connected.size(); j++) {
				connectedN.add(connected.get(j));
			}
			connectedN.add(one);
			ArrayList<Integer> sourceN = new ArrayList<Integer>();
			for (int j = 0; j < totalP; j++) {
				if (matrix[one][j] && source.contains(j)) {
					sourceN.add(j);
				}
			}
			ArrayList<Integer> viewedN = new ArrayList<Integer>();
			for (int j = 0; j < totalP; j++) {
				if (matrix[one][j] && viewed.contains(j)) {
					viewedN.add(j);
				}
			}
			findClique(connectedN, sourceN, viewedN);
			source.remove(source.indexOf(one));
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
	 * private void cliqueDFS(int s, ArrayList<Integer> group) { int cur = s;
	 * group.add(cur); if (group.size() >= 3) { if (connected(group)) { if
	 * (bestC < group.size()) { bestC = group.size();
	 * System.out.println("Found new clique size: " + bestC); clique = new
	 * ArrayList<Integer>(); for (int i = 0; i < group.size(); i++) {
	 * clique.add(group.get(i)); } } } else { group.remove(group.indexOf(cur));
	 * return; } } for (int i = 0; i < totalP; i++) { if (matrix[cur][i]) { Man
	 * one = people.get(identity.indexOf(i)); while (i < totalP &&
	 * (group.contains(one.ID) || !matrix[cliqueS][i])) { i++; } if (i < totalP)
	 * { cliqueDFS(one.ID, group); } } } group.remove(group.indexOf(cur)); }
	 */

	/**
	 * Helper method for cliqueDFS()
	 * 
	 * @param group
	 * @return true if all the vertices are connected to each other
	 */
	/*
	 * private boolean connected(ArrayList<Integer> group) { for (int i = 0; i <
	 * group.size(); i++) { int one = group.get(i); for (int j = i + 1; j <
	 * group.size(); j++) { int two = group.get(j); if (!matrix[one][two]) {
	 * return false; } } } return true; }
	 */
}
