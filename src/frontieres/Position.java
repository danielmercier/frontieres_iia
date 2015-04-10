package frontieres;

//Classe permettant de stocker une position, soit, le numéro d'une colonne et d'une ligne.
public class Position{
	public int row;
	public int col;
	
	public Position(int row, int col){
		this.row = row;
		this.col = col;
	}
	
	public void setRow(int row){
		this.row = row;
	}
	
	public void setCol(int col){
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
