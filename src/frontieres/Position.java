package frontieres;

public class Position{
	public int row;
	public int col;
	
	public Position(int row, int col){
		this.row = row;
		this.col = col;
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(!(o instanceof Position)) return false;
		Position p = (Position) o;
		return (row == p.row && col == p.col);
	}
	
	@Override
	public String toString(){
		return "(" + row +" , " + col + ")";
	}
}
