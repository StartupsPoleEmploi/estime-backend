package fr.poleemploi.estime.commun.utile.demandeuremploi;

import org.springframework.stereotype.Component;

import fr.poleemploi.estime.services.ressources.Salaire;

@Component
public class SalaireUtile {

    public Salaire getSalaireMontantZero() {
	Salaire salaire = new Salaire();
	salaire.setMontantMensuelBrut(0);
	salaire.setMontantMensuelNet(0);
	return salaire;
    }
}
