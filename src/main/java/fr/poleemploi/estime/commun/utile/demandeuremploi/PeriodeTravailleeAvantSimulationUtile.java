package fr.poleemploi.estime.commun.utile.demandeuremploi;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.MoisTravailleAvantSimulation;
import fr.poleemploi.estime.services.ressources.Salaire;

@Component
public class PeriodeTravailleeAvantSimulationUtile {

    @Autowired
    private BeneficiaireAidesUtile beneficiaireAidesUtile;
    
    @Autowired
    private SalaireUtile salaireUtile;

    public Optional<MoisTravailleAvantSimulation> getMoisTravailleAvantSimulation(DemandeurEmploi demandeurEmploi, int nMoisAvant) {
        if(hasSalairesAvantPeriodeSimulation(demandeurEmploi)) {
            switch (nMoisAvant) {
            case 1:
                return Optional.of(demandeurEmploi.getRessourcesFinancieres().getPeriodeTravailleeAvantSimulation().getMoisMoins1());
            case 2:                
                return Optional.of(demandeurEmploi.getRessourcesFinancieres().getPeriodeTravailleeAvantSimulation().getMoisMoins2());            
            case 3:
                return Optional.of(demandeurEmploi.getRessourcesFinancieres().getPeriodeTravailleeAvantSimulation().getMoisMoins3());
            default:
                return Optional.empty();
            }
        }
        return Optional.empty();
    }  

    /**
     * Fonction qui permet de récupérer le nombre de mois travaillés dans la péridoe de 3 mois avant la simulation
     * @param demandeurEmploi
     * @return nombre mois travailles au cours des 3 derniers mois
     */
    public int getNombreMoisTravaillesAuCoursDes3DerniersMoisAvantSimulation(DemandeurEmploi demandeurEmploi) {
        int periodeMoisAvantSimulation = 3;
        int nombreMoisTravaillesDerniersMois = 0;
        for (int moisAvantSimulation = 1; moisAvantSimulation <= periodeMoisAvantSimulation ; moisAvantSimulation++) {
            Optional<MoisTravailleAvantSimulation> moisTravailleAvantSimulation = getMoisTravailleAvantSimulation(demandeurEmploi, moisAvantSimulation);
            if(moisTravailleAvantSimulation.isPresent() && isMoisTravaille(moisTravailleAvantSimulation.get())) {
                nombreMoisTravaillesDerniersMois++;                    
            }                
        }
        return nombreMoisTravaillesDerniersMois;
    }   

    public Salaire getSalaireAvantPeriodeSimulation(DemandeurEmploi demandeurEmploi, int numeroMoisPeriodeOpenfisca) {
        if(beneficiaireAidesUtile.isBeneficiaireASS(demandeurEmploi)) {
            return getSalaireAvantPeriodeSimulationDemandeurASS(demandeurEmploi, numeroMoisPeriodeOpenfisca);
        } 
        if(beneficiaireAidesUtile.isBeneficiaireAAH(demandeurEmploi)) {
            return getSalaireAvantPeriodeSimulationDemandeurAAH(demandeurEmploi, numeroMoisPeriodeOpenfisca);
        }  
        if(beneficiaireAidesUtile.isBeneficiaireRSA(demandeurEmploi)) {
            return getSalaireAvantPeriodeSimulationDemandeurRSA(demandeurEmploi, numeroMoisPeriodeOpenfisca);
        }  
        return salaireUtile.getSalaireMontantZero();
    }    

    public Salaire getSalaireAvantPeriodeSimulationDemandeurASS(DemandeurEmploi demandeurEmploi, int numeroMoisPeriodeOpenfisca) {
        int nombreMoisCumulesAssEtSalaire = getNombreMoisTravaillesAuCoursDes3DerniersMoisAvantSimulation(demandeurEmploi);
        return getMontantSalaireAvantPeriodeSimulationDemandeurASS(demandeurEmploi, nombreMoisCumulesAssEtSalaire, numeroMoisPeriodeOpenfisca);
    }

    public Salaire getSalaireAvantPeriodeSimulationDemandeurRSA(DemandeurEmploi demandeurEmploi, int numeroMoisPeriodeOpenfisca) {
        Integer prochaineDeclarationRSA = demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getProchaineDeclarationRSA();
        if((prochaineDeclarationRSA == 3 || prochaineDeclarationRSA == 0) && numeroMoisPeriodeOpenfisca == 0) {
            return getSalaireAvantSimulation(demandeurEmploi, 1);            
        }        
        if(prochaineDeclarationRSA == 1) {                
            return getSalaireDemandeurRsaProchaineDeclaration2MoisApresSimulation(demandeurEmploi,numeroMoisPeriodeOpenfisca);
        }
        if(prochaineDeclarationRSA == 2) {
            return getSalaireDemandeurRsaProchaineDeclaration3MoisApresSimulation(demandeurEmploi,numeroMoisPeriodeOpenfisca);               
        }
        return salaireUtile.getSalaireMontantZero();
    }

    public Salaire getMontantSalaireAvantPeriodeSimulationDemandeurASS(DemandeurEmploi demandeurEmploi, int nombreMoisCumulesAssEtSalaire, int numeroMoisPeriodeOpenfisca) {
        if(nombreMoisCumulesAssEtSalaire == 2 && numeroMoisPeriodeOpenfisca == 0) {
            return getSalaireAvantSimulation(demandeurEmploi, 1);
        }
        if(nombreMoisCumulesAssEtSalaire == 3) {
            if(numeroMoisPeriodeOpenfisca == 0) {
                return getSalaireAvantSimulation(demandeurEmploi, 2);
            }
            if(numeroMoisPeriodeOpenfisca == 1) {
                return getSalaireAvantSimulation(demandeurEmploi, 1);
            }
        }
        if(nombreMoisCumulesAssEtSalaire == 4) {
            if(numeroMoisPeriodeOpenfisca == 0) {
                return getSalaireAvantSimulation(demandeurEmploi, 3);
            }
            if(numeroMoisPeriodeOpenfisca == 1) {
                return getSalaireAvantSimulation(demandeurEmploi, 2);
            }
            if(numeroMoisPeriodeOpenfisca == 2) {
                return getSalaireAvantSimulation(demandeurEmploi, 1);
            }
        }
        return salaireUtile.getSalaireMontantZero();
    }

    public boolean hasSalairesAvantPeriodeSimulation(DemandeurEmploi demandeurEmploi) {
        return demandeurEmploi.getRessourcesFinancieres() != null && demandeurEmploi.getRessourcesFinancieres().getPeriodeTravailleeAvantSimulation() != null;
    }    

    private Salaire getSalaireAvantPeriodeSimulationDemandeurAAH(DemandeurEmploi demandeurEmploi, int numeroMoisPeriodeOpenfisca) {
        int nombreMoisTravaillesDerniersMois = demandeurEmploi.getRessourcesFinancieres().getNombreMoisTravaillesDerniersMois();
        if(nombreMoisTravaillesDerniersMois > 0) {
            if(numeroMoisPeriodeOpenfisca == 0) {
                return demandeurEmploi.getRessourcesFinancieres().getPeriodeTravailleeAvantSimulation().getMoisMoins2().getSalaire();            
            }
            if(numeroMoisPeriodeOpenfisca == 1) {
                return demandeurEmploi.getRessourcesFinancieres().getPeriodeTravailleeAvantSimulation().getMoisMoins1().getSalaire();            
            }
        }
        return salaireUtile.getSalaireMontantZero();
    }

    private Salaire getSalaireAvantSimulation(DemandeurEmploi demandeurEmploi, int nMoisAvant) {
        switch (nMoisAvant) {
        case 1:
            return demandeurEmploi.getRessourcesFinancieres().getPeriodeTravailleeAvantSimulation().getMoisMoins1().getSalaire();
        case 2:                
            return demandeurEmploi.getRessourcesFinancieres().getPeriodeTravailleeAvantSimulation().getMoisMoins2().getSalaire();
        case 3:
            return demandeurEmploi.getRessourcesFinancieres().getPeriodeTravailleeAvantSimulation().getMoisMoins3().getSalaire();            
        default:
            return salaireUtile.getSalaireMontantZero();
        }
    }

    private Salaire getSalaireDemandeurRsaProchaineDeclaration2MoisApresSimulation(DemandeurEmploi demandeurEmploi, int numeroMoisPeriodeOpenfisca) {
        if(numeroMoisPeriodeOpenfisca == 0) {
            return getSalaireAvantSimulation(demandeurEmploi, 3);                            
        }            
        if(numeroMoisPeriodeOpenfisca == 1) {
            return getSalaireAvantSimulation(demandeurEmploi, 2);            
        }            
        if(numeroMoisPeriodeOpenfisca == 2) {
            return getSalaireAvantSimulation(demandeurEmploi, 1);            
        }
        return salaireUtile.getSalaireMontantZero();
    }

    private Salaire getSalaireDemandeurRsaProchaineDeclaration3MoisApresSimulation(DemandeurEmploi demandeurEmploi, int numeroMoisPeriodeOpenfisca) {
        if(numeroMoisPeriodeOpenfisca == 0) {
            return getSalaireAvantSimulation(demandeurEmploi, 2);                            
        }            
        if(numeroMoisPeriodeOpenfisca == 1) {
            return getSalaireAvantSimulation(demandeurEmploi, 1);            
        }    
        return salaireUtile.getSalaireMontantZero();
    }

    private boolean isMoisTravaille(MoisTravailleAvantSimulation moisTravailleAvantSimulation) {
        return !moisTravailleAvantSimulation.isSansSalaire() && moisTravailleAvantSimulation.getSalaire().getMontantNet() > 0;
    }
}
