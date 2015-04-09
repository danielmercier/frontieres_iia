package frontieres;

public class JoueurAlpha implements IJoueur {

	private int myCol;
	private PlateauFrontieres board;
	private AlgoFrontieres algo;
	
	@Override
	public void initJoueur(int mycolour) {
		myCol = mycolour;
		Joueur j1 = new Joueur("Blanc");
		Joueur j2 = new Joueur("Noir");
		Joueur moi = (mycolour == BLANC) ? j1 : j2;
		board = new PlateauFrontieres(j1, j2, j1);
		
		algo = new AlphaBeta(new HeuristiqueFrontieres(HeuristiqueFrontieres.MODE2, moi));
	}

	@Override
	public int getNumJoueur() {
		return myCol;
	}

	@Override
	public String choixMouvement() {
		CoupFrontieres coup = algo.meilleurCoup(6, board);
		board.joue(coup);
		
		return coup.toString();
	}

	@Override
	public void declareLeVainqueur(int colour) {
		
	}

	@Override
	public void mouvementEnnemi(String coup) {
		try {
			board.joue(new CoupFrontieres(coup));
		} catch (Exception e) {
			System.out.println("ERROR sur le coup");
		}
	}

	@Override
	public String binoName() {
		// TODO Auto-generated method stub
		return "AlphaBeta";
	}

}
