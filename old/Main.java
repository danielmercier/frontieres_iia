public class Main {
	public static void argsError() {
		System.out.println("Expecting 2 strictly positive integer arguments : maxProfJ1 maxProfJ2");
		System.exit(1);
	}
	public static void main(String[] args) {
		if(args.length != 2) {
			argsError();
		}
		else {
			int mp1 = 8;
			int mp2 = 8;;
			try {
				mp1 = Integer.parseInt(args[0]);
				mp2 = Integer.parseInt(args[1]);
				if(mp1 <= 0 || mp2 <= 0)
					throw(new NumberFormatException());
			}
			catch(NumberFormatException e) {
				argsError();
			}
			PartieFrontieres model = new PartieFrontieres(mp1, mp2);
			VueFrontieres view = new VueFrontieres(model);
			new ControleurFrontieres(view, model);
		}
	}
}