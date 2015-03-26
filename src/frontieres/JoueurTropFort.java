package frontieres;

public class JoueurTropFort implements IJoueur {
	private PlateauFrontieres plateau;
	private Joueur me;
	
	public JoueurTropFort() {
	}

	@Override
	public void initJoueur(int mycolour) {
		Joueur j1 = new Joueur("blanc");
		Joueur j2 = new Joueur("noir");
		plateau = new PlateauFrontieres(j1, j2, j1);
		
		if(mycolour == 0){
			
		}
		else{
			
		}
	}

	@Override
	public int getNumJoueur() {
		return 0;
	}

	@Override
	public String choixMouvement() {
		
	}

	@Override
	public void declareLeVainqueur(int colour) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouvementEnnemi(String coup) {
		
	}

	@Override
	public String binoName() {
		// TODO Auto-generated method stub
		return null;
	}

}
