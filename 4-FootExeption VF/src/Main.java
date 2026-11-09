package Aff;

import gui.FenetreJFrame;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                FenetreJFrame fenetre = new FenetreJFrame();
                fenetre.setVisible(true);
            }
        });
    }
}