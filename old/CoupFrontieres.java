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
			return "no move";
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
