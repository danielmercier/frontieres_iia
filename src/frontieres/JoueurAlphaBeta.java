// Joueur avec alpha beta simple

package frontieres;

public class JoueurAlphaBeta implements IJoueur {
	private PlateauFrontieres plateau;
	private String me;
	private String ennemi;
	private AlgoFrontieres algo;
	private int nbCoups; // décompte nb coups joués depuis le début de partie
	
	public JoueurAlphaBeta() {
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
		algo = new AlphaBeta(new HeuristiqueFrontieres(HeuristiqueFrontieres.MODE2, mej));
	}

	@Override
	public int getNumJoueur() {
		return (me == "blanc") ? IJoueur.BLANC : IJoueur.NOIR;
	}

	@Override
	public String choixMouvement() {
		String coup = "";
		
		if(me == "blanc" && nbCoups < Ouverture.ouvertureBlancs.length) {
			coup = Ouverture.ouvertureBlancs[nbCoups];
		}

		else if(me == "noir" && nbCoups < Ouverture.ouvertureNoirs.length) {
			coup = Ouverture.ouvertureNoirs[nbCoups];
		}

		else { // début des hostilités
			// TODO : calcul avancement jeu + temps à allouer
			coup = algo.meilleurCoup(6, plateau).toString();
		}

		plateau.play(coup, me);
		nbCoups += 1;
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
