package artifactsmmo.services;

import artifactsmmo.enums.ErrorCode;
import artifactsmmo.models.response.GenerateTokenResponse;
import artifactsmmo.utils.RestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class TokenService {

    @Autowired
    private RestTemplate restTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenService.class);
    private final RestUtils restUtils = new RestUtils();

    /**
     * Génère un jeton d'authentification en envoyant une requête POST à un service REST avec les informations d'identification fournies.
     * <p>
     * Cette méthode prend en entrée un nom d'utilisateur et un mot de passe, puis les envoie
     * à un service REST pour générer un jeton d'authentification. Si la génération du jeton est réussie,
     * le message de réponse du service (contenant le jeton) est renvoyé. En cas d'erreur HTTP, une exception est gérée,
     * et `null` est retourné.
     * </p>
     *
     * @param username le nom d'utilisateur à utiliser pour l'authentification
     * @param password le mot de passe à utiliser pour l'authentification
     * @return le jeton d'authentification renvoyé par le service REST, ou `null` en cas d'erreur
     * @throws HttpClientErrorException si une erreur HTTP se produit lors de l'appel au service REST
     */
    public String generateToken(String username, String password) {
        LOGGER.debug("Entry in generateToken with parameter : username={}, password={}", username, password);

        try {
            String url = restUtils.getTokenUrl();

            // Create the request body as a MultiValueMap
            Map<String, Object> body = new HashMap<>();
            body.put("username", username);
            body.put("password", password);

            HttpEntity<Object> entity = restUtils.entityHeader(body);

            ResponseEntity<GenerateTokenResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, GenerateTokenResponse.class);
            LOGGER.info("POST {} status : {}", url, response.getStatusCode());

            return Objects.requireNonNull(response.getBody()).getMessage();
        } catch (HttpClientErrorException e) {
            LOGGER.error("Erreur lors de l'appel au service REST : {}", e.getMessage(), e);
            handleException(e);
            return null;
        }
    }

    /**
     * Gère les exceptions {@link HttpClientErrorException} en fonction du code d'état HTTP.
     *
     * <p>Cette méthode traite les exceptions HTTP en lançant des exceptions spécifiques selon le code d'état de la réponse HTTP.
     * Si le code d'état est 404 (Not Found), une exception RuntimeException est lancée avec un message d'erreur spécifique.
     * Pour tous les autres codes d'état HTTP, une {@link HttpClientErrorException} est relancée avec le même code d'état et message.</p>
     *
     * @param e l'exception {@link HttpClientErrorException} à traiter.
     * @throws RuntimeException         si le code d'état HTTP est 404 (Not Found), avec un message d'erreur spécifique.
     * @throws HttpClientErrorException pour tout autre code d'état HTTP, relancée avec le code et le message d'origine.
     */
    private void handleException(HttpClientErrorException e) {
        switch (e.getStatusCode().value()) {
            case 455:
                throw new RuntimeException(ErrorCode.TOKEN_GENERATION_FAIL.getReason(), e);
            default:
                throw new HttpClientErrorException(e.getStatusCode(), e.getMessage());
        }
    }
}
