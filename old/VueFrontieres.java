import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class VueFrontieres extends JFrame {

	static final Color DEFAULT_CASE_BG = new Color(220, 150, 100);
	static final Color TARGET_BG = new Color(120, 190, 130);
	static final Color SOURCE_RECT = new Color(0, 0, 0);
	static final Color MAIN_BACKGROUND = new Color(95, 145, 140);

	private class DrawableButton extends JButton implements MouseListener {
		private Color pawnColor;
		private int i, j;
		private boolean isSource, isTarget;
		public DrawableButton(int i, int j) {
			setBackground(DEFAULT_CASE_BG);
			resetState();
			pawnColor = null;
			addMouseListener(this);
			this.i = i;
			this.j = j;
		}
		public void paintComponent(Graphics g) {
			Graphics2D g2D = (Graphics2D) g;
			g2D.setRenderingHints(new RenderingHints(
					RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON)
			);
			if(isTarget || isSource) {
				g2D.setColor(TARGET_BG);
				g2D.fillRect(0, 0, this.getWidth(), this.getHeight());
			}
			else {
				g2D.setColor(DEFAULT_CASE_BG);
				g2D.fillRect(0, 0, this.getWidth(), this.getHeight());
			}
			if(pawnColor != null) {
				g2D.setColor(pawnColor);
				g2D.fillOval(10, 10, 30, 30);
			}
		}
		public void setPawnColor(Color c) {
			pawnColor = c;
		}
		public void resetState() {
			setActionCommand("");
			isTarget = false;
			isSource = false;
		}
		public void setAsTarget() {
			setActionCommand("t" + String.valueOf(i) + String.valueOf(j));
			isTarget = true;
			isSource = false;
		}
		public void setAsSource() {
			setActionCommand("s" + String.valueOf(i) + String.valueOf(j));
			isSource = true;
			isTarget = false;
		}
		public void mouseClicked(MouseEvent e) {
			resetAllCasesState();
			if(!isTarget && !isSource) {
				highlightPossibleMoves(i, j);
				doClick();
			}
		}
		public void mouseEntered(MouseEvent e) {
		}
		public void mouseExited(MouseEvent e) {
		}
		public void mousePressed(MouseEvent e) {
		}
		public void mouseReleased(MouseEvent e) {
		}
	}

	private PartieFrontieres partie;
	private DrawableButton[][] board;
	private JButton letAIplay, resetGame, undo, redo, passerTour;
	private JLabel scoreJ1, scoreJ2, prisesJ1, prisesJ2, timeJ1, timeJ2;
	private JPanel main;
	private int rows, cols;

	private static final int boxSize = 50;

	public VueFrontieres(PartieFrontieres p) {
		partie = p;
		rows = PlateauFrontieres.NB_LIGNES;
		cols = PlateauFrontieres.NB_COLONNES;

		board = new DrawableButton[rows][cols];

		this.setTitle("Frontières");
		this.setSize((boxSize+10)*cols, (boxSize+10)*rows+120);
		this.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		Container contentPane = this.getContentPane();
		contentPane.setLayout(new BorderLayout());

		main = new JPanel(new GridBagLayout());
		main.setBackground(MAIN_BACKGROUND);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		
		c.gridy = 0;
		c.gridwidth = cols/2;
		
		scoreJ1 = new JLabel();
		scoreJ1.setHorizontalAlignment(JLabel.CENTER);
		scoreJ1.setOpaque(true);
		scoreJ1.setBackground(Color.white);
		scoreJ1.setForeground(Color.black);
		main.add(scoreJ1, c);
		
		scoreJ2 = new JLabel();
		scoreJ2.setHorizontalAlignment(JLabel.CENTER);
		scoreJ2.setOpaque(true);
		scoreJ2.setBackground(Color.white);
		scoreJ2.setForeground(Color.black);
		main.add(scoreJ2, c);
		
		++ c.gridy;
		
		prisesJ1 = new JLabel();
		prisesJ1.setHorizontalAlignment(JLabel.CENTER);
		prisesJ1.setOpaque(true);
		prisesJ1.setBackground(Color.white);
		prisesJ1.setForeground(Color.black);
		main.add(prisesJ1, c);
		
		prisesJ2 = new JLabel();
		prisesJ2.setHorizontalAlignment(JLabel.CENTER);
		prisesJ2.setOpaque(true);
		prisesJ2.setBackground(Color.white);
		prisesJ2.setForeground(Color.black);
		main.add(prisesJ2, c);
		
		++ c.gridy;
		
		timeJ1 = new JLabel();
		timeJ1.setHorizontalAlignment(JLabel.CENTER);
		timeJ1.setOpaque(true);
		timeJ1.setBackground(Color.white);
		timeJ1.setForeground(Color.black);
		main.add(timeJ1, c);
		
		timeJ2 = new JLabel();
		timeJ2.setHorizontalAlignment(JLabel.CENTER);
		timeJ2.setOpaque(true);
		timeJ2.setBackground(Color.white);
		timeJ2.setForeground(Color.black);
		main.add(timeJ2, c);

		++ c.gridy;
		
		main.add(new JLabel(" "), c); // cheap separator
		
		c.gridwidth = 1;
		++ c.gridy;

		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				DrawableButton db = new DrawableButton(i ,j);
				db.setPreferredSize(new Dimension(boxSize, boxSize));
				db.setMinimumSize(new Dimension(boxSize, boxSize));
				db.setMaximumSize(new Dimension(boxSize, boxSize));
				main.add(db, c);
				board[i][j] = db;
			}
			++ c.gridy;
		}

		c.gridwidth = cols/2;
		
		letAIplay = new JButton("Let AI play");
		letAIplay.setActionCommand("letAIplay");
		main.add(letAIplay, c);

		passerTour = new JButton("passerTour");
		passerTour.setActionCommand("passerTour");
		main.add(passerTour, c);
		
		++ c.gridy;

		undo = new JButton("undo");
		undo.setActionCommand("undo");
		main.add(undo, c);

		redo = new JButton("redo");
		redo.setActionCommand("redo");
		main.add(redo, c);

		++ c.gridy;
		c.gridwidth = cols;
		
		resetGame = new JButton("Reset game");
		resetGame.setActionCommand("resetGame");
		main.add(resetGame, c);

		contentPane.add(main);
		update_all();
		this.setVisible(true);
	}
	
	public void setPartie(PartieFrontieres p) {
		partie = p;
	}

	public void update_all() {
		resetAllCasesState();
		char[][] state = partie.getPlateau().getCharArray();
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				char symb = state[i][j];
				Color c;
				if(symb == PlateauFrontieres.PION_J1)
					c = Color.WHITE;
				else if(symb == PlateauFrontieres.PION_J2)
					c = Color.BLACK;
				else
					c = null;
				board[i][j].setPawnColor(c);
				board[i][j].repaint();
			}
		}
		scoreJ1.setText("score J1 = " + String.valueOf(PartieFrontieres.partiesGagneesJ1));
		scoreJ2.setText("score J2 = " + String.valueOf(PartieFrontieres.partiesGagneesJ2));
		prisesJ1.setText("prises J1 = " + String.valueOf(partie.getPlateau().getNbPrisesJ1()));
		prisesJ2.setText("prises J2 = " + String.valueOf(partie.getPlateau().getNbPrisesJ2()));
		// TODO
		timeJ1.setText("00:00:00");
		timeJ2.setText("00:00:00");
		if(partie.getPartieFinie()) {
			String message = "";
			if(partie.getPlateau().getTie())
				message = "Match nul";
			else if(partie.getPlateau().getJ1wins())
				message = partie.getJ1() + " gagne";
			else if(partie.getPlateau().getJ2wins())
				message = partie.getJ2() + " gagne";
			JOptionPane.showMessageDialog(this, message, "Partie terminée", JOptionPane.PLAIN_MESSAGE);
		}
	}

	public void resetAllCasesState() {
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				board[i][j].resetState();
				board[i][j].repaint();
			}
		}
	}

	public void highlightPossibleMoves(int i, int j) {
		ArrayList<CoupFrontieres> cp = partie.getPlateau().coupsPossiblesCase(i, j);
		if(partie.getPlateau().caseJouable(i, j)) {
			board[i][j].setAsSource();
			board[i][j].repaint();
			for(CoupFrontieres c : cp) {
				board[c.geti()][c.getj()].setAsTarget();
				board[c.geti()][c.getj()].repaint();
			}
		}
	}

	public void addGlobalListener(ActionListener al) {
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++)
				board[i][j].addActionListener(al);
		}
		letAIplay.addActionListener(al);
		passerTour.addActionListener(al);
		undo.addActionListener(al);
		redo.addActionListener(al);
		resetGame.addActionListener(al);
	}
}

