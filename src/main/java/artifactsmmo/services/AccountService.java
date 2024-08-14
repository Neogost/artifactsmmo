package artifactsmmo.services;

import artifactsmmo.enums.ErrorCode;
import artifactsmmo.models.response.CreateAccountResponse;
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
public class AccountService {


    @Autowired
    private RestTemplate restTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountService.class);
    private final RestUtils restUtils = new RestUtils();

    /**
     * Crée un compte utilisateur en envoyant une requête POST à un service REST avec les informations fournies.
     * <p>
     * Cette méthode prend en entrée un nom d'utilisateur, un mot de passe et une adresse e-mail, puis les envoie
     * à un service REST pour créer un nouveau compte utilisateur. Si la création du compte est réussie,
     * le message de réponse du service est renvoyé. En cas d'erreur HTTP, une exception est gérée, et `null` est retourné.
     * </p>
     *
     * @param username le nom d'utilisateur à associer au nouveau compte
     * @param password le mot de passe du compte
     * @param email l'adresse e-mail associée au compte
     * @return le message de réponse du service REST après la création du compte, ou `null` en cas d'erreur
     * @throws HttpClientErrorException si une erreur HTTP se produit lors de l'appel au service REST
     */
    public String createAccount(String username, String password, String email) {
        LOGGER.debug("Entry in createAccount with parameters : username={}, password={}, email={}",
                username, password, email);

        try {
            String url = restUtils.getAccountCreateUrl();
            // Create the request body as a MultiValueMap
            Map<String, String> body = new HashMap<>();
            body.put("username", username);
            body.put("password", password);
            body.put("email", email);
            HttpEntity<Object> entity = restUtils.entityHeader(body);

            ResponseEntity<CreateAccountResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, CreateAccountResponse.class);
            LOGGER.info("POST {} status : {}", url, response.getStatusCode());

            return Objects.requireNonNull(response.getBody()).getMessage();
        } catch (HttpClientErrorException e) {
            LOGGER.error("Erreur lors de l'appel au service REST : {}", e.getMessage(), e);
            handleException(e);
        }
        return null;
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
            case 456:
                throw new RuntimeException(ErrorCode.USERNAME_ALREADY_USED.getReason(), e);
            case 457:
                throw new RuntimeException(ErrorCode.EMAIL_ALREADY_USED.getReason(), e);
            default:
                throw new HttpClientErrorException(e.getStatusCode(), e.getMessage());
        }
    }
}
