package frontieres;

public interface AlgoFrontieres {
	public CoupFrontieres meilleurCoup(int param, PlateauFrontieres board);
	public void showStat();
	public HeuristiqueFrontieres getHeuristique();
}
