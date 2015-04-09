// joueur avec ID + ouverture + gestion du temps

package frontieres;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class JoueurTropFort implements IJoueur {

	private static final int TOTAL_TIME = 120000; // temps total accordé
	private static final int MIN_TIMELIMIT = 500; // minimum pour timelimit
	
	private static final int ESTIM_MAJ_NB_COUPS = 40; // estimation du nombre total de demi-coups
		// 40 semble être un bon majorant, on dépasse très rarement 35.
	
	private static final String TIME_BMP = "time.bmp";
	private static final String EXP_AVANCEE_BMP = "avancee.bmp";
	private static final String COEF_PRISES_BMP = "prises.bmp";
	private static final String COEF_BLOQUEURS_BMP = "bloqueurs.bmp";
	
	// bornes inf et sup des paramètres variables (hors temps de recherche)
	// utilisés pour dé-normaliser les courbes bitmap
	private static final float MIN_EXP_AVANCEE = 1.1f;
	private static final float MAX_EXP_AVANCEE = 3f;
	private static final float MIN_COEF_PRISES = 200f;
	private static final float MAX_COEF_PRISES = 300f;
	private static final float MIN_COEF_BLOQUEURS = 0f;
	private static final float MAX_COEF_BLOQUEURS = 20f;
	
	private int[] expAvanceeEvol;
	private int[] coefPrisesEvol;
	private int[] coefBloqueursEvol;
	private int[] timeEvol;
	private int[] timeCurveSums;
	
	private int bmpHeightExpAvancee;
	private int bmpHeightCoefPrises;
	private int bmpHeightCoefBloqueurs;
	
	private float amplitudeExpAvancee;
	private float amplitudeCoefPrises;
	private float amplitudeCoefBloqueurs;
	
	private int remaining; // temps restant en millisec
	
	private PlateauFrontieres plateau;
	private String me;
	private String ennemi;
	private AlgoFrontieres algo;
	private int nbCoups;
	private Joueur mej;

	public JoueurTropFort() {
		nbCoups = 0;
		remaining = TOTAL_TIME;
		amplitudeExpAvancee = MAX_EXP_AVANCEE - MIN_EXP_AVANCEE;
		amplitudeCoefPrises = MAX_COEF_PRISES - MIN_COEF_PRISES;
		amplitudeCoefBloqueurs = MAX_COEF_BLOQUEURS - MIN_COEF_BLOQUEURS;
		readBitMapTime();
		readBitMapCoefPrises();
		readBitMapExpAvancee();
		readBitMapCoefBloqueurs();
	}

	@Override
	public void initJoueur(int mycolour) {
		Joueur j1 = new Joueur("blanc");
		Joueur j2 = new Joueur("noir");
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
		algo = new IterativeDeepening(new HeuristiqueFrontieres(HeuristiqueFrontieres.MODE2, mej));
	}

	@Override
	public int getNumJoueur() {
		return (me == "blanc") ? IJoueur.BLANC : IJoueur.NOIR;
	}

	@Override
	public String choixMouvement() {

		if(plateau.finDePartie()){
			return "xxxxx";
		}
		
		long startTime = System.nanoTime();
		
		String coup = "";
		
		if(me == "blanc" && nbCoups < Ouverture.ouvertureBlancs.length) {
			coup = Ouverture.ouvertureBlancs[nbCoups];
		}

		else if(me == "noir" && nbCoups < Ouverture.ouvertureNoirs.length) {
			coup = Ouverture.ouvertureNoirs[nbCoups];
		}

		else {
			int timeLimit = calcTimeLimit();
			float coefPrises = calcCoefPrises();
			float expAvancee = calcExpAvancee();
			float coefBloqueurs = calcCoefBloqueurs();
			algo.getHeuristique().setCoefPrises(coefPrises);
			algo.getHeuristique().setExpAvancee(expAvancee);
			algo.getHeuristique().setCoefBloqueurs(coefBloqueurs);
			
			System.out.println("NB MOVES = " + nbCoups);
			System.out.println("TIME REMAINING = " + remaining);
			System.out.println("TIME LIMIT = " + timeLimit);
			System.out.println("COEF PRISES = " + coefPrises);
			System.out.println("EXP AVANCEE = " + expAvancee);
			System.out.println("COEF BLOQUEURS = " + coefBloqueurs);
			
			coup = algo.meilleurCoup(timeLimit, plateau).toString();
		}

		plateau.play(coup, me);
		nbCoups += 1;
		remaining -= (System.nanoTime() - startTime) / 1000000;
		return coup;
	}
	
	private int calcTimeLimit() {
		int nbCoupsRestants = ESTIM_MAJ_NB_COUPS - nbCoups;
		int av = (timeEvol.length * nbCoups) / (nbCoups + nbCoupsRestants);
		
		// bidouille dans le cas très peu probable où on dépasserait ESTIM_MAJ_NB_COUPS
		while(av >= timeEvol.length) {
			av -= 1;
			nbCoupsRestants += 1;
		}
		
		int sumTR = 0;
		float step = (float) (timeEvol.length - av) / (float) nbCoupsRestants;
		for(int i = 0; i < nbCoupsRestants; i++) {
			int offset = (int) Math.floor(step * (float)i);
			sumTR += timeEvol[av+offset];
		}
		int timeLimit = (timeEvol[av] * remaining) / sumTR;
		
		// idem : cas où dépassement de ESTIM_MAJ_NB_COUPS
		// (ce qui pourrait potentiellement mener à un timeLimit négatif → crash)
		if(timeLimit < MIN_TIMELIMIT) 
			timeLimit = MIN_TIMELIMIT;
		
		return timeLimit;
	}
	
	private float calcExpAvancee() {
		int av = (expAvanceeEvol.length * nbCoups) / ESTIM_MAJ_NB_COUPS;
		if(av >= expAvanceeEvol.length) {
			av = (3 * expAvanceeEvol.length) / 4;
		}
		return MIN_EXP_AVANCEE + (amplitudeExpAvancee * expAvanceeEvol[av]) / bmpHeightExpAvancee;
	}
	
	private float calcCoefPrises() {
		int av = (coefPrisesEvol.length * nbCoups) / ESTIM_MAJ_NB_COUPS;
		if(av >= coefPrisesEvol.length) {
			av = (3 * coefPrisesEvol.length) / 4;
		}
		return MIN_COEF_PRISES + (amplitudeCoefPrises * coefPrisesEvol[av]) / bmpHeightCoefPrises;
	}
	
	private float calcCoefBloqueurs() {
		int av = (coefBloqueursEvol.length * nbCoups) / ESTIM_MAJ_NB_COUPS;
		if(av >= coefBloqueursEvol.length) {
			av = (3 * coefBloqueursEvol.length) / 4;
		}
		return MIN_COEF_BLOQUEURS + (amplitudeCoefBloqueurs * coefBloqueursEvol[av]) / bmpHeightCoefBloqueurs;
	}
	
	private void readBitMapTime() {
		BufferedImage image;
		try {
			image = ImageIO.read(getClass().getResource(TIME_BMP));
			int w = image.getWidth();
			int h = image.getHeight();
			timeEvol = new int[w];
			timeCurveSums = new int[w];
			int sum = 0;
			for(int x = 0; x < w; x++) {
				int y = h-1;
				boolean ptFound = false;
				while(y >= 0 && !ptFound) {
					int color = image.getRGB(x, y);
					ptFound = (color == Color.BLACK.getRGB());
					y -= 1;
				}
				y += 1;
				timeEvol[x] = h-y;
				sum += h-y;
			}
			timeCurveSums[0] = sum;
			for(int i = 1; i < w; i++) {
				timeCurveSums[i] = timeCurveSums[i-1] - timeEvol[i];
			}
		} catch (IOException e) {
			System.out.println("Error reading bmp file : " + TIME_BMP);
			System.exit(1);
		}
	}
	
	private void readBitMapCoefPrises() {
		BufferedImage image;
		try {
			image = ImageIO.read(getClass().getResource(COEF_PRISES_BMP));
			int w = image.getWidth();
			int h = image.getHeight();
			bmpHeightCoefPrises = h;
			coefPrisesEvol = new int[w];
			for(int x = 0; x < w; x++) {
				int y = h-1;
				boolean ptFound = false;
				while(y >= 0 && !ptFound) {
					int color = image.getRGB(x, y);
					ptFound = (color == Color.BLACK.getRGB());
					y -= 1;
				}
				y += 1;
				coefPrisesEvol[x] = h-y;
			}
		} catch (IOException e) {
			System.out.println("Error reading bmp file : " + COEF_PRISES_BMP);
			System.exit(1);
		}
	}
	
	private void readBitMapExpAvancee() {
		BufferedImage image;
		try {
			image = ImageIO.read(getClass().getResource(EXP_AVANCEE_BMP));
			int w = image.getWidth();
			int h = image.getHeight();
			bmpHeightExpAvancee = h;
			expAvanceeEvol = new int[w];
			for(int x = 0; x < w; x++) {
				int y = h-1;
				boolean ptFound = false;
				while(y >= 0 && !ptFound) {
					int color = image.getRGB(x, y);
					ptFound = (color == Color.BLACK.getRGB());
					y -= 1;
				}
				y += 1;
				expAvanceeEvol[x] = h-y;
			}
		} catch (IOException e) {
			System.out.println("Error reading bmp file : " + EXP_AVANCEE_BMP);
			System.exit(1);
		}
	}
	
	private void readBitMapCoefBloqueurs() {
		BufferedImage image;
		try {
			image = ImageIO.read(getClass().getResource(COEF_BLOQUEURS_BMP));
			int w = image.getWidth();
			int h = image.getHeight();
			bmpHeightCoefBloqueurs = h;
			coefBloqueursEvol = new int[w];
			for(int x = 0; x < w; x++) {
				int y = h-1;
				boolean ptFound = false;
				while(y >= 0 && !ptFound) {
					ptFound = (image.getRGB(x, y) == Color.BLACK.getRGB());
					y -= 1;
				}
				y += 1;
				coefBloqueursEvol[x] = h-y;
			}
		} catch (IOException e) {
			System.out.println("Error reading bmp file : " + COEF_BLOQUEURS_BMP);
			System.exit(1);
		}
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
