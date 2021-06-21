package fr.poleemploi.estime.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import fr.poleemploi.estime.commun.enumerations.Nationalites;
import fr.poleemploi.estime.commun.enumerations.TypesContratTravail;
import fr.poleemploi.estime.commun.enumerations.exceptions.BadRequestMessages;
import fr.poleemploi.estime.commun.utile.demandeuremploi.TypeContratUtile;
import fr.poleemploi.estime.services.exceptions.BadRequestException;
import fr.poleemploi.estime.services.ressources.AllocationsCAF;
import fr.poleemploi.estime.services.ressources.AllocationsPoleEmploi;
import fr.poleemploi.estime.services.ressources.BeneficiaireAidesSociales;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.FuturTravail;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;
import fr.poleemploi.estime.services.ressources.Personne;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;
import fr.poleemploi.estime.services.ressources.Salaire;
import fr.poleemploi.estime.services.ressources.SituationFamiliale;
import fr.poleemploi.test.utile.TestUtile;


@SpringBootTest
@ContextConfiguration
@TestPropertySource(locations="classpath:application-test.properties")
class DemandeurEmploiServicesControleDonneesTests {

    @Autowired
    private IndividuService individuService;
    
    @Autowired
    private TypeContratUtile typeContratUtile;
    
    @Autowired
    private TestUtile testUtile;
    
    @Configuration
    @ComponentScan({"fr.poleemploi.test","fr.poleemploi.estime"})
    public static class SpringConfig {

    }

    @Test
    void controlerDonneeesEntreeDemandeurEmploiTest1() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAidesSociales(null);
        }).getMessage()).isEqualTo(BadRequestMessages.DEMANDEUR_EMPLOI_OBLIGATOIRE.getMessage());
    }
    
    /*********************   Futur Travail ******************/
    
    @Test
    void controlerDonneeesEntreeFuturTravailTest1() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        demandeurEmploi.setBeneficiaireAidesSociales(new BeneficiaireAidesSociales());
        demandeurEmploi.setFuturTravail(null);
        demandeurEmploi.setInformationsPersonnelles(creerInformationsPersonnelles());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());
        
        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAidesSociales(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "futurTravail"));
    }
    
    @Test
    void controlerDonneeesEntreeFuturTravailTest2() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        demandeurEmploi.setBeneficiaireAidesSociales(new BeneficiaireAidesSociales());
        demandeurEmploi.setInformationsPersonnelles(creerInformationsPersonnelles());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());
        
        FuturTravail futurTravail = creerFuturTravail();
        futurTravail.setTypeContrat(null);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAidesSociales(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "typeContrat de futurTravail")));
    }
    
    @Test
    void controlerDonneeesEntreeFuturTravailTest3() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        demandeurEmploi.setBeneficiaireAidesSociales(new BeneficiaireAidesSociales());
        demandeurEmploi.setInformationsPersonnelles(creerInformationsPersonnelles());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());
        
        FuturTravail futurTravail = creerFuturTravail();
        futurTravail.setTypeContrat("TOTO");
        demandeurEmploi.setFuturTravail(futurTravail);
        
        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAidesSociales(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(BadRequestMessages.TYPE_CONTRAT_INCORRECT.getMessage(), typeContratUtile.getListeFormateeTypesContratPossibles()));
    }
    
    @Test
    void controlerDonneeesEntreeFuturTravailTest4() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        demandeurEmploi.setBeneficiaireAidesSociales(new BeneficiaireAidesSociales());
        demandeurEmploi.setInformationsPersonnelles(creerInformationsPersonnelles());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());
        
        FuturTravail futurTravail = creerFuturTravail();
        futurTravail.setNombreMoisContratCDD(null);
        futurTravail.setTypeContrat(TypesContratTravail.CDD.name());
        demandeurEmploi.setFuturTravail(futurTravail);
        
        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAidesSociales(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "nombreMoisContratCDD de futurTravail"));
    }
    
    @Test
    void controlerDonneeesEntreeFuturTravailTest5() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        demandeurEmploi.setBeneficiaireAidesSociales(new BeneficiaireAidesSociales());
        demandeurEmploi.setInformationsPersonnelles(creerInformationsPersonnelles());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());
        
        FuturTravail futurTravail = creerFuturTravail();
        futurTravail.setNombreHeuresTravailleesSemaine(0);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAidesSociales(demandeurEmploi);
        }).getMessage()).isEqualTo(BadRequestMessages.NOMBRE_HEURE_TRAVAILLEES_ZERO.getMessage());
    }
    
    @Test
    void controlerDonneeesEntreeFuturTravailTest6() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        demandeurEmploi.setBeneficiaireAidesSociales(new BeneficiaireAidesSociales());
        demandeurEmploi.setInformationsPersonnelles(creerInformationsPersonnelles());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());
        
        FuturTravail futurTravail = creerFuturTravail();
        Salaire salaire = new Salaire();
        salaire.setMontantNet(0);
        salaire.setMontantBrut(0);
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAidesSociales(demandeurEmploi);
        }).getMessage()).isEqualTo(BadRequestMessages.SALAIRE_MENSUEL_NET_ZERO.getMessage());
    }
    
    @Test
    void controlerDonneeesEntreeFuturTravailTest7() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        demandeurEmploi.setBeneficiaireAidesSociales(new BeneficiaireAidesSociales());
        demandeurEmploi.setInformationsPersonnelles(creerInformationsPersonnelles());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());
        
        FuturTravail futurTravail = creerFuturTravail();
        Salaire salaire = new Salaire();
        salaire.setMontantNet(0);
        salaire.setMontantBrut(0);
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAidesSociales(demandeurEmploi);
        }).getMessage()).isEqualTo(BadRequestMessages.SALAIRE_MENSUEL_NET_ZERO.getMessage());
    }
    
    /**************       Beneficiaire Aides Sociales     **************/
    
    @Test
    void controlerDonneeesEntreeBeneficiaireAidesSocialesTest1() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        demandeurEmploi.setBeneficiaireAidesSociales(null);
        demandeurEmploi.setFuturTravail(creerFuturTravail());
        demandeurEmploi.setInformationsPersonnelles(creerInformationsPersonnelles());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());
               
        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAidesSociales(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "beneficiaireAidesSociales"));
    }
   
    /**************       Informations Identite     **************/
    
    @Test
    void controlerDonneeesEntreeInformationsPersonnellesTest1() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        demandeurEmploi.setBeneficiaireAidesSociales(new BeneficiaireAidesSociales());
        demandeurEmploi.setFuturTravail(creerFuturTravail());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());
        
        demandeurEmploi.setInformationsPersonnelles(null);

        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAidesSociales(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "informationsPersonnelles"));
    }
    
    @Test
    void controlerDonneeesEntreeInformationsPersonnellesTest2() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        demandeurEmploi.setBeneficiaireAidesSociales(new BeneficiaireAidesSociales());
        demandeurEmploi.setFuturTravail(creerFuturTravail());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());
        
        InformationsPersonnelles informationsPersonnelles = creerInformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(null);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAidesSociales(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "dateNaissance de informationsPersonnelles"));
    }
    
    @Test
    void controlerDonneeesEntreeInformationsPersonnellesTest3() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        demandeurEmploi.setBeneficiaireAidesSociales(new BeneficiaireAidesSociales());
        demandeurEmploi.setFuturTravail(creerFuturTravail());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());
        
        InformationsPersonnelles informationsPersonnelles = creerInformationsPersonnelles();
        informationsPersonnelles.setCodePostal(null);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAidesSociales(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "codePostal de informationsPersonnelles"));
    }
    
    @Test
    void controlerDonneeesEntreeInformationsPersonnellesTest4() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        demandeurEmploi.setBeneficiaireAidesSociales(new BeneficiaireAidesSociales());
        demandeurEmploi.setFuturTravail(creerFuturTravail());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());
        
        InformationsPersonnelles informationsPersonnelles = creerInformationsPersonnelles();
        informationsPersonnelles.setNationalite(null);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAidesSociales(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "nationalite de informationsPersonnelles"));
    }
    
    @Test
    void controlerDonneeesEntreeInformationsPersonnellesTest5() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        demandeurEmploi.setBeneficiaireAidesSociales(new BeneficiaireAidesSociales());
        demandeurEmploi.setFuturTravail(creerFuturTravail());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());
        
        InformationsPersonnelles informationsPersonnelles = creerInformationsPersonnelles();
        informationsPersonnelles.setNationalite(Nationalites.AUTRE.getValeur());
        informationsPersonnelles.setTitreSejourEnFranceValide(null);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAidesSociales(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "titreSejourEnFranceValide de informationsPersonnelles"));
    }
    
    /**************       Ressources Financieres     **************/

    @Test
    void controlerDonneeesEntreeRessourcesFinancieresDemandeurASSTest1() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireASS(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        demandeurEmploi.setFuturTravail(creerFuturTravail());
        demandeurEmploi.setInformationsPersonnelles(creerInformationsPersonnelles());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());

        
        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAidesSociales(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "ressourcesFinancieres dans DemandeurEmploi"));
    }
    
    @Test
    void controlerDonneeesEntreeRessourcesFinancieresDemandeurASSTest2() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireASS(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        demandeurEmploi.setFuturTravail(creerFuturTravail());
        demandeurEmploi.setInformationsPersonnelles(creerInformationsPersonnelles());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);

        
        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAidesSociales(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "allocationsPoleEmploi dans RessourcesFinancieres de DemandeurEmploi"));
    }
    
    @Test
    void controlerDonneeesEntreeRessourcesFinancieresDemandeurASSTest3() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireASS(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        demandeurEmploi.setFuturTravail(creerFuturTravail());
        demandeurEmploi.setInformationsPersonnelles(creerInformationsPersonnelles());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());

        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsPoleEmploi allocationsPoleEmploi = new AllocationsPoleEmploi();
        allocationsPoleEmploi.setAllocationJournaliereNet(0f);
        ressourcesFinancieres.setAllocationsPoleEmploi(allocationsPoleEmploi);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);

        
        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAidesSociales(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(BadRequestMessages.MONTANT_INCORRECT_INFERIEUR_EGAL_ZERO.getMessage(), "allocationJournaliereNetASS"));
    }
    
    @Test
    void controlerDonneeesEntreeRessourcesFinancieresDemandeurASSTest4() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireASS(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        demandeurEmploi.setFuturTravail(creerFuturTravail());
        demandeurEmploi.setInformationsPersonnelles(creerInformationsPersonnelles());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());

        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsPoleEmploi allocationsPoleEmploi = new AllocationsPoleEmploi();
        allocationsPoleEmploi.setAllocationJournaliereNet(16.89f);
        ressourcesFinancieres.setAllocationsPoleEmploi(allocationsPoleEmploi);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);

        
        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAidesSociales(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "dateDerniereOuvertureDroitASS dans AllocationsPoleEmploi dans RessourcesFinancieres de DemandeurEmploi"));
    }
    
    
    @Test
    void controlerDonneeesEntreeRessourcesFinancieresDemandeurASSTest5() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireASS(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        demandeurEmploi.setFuturTravail(creerFuturTravail());
        demandeurEmploi.setInformationsPersonnelles(creerInformationsPersonnelles());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());

        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsPoleEmploi allocationsPoleEmploi = new AllocationsPoleEmploi();
        allocationsPoleEmploi.setAllocationJournaliereNet(16.89f);
        allocationsPoleEmploi.setDateDerniereOuvertureDroitASS(testUtile.getDate("14-04-2020"));
        ressourcesFinancieres.setAllocationsPoleEmploi(allocationsPoleEmploi);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);

        
        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAidesSociales(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "hasTravailleAuCoursDerniersMois dans RessourcesFinancieres"));
    }
    
    @Test
    void controlerDonneeesEntreeRessourcesFinancieresDemandeurAAHTest1() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireAAH(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        demandeurEmploi.setFuturTravail(creerFuturTravail());
        demandeurEmploi.setInformationsPersonnelles(creerInformationsPersonnelles());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());

        
        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAidesSociales(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "ressourcesFinancieres dans DemandeurEmploi"));
    }
    
    @Test
    void controlerDonneeesEntreeRessourcesFinancieresDemandeurAAHTest2() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireAAH(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        demandeurEmploi.setFuturTravail(creerFuturTravail());
        demandeurEmploi.setInformationsPersonnelles(creerInformationsPersonnelles());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);

        
        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAidesSociales(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "allocationsCAF dans RessourcesFinancieres de DemandeurEmploi"));
    }
    
    @Test
    void controlerDonneeesEntreeRessourcesFinancieresDemandeurAAHTest3() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireAAH(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        demandeurEmploi.setFuturTravail(creerFuturTravail());
        demandeurEmploi.setInformationsPersonnelles(creerInformationsPersonnelles());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());

        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationMensuelleNetAAH(0f);
        ressourcesFinancieres.setAllocationsCAF(allocationsCAF);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);

        
        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAidesSociales(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(BadRequestMessages.MONTANT_INCORRECT_INFERIEUR_EGAL_ZERO.getMessage(), "allocationMensuelleNetAAH"));
    }
    
    @Test
    void controlerDonneeesEntreeRessourcesFinancieresDemandeurAAHTest4() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireAAH(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        demandeurEmploi.setFuturTravail(creerFuturTravail());
        demandeurEmploi.setInformationsPersonnelles(creerInformationsPersonnelles());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());

        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationMensuelleNetAAH(900f);
        ressourcesFinancieres.setAllocationsCAF(allocationsCAF);
        ressourcesFinancieres.setNombreMoisTravaillesDerniersMois(null);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);

        
        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAidesSociales(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "nombreMoisTravaillesDerniersMois dans RessourcesFinancieres"));
    }
    
    /*************  Situation Familiale ***************/
    
    @Test
    void controlerDonneeesEntreeSituationFamilialeTest1() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        demandeurEmploi.setBeneficiaireAidesSociales(new BeneficiaireAidesSociales());
        demandeurEmploi.setFuturTravail(creerFuturTravail());
        demandeurEmploi.setInformationsPersonnelles(creerInformationsPersonnelles());
        
        demandeurEmploi.setSituationFamiliale(null);

        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAidesSociales(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "situationFamiliale"));
    }
    
    @Test
    void controlerDonneeesEntreeSituationFamilialeTest2() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        demandeurEmploi.setBeneficiaireAidesSociales(new BeneficiaireAidesSociales());
        demandeurEmploi.setFuturTravail(creerFuturTravail());
        demandeurEmploi.setInformationsPersonnelles(creerInformationsPersonnelles());
        
        SituationFamiliale situationFamiliale = creerSituationFamiliale();
        situationFamiliale.setIsEnCouple(null);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);

        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAidesSociales(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "enCouple de situationFamiliale"));
    }
    
    @Test
    void controlerDonneeesEntreeSituationFamilialeTest3() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        demandeurEmploi.setBeneficiaireAidesSociales(new BeneficiaireAidesSociales());
        demandeurEmploi.setFuturTravail(creerFuturTravail());
        demandeurEmploi.setInformationsPersonnelles(creerInformationsPersonnelles());
        
        SituationFamiliale situationFamiliale = creerSituationFamiliale();
        situationFamiliale.setConjoint(null);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);

        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAidesSociales(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "conjoint de situationFamiliale"));
    }
    

    private SituationFamiliale creerSituationFamiliale() {
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        situationFamiliale.setIsEnCouple(true);
        Personne personne = new Personne();
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(LocalDate.now());
        personne.setInformationsPersonnelles(informationsPersonnelles);
        situationFamiliale.setConjoint(personne);
        return situationFamiliale;
    }
    
    /************ Utile tests **************/
    
    private InformationsPersonnelles creerInformationsPersonnelles() {
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(LocalDate.now());
        informationsPersonnelles.setNationalite(Nationalites.FRANCAISE.getValeur());
        informationsPersonnelles.setCodePostal("44200");
        return informationsPersonnelles;
    }
    
    private FuturTravail creerFuturTravail() {
        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setNombreMoisContratCDD(6);
        futurTravail.setDistanceKmDomicileTravail(50);
        futurTravail.setNombreHeuresTravailleesSemaine(15);
        Salaire salaire = new Salaire();
        salaire.setMontantNet(900);
        salaire.setMontantBrut(1200);
        futurTravail.setSalaire(salaire);        
        futurTravail.setTypeContrat(TypesContratTravail.CDD.name());
        return futurTravail;
    }
}
