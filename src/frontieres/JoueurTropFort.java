package frontieres;

public class JoueurTropFort implements IJoueur {
	private PlateauFrontieres plateau;
	private String me;
	private String ennemi;
	
	public JoueurTropFort() {
		Joueur j1 = new Joueur("blanc");
		Joueur j2 = new Joueur("noir");
		plateau = new PlateauFrontieres(j1, j2, j1);
	}

	@Override
	public void initJoueur(int mycolour) {
		
		if(mycolour == IJoueur.BLANC){
			me = "blanc";
			ennemi = "noir";
		}
		else{
			me = "noir";
			ennemi = "blanc";
		}
	}

	@Override
	public int getNumJoueur() {
		return (me == "blanc") ? IJoueur.BLANC : IJoueur.NOIR;
	}

	@Override
	public String choixMouvement() {
		// TODO
		return "";
	}

	@Override
	public void declareLeVainqueur(int colour) {
		if(colour == IJoueur.BLANC){
			if(me == "blanc"){
				System.out.println("J'AI GAGNÉ");
			}
			else{
				System.out.println("...");
			}
		}
		else{
			if(me == "noir"){
				System.out.println("J'AI GAGNÉ");
			}
			else{
				System.out.println("...");
			}
		}
	}

	@Override
	public void mouvementEnnemi(String coup) {
		plateau.play(ennemi, coup);
	}

	@Override
	public String binoName() {
		return "Mercier_Morier";
	}

}
