package frontieres;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class IterativeDeepening extends TimerTask implements AlgoFrontieres {

	private class Couple<T1, T2> {
		public T1 a;
		public T2 b;
		public Couple(T1 a, T2 b) {
			this.a = a;
			this.b = b;
		}
		public String toString() {
			return "(a=" + a.toString() + ", b=" + b.toString() + ")";
		}
	}

	private Hashtable<String, ArrayList<CoupFrontieres>> orderedMoves;
	private ArrayList<Couple<CoupFrontieres, Float>> couplesCoupsHeur;
	private int timeLimit, profMax;
	private HeuristiqueFrontieres heuristique;
	private int leaves, nodes;
	private long startTime, elapsed;
	private Timer timer;
	private boolean coupeRacine, discard;
	
	// TODO :
	// simplification màj meilleur coup (utiliser dernier et avant-dernier retours seulement)
	// structure d'arbre avec couples (coup, heuristique) dans la table

	public IterativeDeepening(HeuristiqueFrontieres heuristique, int timeLimit) {
		this.heuristique = heuristique;
		this.timeLimit = timeLimit;
		timer = new Timer();
		timer.schedule(this, 0, 10);
		initHT();
	}

	public void run() {
		elapsed = (System.nanoTime() - startTime) / 1000000;
	}

	public void initHT() {
		orderedMoves = new Hashtable<String, ArrayList<CoupFrontieres>>();
	}

	public void showStat() {
		System.out.println("HashTable size = " + orderedMoves.size() + ", leaves = " + leaves + ", nodes = " + nodes);
	}

	public CoupFrontieres meilleurCoup(PlateauFrontieres board) {
		leaves = nodes = 0;
		orderedMoves.clear();
		couplesCoupsHeur = new ArrayList<Couple<CoupFrontieres, Float>>();
		Couple<CoupFrontieres, Float> retour = null;
		profMax = 2; // on commence à 2 minimum
		startTime = System.nanoTime();
		elapsed = 0;
		while(elapsed < timeLimit) {
			retour = search(board);
			// System.out.println("\tBest move : " + retour.a);
			if(retour.b == HeuristiqueFrontieres.MAX_HEUR || retour.b == HeuristiqueFrontieres.MIN_HEUR || retour.b == HeuristiqueFrontieres.TIE_HEUR)
				return retour.a; // issue de la partie fixée : on arrête l'itération, coup renvoyé directement
			++ profMax;
		}
		// showStat();
		// on cherche le meilleur coup trouvé
		CoupFrontieres meilleur = couplesCoupsHeur.get(0).a;
		float meilleureHeuristique = couplesCoupsHeur.get(0).b;
		for(Couple<CoupFrontieres, Float> cch : couplesCoupsHeur) {
			if(cch.b > meilleureHeuristique) {
				meilleureHeuristique = cch.b;
				meilleur = cch.a;
			}
		}
		// System.out.println("Liste finale des coups :");
		// System.out.println(couplesCoupsHeur);
		// System.out.println("Best move : " + meilleur);
		// System.out.println("Total time : " + elapsed);
		return meilleur;
	}

	private Couple<CoupFrontieres, Float> search(PlateauFrontieres board) {
		// System.out.println("----- Iteration for depth " + profMax + " : ");
		String hKey = board.getHashKey();
		ArrayList<CoupFrontieres> cp = orderedMoves.get(hKey);
		ArrayList<CoupFrontieres> ordered = new ArrayList<CoupFrontieres>();
		if(cp == null) { // si pas encore de clé pour ce plateau
			List<CoupFrontieres> all = board.coupsPossibles();
			Collections.shuffle(all);
			cp = (ArrayList<CoupFrontieres>) all;
		}
		float alpha = HeuristiqueFrontieres.MIN_HEUR;
		float beta = HeuristiqueFrontieres.MAX_HEUR;
		float newAlpha;
		CoupFrontieres meilleur = cp.get(0);
		PlateauFrontieres newBoard;
		int i = 0;
		for(CoupFrontieres coup : cp) {
			++ i;
			newBoard = board.copy();
			newBoard.joue(newBoard.getCurrent(), coup);
			coupeRacine = discard = false;
			newAlpha = -negAlphaBeta(newBoard, -beta, -alpha, 1, false);
			Couple<CoupFrontieres, Float> coupHeur = new Couple<CoupFrontieres, Float>(coup, newAlpha);
			if(coupeRacine) // coupe à la racine : coup à rejeter (on met son heuristique au minimum)
				coupHeur.b = HeuristiqueFrontieres.MIN_HEUR;
			if(!discard) { // coup exploré entièrement : màj de son heuristique
				boolean dejaCalc = false;
				for(Couple<CoupFrontieres, Float> cch : couplesCoupsHeur) {
					if(cch.a.equals(coupHeur.a)) {
						dejaCalc = true;
						cch.b = coupHeur.b;
					}
				}
				if(!dejaCalc)
					couplesCoupsHeur.add(coupHeur);
			}
			// System.out.println("\t" + coup + " : " + coupHeur.b + "; coupeRacine = " + coupeRacine + "; discard = " + discard);
			if(newAlpha > alpha) {
				ordered.add(0, coup);
				alpha = newAlpha;
				meilleur = coup;
				if(alpha == HeuristiqueFrontieres.MAX_HEUR) { // strat gagnante
					if(i < cp.size())
						ordered.addAll(cp.subList(i, cp.size()));
					orderedMoves.put(hKey, ordered);
					return new Couple<CoupFrontieres, Float>(meilleur, alpha);
				}
			}
			else if(newAlpha == alpha) {
				ordered.add(0, coup);
			}
			else {
				ordered.add(coup);
			}
			if(elapsed >= timeLimit) {
				break;
			}
		}
		orderedMoves.put(hKey, ordered);
		return new Couple<CoupFrontieres, Float>(meilleur, alpha);
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
			ArrayList<CoupFrontieres> cp = orderedMoves.get(hKey);
			ArrayList<CoupFrontieres> ordered = new ArrayList<CoupFrontieres>();
			if(cp == null) // pas de clé pour ce plateau
				cp = board.coupsPossibles();
			PlateauFrontieres newBoard;
			float newAlpha;
			int i = 0;
			for(CoupFrontieres coup : cp) {
				++ i;
				newBoard = board.copy();
				newBoard.joue(newBoard.getCurrent(), coup);
				newAlpha = -negAlphaBeta(newBoard, -beta, -alpha, depth+1, !even);
				if(newAlpha > alpha) {
					alpha = newAlpha;
					ordered.add(0, coup);
				}
				else if(newAlpha == alpha) {
					ordered.add(0, coup);
				}
				else {
					ordered.add(coup);
				}
				if(alpha >= beta) { // coupe
					if(depth == 1) // si on coupe à la racine : coup à rejeter
						coupeRacine = true;
					if(i < cp.size())
						ordered.addAll(cp.subList(i, cp.size()));
					orderedMoves.put(hKey, ordered);
					return alpha;
				}
				else if(elapsed >= timeLimit) {
					if(i < cp.size()) // coup pas exploré entièrement
						discard = true; // pas de màj sur l'heuristique de ce coup
					return alpha;
				}
			}
			orderedMoves.put(hKey, ordered);
			return alpha;
		}
	}

	public String toString() {
		return "Iterative Deepening (timeLimit = " + timeLimit + ")";
	}
}
