import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControleurFrontieres implements ActionListener {
	private VueFrontieres vue;
	private PartieFrontieres partie;
	CoupFrontieres from, coup;

	public ControleurFrontieres(VueFrontieres v, PartieFrontieres p) {
		vue = v;
		partie = p;
		vue.addGlobalListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		String ac = e.getActionCommand();
		if(ac.equals("letAIplay")) {
			partie.jouerMachine();
			vue.update_all();
		}
		else if(ac.equals("resetGame")) {
			partie = new PartieFrontieres(partie.getMaxProfJ1(), partie.getMaxProfJ2());
			vue.setPartie(partie);
			vue.update_all();
		}
		else if(ac.equals("undo")) {
			partie.undo();
			vue.update_all();
		}
		else if(ac.equals("redo")) {
			partie.redo();
			vue.update_all();
		}
		else { // coup humain
			coup = null;
			if(ac.equals("passerTour"))
				coup = CoupFrontieres.NO_MOVE;
			else if(ac.length() == 3) {
				char type = ac.charAt(0);
				int i = ac.charAt(1)-'0';
				int j = ac.charAt(2)-'0';
				if(type == 's') { // source ("from")
					from = new CoupFrontieres(i, j);
				}
				else if(type == 't') { // target ("to")
					coup = new CoupFrontieres(from, i, j);
				}
			}
			if(coup != null) {
				partie.jouerHumain(coup);
				vue.update_all();
			}
		}
	}
}

