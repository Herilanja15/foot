package gui;

import java.awt.BorderLayout;
import javax.swing.JFrame;

public class FenetreJFrame extends JFrame {

    private DessinJPanel dessinJPanel;

    public FenetreJFrame() {
        // Configuration de la fenêtre principale
        setTitle("Gestionnaire de Match de Football - Oracle & Swing");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrer la fenêtre sur l'écran

        // Initialisation et ajout du panneau principal (DessinJPanel)
        dessinJPanel = new DessinJPanel();
        this.setLayout(new BorderLayout());
        this.add(dessinJPanel, BorderLayout.CENTER);
    }
}