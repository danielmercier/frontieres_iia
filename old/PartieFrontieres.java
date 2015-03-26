import java.util.ArrayList;

public class PartieFrontieres {

	public static int partiesGagneesJ1 = 0;
	public static int partiesGagneesJ2 = 0;

	private Joueur jBlanc;
	private Joueur jNoir;
	private Joueur[] lesJoueurs;
	private AlgoFrontieres AlgoJoueur[];
	private int maxProfJ1, maxProfJ2;
	private PlateauFrontieres plateauCourant;
	private ArrayList<PlateauFrontieres> histo;
	private int histoIndex, jnum;
	boolean partieFinie, dejaFinie;
	// dejaFinie : évite d'incrémenter le score en rejouant le dernier coup

	public PartieFrontieres(int mp1, int mp2) {
		maxProfJ1 = mp1;
		maxProfJ2 = mp2;

		partieFinie = false;
		dejaFinie = false;

		jBlanc = new Joueur("Blanc");
		jNoir = new Joueur("Noir");

		lesJoueurs = new Joueur[2];
		lesJoueurs[0] = jBlanc;
		lesJoueurs[1] = jNoir;
		jnum = 0; // blancs commencent

		AlgoJoueur = new AlgoFrontieres[2];
		AlgoJoueur[0] = new IterativeDeepening(new HeuristiqueFrontieres(HeuristiqueFrontieres.MODE2, jBlanc), 2000);
		// AlgoJoueur[1] = new AlphaBeta(new HeuristiqueFrontieres(HeuristiqueFrontieres.MODE2, jNoir), maxProfJ2);
		AlgoJoueur[1] = new IterativeDeepening(new HeuristiqueFrontieres(HeuristiqueFrontieres.MODE2, jNoir), 3000);
		
		plateauCourant = new PlateauFrontieres(jBlanc, jNoir, lesJoueurs[jnum]);

		histo = new ArrayList<PlateauFrontieres>();
		histo.add(plateauCourant.copy());
		histoIndex = 0;

		System.out.println("---------- Nouvelle Partie ----------");
		System.out.println(lesJoueurs[jnum] + " joue en premier.");
		System.out.println("Paramètres IA : ");
		System.out.println(lesJoueurs[0] + " : " + AlgoJoueur[0]);
		System.out.println(lesJoueurs[1] + " : " + AlgoJoueur[1]);
	}

	public void jouerMachine() {
		if(!partieFinie) {
			CoupFrontieres meilleurCoup = AlgoJoueur[jnum].meilleurCoup(plateauCourant);
			System.out.println(histoIndex + "  " + lesJoueurs[jnum] + " joue '" + meilleurCoup + "'");
			// AlgoJoueur[jnum].showStat();
			plateauCourant.joue(lesJoueurs[jnum], meilleurCoup);
			jnum = 1-jnum;
			partieFinie = plateauCourant.finDePartie();
			++ histoIndex;
			histo.add(histoIndex, plateauCourant.copy());
			int lastIndex = histo.size()-1;
			for(int i = lastIndex; i > histoIndex; i--)
				histo.remove(i); // clear former history branch
			if(partieFinie) {
				showEndMsg();
				updateScore();
				dejaFinie = true;
			}
		}
	}

	public void jouerHumain(CoupFrontieres coup) {
		if(!partieFinie) {
			if(plateauCourant.joue(lesJoueurs[jnum], coup)) {
				System.out.println(histoIndex + "  " + lesJoueurs[jnum] + " joue '" + coup + "'");
				jnum = 1-jnum;
				partieFinie = plateauCourant.finDePartie();
				++ histoIndex;
				histo.add(histoIndex, plateauCourant.copy());
				int lastIndex = histo.size()-1;
				for(int i = lastIndex; i > histoIndex; i--)
					histo.remove(i); // clear former history branch
				if(partieFinie) {
					showEndMsg();
					updateScore();
					dejaFinie = true;
				}
			}
			else {
				System.out.println(histoIndex + "  " + lesJoueurs[jnum] + " joue '" + coup + "' : coup illégal");
			}
		}
	}

	public void updateScore() {
		if(!dejaFinie) {
			if(plateauCourant.getJ1wins())
				++ partiesGagneesJ1;
			else if(plateauCourant.getJ2wins())
				++ partiesGagneesJ2;
		}
	}

	public void undo() {
		if(histoIndex > 0) {
			jnum = 1-jnum;
			-- histoIndex;
			plateauCourant = histo.get(histoIndex).copy();
			partieFinie = false;
		}
	}

	public void redo() {
		if(histoIndex < histo.size()-1) {
			jnum = 1-jnum;
			++ histoIndex;
			plateauCourant = histo.get(histoIndex).copy();
		}
	}

	public void showEndMsg() {
		if(plateauCourant.getJ1wins())
			System.out.println(lesJoueurs[0] + " gagne");
		else if(plateauCourant.getJ2wins())
			System.out.println(lesJoueurs[1] + " gagne");
		else
			System.out.println("Match nul");
	}

	public boolean getPartieFinie() {
		return partieFinie;
	}

	public PlateauFrontieres getPlateau() {
		return plateauCourant;
	}

	public ArrayList<PlateauFrontieres> getHisto() {
		return histo;
	}

	public int getMaxProfJ1() {
		return maxProfJ1;
	}

	public int getMaxProfJ2() {
		return maxProfJ2;
	}

	public Joueur getJ1() {
		return lesJoueurs[0];
	}

	public Joueur getJ2() {
		return lesJoueurs[1];
	}
}