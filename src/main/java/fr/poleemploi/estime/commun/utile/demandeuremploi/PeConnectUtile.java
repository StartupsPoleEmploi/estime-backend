package fr.poleemploi.estime.commun.utile.demandeuremploi;

import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.PeConnectAuthorizationPEIO;
import fr.poleemploi.estime.services.ressources.PeConnectAuthorization;

@Component
public class PeConnectUtile {

    public PeConnectAuthorization mapInformationsAccessTokenPeConnect(PeConnectAuthorizationPEIO peConnectAuthorizationESD) {
        PeConnectAuthorization peConnectAuthorization = new PeConnectAuthorization();
        peConnectAuthorization.setAccessToken(peConnectAuthorizationESD.getAccessToken());
        peConnectAuthorization.setExpireIn(peConnectAuthorizationESD.getExpiresIn());
        peConnectAuthorization.setIdToken(peConnectAuthorizationESD.getIdToken());
        peConnectAuthorization.setRefreshToken(peConnectAuthorizationESD.getRefreshToken());
        peConnectAuthorization.setScope(peConnectAuthorizationESD.getScope());
        peConnectAuthorization.setTokenType(peConnectAuthorizationESD.getTokenType());
        return peConnectAuthorization;
    }
}
