package fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile;

import java.math.BigDecimal;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.poleemploiio.PoleEmploiIOClient;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ArePEIOIn;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ArePEIOOut;
import fr.poleemploi.estime.commun.utile.AccesTokenUtile;
import fr.poleemploi.estime.logique.simulateur.aides.utile.AideUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.commun.enumerations.Aides;
import fr.poleemploi.estime.commun.enumerations.Organismes;
import fr.poleemploi.estime.commun.enumerations.exceptions.LoggerMessages;

@Component
public class AreUtile {
	@Autowired
	private AccesTokenUtile accesTokenUtile;

	@Autowired
	private AideUtile aideUtile;

	@Autowired
	private PoleEmploiIOClient poleEmploiIOClient;
	
	 private static final Logger LOGGER = LoggerFactory.getLogger(AreUtile.class);
	
	public Optional<Aide> simulerAide(DemandeurEmploi demandeurEmploi) {
		String bearerToken = accesTokenUtile.getBearerToken(demandeurEmploi.getPeConnectAuthorization().getAccessToken());
		ArePEIOIn areIn = remplirAreIn(demandeurEmploi);
		Optional<ArePEIOOut> optionalAreOut = poleEmploiIOClient.callAreEndPoint(areIn, bearerToken);
		if(optionalAreOut.isPresent()) { 
			ArePEIOOut areOut = optionalAreOut.get();
			float montantAide = areOut.getAllocationMensuelle();
			return Optional.of(creerAide(montantAide));           	
		}else {
			LOGGER.error(LoggerMessages.USER_INFO_KO.getMessage());
		} 
		return Optional.empty();
	}

	private Aide creerAide(float montantAide) {
		Aide are = new Aide();
		are.setCode(Aides.ALLOCATION_RETOUR_EMPLOI.getCode());
		Optional<String> detailAideOptional = aideUtile.getDescription(Aides.ALLOCATION_RETOUR_EMPLOI.getNomFichierDetail());
		if(detailAideOptional.isPresent()) {
			are.setDetail(detailAideOptional.get());
		}
		are.setNom(Aides.ALLOCATION_RETOUR_EMPLOI.getNom());
		are.setOrganisme(Organismes.PE.getNom());
		are.setMontant(montantAide);
		are.setReportee(false);
		return are;
	}

	private ArePEIOIn remplirAreIn(DemandeurEmploi demandeurEmploi) {
		ArePEIOIn areIn = new ArePEIOIn();
		areIn.setAllocationBruteJournaliere(demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE().getMontantJournalierBrut());
		areIn.setSalaireBrutJournalier(demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE().getSalaireJournalierReferenceBrut());
		areIn.setGainBrut(demandeurEmploi.getRessourcesFinancieres().getSalaire().getMontantBrut());
		return areIn;
	}
}
