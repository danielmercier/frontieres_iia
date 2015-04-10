package frontieres;

import java.util.LinkedList;

import frontieres.Applet.Board;

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
	private float coefLib;

	public void setExpAvancee(float ea) {
		expAvancee = ea;
	}

	public void setCoefPrises(float cp) {
		coefPrises = cp;
	}

	public void setCoefBloqueurs(float cb) {
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

	public int liberte(Joueur who, PlateauFrontieres board){
		int coefselonj = (board.isJoueurBlanc(who)) ? 1 : -1;
		Joueur me = who;
		Joueur other = board.getOther(who);
		int nbPieceLibre = 0;
		boolean mange;
		
		for(Position posMe : board.getPieces(me)){
			mange = false;
			for(Position posOther : board.getPieces(other)){
				if(coefselonj * (posMe.row - posOther.row) > Math.abs(posMe.col - posOther.col)){
					//Dans ce cas, la pièce pourra être manger
					mange = true;
					break;
				}
			}
			if(!mange){
				nbPieceLibre++;
			}
		}

		return nbPieceLibre;
	}

	public int liberteJ2(){
		return 3;
	}

	//simulation pour trouver la liberte d'une piece
	public int liberte(PlateauFrontieres board, Position pos, int alpha, int beta, int prof){
		//3 coups possible max
		int nbBouge = 0;
		PlateauFrontieres newBoard;

		if(pos.row > 0){
			//On peu aller en face
			Position newPos = new Position(pos.row - 1, pos.col);

			if(board.isFreeOrEnemy(newPos.row, newPos.col)){
				CoupFrontieres coup = new CoupFrontieres(new CoupFrontieres(pos.row, pos.col), newPos.row, newPos.col);
				newBoard = board.copy();
				nbBouge++;

				newBoard.joue(coup);

				alpha = Math.max(alpha, ennemy(newBoard, newPos, alpha, beta, prof + 1));

				if(alpha >= beta){
					return beta;
				}
			}

			if(pos.col < (PlateauFrontieres.NB_COLONNES - 1)){
				//On peut aller en diag droite
				newPos = new Position(pos.row - 1, pos.col + 1);

				if(board.isFreeOrEnemy(newPos.row, newPos.col)){
					CoupFrontieres coup = new CoupFrontieres(new CoupFrontieres(pos.row, pos.col), newPos.row, newPos.col);
					newBoard = board.copy();
					nbBouge++;

					newBoard.joue(coup);

					alpha = Math.max(alpha, ennemy(newBoard, newPos, alpha, beta, prof + 1));

					if(alpha >= beta){
						return beta;
					}
				}
			}

			if(pos.col > 0){
				//On peut aller en diag gauche
				newPos = new Position(pos.row - 1, pos.col - 1);

				if(board.isFreeOrEnemy(newPos.row, newPos.col)){
					CoupFrontieres coup = new CoupFrontieres(new CoupFrontieres(pos.row, pos.col), newPos.row, newPos.col);
					newBoard = board.copy();
					nbBouge++;

					newBoard.joue(coup);

					alpha = Math.max(alpha, ennemy(newBoard, newPos, alpha, beta, prof + 1));

					if(alpha >= beta){
						return beta;
					}
				}
			}

			if(nbBouge == 0){
				return 0;
			}
			else{
				return alpha;
			}
		}
		else{
			return 0;
		}
	}

	public int ennemy(PlateauFrontieres board, Position pos, int alpha, int beta, int prof){

		LinkedList<CoupFrontieres> ajouer = new LinkedList<CoupFrontieres>();

		for(Position enpos : board.getPiecesJ2()){
			if((pos.row - enpos.row) <= Math.abs(pos.col - enpos.col)){
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


				if(board.isFreeOrEnemy(posAv.row, posAv.col) && enpos.col == pos.col){
					//Test si on mange la piece
					if(posAv.equals(pos)){
						return 0;
					}

					CoupFrontieres coup = new CoupFrontieres(new CoupFrontieres(enpos.row, enpos.col), posAv.row, posAv.col);
					ajouer.add(coup);

					//Pas besoin de regarder la suite
					continue;
				}

				if(posAvDD != null){
					//On peut aller en diag droite

					if(board.isFreeOrEnemy(posAvDD.row, posAvDD.col) && enpos.col < pos.col){
						//Test si on mange la piece
						if(posAvDD.equals(pos)){
							return 0;
						}

						CoupFrontieres coup = new CoupFrontieres(new CoupFrontieres(enpos.row, enpos.col), posAvDD.row, posAvDD.col);
						ajouer.add(coup);

						//Pas besoin de regarder la suite
						continue;
					}
				}

				if(posAvDG != null){
					//On peut aller en diag droite

					if(board.isFreeOrEnemy(posAvDG.row, posAvDG.col) && enpos.col > pos.col){
						//Test si on mange la piece
						if(posAvDG.equals(pos)){
							return 0;
						}

						CoupFrontieres coup = new CoupFrontieres(new CoupFrontieres(enpos.row, enpos.col), posAvDG.row, posAvDG.col);
						ajouer.add(coup);

						//Pas besoin de regarder la suite
						continue;
					}
				}
			}
		}

		boolean empty = true;


		for(CoupFrontieres coup : ajouer){
			empty = false;
			PlateauFrontieres newBoard = board.copy();
			newBoard.joue(coup);

			beta = Math.min(beta, liberte(newBoard, pos, alpha, beta, prof));

			if(alpha >= beta){
				return alpha;
			}
		}

		if(empty){
			PlateauFrontieres newBoard = board.copy();
			newBoard.joue(board.coupsPossibles().get(0));

			beta = liberte(newBoard, pos, alpha, beta, prof);

			if(alpha >= beta){
				return alpha;
			}
		}

		return beta + 1;
	}

	public float eval(PlateauFrontieres board) {
		// heuristique toujours calculée par rapport au joueur (jmax) passé au constructeur
		float h = 0;
		float diffPrises = board.getNbPrisesX(joueur) - board.getNbPrisesX(board.getOther(joueur));
		float diffAvancee = board.getAvanceeX(joueur, expAvancee) - board.getAvanceeX(board.getOther(joueur), expAvancee);
		
		h = coefPrises * diffPrises + diffAvancee;

		if(board.getXwins(joueur))
			h = MAX_HEUR;
		else if(board.getXwins(board.getOther(joueur)))
			h = MIN_HEUR;
		else if(board.getTie())
			h = TIE_HEUR;
		return h;
	}
}

