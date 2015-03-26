package frontieres;

public class CoupFrontieres {
	public static final CoupFrontieres NO_MOVE = new CoupFrontieres();

	private CoupFrontieres from;
	private boolean noMove;
	private int i, j;

	public CoupFrontieres(CoupFrontieres from, int i, int j) {
		this(i, j);
		this.from = from;
	}

	public CoupFrontieres(int i, int j) {
		noMove = false;
		this.i = i;
		this.j = j;
	}

	private CoupFrontieres() {
		noMove = true;
	}
	
	public CoupFrontieres(String coups) throws Exception{
		String moves[] = coups.split("-");
		if(moves.length != 2) throw new Exception("Syntax error");
		char firstMove[] = moves[0].toCharArray();
		if(firstMove.length != 2) throw new Exception("Syntax error");
		int jFirst = firstMove[0] - 'A';
		int iFirst = firstMove[1] - '1';
		if(iFirst >= 8 || jFirst >= 8) throw new Exception("Syntax error");
		
		char secondMove[] = moves[1].toCharArray();
		if(secondMove.length != 2) throw new Exception("Syntax error");
		int jSecond = secondMove[0] - 'A';
		int iSecond = secondMove[1] - '1';
		if(iSecond >= 8 || jSecond >= 8) throw new Exception("Syntax error");
		
		from = new CoupFrontieres(iFirst,jFirst);
		this.i = iSecond;
		this.j = jSecond;
		noMove = false;
	}

	public int geti() {
		return i;
	}

	public int getj() {
		return j;
	}

	public CoupFrontieres getFrom() {
		return from;
	}

	public boolean isNoMove() {
		return noMove;
	}

	public String toString() {
		if(noMove) {
			return "PASSE";
		}
		else {
			String s = String.valueOf((char)('A' + from.getj())) + String.valueOf(from.geti()+1);
			s +=  "-" + String.valueOf((char)('A' + j)) + String.valueOf(i+1);
			return s;
		}
	}
	
	public boolean equals(CoupFrontieres c2) {
		return toString().equals(c2.toString());
	}
}
