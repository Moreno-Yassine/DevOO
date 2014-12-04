package Modele;

import java.sql.Time;
import java.util.Vector;

import org.w3c.dom.Element;

/**
 * Demande de livraison associee � un client
 * @author Yassine Moreno
 */
public class Livraison {

    public Livraison() {
    }
    /**
     * Temps effectif de passage lors de la tourn�e
     */
    public Time tempsPassage;
    public Time getTempsPassage() {
		return tempsPassage;
	}

	public void setTempsPassage(Time tempsPassage) {
		this.tempsPassage = tempsPassage;
	}

	/**
     * Identifiant de la livraison
     */
    private int idLivraison;

    /**
     * Client associ� � la livraison
     */
    private Client destinataire;

    /**
	 * methode d'initialisation des livraisons
	 * @param XMLnode Element XML contenant les informations pour l'initialisation
	 * @param l Noeuds disponibles dans le plan
	 * @throws Exception li�e aux malformations s�mantiques et syntaxiques des fichiers
     */
    public void initLivraison(Element XMLnode, Vector<Noeud> l) throws Exception {
        this.idLivraison = Integer.parseInt(XMLnode.getAttribute("id"));
        try {
			Noeud noeudClient = l.get(Integer.parseInt(XMLnode.getAttribute("adresse")));
			this.destinataire = new Client();
			destinataire.initClient( noeudClient , Integer.parseInt(XMLnode.getAttribute("client")));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new Exception("Client Inconnu");
		}
    }

	/**
	 * @return the idLivraison
	 */
	public int getIdLivraison() {
		return idLivraison;
	}


	/**
	 * @return the destinataire
	 */
	public Client getDestinataire() {
		return destinataire;
	}

	public void setDestinataire(Client destinataire) {
		this.destinataire = destinataire;
	}
	
	public void generateIdLivraison()
	{
		this.idLivraison = (int)(Math.random()*50000);
	}
	public String toString()
	{
		return "Destinataire : " + destinataire.getIdClient() + " a l'adresse X :" + destinataire.getNoeudAdresse().getX() + " Y : " + destinataire.getNoeudAdresse().getY(); 
	}

}