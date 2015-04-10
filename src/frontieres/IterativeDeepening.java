package frontieres;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class IterativeDeepening extends TimerTask implements AlgoFrontieres {

	private class Couple<T1 extends Comparable<T1>, T2> implements Comparable<Couple<T1, T2>>{
		public T1 a;
		public T2 b;
		public Couple(T1 a, T2 b) {
			this.a = a;
			this.b = b;
		}
		public String toString() {
			return "(a=" + a.toString() + ", b=" + b.toString() + ")";
		}
		@Override
		public int compareTo(Couple<T1, T2> o) {
			return -this.a.compareTo(o.a);
		}
		@Override
		public boolean equals(Object o){
			if(this == o) return true;
			if(!(o instanceof Couple)) return false;

			@SuppressWarnings("unchecked")
			Couple<T1, T2> c = (Couple<T1, T2>) o;
			if(this.a.compareTo(c.a) == 0) return true;
			else return false;
		}
	}

	//Permet d'ordonner les fils de chaque plateau pour iterative deepening
	private Hashtable<String, BinaryTree<Couple<Float, CoupFrontieres>>> orderedMoves;
	
	private int profMax, timeLimit;
	private HeuristiqueFrontieres heuristique;
	private int leaves, nodes;
	private long startTime, elapsed;
	private Timer timer;
	private boolean discard;
	private long timePassedGetting;

	public IterativeDeepening(HeuristiqueFrontieres heuristique) {
		this.heuristique = heuristique;
		timer = new Timer();
		timer.schedule(this, 0, 10);
		initHT();
	}

	public void run() {
		elapsed = (System.nanoTime() - startTime) / 1000000;
	}

	public void initHT() {
		orderedMoves = new Hashtable<String, BinaryTree<Couple<Float, CoupFrontieres>>>();
	}

	public void showStat() {
		System.out.println("HashTable size = " + orderedMoves.size() + ", leaves = " + leaves + ", nodes = " + nodes);
	}

	public CoupFrontieres meilleurCoup(int nbMilliCoup, PlateauFrontieres board) {
		timePassedGetting = 0;
		leaves = nodes = 0;
		orderedMoves.clear();
		Couple<Float, CoupFrontieres> retour = null;
		profMax = 2; // on commence à 2 minimum
		startTime = System.nanoTime();
		elapsed = 0;
		timeLimit = nbMilliCoup;

		while(elapsed < timeLimit) {
			retour = search(board);
			System.out.println("\tBest move : " + retour.b);
			if(retour.a == HeuristiqueFrontieres.MAX_HEUR || retour.a == HeuristiqueFrontieres.MIN_HEUR || retour.a == HeuristiqueFrontieres.TIE_HEUR)
				return retour.b; // issue de la partie fixée : on arrête l'itération, coup renvoyé directement
			++ profMax;
		}
		
		showStat();
		
		System.out.println("Total time : " + elapsed);
		System.out.println("ProfMax : " + profMax);
		System.out.println("Time passed getting in hashtable : " + timePassedGetting / 1000000.0);

		return retour.b;
	}

	private Couple<Float, CoupFrontieres> search(PlateauFrontieres board) {
		System.out.println("----- Iteration for depth " + profMax + " : ");
		String hKey = board.getHashKey();

		long time = System.nanoTime();
		
		BinaryTree<Couple<Float, CoupFrontieres>> tree = orderedMoves.get(hKey);
		timePassedGetting += System.nanoTime() - time;

		ArrayList<CoupFrontieres> cp = null;

		if(tree == null) { // si pas encore de clé pour ce plateau
			List<CoupFrontieres> all = board.coupsPossibles();
			Collections.shuffle(all);
			cp = (ArrayList<CoupFrontieres>) all;
			tree = new BinaryTree<Couple<Float, CoupFrontieres>>();

			time = System.nanoTime();
			orderedMoves.put(hKey, tree);
			timePassedGetting += System.nanoTime() - time;
		}
		else {
			cp = new ArrayList<CoupFrontieres>();
			for(Couple<Float, CoupFrontieres> cpl : tree){
				cp.add(cpl.b);
			}
			tree.clear();
		}

		float alpha = HeuristiqueFrontieres.MIN_HEUR;
		float beta = HeuristiqueFrontieres.MAX_HEUR;
		float newAlpha;
		boolean gagn = false;
		boolean timeout = false;
		PlateauFrontieres newBoard;

		for(CoupFrontieres coup : cp) {
			if(!gagn && !timeout){
				newBoard = board.copy();
				newBoard.joue(coup);
				discard = false;
				newAlpha = -negAlphaBeta(newBoard, -beta, -alpha, 1, false);
				Couple<Float, CoupFrontieres> coupHeur = new Couple<Float, CoupFrontieres>(newAlpha, coup);

				if(!discard) { // coup exploré entièrement : màj de son heuristique
					tree.add(coupHeur);
				}
				else{
					tree.add(new Couple<Float, CoupFrontieres>(HeuristiqueFrontieres.MIN_HEUR, coup));
				}

				if(newAlpha > alpha) {
					alpha = newAlpha;
					if(alpha == HeuristiqueFrontieres.MAX_HEUR) { // strat gagnante
						gagn = true;
					}
				}

				if(elapsed >= timeLimit) {
					timeout = true;
				}
			}
			else if(!gagn && timeout){
				//On ajoute le coup avec son heuristique de la profondeur précédente.
				tree.add(new Couple<Float, CoupFrontieres>(HeuristiqueFrontieres.MIN_HEUR, coup));
			}
			else{
				break;
			}
		}
		
		return tree.min();
	}

	private float negAlphaBeta(PlateauFrontieres board, float alpha, float beta, int depth, boolean even) {
		if(depth == profMax || board.finDePartie()) {
			++ leaves;
			float h = heuristique.eval(board);
			if(!even)
				h = -h;
			return h;
		}
		else {
			++ nodes;
			String hKey = board.getHashKey();

			long time = System.nanoTime();
			BinaryTree<Couple<Float, CoupFrontieres>> tree = orderedMoves.get(hKey);
			timePassedGetting += System.nanoTime() - time;

			ArrayList<CoupFrontieres> cp;

			if(tree == null) { // si pas encore de clé pour ce plateau
				List<CoupFrontieres> all = board.coupsPossibles();
				Collections.shuffle(all);
				cp = (ArrayList<CoupFrontieres>) all;
				tree = new BinaryTree<Couple<Float, CoupFrontieres>>();
				time = System.nanoTime();
				orderedMoves.put(hKey, tree);
				timePassedGetting += System.nanoTime() - time;
			}
			else{
				cp = new ArrayList<CoupFrontieres>();
				for(Couple<Float, CoupFrontieres> cpl : tree){
					cp.add(cpl.b);
				}
				tree.clear();
			}

			PlateauFrontieres newBoard;
			float newAlpha;
			boolean timeout = false;
			int i = 0;

			for(CoupFrontieres coup : cp) {
				if(!timeout){
					++ i;
					newBoard = board.copy();
					newBoard.joue(coup);
					newAlpha = -negAlphaBeta(newBoard, -beta, -alpha, depth+1, !even);

					if(newAlpha > alpha) {
						alpha = newAlpha;
					}

					tree.add(new Couple<Float, CoupFrontieres>(newAlpha, coup));

					if(alpha >= beta) { // coupe
						timeout = true;
					}
					else if(elapsed >= timeLimit) {
						if(i < cp.size()) // coup pas exploré entièrement
							discard = true; // pas de màj sur l'heuristique de ce coup
						timeout = true;
					}
				}
				else{
					tree.add(new Couple<Float, CoupFrontieres>((float)HeuristiqueFrontieres.MIN_HEUR, coup));
				}
			}

			return alpha;
		}
	}

	public String toString() {
		return "Iterative Deepening (timeLimit = " + timeLimit + ")";
	}

	@Override
	public HeuristiqueFrontieres getHeuristique() {
		return heuristique;
	}
}

