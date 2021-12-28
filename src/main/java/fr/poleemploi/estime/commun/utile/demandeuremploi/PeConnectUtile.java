package fr.poleemploi.estime.commun.utile.demandeuremploi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.AccessTokenPEIOOut;
import fr.poleemploi.estime.clientsexternes.poleemploiio.utile.AccessTokenUtile;
import fr.poleemploi.estime.services.ressources.PeConnectAuthorization;

@Component
public class PeConnectUtile {
    
    @Autowired
    private AccessTokenUtile bearerTokenUtile;

    public PeConnectAuthorization mapInformationsAccessTokenPeConnect(AccessTokenPEIOOut peConnectAuthorizationESD) {
        PeConnectAuthorization peConnectAuthorization = new PeConnectAuthorization();
        peConnectAuthorization.setBearerToken(bearerTokenUtile.getBearerToken(peConnectAuthorizationESD.getAccessToken()));
        peConnectAuthorization.setExpireIn(peConnectAuthorizationESD.getExpiresIn());
        peConnectAuthorization.setIdToken(peConnectAuthorizationESD.getIdToken());
        peConnectAuthorization.setRefreshToken(peConnectAuthorizationESD.getRefreshToken());
        peConnectAuthorization.setScope(peConnectAuthorizationESD.getScope());
        peConnectAuthorization.setTokenType(peConnectAuthorizationESD.getTokenType());
        return peConnectAuthorization;
    }
}
