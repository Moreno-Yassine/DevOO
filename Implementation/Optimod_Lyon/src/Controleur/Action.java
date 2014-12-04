package Controleur;

/**
 * @author Yassine Moreno
 */
public abstract class Action {

    public Action() {
    }
    
    /**
	 * methode d'execution d'une action
	 * @return bool�en de confirmation de l'execution
	 * @exception exception relev�e lors du chargement d'�l�ments (plan/livraisons) li�s aux cas d'erreurs sp�cifi�s
	 */
    public abstract boolean Executer();
    /**
	 * methode d'annulation d'une action
	 * @return bool�en de confirmation de l'annulation
	 */
    public abstract boolean Annuler();

}