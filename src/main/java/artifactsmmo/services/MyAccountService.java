package artifactsmmo.services;

import artifactsmmo.enums.ErrorCode;
import artifactsmmo.exception.ItemsNotFoundException;
import artifactsmmo.models.entity.Gold;
import artifactsmmo.models.entity.ItemSimple;
import artifactsmmo.models.response.BankItemsResponse;
import artifactsmmo.models.response.GoldResponse;
import artifactsmmo.utils.RestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@Service
public class MyAccountService {

    @Autowired
    private RestTemplate restTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(MyAccountService.class);
    private final RestUtils restUtils = new RestUtils();

    /**
     * Récupère la quantité d'or stockée dans le compte bancaire de l'utilisateur.
     * <p>
     * Cette méthode envoie une requête GET à un service REST pour obtenir les détails de l'or disponible dans
     * le compte bancaire de l'utilisateur. Elle construit l'URL à partir de l'URL de base fournie par `restUtils`,
     * envoie la requête avec les en-têtes appropriés, puis traite la réponse. Si la requête est réussie et que la
     * réponse contient des informations sur l'or, cet objet est retourné. En cas d'erreur HTTP ou si la réponse est vide,
     * la méthode renvoie `null`.
     * </p>
     *
     * @return un objet {@link Gold} représentant la quantité d'or dans le compte bancaire, ou `null` si la requête échoue
     *         ou si les informations sur l'or ne sont pas trouvées.
     * @throws HttpClientErrorException si une erreur HTTP se produit lors de l'appel au service REST.
     */
    public Gold getBankGold() {
        LOGGER.debug("Entry in getBankGold");

        try {
            String url = restUtils.getMyAccountBankGoldUrl();
            HttpEntity<String> entity = restUtils.entityHeader();

            ResponseEntity<GoldResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, GoldResponse.class);
            LOGGER.info("GET {} : status : {}", url, response.getStatusCode());

            return Optional.ofNullable(response.getBody())
                    .map(GoldResponse::getGold)
                    .orElse(null);

        } catch (HttpClientErrorException e) {
            LOGGER.error("Erreur lors de l'appel au service REST : {}", e.getMessage(), e);
            handleException(e);
            return null;
        }
    }

    /**
     * Modifie le mot de passe de l'utilisateur en envoyant une requête POST à un service REST avec le nouveau mot de passe.
     * <p>
     * Cette méthode prend en entrée le nouveau mot de passe, crée le corps de la requête avec cette information,
     * et envoie une requête POST au service REST pour mettre à jour le mot de passe de l'utilisateur. Si la requête
     * est réussie, le corps de la réponse est retourné. En cas d'erreur HTTP ou si la requête échoue, la méthode renvoie `null`.
     * </p>
     *
     * @param password le nouveau mot de passe de l'utilisateur.
     * @return une chaîne de caractères représentant la réponse du service REST, ou `null` en cas d'échec de la requête
     *         ou si une erreur HTTP se produit.
     * @throws HttpClientErrorException si une erreur HTTP se produit lors de l'appel au service REST.
     */
    public String changePassword(String password) {
        LOGGER.debug("Entry in changePassword with parameter : password={}", password);

        try {
            // Create the request body as a MultiValueMap
            Map<String, String> body = new HashMap<>();
            body.put("password", password);

            String url = restUtils.getMyAccountChangePasswordUrl();
            HttpEntity<Object> entity = restUtils.entityHeader(body);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            LOGGER.info("POST {} : status : {}", url, response.getStatusCode());

            return response.getBody();

        } catch (HttpClientErrorException e) {
            LOGGER.error("Erreur lors de l'appel au service REST : {}", e.getMessage(), e);
            handleException(e);
            return null;
        }
    }
    /**
     * Récupère les articles de la banque en envoyant des requêtes GET paginées à un service REST.
     * <p>
     * Cette méthode permet de récupérer une liste d'articles de la banque à partir d'un service REST en utilisant la pagination.
     * Les paramètres de code d'article, de page et de taille sont utilisés pour construire l'URL de la requête.
     * La méthode continue de paginer tant que toutes les pages ne sont pas récupérées ou qu'une erreur survient.
     * </p>
     *
     * @param codeItem le code de l'article à rechercher. Peut être `null` pour récupérer tous les articles.
     * @param page le numéro de la page à récupérer, indexé à partir de 0.
     * @param size le nombre d'éléments par page.
     * @return une liste de {@link ItemSimple} représentant les articles récupérés de la banque.
     *         Si une erreur se produit ou si aucun article n'est trouvé, la méthode renvoie une liste vide.
     * @throws HttpClientErrorException si une erreur HTTP se produit lors de l'appel au service REST.
     */
    public List<ItemSimple> getBankItems(String codeItem, int page, int size) {
        LOGGER.debug("Entry in getBankItems with parameter : codeItem={}, page={}, size={}", codeItem, page, size);

        List<ItemSimple> items = new ArrayList<>();
        try {
            HttpEntity<String> entity = restUtils.entityHeader();

            while (true) {
                // Create URL with params
                String url = UriComponentsBuilder.fromHttpUrl(restUtils.getMyBankItems())
                        .queryParamIfPresent("item_code", Optional.ofNullable(codeItem))
                        .queryParamIfPresent("page", Optional.of(page))
                        .queryParamIfPresent("size", Optional.of(size))
                        .encode()
                        .toUriString();

                ResponseEntity<BankItemsResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, BankItemsResponse.class);
                LOGGER.info("GET {} status : {}", url, response.getStatusCode());

                if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                    break; // Break if the response is not OK or body is null
                }

                BankItemsResponse bankItemsResponse = response.getBody();
                items.addAll(bankItemsResponse.getItems());

                // Verify if we continue to the next page
                if (bankItemsResponse.getPage() >= bankItemsResponse.getPages()) {
                    break; // Get out if we are on the last page
                }

                page++; // Increment page to the next iteration
            }
            return items;
        } catch (HttpClientErrorException e) {
            LOGGER.error("Erreur lors de l'appel au service REST : {}", e.getMessage());
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
            case 404:
                throw new ItemsNotFoundException(ErrorCode.ITEMS_NOT_FOUND.getReason());
            case 458:
                throw new RuntimeException(ErrorCode.USE_DIFFERENT_PASSWORD.getReason(), e);
            default:
                throw new HttpClientErrorException(e.getStatusCode(), e.getMessage());
        }
    }
}
