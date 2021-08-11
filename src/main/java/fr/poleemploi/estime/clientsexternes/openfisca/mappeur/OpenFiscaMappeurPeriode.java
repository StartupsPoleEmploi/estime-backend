package fr.poleemploi.estime.clientsexternes.openfisca.mappeur;

import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.SALAIRE_BASE;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.SALAIRE_IMPOSABLE;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.tsohr.JSONObject;

import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.BeneficiaireAidesUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.RessourcesFinancieresUtile;
import fr.poleemploi.estime.logique.simulateur.aides.utile.AideUtile;
import fr.poleemploi.estime.services.ressources.AllocationsLogement;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Salaire;
import fr.poleemploi.estime.services.ressources.SimulationAides;

@Component
public class OpenFiscaMappeurPeriode {

    public static final int NUMERO_MOIS_PERIODE = 0;
    public static final int NOMBRE_MOIS_PERIODE_OPENFISCA = 4;
    public static final int NOMBRE_MOIS_PERIODE_OPENFISCA_TNS_AUTO_ENTREPRENEUR_CHIFFRE_AFFAIRES = 3;
    public static final int NOMBRE_MOIS_PERIODE_36 = 36; 

    @Autowired
    private DateUtile dateUtile;

    @Autowired
    private AideUtile aideeUtile;

    @Autowired
    private BeneficiaireAidesUtile beneficiaireAidesUtile;

    @Autowired
    private RessourcesFinancieresUtile ressourcesFinancieresUtile;

    public JSONObject creerPeriodes(Object valeur, LocalDate dateDebutSimulation, int numeroMoisSimule, int nombrePeriodes) {
        JSONObject periode = new JSONObject();
        for (int numeroMoisPeriode = NUMERO_MOIS_PERIODE; numeroMoisPeriode < nombrePeriodes ; numeroMoisPeriode++) {
            periode.put(getPeriodeFormateeRessourceFinanciere(dateDebutSimulation, numeroMoisSimule, numeroMoisPeriode), valeur);
        }
        return periode;
    }

    public JSONObject creerPeriodesValeurNulleEgaleZero(Object valeur, LocalDate dateDebutSimulation, int numeroMoisSimule, int nombrePeriode) {
        JSONObject periode = new JSONObject();
        for (int numeroMoisPeriode = NUMERO_MOIS_PERIODE; numeroMoisPeriode < nombrePeriode ; numeroMoisPeriode++) {
            if(valeur != null) {
                periode.put(getPeriodeFormateeRessourceFinanciere(dateDebutSimulation, numeroMoisSimule, numeroMoisPeriode), valeur);
            } else  {
                periode.put(getPeriodeFormateeRessourceFinanciere(dateDebutSimulation, numeroMoisSimule, numeroMoisPeriode), 0);                
            }
        }
        return periode;
    }

    public JSONObject creerPeriodesRevenusImmobilier(Object valeur, LocalDate dateDebutSimulation) {
        JSONObject periode = new JSONObject();
        LocalDate datePlusNbrPeriodeMois = dateUtile.ajouterMoisALocalDate(dateDebutSimulation, NOMBRE_MOIS_PERIODE_OPENFISCA);
        for (int nombreMoisAEnlever = 36; nombreMoisAEnlever >= 1 ; nombreMoisAEnlever--) {
            LocalDate datePeriode = dateUtile.enleverMoisALocalDate(datePlusNbrPeriodeMois, nombreMoisAEnlever);
            periode.put(getPeriodeFormatee(datePeriode), valeur);
        }
        return periode;
    }

    public JSONObject creerPeriodesRSA(float montantRsaDemandeur, float prochaineDeclarationRsa, LocalDate dateDebutSimulation, int numeroMoisSimule) {
        JSONObject periode = new JSONObject();
        for (int numeroMoisPeriode = NUMERO_MOIS_PERIODE; numeroMoisPeriode < OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA ; numeroMoisPeriode++) {
            if(numeroMoisPeriode <= prochaineDeclarationRsa) {
                periode.put(getPeriodeFormateeRessourceFinanciere(dateDebutSimulation, numeroMoisSimule, numeroMoisPeriode), montantRsaDemandeur);                
            } else {
                periode.put(getPeriodeFormateeRessourceFinanciere(dateDebutSimulation, numeroMoisSimule, numeroMoisPeriode), JSONObject.NULL);                 
            }
        }
        return periode;
    }

    public JSONObject creerPeriodesallocationsLogement(AllocationsLogement allocationsLogement, LocalDate dateDebutSimulation, int numeroMoisSimule) {
        JSONObject periode = new JSONObject();        
        periode.put(getPeriodeFormateeRessourceFinanciere(dateDebutSimulation, numeroMoisSimule, 0), allocationsLogement.getMoisNMoins3());             
        periode.put(getPeriodeFormateeRessourceFinanciere(dateDebutSimulation, numeroMoisSimule, 1), allocationsLogement.getMoisNMoins2());             
        periode.put(getPeriodeFormateeRessourceFinanciere(dateDebutSimulation, numeroMoisSimule, 2), allocationsLogement.getMoisNMoins1());             
        periode.put(getPeriodeFormateeRessourceFinanciere(dateDebutSimulation, numeroMoisSimule, 3), allocationsLogement.getMoisNMoins1());
        return periode;
    }

    public void creerPeriodesSalaireDemandeur(JSONObject demandeurJSON, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation, int numeroMoisSimule) {
        JSONObject periodeSalaireDeBase = new JSONObject();
        JSONObject periodeSalaireImposable = new JSONObject();

        for(int numeroMoisPeriodeOpenfisca = NUMERO_MOIS_PERIODE; numeroMoisPeriodeOpenfisca < NOMBRE_MOIS_PERIODE_OPENFISCA ; numeroMoisPeriodeOpenfisca++) {
            Salaire salairePourSimulation = getSalairePourSimulation(demandeurEmploi, numeroMoisSimule, numeroMoisPeriodeOpenfisca);                
            periodeSalaireDeBase.put(getPeriodeFormateeRessourceFinanciere(dateDebutSimulation, numeroMoisSimule, numeroMoisPeriodeOpenfisca), salairePourSimulation.getMontantBrut());
            periodeSalaireImposable.put(getPeriodeFormateeRessourceFinanciere(dateDebutSimulation, numeroMoisSimule, numeroMoisPeriodeOpenfisca), salairePourSimulation.getMontantNet());
        }

        demandeurJSON.put(SALAIRE_BASE, periodeSalaireDeBase);
        demandeurJSON.put(SALAIRE_IMPOSABLE, periodeSalaireImposable);
    }    

    public JSONObject creerPeriodesAidee(DemandeurEmploi demandeurEmploi, SimulationAides simulationAides, String codeAide, LocalDate dateDebutSimulation, int numeroMoisSimule) {
        JSONObject periode = new JSONObject();
        int numeroMoisMontantARecuperer = numeroMoisSimule - (OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA - 1);
        for (int numeroMoisPeriode = NUMERO_MOIS_PERIODE; numeroMoisPeriode < OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA; numeroMoisPeriode++) { 
            float montantAidee = 0;
            if(isNumeroMoisMontantARecupererDansPeriodeSimulation(numeroMoisMontantARecuperer)) {
                montantAidee = aideeUtile.getMontantAidePourCeMoisSimule(simulationAides, codeAide, numeroMoisMontantARecuperer); 
            } else {
                montantAidee = aideeUtile.getMontantAideeAvantSimulation(numeroMoisMontantARecuperer, demandeurEmploi, codeAide, dateDebutSimulation);
            }
            periode.put(getPeriodeFormateeRessourceFinanciere(dateDebutSimulation, numeroMoisPeriode, numeroMoisSimule), montantAidee);
            numeroMoisMontantARecuperer++;
        }
        return periode;
    }

    public JSONObject creerPeriodesAnnees(Object valeur, LocalDate dateDebutSimulation) {
        JSONObject periode = new JSONObject();
        int anneeDateSimulation = dateDebutSimulation.getYear();
        String anneeMoinsUnAnneeSimulation = String.valueOf(anneeDateSimulation - 1);
        String anneeMoinsDeuxAnneeSimulation = String.valueOf(anneeDateSimulation - 2);
        periode.put(anneeMoinsUnAnneeSimulation, valeur);
        periode.put(anneeMoinsDeuxAnneeSimulation, valeur);
        return periode;
    }

    public String getPeriodeFormatee(LocalDate datePeriode) {
        return datePeriode.getYear() + "-" + dateUtile.getMonthFromLocalDate(datePeriode);
    }

    public String getPeriodeFormateeRessourceFinanciere(LocalDate dateDebutSimulation, int numeroMoisSimule, int numeroPeriodeOpenfisca) {
        LocalDate dateDeclenchementCalculAide = dateDebutSimulation.plusMonths((long)numeroMoisSimule);
        LocalDate dateDebutPeriodeOpenfisca = dateDeclenchementCalculAide.minusMonths(NOMBRE_MOIS_PERIODE_OPENFISCA - ((long)numeroPeriodeOpenfisca));   
        return getPeriodeFormatee(dateDebutPeriodeOpenfisca);
    }

    public String getPeriodeOpenfiscaCalculAide(LocalDate dateDebutSimulation, int numeroMoisSimule) {
        LocalDate dateDeclenchementCalculAide = dateDebutSimulation.plusMonths((long)numeroMoisSimule - 1);
        return getPeriodeFormatee(dateDeclenchementCalculAide);
    }

    private Salaire getSalairePourSimulation(DemandeurEmploi demandeurEmploi, int numeroMoisSimule, int numeroMoisPeriodeOpenfisca) {       
        if(isMoisPeriodeOpenFiscaAvantPeriodeSimulation(numeroMoisSimule, numeroMoisPeriodeOpenfisca)) {
            return getSalaireAvantPeriodeSimulation(demandeurEmploi, numeroMoisPeriodeOpenfisca);                     
        }
        return demandeurEmploi.getFuturTravail().getSalaire();
    }

    private Salaire getSalaireAvantPeriodeSimulation(DemandeurEmploi demandeurEmploi, int numeroMoisPeriodeOpenfisca) {
        if(beneficiaireAidesUtile.isBeneficiaireASS(demandeurEmploi)) {
            return getSalaireAvantPeriodeSimulationDemandeurASS(demandeurEmploi, numeroMoisPeriodeOpenfisca);
        } 
        if(beneficiaireAidesUtile.isBeneficiaireAAH(demandeurEmploi)) {
            return getSalaireAvantPeriodeSimulationDemandeurAAH(demandeurEmploi, numeroMoisPeriodeOpenfisca);
        }  
        if(beneficiaireAidesUtile.isBeneficiaireRSA(demandeurEmploi)) {
            return getSalaireAvantPeriodeSimulationDemandeurRSA(demandeurEmploi, numeroMoisPeriodeOpenfisca);
        }  
        return getSalaireMontantZero();
    }

    /**
     * Méthode permettant de savoir si la mois de la periode OpenFisca est avant la période de simulation 
     * @param numeroMoisSimule : numero mois simulé dans la période de simulation
     * @param numeroMoisPeriodeOpenfisca : numero du mois dans la periode OpenFisca 
     * @param numerosPeriodeOpenfiscaAvantDebutPeriodeSimulee : numeros des mois de la péridoe OpenFisca se situant avant la période de simulation
     * @return true si mois période OpenFisca avant période de simulation, sinon false
     */
    private boolean isMoisPeriodeOpenFiscaAvantPeriodeSimulation(int numeroMoisSimule, int numeroMoisPeriodeOpenfisca) {
        int numeroMoisPeriodeOpenfiscaPlusEloigneAvantDebutPeriodeSimulee = getNumeroMoisPeriodeOpenfiscaPlusEloigneAvantDebutPeriodeSimulee(numeroMoisSimule);
        return numeroMoisSimule <= NOMBRE_MOIS_PERIODE_OPENFISCA && numeroMoisPeriodeOpenfisca < numeroMoisPeriodeOpenfiscaPlusEloigneAvantDebutPeriodeSimulee;
    }

    private Salaire getSalaireAvantPeriodeSimulationDemandeurAAH(DemandeurEmploi demandeurEmploi, int numeroMoisPeriodeOpenfisca) {
        int nombreMoisTravaillesDerniersMois = demandeurEmploi.getRessourcesFinancieres().getNombreMoisTravaillesDerniersMois();
        if(nombreMoisTravaillesDerniersMois > 0) {
            if(numeroMoisPeriodeOpenfisca == 0) {
                return demandeurEmploi.getRessourcesFinancieres().getSalairesAvantPeriodeSimulation().getMoisMoins1MoisDemandeSimulation().getSalaire();            
            }
            if(numeroMoisPeriodeOpenfisca == 1) {
                return demandeurEmploi.getRessourcesFinancieres().getSalairesAvantPeriodeSimulation().getMoisDemandeSimulation().getSalaire();            
            }
        }
        return getSalaireMontantZero();
    }

    private Salaire getSalaireAvantPeriodeSimulationDemandeurASS(DemandeurEmploi demandeurEmploi, int numeroMoisPeriodeOpenfisca) {
        int nombreMoisCumulesAssEtSalaire = ressourcesFinancieresUtile.getNombreMoisTravaillesDerniersMois(demandeurEmploi);
        return getMontantSalaireAvantPeriodeSimulation(demandeurEmploi, nombreMoisCumulesAssEtSalaire, numeroMoisPeriodeOpenfisca);
    }

    private Salaire getSalaireAvantPeriodeSimulationDemandeurRSA(DemandeurEmploi demandeurEmploi, int numeroMoisPeriodeOpenfisca) {
        Integer prochaineDeclarationRSA = demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getProchaineDeclarationRSA();
        if(ressourcesFinancieresUtile.hasTravailleAuCoursDerniersMois(demandeurEmploi)) {
            if((prochaineDeclarationRSA == 3 || prochaineDeclarationRSA == 0) && numeroMoisPeriodeOpenfisca == 0) {
                return ressourcesFinancieresUtile.getSalaireAvantSimulation(demandeurEmploi, 0);            
            }        
            if(prochaineDeclarationRSA == 1) {
                if(numeroMoisPeriodeOpenfisca == 0) {
                    return ressourcesFinancieresUtile.getSalaireAvantSimulation(demandeurEmploi, 2);                            
                }            
                if(numeroMoisPeriodeOpenfisca == 1) {
                    return ressourcesFinancieresUtile.getSalaireAvantSimulation(demandeurEmploi, 1);            
                }            
                if(numeroMoisPeriodeOpenfisca == 2) {
                    return ressourcesFinancieresUtile.getSalaireAvantSimulation(demandeurEmploi, 0);            
                }
            }
            if(prochaineDeclarationRSA == 2) {
                if(numeroMoisPeriodeOpenfisca == 0) {
                    return ressourcesFinancieresUtile.getSalaireAvantSimulation(demandeurEmploi, 1);                            
                }            
                if(numeroMoisPeriodeOpenfisca == 1) {
                    return ressourcesFinancieresUtile.getSalaireAvantSimulation(demandeurEmploi, 0);            
                }    
            }
        }
        return getSalaireMontantZero();
    }

    private Salaire getMontantSalaireAvantPeriodeSimulation(DemandeurEmploi demandeurEmploi, int nombreMoisCumulesAssEtSalaire, int numeroMoisPeriodeOpenfisca) {
        if(nombreMoisCumulesAssEtSalaire == 2 && numeroMoisPeriodeOpenfisca == 0) {
            return ressourcesFinancieresUtile.getSalaireAvantSimulation(demandeurEmploi, 0);
        }
        if(nombreMoisCumulesAssEtSalaire == 3) {
            if(numeroMoisPeriodeOpenfisca == 0) {
                return ressourcesFinancieresUtile.getSalaireAvantSimulation(demandeurEmploi, 1);
            }
            if(numeroMoisPeriodeOpenfisca == 1) {
                return ressourcesFinancieresUtile.getSalaireAvantSimulation(demandeurEmploi, 0);
            }
        }
        if(nombreMoisCumulesAssEtSalaire == 4) {
            if(numeroMoisPeriodeOpenfisca == 0) {
                return ressourcesFinancieresUtile.getSalaireAvantSimulation(demandeurEmploi, 2);
            }
            if(numeroMoisPeriodeOpenfisca == 1) {
                return ressourcesFinancieresUtile.getSalaireAvantSimulation(demandeurEmploi, 1);
            }
            if(numeroMoisPeriodeOpenfisca == 2) {
                return ressourcesFinancieresUtile.getSalaireAvantSimulation(demandeurEmploi, 0);
            }
        }
        return getSalaireMontantZero();
    }


    /**
     * Méthode permettant d'obtenir le numéro du mois de la période OpenFisca qui est le plus éloigné du début de la période de simulation
     * 
     * Exemple : ci-dessous le numero du mois le plus éloigné sera 2
     * 
     *  ------ mois avant simulation   ------ période de simulation --------
     *           periode OpenFisca
     *              
     *              M-1 M0                    M1 M2 M3 M4 M5 M6
     *  --------------------------------------------------------------------   
     *  
     * @param numeroMoisSimule : numéro du mois simulé
     * @return numéro du mois de la période OpenFisca qui est le plus éloigné du début de la période de simulation
     */
    private int getNumeroMoisPeriodeOpenfiscaPlusEloigneAvantDebutPeriodeSimulee(int numeroMoisSimule) {
        return NOMBRE_MOIS_PERIODE_OPENFISCA - numeroMoisSimule;
    }

    /**
     * La simulation se fait sur N si le numeroMoisMontantARecuperer est < NUMERO_MOIS_PERIODE cela veut dire que l'on est sur un mois avant la simulation
     * @param numeroMoisMontantARecuperer : numéro du mois pour lequel on souhaite récupérer le montant de l'aide
     * @return true si numeroMoisMontantARecuperer concerne un mois de la période de simulation
     *         false si numeroMoisMontantARecuperer concerne un mois avant la période de simulation
     */
    private boolean isNumeroMoisMontantARecupererDansPeriodeSimulation(int numeroMoisMontantARecuperer) {
        return numeroMoisMontantARecuperer > NUMERO_MOIS_PERIODE;
    }
    
    private Salaire getSalaireMontantZero() {
        Salaire salaire = new Salaire();
        salaire.setMontantBrut(0);
        salaire.setMontantNet(0);
        return salaire;
    }
}
