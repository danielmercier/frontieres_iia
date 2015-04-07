import java.util.ArrayList;
import java.util.Collections;

public class AlphaBeta implements AlgoFrontieres {
	
	private int profMax;
	private HeuristiqueFrontieres heuristique;
	private int leaves, nodes;
	
	public AlphaBeta(HeuristiqueFrontieres heuristique, int profMax) {
		this.heuristique = heuristique;
		this.profMax = profMax;
	}
	
	public void showStat() {
		System.out.println("leaves = " + leaves + ", nodes = " + nodes);
	}

	public CoupFrontieres meilleurCoup(PlateauFrontieres board) {
		leaves = nodes = 0;
		ArrayList<CoupFrontieres> cp = (ArrayList<CoupFrontieres>) board.coupsPossibles();
		Collections.shuffle(cp);
		CoupFrontieres meilleur = cp.get(0);
		PlateauFrontieres newBoard;
		float alpha = HeuristiqueFrontieres.MIN_HEUR;
		float beta = HeuristiqueFrontieres.MAX_HEUR;
		float newAlpha;
		for(CoupFrontieres coup : cp) {
			newBoard = board.copy();
			newBoard.joue(newBoard.getCurrent(), coup);
			newAlpha = -negAlphaBeta(newBoard, -beta, -alpha, 1, false);
			if(newAlpha > alpha) {
				alpha = newAlpha;
				meilleur = coup;
				if(alpha == HeuristiqueFrontieres.MAX_HEUR) // strat gagnante
					return meilleur;
			}
		}
		return meilleur;
	}
	
	public float negAlphaBeta(PlateauFrontieres board, float alpha, float beta, int depth, boolean even) {
		if(depth == profMax || board.finDePartie()) {
			++ leaves;
			float h = heuristique.eval(board);
			if(!even)
				h = -h;
			return h;
		}
		else {
			++ nodes;
			ArrayList<CoupFrontieres> cp = board.coupsPossibles();
			PlateauFrontieres newBoard;
			float newAlpha;
			for(CoupFrontieres coup : cp) {
				newBoard = board.copy();
				newBoard.joue(newBoard.getCurrent(), coup);
				newAlpha = -negAlphaBeta(newBoard, -beta, -alpha, depth+1, !even);
				alpha = Math.max(alpha, newAlpha);
				if(alpha >= beta)
					return alpha;
			}
			return alpha;
		}
	}
	
	public String toString() {
		return "AlphaBeta (maxDepth = " + profMax + ")";
	}
}