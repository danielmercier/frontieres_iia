package frontieres;

public class JoueurTropFort implements IJoueur {
	private PlateauFrontieres plateau;
	private String me;
	private String ennemi;
	private AlgoFrontieres algo;
	
	public JoueurTropFort() {
	}

	@Override
	public void initJoueur(int mycolour) {
		Joueur j1 = new Joueur("blanc");
		Joueur j2 = new Joueur("noir");
		Joueur mej;
		plateau = new PlateauFrontieres(j1, j2, j1);
		
		if(mycolour == IJoueur.BLANC){
			mej = j1;
			me = "blanc";
			ennemi = "noir";
		}
		else{
			mej = j2;
			me = "noir";
			ennemi = "blanc";
		}
		
		//Spécifier l'algo ici
		algo = new IterativeDeepening(new HeuristiqueFrontieres(HeuristiqueFrontieres.MODE2, mej), 2000);
	}

	@Override
	public int getNumJoueur() {
		return (me == "blanc") ? IJoueur.BLANC : IJoueur.NOIR;
	}

	@Override
	public String choixMouvement() {
		String coup = algo.meilleurCoup(plateau).toString();
		
		plateau.play(coup, me);
		return coup;
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
		plateau.play(coup, ennemi);
	}

	@Override
	public String binoName() {
		return "Mercier_Morier";
	}

}
