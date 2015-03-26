public class HeuristiqueFrontieres {
	
	public static final int MODE1 = 0;
	public static final int MODE2 = 1;
	public static final int MODE3 = 2;
	
	public static final float MIN_HEUR = -Float.MAX_VALUE;
	public static final float MAX_HEUR = Float.MAX_VALUE;
	public static final float TIE_HEUR = -Float.MAX_VALUE+1;
	
	private int mode;
	private Joueur joueur;
	
	public HeuristiqueFrontieres(int mode, Joueur joueur) {
		this.mode = mode;
		this.joueur = joueur;
	}
	
	public float eval(PlateauFrontieres board) {
		// heuristique toujours calculée par rapport au joueur (jmax) passé au constructeur
		float h = 0;
		if(mode == MODE1) {
			h = board.getNbPrisesX(joueur) - board.getNbPrisesX(board.getOther(joueur));
			if(board.getXwins(joueur))
				h = MAX_HEUR;
			else if(board.getXwins(board.getOther(joueur)))
				h = MIN_HEUR;
			else if(board.getTie())
				h = TIE_HEUR;
		}
		else if(mode == MODE2) {
			float expAvancee = 1.2f;
			float coefPrises = 1000;
			float diffPrises = board.getNbPrisesX(joueur) - board.getNbPrisesX(board.getOther(joueur));
			float diffAvancee = board.getAvanceeX(joueur, expAvancee) - board.getAvanceeX(board.getOther(joueur), expAvancee);
			h = coefPrises * diffPrises + diffAvancee;
			if(board.getXwins(joueur))
				h = MAX_HEUR;
			else if(board.getXwins(board.getOther(joueur)))
				h = MIN_HEUR;
			else if(board.getTie())
				h = TIE_HEUR;
		}
		else if(mode == MODE3) {
			// fin de partie
		}
		return h;
	}
}
