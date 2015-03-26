package frontieres;


/**
 * Voici l'interface abstraite qu'il suffit d'implanter pour jouer. Ensuite, vous devez utiliser
 * ClientJeu en lui donnant le nom de votre classe pour qu'il la charge et se connecte au serveur.
 * 
 * @author L. Simon (Univ. Paris-Sud)- 2006-2013
 * 
 */

public interface IJoueur {

    // Mais pas lors de la conversation avec l'arbitre (m�thodes initJoueur et getNumJoueur)
    // Vous pouvez changer cela en interne si vous le souhaitez
    static final int BLANC = -1;
    static final int NOIR = 1;

    /**
     * L'arbitre vient de lancer votre joueur. Il lui informe par cette m�thode que vous devez jouer
     * dans cette couleur. Vous pouvez utiliser cette m�thode abstraite, ou la m�thode constructeur
     * de votre classe, pour initialiser vos structures.
     * 
     * @param mycolour
     *            La couleur dans laquelle vous allez jouer (-1=BLANC, 1=NOIR)
     */
    public void initJoueur(int mycolour);

    // Doit retourner l'argument pass� par la fonction ci-dessus (constantes BLANC ou NOIR)
    public int getNumJoueur();

    /**
     * C'est ici que vous devez faire appel � votre IA pour trouver le meilleur coup � jouer sur le
     * plateau courant.
     * 
     * @return une chaine d�crivant le mouvement. Cette chaine doit ?tre d�crite exactement comme
     *         sur l'exemple : String msg = "" + positionInitiale + "-" +positionFinale + ""; ou "PASSE";
     *          Chaque position contient une lettre et un num�ro, par exemple:A1,B2 (coup "A1-B2")
     */
    public String choixMouvement();

    /**
     * M�thode appel�e par l'arbitre pour d�signer le vainqueur. Vous pouvez en profiter pour
     * imprimer une banni?re de joie... Si vous gagnez...
     * 
     * @param colour
     *            La couleur du gagnant (BLANC=-1, NOIR=1).
     */
    public void declareLeVainqueur(int colour);

    /**
     * On suppose que l'arbitre a v�rifi� que le mouvement ennemi �tait bien l�gal. Il vous informe
     * du mouvement ennemi. A vous de r�percuter ce mouvement dans vos structures. Comme par exemple
     * �liminer les pions que ennemi vient de vous prendre par ce mouvement. Il n'est pas n�cessaire
     * de r�fl�chir d�j� � votre prochain coup � jouer : pour cela l'arbitre appelera ensuite
     * choixMouvement().
     * 
     * @param coup
     * 			une chaine d�crivant le mouvement:  par exemple: "A1-B2" ou "PASSE"
     */
    public void mouvementEnnemi(String coup);

    public String binoName();

}
