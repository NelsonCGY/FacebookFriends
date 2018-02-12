package facebook;

import java.io.*;
import java.util.*;

/**
 * @author CNelson The main class that runs BFS, DFS and answer the questions.
 */
public class Main {

	public static void main(String[] args) throws IOException {
		String filename; // = "facebook_combined.txt";
		Friends circle;
		FriendsM circleM;
		long s, f, LT, MT, bfsLT, dfsLT, cLT, bfsMT, dfsMT, cMT;

		/* Adjacent list implementation part */
		Scanner in = new Scanner(System.in);
		while (true) {
			System.out.println("Please input file name:");
			filename = in.nextLine();
			try {
				System.out.println("\nLoading data to adjacent list...\n");
				s = System.currentTimeMillis();
				circle = new Friends(filename);
				f = System.currentTimeMillis();
				LT = f - s;
				break;
			} catch (IOException e) {
				System.out.println("Invalid input file name\n");
			}
		}
		in.close();

		s = System.currentTimeMillis();
		circle.BFS(0);
		f = System.currentTimeMillis();
		bfsLT = f - s;
		s = System.currentTimeMillis();
		circle.DFS(0);
		f = System.currentTimeMillis();
		dfsLT = f - s;

		int maxF = 0, maxL = 0;
		for (int i = 0; i < circle.people.size(); i++) {
			// System.out.println((i+1)+" "+"id: " + circle.people.get(i).ID+"
			// level: "+circle.people.get(i).level+" startT: "
			// +circle.people.get(i).startT+" finishT:
			// "+circle.people.get(i).finishT);
			maxF = Math.max(maxF, circle.people.get(i).finishT);
			maxL = Math.max(maxL, circle.people.get(i).level);
		}
		System.out.println("First time BFS and DFS from vertex 0 result:");
		System.out.printf("Total people: %d\nTotal edges: %d\n", circle.totalP, circle.totalE);
		System.out.printf("Max finish time: %d\nMax level: %d\n", maxF, maxL);

		System.out.println(
				"\n********** To ensure same situation, source nodes are selected from the head of the list instead of randomly. "
						+ "Or the results for tests and answers will definitely be random for this reason. **********");
		System.out.print("\na. ");
		circle.distance(75, 2190);
		System.out.print("\nb. ");
		circle.connected();
		System.out.print("\nc. ");
		circle.run3BFS();
		System.out.print("\nd. ");
		circle.run3DFS(10);
		System.out.print("\ne. ");
		circle.rangeCount(1912, 3);
		System.out.println();

		/* Adjacent matrix implementation part */
		System.out.println("\nLoading data to adjacent matrix...");
		s = System.currentTimeMillis();
		circleM = new FriendsM(filename, circle.totalP);
		f = System.currentTimeMillis();
		MT = f - s;

		s = System.currentTimeMillis();
		circleM.BFS(0);
		f = System.currentTimeMillis();
		bfsMT = f - s;
		s = System.currentTimeMillis();
		circleM.DFS(0);
		f = System.currentTimeMillis();
		dfsMT = f - s;

		System.out.println("\nTime comparing:(Milliseconds)");
		System.out.printf("Load file   List: %d\tMatrix: %d\n", LT, MT);
		System.out.printf("BFS         List: %d\tMatrix: %d\n", bfsLT, bfsMT);
		System.out.printf("DFS         List: %d\tMatrix: %d\n", dfsLT, dfsMT);

		System.out.println("\nThe file loading time using list is larger than matrix.\nBecause each edge we "
				+ "have to find the two vertices of the edge and check whether a vertex is already in the list.\n"
				+ "While using matrix we only need to set matrix[left][right] and matrix[right][left] to true.\n"
				+ "This is O(V^2) vs O(E) running time.");
		System.out.println("\nThe BFS and DFS and Clique time using list is all smaller than matrix.\n"
				+ "Because each time we visited a vertex's children, we only need to visit a bit number of"
				+ "the vertices (the parent's degree) using list.\nWhile using matrix we need to visit all the vertices"
				+ "to see if they are connected.\nThis is O(V+E) vs O(V^2) running time.\nThe time for clique will be much "
				+ "larger for matrix as there are much more loops for searching the connected children to find the clique member.\n"
				+ "But in some cases here the time for matrix may be even much faster. This is because there is some facts like picking "
				+ "the pivot in my algorithm.\nAs we are picking the pivot for each recursion by the middle, the time may varies"
				+ " significantly due to the time saved by exclusion of the pivot.");

		System.out.println();
		System.out.print("\nf. ");
		s = System.currentTimeMillis();
		circle.clique();
		f = System.currentTimeMillis();
		cLT = f - s;
		s = System.currentTimeMillis();
		circleM.clique();
		f = System.currentTimeMillis();
		cMT = f - s;
		System.out.printf("\nTime comparing:(Milliseconds)\nClique      List: %d\tMatrix: %d\n", cLT, cMT);
		System.out.println("\n\nEnd of running.");
	}
}
