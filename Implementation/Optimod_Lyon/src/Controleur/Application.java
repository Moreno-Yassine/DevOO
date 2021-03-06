package Controleur;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;

import Modele.Chemin;
import Modele.Client;
import Modele.DataWareHouse;
import Modele.Livraison;
import Modele.PlageHoraire;
import Modele.Tournee;
import Outils.*;
import Vue.AjoutLivraison;
import Vue.Fenetre;
import Vue.VueNoeud;
import Vue.VueNoeudLivraison;
import Vue.VuePopup;

/**
 * Controleur principal.
 * Recoit les evenements de la vue, effectue les actions correspondantes sur le modele puis notifie la vue des changements
 * @author Yassine Moreno
 */
public class Application implements MouseListener, ActionListener{

	/**
	 * Modele associe a l'application
	 */
	private DataWareHouse modele;
	/**
	 * Fenetre associee a l'application
	 */
	private Fenetre vue;
	/**
	 * Liste des actions annulees
	 */
	private Vector<Action> listeAnnulation;
	/**
	 * Liste des actions executees
	 */
	private Vector<Action> listeExecution;
	
    public Application(Fenetre vue, DataWareHouse modele) {
    	this.listeAnnulation = new Vector<Action>();
    	this.listeExecution = new Vector<Action>();
    	this.modele = modele;
    	this.vue = vue; 
    }

    public Application()
    {
    	this(new Fenetre(), new DataWareHouse());
    }

    /**
	 * point d'entree du controleur, dispatch des commandes a executer. gestion des undo/redo
	 * @param nom de code de la commande a executer
	 * @param liste d'arguments necessaires pour la commande
	 */
    public void gererCommande(String commande, ArrayList<Object> args) {
        try {
				switch (commande)
				{
				case Proprietes.AJOUTER_LIVRAISON :
					if(args.size() == 2)
					{
						PlageHoraire a = (PlageHoraire) args.get(0);
						Livraison l = (Livraison) args.get(1);
						ActionAjouterLivraison action = new ActionAjouterLivraison(modele, a, l);
						action.Executer();
						this.listeExecution.addElement(action);
						this.listeAnnulation.clear();	

						vue.chargerLivraison(modele.getLivraisonData(), modele.getEntrepot());
						vue.getPlan().chargerTournee(null, null);
						vue.getBtnEnregistrer().setEnabled(false);
						vue.getPlan().repaint();
						vue.updateUndoRedo(listeExecution.size()>0, listeAnnulation.size()>0);
						vue.logText("Livraison Ajoutee");
					}
					break;
				case Proprietes.CALC_TOURNEE :
						Tournee t = new Tournee(calculerTournee());
						modele.setTournee(t);
						vue.getPlan().chargerTournee(t, modele.getLivraisonData());
						vue.getPlan().repaint();
						vue.getBtnEnregistrer().setEnabled(true);
					break;
				case Proprietes.SUPP_LIVRAISON :
					if(args.size() == 1)
					{
						Livraison l = (Livraison) args.get(0);
						ActionSupprimerLivraison action1 = new ActionSupprimerLivraison(modele,l);
						action1.Executer();
						this.listeExecution.addElement(action1);
						this.listeAnnulation.clear();
						
						vue.chargerLivraison(modele.getLivraisonData(), modele.getEntrepot());
						vue.getPlan().chargerTournee(null,null);
						vue.getPlan().repaint();
						vue.updateUndoRedo(listeExecution.size()>0, listeAnnulation.size()>0);
						vue.getBtnEnregistrer().setEnabled(false);
						vue.logText("Livraison Supprimee");
					}
					break;
				case Proprietes.CHARGER_PLAN :
					if(args.size() == 1)
					{
						String path = (String) args.get(0);
						ActionChargerPlan action2 = new ActionChargerPlan(modele, path);
						if(action2.Executer())
						{
							vue.chargerPlan(modele.getPlanApp());
							vue.chargerLivraison(modele.getLivraisonData(), modele.getEntrepot());
							vue.getPlan().chargerTournee(null,null);
							vue.repaint();
							vue.logText("Plan chargé");
							this.listeAnnulation.clear();
							this.listeExecution.clear();
							vue.getBtnChargerDemandeLivraison().setEnabled(true);
							vue.getBtnEnregistrer().setEnabled(false);
							vue.getBtnCalcul().setEnabled(false);
							vue.updateUndoRedo(listeExecution.size()>0, listeAnnulation.size()>0);
						}else{
							vue.logText("Erreur lors du chargement du plan.");
						}
					}
					break;
				case Proprietes.CHARGER_LIVRAISON :
					if(args.size() == 1)
					{
						String path = (String) args.get(0);
						ActionChargerLivraison action3 = new ActionChargerLivraison(modele, path);
						if(action3.Executer())
						{
							vue.chargerLivraison(modele.getLivraisonData(), modele.getEntrepot());
							vue.getPlan().chargerTournee(null,null);
							vue.repaint();
							vue.logText("Demande de livraison chargée");
							vue.getBtnCalcul().setEnabled(true);
							vue.getBtnEnregistrer().setEnabled(false);
							this.listeAnnulation.clear();
							this.listeExecution.clear();
							vue.updateUndoRedo(listeExecution.size()>0, listeAnnulation.size()>0);
						}else
						{
							vue.logText("Erreur lors du chargement de la demande de livraison.");
						}
					}
					break;
				case Proprietes.UNDO :
					if(listeExecution.size() > 0)
					{
						Action actionAnnulable = this.listeExecution.lastElement();
						actionAnnulable.Annuler();
						this.listeExecution.removeElementAt(listeExecution.size()-1);
						listeAnnulation.addElement(actionAnnulable);
						
						vue.chargerLivraison(modele.getLivraisonData(), modele.getEntrepot());
						vue.getPlan().chargerTournee(null,null);
						vue.getPlan().repaint();
						vue.getBtnEnregistrer().setEnabled(false);
						vue.updateUndoRedo(listeExecution.size()>0, listeAnnulation.size()>0);
					}
					break;
				case Proprietes.REDO : 
					if(listeAnnulation.size() > 0 )
					{
						Action actionAnnulee = listeAnnulation.lastElement();
						actionAnnulee.Executer();
						listeAnnulation.removeElementAt(listeAnnulation.size()-1);
						listeExecution.addElement(actionAnnulee);	
						
						vue.chargerLivraison(modele.getLivraisonData(), modele.getEntrepot());
						vue.getPlan().chargerTournee(null,null);
						vue.getPlan().repaint();
						vue.getBtnEnregistrer().setEnabled(false);
						vue.updateUndoRedo(listeExecution.size()>0, listeAnnulation.size()>0);
					}
					break;
				case Proprietes.SAVE:
					if(args.size() == 1)
					{
						String path = (String) args.get(0);
						ActionSauvegarder actionSave = new ActionSauvegarder(modele,path);
						actionSave.Executer();
						vue.logText("Fichier sauvegarde");
					}
					break;
				}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    
	/**
	 * @return the listeAnnulation
	 */
	public Vector<Action> getListeAnnulation() {
		return listeAnnulation;
	}

	/**
	 * @return the listeExecution
	 */
	public Vector<Action> getListeExecution() {
		return listeExecution;
	}

	/**
	 * @return the modele
	 */
	public DataWareHouse getModele() {
		return modele;
	}
	
	//--- CALCUL
    /**
	 * methode de calcul de la tournee, utilise chocoGraph
	 * @return la tournee calculee subdivisee par plages horaires
	 */
	public Vector<Vector<Chemin>> calculerTournee()
	{
		Graph chocoGraph = new RegularGraph(modele.getEntrepot(), modele.getLivraisonData(), modele.getPlanApp());	
		return chocoGraph.calculerChoco();
	}
	

	
	//-------------------------------------Mouse Listener--------------------------------------------//
	/**
	 * Methode qui gere le clic sur la table
	 * Selectionne le noeud correspondant sur le plan
	 * @param e
	 */
	public void clickTable(MouseEvent e)
	{
		JTable target = (JTable)e.getSource();
        int row = target.getSelectedRow();
        
		for(int i=0; i<vue.getPlan().getListeVueNoeudLivraisons().size(); i++)
			vue.getPlan().getListeVueNoeudLivraisons().get(i).selected= (i==row);
			
		for(VueNoeud a : vue.getPlan().getListeVueNoeuds())
			a.selected=false;
		
		vue.getPlan().repaint();
	}
	
	
	/**
	 * Methode qui gere le click droit sur le plan et affiche le popup avec les bons boutons grises
	 * @param e
	 * @param livraison Si le noeud selectionne est une livraison
	 * @param noeud	Vue associee au point clique
	 */
	public void gererClickDroit(MouseEvent e, boolean livraison, VueNoeud noeud)
	{
		if (e.getModifiers() == InputEvent.BUTTON3_MASK) 
		{
			VuePopup pop = new VuePopup(livraison); 
			pop.show(e.getComponent(), e.getX(), e.getY());
			
			if(livraison)
			{
				VueNoeudLivraison a = (VueNoeudLivraison) noeud; 
				
				pop.getB().addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						ArrayList<Object> args = new ArrayList<Object>();
						args.add(a.getLivraison());
						gererCommande(Proprietes.SUPP_LIVRAISON, args);		
					}					
				});
				pop.getC().addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						for(PlageHoraire p : modele.getLivraisonData())
						{
							for(Livraison l : p.getLivraisons())
							{
								if(l == a.getLivraison())
								{
									JOptionPane.showMessageDialog(null, "Plage Horaire " + p.getHeureDebut() + "-" + p.getHeureFin() + "\n-" + a.getLivraison());
								}
							}
						}
					}
				});
			}else{
				pop.getA().addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						AjoutLivraison md1 = new AjoutLivraison(vue, "Ajouter Livraison", true, modele.getLivraisonData(), noeud.getNoeudAssocie());
						
						if(md1.isBtnOkSelected())
						{
							if(md1.getIdClientSelectionne() != -1 )
							{
								Livraison l = new Livraison(); 
						        Client c = new Client();
						        c.initClient( noeud.getNoeudAssocie() , md1.getIdClientSelectionne());
						        l.setDestinataire(c);
						        l.generateIdLivraison();
						        
						        ArrayList<Object> args = new ArrayList<Object>(); 
						        args.add(md1.getPlageSelectionnee());
						        args.add(l);

						        gererCommande(Proprietes.AJOUTER_LIVRAISON, args);				
							}
						}
										 
					}					
				});
			}
		}
	}
	
	
	/**
	 * Methode qui gere le click sur un noeud associe a une livraison
	 * @param e
	 * @return boolean : Si une livraison est selectionnee ou pas
	 */
	public boolean gererClickLivraison(MouseEvent e)
	{
		boolean livraisonSelected = false; 

		for(int i=0; i < vue.getPlan().getListeVueNoeudLivraisons().size(); i++)
		{
			VueNoeudLivraison a = vue.getPlan().getListeVueNoeudLivraisons().get(i);
			if(a.clickDessus(e.getX(), e.getY()))
			{
				a.selected = true;	
				livraisonSelected = true;
				gererClickDroit(e,true, a);		
				vue.getTable().getT().setRowSelectionInterval(i, i);
				vue.logText("Clique sur une livraison");
			}else{
				a.selected = false;
			}
		}
				
		//Si livraison selectionnee, on delesectionne tous les noeuds du plan 
		//Sinon, on deselectionne toutes les row du JTable
		if(livraisonSelected)
		{
			for(VueNoeud a : vue.getPlan().getListeVueNoeuds())
				a.selected = false;
		}else
		{
			if(vue.getPlan().getListeVueNoeudLivraisons().size() > 0 ) 
				vue.getTable().getT().removeRowSelectionInterval(0, vue.getPlan().getListeVueNoeudLivraisons().size()-1);
		}
		
		return livraisonSelected; 
	}
	
	/**
	 * Methode qui gere le click sur un noeud du plan
	 * @param e
	 * @return boolean : Si un noeud du plan est selectionne ou pas
	 */
	public boolean gererClickPlan(MouseEvent e)
	{
		boolean selected = false; 
		
		for(VueNoeud a : vue.getPlan().getListeVueNoeuds())
		{
			if(a.clickDessus(e.getX(), e.getY()))
			{
				a.selected = true;
				selected = true; 
				gererClickDroit(e,false, a);		
				vue.logText("Clique sur X : " + a.getNoeudAssocie().getX() + " Y : " + a.getNoeudAssocie().getY() + " id:" + a.getNoeudAssocie().getIdNoeud());
			}else{
				a.selected = false;
			}
		}	

		return selected;
	}
	
	/**
	 * Methode qui gere le clic sur un plan
	 * @param e
	 */
	public void clickPlan(MouseEvent e)
	{
		// TODO Auto-generated method stub

		if(vue.getPlan().getListeVueNoeudLivraisons() != null)
		{
			//Si on a bien cliqué sur une livraison, on peut ne pas faire la suite
			if(gererClickLivraison(e))
			{
				vue.getPlan().repaint();
				return;	
			}
		}
				
		if(vue.getPlan().getListeVueNoeuds() != null)
		{
			if(!gererClickPlan(e))
				vue.logText("Clique sur autre chose qu'un noeud");
		}
				
		vue.getPlan().repaint();
	}
	
	/**
	 * Methode qui recoit les clics de souris et les dispatch a la table et au plan
	 */
	@Override
	public void mouseClicked(MouseEvent e) {	
		if(e.getSource() == vue.getTable().getT())
		{
			clickTable(e);
		}else if(e.getSource() == vue.getPlan())
		{
			clickPlan(e);
		}
	}
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	
	/**
	 * Methode qui recoit les clics sur les boutons et appelle la methode gererCommande
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == vue.getBtnChargerPlan()){		
			String path = XMLhandler.selectXML();
			ArrayList<Object> args = new ArrayList<Object>();
			args.add(path);
			gererCommande(Proprietes.CHARGER_PLAN, args);
		}
		if(e.getSource() == vue.getBtnChargerDemandeLivraison())
		{
			String path = XMLhandler.selectXML();
			ArrayList<Object> args = new ArrayList<Object>();
			args.add(path);
			gererCommande(Proprietes.CHARGER_LIVRAISON,args);
		}
		if(e.getSource() == vue.getBtnCalcul())
		{
			gererCommande(Proprietes.CALC_TOURNEE, null);
		}
		if(e.getSource() == vue.getBtnUndo())
		{
			gererCommande(Proprietes.UNDO,null);
		}
		if(e.getSource() == vue.getBtnRedo())
		{
			gererCommande(Proprietes.REDO,null);
		}
		if(e.getSource() == vue.getBtnEnregistrer())
		{
	    	JFileChooser jFileChooserSave = new JFileChooser();
	    	int returnVal = jFileChooserSave.showSaveDialog(null);
	    	if (returnVal == JFileChooser.APPROVE_OPTION){
       		 	String path = jFileChooserSave.getSelectedFile().getAbsolutePath();
       		 	ArrayList<Object> args = new ArrayList<Object>();
       		 	args.add(path);
       		 	gererCommande(Proprietes.SAVE, args);
	    	}
		}
	}
}