package Modele;

import java.util.*;

/**
 * Tournee contenant les plus courts chemins calcul�s
 */
public class Tournee {

    public Tournee(Vector<Vector<Chemin>> map) {
    	this.listeChemins = map; 
    }

    /**
     * Chemins calcul�s subdivis�s en plages horaires
     */
    private Vector<Vector<Chemin>> listeChemins;

	public Vector<Vector<Chemin>> getListeChemins() {
		return listeChemins;
	}
    
    
}