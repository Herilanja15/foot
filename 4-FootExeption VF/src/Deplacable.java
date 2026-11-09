package modele;

public class Deplacable {
    protected double x;
    protected double y;

    // Constructeur
    public Deplacable(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // Getters
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    // Setters
    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    // Méthode de déplacement
    public void deplacer(double dx, double dy) {
        this.x += dx;
        this.y += dy;
    }
}