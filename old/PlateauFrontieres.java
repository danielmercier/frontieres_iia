import java.util.ArrayList;

public class PlateauFrontieres {

	public static final int NB_WIN = 3;

	public static final int NB_LIGNES = 8;
	public static final int NB_COLONNES = 8;

	public static final char VIDE = '-';
	public static final char PION_J1 = '1';
	public static final char PION_J2 = '2';

	public static final int NB_PIONS_INIT_J1 = 8;
	public static final int NB_PIONS_INIT_J2 = 8;

	private char[][] plateau;

	private Joueur joueur1, joueur2;
	private Joueur current; // joueur dont c'est le tour

	private int nbPrisesJ1, nbPrisesJ2;
	private int nbCoupsJ1, nbCoupsJ2;

	private boolean j1noMove, j2noMove, j1wins, j2wins, tie;

	public PlateauFrontieres(Joueur j1, Joueur j2, Joueur curr) {
		this();
		joueur1 = j1;
		joueur2 = j2;
		current = curr;
		nbPrisesJ1 = nbPrisesJ2 = nbCoupsJ1 = nbCoupsJ2 = 0;
		j1noMove = j2noMove = tie = j1wins = j2wins = false;
		reset();
	}

	public PlateauFrontieres() {
		plateau = new char[NB_LIGNES][NB_COLONNES];
	}

	private void swapJoueur() {
		current = current.equals(joueur1) ? joueur2 : joueur1;
	}
	
	public void reset() {
		for(int j = 0; j < NB_COLONNES; j++) {
			if(j < NB_PIONS_INIT_J1)
				plateau[NB_LIGNES-1][j] = PION_J1;
			else
				plateau[NB_LIGNES-1][j] = VIDE;
		}
		for(int j = 0; j < NB_COLONNES; j++) {
			if(j < NB_PIONS_INIT_J2)
				plateau[0][j] = PION_J2;
			else
				plateau[0][j] = VIDE;
		}
		for(int i = 1; i < NB_LIGNES-1; i++) {
			for(int j = 0; j < NB_COLONNES; j++)
				plateau[i][j] = VIDE;
		}
	}

	private boolean isFree(int i, int j) {
		return plateau[i][j] == VIDE;
	}

	private boolean isEnemy(int i, int j) {
		return current.equals(joueur1) ? plateau[i][j] == PION_J2 : plateau[i][j] == PION_J1;
	}

	public ArrayList<CoupFrontieres> coupsPossibles() {
		// (toujours pour le joueur "current")
		ArrayList<CoupFrontieres> cp = new ArrayList<CoupFrontieres>();
		int nbPions, iIncrement;
		char symb;
		if(current.equals(joueur1)) {
			symb = PION_J1;
			nbPions = NB_PIONS_INIT_J1-nbPrisesJ2;
			iIncrement = -1; // aller vers le "haut" du plateau
		}
		else {
			symb = PION_J2;
			nbPions = NB_PIONS_INIT_J2-nbPrisesJ1;
			iIncrement = 1; // aller vers le "bas" du plateau
		}
		int k = 0, i = 0, j = 0;
		CoupFrontieres from, left, straight, right;
		boolean done = false;
		while(!done && i < NB_LIGNES) {
			j = 0;
			while(!done && j < NB_COLONNES) {
				if(plateau[i][j] == symb) {
					++ k;
					from = new CoupFrontieres(i, j);
					left = new CoupFrontieres(from, i + iIncrement, j-1);
					straight = new CoupFrontieres(from, i + iIncrement, j);
					right = new CoupFrontieres(from, i + iIncrement, j+1);
					if(coupValide(current, left))
						cp.add(left);
					if(coupValide(current, straight))
						cp.add(straight);
					if(coupValide(current, right))
						cp.add(right);
				}
				++ j;
				done = (k == nbPions);
			}
			++ i;
		}
		if(cp.size() == 0) { // si pas d'autre choix que passer son tour
			cp.add(CoupFrontieres.NO_MOVE);
		}
		return cp;
	}

	public boolean coupValide(Joueur who, CoupFrontieres c) {
		// "who" = vérif en mode interactif
		if(c.isNoMove()) {
			return who.equals(current) && coupsPossibles().get(0).isNoMove();
		}
		else {
			int i = c.geti();
			int j = c.getj();
			if(0 <= i && i < NB_LIGNES && 0 <= j && j < NB_COLONNES)
				return who.equals(current) && (isFree(i, j) || isEnemy(i, j));
			return false;
		}
	}
	
	public boolean joue(Joueur who, CoupFrontieres c) {
		// "who" = vérif en mode interactif
		if(coupValide(who, c)) {
			if(!c.isNoMove()) {
				int new_i = c.geti();
				int new_j = c.getj();
				if(current.equals(joueur1)) {
					j1noMove = false;
					++ nbCoupsJ1;
					if(isEnemy(new_i, new_j))
						++ nbPrisesJ1;
					plateau[new_i][new_j] = PION_J1;
				}
				else {
					j2noMove = false;
					++ nbCoupsJ2;
					if(isEnemy(new_i, new_j))
						++ nbPrisesJ2;
					plateau[new_i][new_j] = PION_J2;
				}				
				plateau[c.getFrom().geti()][c.getFrom().getj()] = VIDE;
			}
			else {
				if(current.equals(joueur1))
					j1noMove = true;
				else
					j2noMove = true;
			}
			// on regarde si fin de partie (gain ou nul)
			// si il y a gain, ce n'est pas forcément en faveur de current.
			// ex : j1 est coincé avec 2 pions en frontière, et j2 n'a plus qu'un pion qu'il envoie à la frontière
			// j2 joue son dernier coup, et c'est j1 qui gagne
			int nbFrontJ1 = nbFrontPawns(joueur1);
			int nbFrontJ2 = nbFrontPawns(joueur2);
			j1wins = (nbFrontPawns(joueur1) == NB_WIN) || (j1noMove && j2noMove && nbFrontJ1 > nbFrontJ2);
			j2wins = (nbFrontPawns(joueur2) == NB_WIN) || (j1noMove && j2noMove && nbFrontJ2 > nbFrontJ1);
			tie = j1noMove && j2noMove && nbFrontJ1 == nbFrontJ2;
			swapJoueur();
			return true;
		}
		else {
			return false;
		}
	}

	public int nbFrontPawns(Joueur who) {
		char symb = who.equals(joueur1) ? PION_J1 : PION_J2;
		int i = who.equals(joueur1) ? 0 : NB_LIGNES-1;
		int j = 0;
		int nbFront = 0;
		while(nbFront < NB_WIN && j < NB_COLONNES) {
			if(plateau[i][j] == symb)
				++ nbFront;
			++ j;
		}
		return nbFront;
	}

	public boolean finDePartie() {
		return j1wins || j2wins || tie;
	}

	public PlateauFrontieres copy() {		
		PlateauFrontieres newPlateau = new PlateauFrontieres();
		for(int i = 0; i < NB_LIGNES; i++) {
			for(int j = 0; j < NB_COLONNES; j++)
				newPlateau.plateau[i][j] = plateau[i][j];
		}
		newPlateau.joueur1 = joueur1;
		newPlateau.joueur2 = joueur2;		
		newPlateau.current = current;
		newPlateau.nbCoupsJ1 = nbCoupsJ1;
		newPlateau.nbCoupsJ2 = nbCoupsJ2;
		newPlateau.nbPrisesJ1 = nbPrisesJ1;
		newPlateau.nbPrisesJ2 = nbPrisesJ2;
		newPlateau.j1noMove = j1noMove;
		newPlateau.j2noMove = j2noMove;
		return newPlateau;
	}

	public String toString() {
		String s = "";
		for(int i = 0; i < NB_LIGNES; i++) {
			for(int j = 0; j < NB_COLONNES; j++)
				s += String.valueOf(plateau[i][j]) + " ";
			s += "\n";
		}
		return s;
	}
	
	public float getAvanceeX(Joueur who, float exp) {
		int i = 0, j = 0, k = 0, nbPions;
		char symb;
		float avancee = 0, increment;
		if(who.equals(joueur1)) {
			nbPions = NB_PIONS_INIT_J1 - nbPrisesJ2;
			symb = PION_J1;
		}
		else {
			nbPions = NB_PIONS_INIT_J2 - nbPrisesJ1;
			symb = PION_J2;
		}
		boolean done = false;
		while(!done && i < NB_LIGNES) {
			j = 0;
			while(!done && j < NB_COLONNES) {
				if(plateau[i][j] == symb) {
					++ k;
					increment = (who.equals(joueur1) ? NB_LIGNES-1-i : i);
					avancee += Math.pow(increment, exp);
				}
				++ j;
				done = (k == nbPions);
			}
			++ i;
		}
		return avancee;
	}
	
	public String getHashKey() {
		// optimisation possible : profiter d'une autre méthode pour faire ce calcul ?
		String hKey = current.toString(); // header = id du joueur courant
			// (nécessaire pour gérer le cas limite des deux joueurs bloqués)
		int i = 0, j = 0, k = 0;
		int nbPions = NB_PIONS_INIT_J1-nbPrisesJ2 + NB_PIONS_INIT_J2-nbPrisesJ1;
		boolean done = false;
		while(!done && i < NB_LIGNES) {
			j = 0;
			while(!done && j < NB_COLONNES) {
				if(plateau[i][j] == PION_J1 || plateau[i][j] == PION_J2) {
					hKey += String.valueOf(i) + String.valueOf(j) + plateau[i][j];
					++ k;
					done = (k == nbPions);
				}
				++ j;
			}
			++ i;
		}
		return hKey;
	}
	
	///////////////
	/* GETTERS : */
	///////////////


	public char[][] getCharArray() {
		return plateau;
	}
	
	public Joueur getCurrent() {
		return current;
	}
	
	public Joueur getOther(Joueur j) {
		return (j.equals(joueur1) ? joueur2 : joueur1);
	}
	
	public boolean getXwins(Joueur j) {
		return (j.equals(joueur1) ? getJ1wins() : getJ2wins());
	}
	
	public int getNbPrisesX(Joueur j) {
		return (j.equals(joueur1) ? getNbPrisesJ1() : getNbPrisesJ2());
	}

	public boolean getJ1wins() {
		return j1wins;
	}

	public boolean getJ2wins() {
		return j2wins;
	}

	public boolean getTie() {
		return tie;
	}

	public int getNbPrisesJ1() {
		return nbPrisesJ1;
	}

	public int getNbPrisesJ2() {
		return nbPrisesJ2;
	}
	
	////////////////////////////////////
	/* FONCTIONS UTILES POUR LA VUE : */
	////////////////////////////////////

	public ArrayList<CoupFrontieres> coupsPossiblesCase(int i, int j) {
		// pour faire une surbrillance sur les coups possibles à partir d'une case, dans la vue
		ArrayList<CoupFrontieres> cp = new ArrayList<CoupFrontieres>();
		if(caseJouable(i, j)) {
			int iIncrement;
			if(current.equals(joueur1))
				iIncrement = -1;
			else
				iIncrement = 1;
			CoupFrontieres from = new CoupFrontieres(i, j);
			CoupFrontieres left = new CoupFrontieres(from, i + iIncrement, j-1);
			CoupFrontieres straight = new CoupFrontieres(from, i + iIncrement, j);
			CoupFrontieres right = new CoupFrontieres(from, i + iIncrement, j+1);
			if(coupValide(current, left))
				cp.add(left);
			if(coupValide(current, straight))
				cp.add(straight);
			if(coupValide(current, right))
				cp.add(right);
		}
		return cp;
	}

	public boolean caseJouable(int i, int j) {
		if(current.equals(joueur1))
			return plateau[i][j] == PION_J1;
		else
			return plateau[i][j] == PION_J2;
	}
}
