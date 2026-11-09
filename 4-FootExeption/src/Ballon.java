package modele;

public class Ballon extends Deplacable {

    // Constructeur
    public Ballon(double x, double y) {
        super(x, y);
    }

    @Override
    public String toString() {
        return "Ballon [x=" + x + ", y=" + y + "]";
    }
}