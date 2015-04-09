package frontieres;

import java.util.LinkedList;

public class HeuristiqueFrontieres {
	
	public static final int MODE1 = 0;
	public static final int MODE2 = 1;
	public static final int MODE3 = 2;
	
	public static final float MIN_HEUR = -Float.MAX_VALUE;
	public static final float MAX_HEUR = Float.MAX_VALUE;
	public static final float TIE_HEUR = 0.001f;
	
	private int mode;
	private Joueur joueur;
	private float expAvancee;
	private float coefPrises;
	private float coefBloqueurs;
	
	public void setExpAvancee(float ea) {
		// TODO : recalculer TIE_HEUR
		expAvancee = ea;
	}
	
	public void setCoefPrises(float cp) {
		// TODO : recalculer TIE_HEUR
		coefPrises = cp;
	}
	
	public void setCoefBloqueurs(float cb) {
		// TODO : recalculer TIE_HEUR
		coefPrises = cb;
	}
	
	public HeuristiqueFrontieres(int mode, Joueur joueur) {
		this.mode = mode;
		this.joueur = joueur;
	}
	
	//Liberté d'une piece, le nombre de case qu'elle peut parcourir sans ce faire manger, sachant que l'adv essayera de la manger.
	/*public void liberte(PlateauFrontieres board){
		ArrayList<Position> piecesMoi = board.getPieces(joueur);
		ArrayList<Position> piecesAutre = board.getPieces(board.getOther(joueur));
		
		int liberteMoi = 0;
		int liberteAutre = Integer.MAX_VALUE;
		
		int nbPossibleFrontiereMoi = 0;
		
		for(Position posMoi : piecesMoi){
			int liberteInter = Integer.MAX_VALUE;
			for(Position posAutre : piecesAutre){
				int newLiberteMoi = ((board.isJoueurBlanc(joueur)) ? 1 : -1) * (posMoi.row - posAutre.row) - Math.abs(posMoi.col - posAutre.col) - 1;
				
				if(newLiberteMoi < 0){
					if((board.isJoueurBlanc(joueur))
							newLiberteMoi = posMoi.row;
				}
				else if(newLiberteMoi == 0)
				
				if(newLiberteMoi < liberteInter){
					liberteInter = newLiberteMoi;
				}
			}
			
			if((liberteInter + posMoi.row) >= 8){
				nbPossibleFrontiereMoi++;
			}
			
			liberteMoi += liberteInter;
		}
	}*/
	
	//simulation pour trouver la liberte d'une piece
	public int liberte(PlateauFrontieres board, Position pos, int prof){
		//3 coups possible max
		int val = Integer.MIN_VALUE;
		int newVal;
		int nbBouge = 0;
		PlateauFrontieres newBoard;
		
		if(pos.row > 0){
			//On peu aller en face
			Position newPos = new Position(pos.row - 1, pos.col);
			
			if(board.isFree(newPos.row, newPos.col)){
				CoupFrontieres coup = new CoupFrontieres(new CoupFrontieres(pos.row, pos.col), newPos.row, newPos.col);
				newBoard = board.copy();
				nbBouge++;
				
				newBoard.joue(coup);
				
				newVal = ennemy(newBoard, newPos, prof + 1);
				if(newVal > val){
					newVal = val;
				}
			}
			
			if(pos.col < (PlateauFrontieres.NB_COLONNES - 1)){
				//On peut aller en diag droite
				newPos = new Position(pos.row - 1, pos.col + 1);
				
				if(board.isFree(newPos.row, newPos.col)){
					CoupFrontieres coup = new CoupFrontieres(new CoupFrontieres(pos.row, pos.col), newPos.row, newPos.col);
					newBoard = board.copy();
					nbBouge++;
					
					newBoard.joue(coup);
					
					newVal = ennemy(newBoard, newPos, prof + 1);
					if(newVal > val){
						newVal = val;
					}
				}
			}
			
			if(pos.col > 0){
				//On peut aller en diag gauche
				newPos = new Position(pos.row - 1, pos.col - 1);
				
				if(board.isFree(newPos.row, newPos.col)){
					CoupFrontieres coup = new CoupFrontieres(new CoupFrontieres(pos.row, pos.col), newPos.row, newPos.col);
					newBoard = board.copy();
					nbBouge++;
					
					newBoard.joue(coup);
					
					newVal = ennemy(newBoard, newPos, prof + 1);
					if(newVal > val){
						newVal = val;
					}
				}
			}
			
			if(nbBouge == 0){
				return 0;
			}
			else{
				return val;
			}
		}
		else{
			return 0;
		}
	}
	
	public int ennemy(PlateauFrontieres board, Position pos, int prof){

		LinkedList<CoupFrontieres> ajouer = new LinkedList<CoupFrontieres>();
		
		
		int val = Integer.MAX_VALUE;
		
		for(Position enpos : board.getPieces(board.getOther(joueur))){
			if(enpos.row >= pos.row/* || Math.abs(enpos.row - pos.row)  Math.abs(enpos.col - pos.row)*/){
				//Pas la peine de regarder ce coup, il ne permetra pas de manger la piece
				continue;
			}
			if(enpos.row < (PlateauFrontieres.NB_LIGNES - 1)){
				//On peu avancer
				Position posAv = new Position(enpos.row + 1, enpos.col);
				Position posAvDD = null;
				Position posAvDG = null;
				
				if(enpos.col < (PlateauFrontieres.NB_COLONNES - 1)){
					posAvDD = new Position(enpos.row + 1, enpos.col + 1);
				}
				
				if(enpos.col > 0){
					posAvDG = new Position(enpos.row + 1, enpos.col - 1);
				}
				
				//On l'a mangé
				if(posAv.equals(pos)){
					return 0;
				}
				else{
					if(enpos.col == pos.col || (!board.isFree(posAv.row, posAv.col) && (posAvDD != null || posAvDG != null)){
						CoupFrontieres coup = new CoupFrontieres(new CoupFrontieres(enpos.row, enpos.col), newPos.row, newPos.col);
						ajouer.add(coup);
					}
				}
				
				if(enpos.col < (PlateauFrontieres.NB_COLONNES - 1)){
					//On peut aller en diag droite
					newPos = new Position(enpos.row + 1, enpos.col + 1);
					
					//On l'a mangé
					if(newPos.equals(pos)){
						return 0;
					}
					else{
						if(enpos.col < pos.col){
							CoupFrontieres coup = new CoupFrontieres(new CoupFrontieres(enpos.row, enpos.col), newPos.row, newPos.col);
							ajouer.add(coup);
						}
					}
				}
				
				if(enpos.col > 0){
					//On peut aller en diag gauche
					newPos = new Position(enpos.row + 1, enpos.col - 1);
					
					//On l'a mangé
					if(newPos.equals(pos)){
						return 0;
					}
					else{
						if(enpos.col > pos.col){
							CoupFrontieres coup = new CoupFrontieres(new CoupFrontieres(enpos.row, enpos.col), newPos.row, newPos.col);
							ajouer.add(coup);
						}
					}
				}
			}
		}
		
		for(CoupFrontieres coup : ajouer){
			PlateauFrontieres newBoard = board.copy();
			newBoard.joue(coup);
			int newVal = liberte(newBoard, pos, prof);
			if(newVal < val){
				val = newVal;
			}
		}
		
		return val + 1;
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
			expAvancee = 1.2f;
			coefPrises = 1000;
			
			float diffPrises = board.getNbPrisesX(joueur) - board.getNbPrisesX(board.getOther(joueur));
			float diffAvancee = board.getAvanceeX(joueur, expAvancee) - board.getAvanceeX(board.getOther(joueur), expAvancee);
			h = coefPrises * diffPrises + diffAvancee;
			
			/*if(board.isJoueurBlanc(joueur)){
				long tStart = System.currentTimeMillis();
	
				for(Position p : board.getPieces(joueur)){
					liberte(board, p, 0);
				}
				
				long tEnd = System.currentTimeMillis();
				long tDelta = tEnd - tStart;
				double elapsedSeconds = tDelta / 1000.0;
				
				if(elapsedSeconds >= 0.1){
					System.out.println("TOO LONG ! " + elapsedSeconds);
				}
			}*/
			
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
	
	public static void main(String args[]){
		Joueur j1 = new Joueur("blanc");
		Joueur j2 = new Joueur("noir");
		PlateauFrontieres b = new PlateauFrontieres(j1, j2, j1);
		HeuristiqueFrontieres h = new HeuristiqueFrontieres(MODE2, j1);
		
		/*for(Position p : b.getPieces(j1)){
			System.out.println(h.liberte(b, p, 0));
		}*/
		System.out.println(h.liberte(b, new Position(7, 0), 0));
	}
}
