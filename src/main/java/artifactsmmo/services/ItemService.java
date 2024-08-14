package artifactsmmo.services;

import artifactsmmo.enums.ErrorCode;
import artifactsmmo.models.entity.Item;
import artifactsmmo.models.response.ItemResponse;
import artifactsmmo.models.response.ItemsResponse;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ItemService {


    @Autowired
    private RestTemplate restTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemService.class);
    private final RestUtils restUtils = new RestUtils();

    /**
     * Récupère une liste d'items en fonction des critères de recherche spécifiés.
     *
     * <p>Cette méthode envoie des requêtes HTTP GET pour obtenir des items depuis le service REST en utilisant divers paramètres
     * de recherche tels que le matériau de fabrication, la compétence de fabrication, les niveaux maximaux et minimaux, le nom,
     * la page et la taille. Les résultats sont récupérés page par page jusqu'à ce que toutes les pages aient été traitées.</p>
     *
     * <p>Les paramètres de recherche sont ajoutés dynamiquement à l'URL de la requête. La méthode utilise un en-tête HTTP sans
     * jeton d'authentification. En cas d'erreur HTTP, la méthode {@link #handleException(HttpClientErrorException, String)} est
     * appelée pour gérer l'exception, et un log d'erreur est généré.</p>
     *
     * @param craftMaterial le matériau de fabrication utilisé pour filtrer les items. Peut être {@code null} si non utilisé.
     * @param craftSkill la compétence de fabrication utilisée pour filtrer les items. Peut être {@code null} si non utilisé.
     * @param maxLevel le niveau maximum des items à récupérer.
     * @param minLevel le niveau minimum des items à récupérer.
     * @param name le nom de l'item à rechercher. Peut être {@code null} si non utilisé.
     * @param page le numéro de la page à récupérer, utilisé pour la pagination des résultats.
     * @param size la taille de chaque page, c'est-à-dire le nombre d'items par page.
     * @param type le type d'item à récupérer. Peut être {@code null} si non utilisé.
     * @return une liste d'objets {@link Item} correspondant aux critères de recherche spécifiés. Si aucune donnée n'est trouvée
     *         ou en cas d'erreur, la liste sera vide.
     * @throws HttpClientErrorException si une erreur HTTP survient lors de l'appel au service REST. Les erreurs sont
     *                                  loguées et traitées par la méthode {@link #handleException(HttpClientErrorException, String)}.
     */
    public List<Item> getAllItems(String craftMaterial, String craftSkill, int maxLevel, int minLevel, String name, int page, int size, String type) {
        LOGGER.debug("Entry in getAllItems with parameters : craftMaterial={}, craftSkill={}, maxLevel={}, minLevel={}, name={}, page={}, size={}, type={}",
                craftMaterial, craftSkill, maxLevel, minLevel, name, page, size, type);

        List<Item> items = new ArrayList<>();
        try {
            HttpEntity<String> entity = restUtils.entityHeaderWithoutToken();

            while (true) {
                // Create URL with params
                String url = UriComponentsBuilder.fromHttpUrl(restUtils.getItemsUrl())
                        .queryParamIfPresent("craft_material", Optional.ofNullable(craftMaterial))
                        .queryParamIfPresent("craft_skill", Optional.ofNullable(craftSkill))
                        .queryParamIfPresent("max_level", Optional.of(maxLevel))
                        .queryParamIfPresent("min_level", Optional.of(minLevel))
                        .queryParamIfPresent("name", Optional.ofNullable(name))
                        .queryParamIfPresent("page", Optional.of(page))
                        .queryParamIfPresent("size", Optional.of(size))
                        .queryParamIfPresent("type", Optional.ofNullable(type))
                        .encode()
                        .toUriString();

                ResponseEntity<ItemsResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, ItemsResponse.class);
                LOGGER.info("GET {} status : {}", url, response.getStatusCode());


                if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                    break; // Break if the response is not OK or body is null
                }

                ItemsResponse resourcesResponse = response.getBody();
                items.addAll(resourcesResponse.getItems());

                // Verify if we continue to the next page
                if (resourcesResponse.getPage() >= resourcesResponse.getPages()) {
                    break; // Get out if we are on the last page
                }

                page++; // Increment page to the next iteration
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("Erreur lors de l'appel au service REST : {}", e.getMessage(), e);
            handleException(e, restUtils.getItemsUrl());
        }
        return items;
    }

    /**
     * Récupère un item spécifique basé sur le code fourni.
     *
     * <p>Cette méthode envoie une requête HTTP GET pour obtenir les détails d'un item depuis le service REST en utilisant
     * le code de l'item comme paramètre dans l'URL. La réponse est ensuite retournée sous la forme d'un objet {@link ItemResponse}.</p>
     *
     * <p>La méthode utilise un en-tête HTTP sans jeton d'authentification. En cas d'erreur HTTP, la méthode {@link #handleException(HttpClientErrorException, String)}
     * est appelée pour gérer l'exception, et un log d'erreur est généré.</p>
     *
     * @param code le code de l'item à récupérer.
     * @return un objet {@link ItemResponse} contenant les détails de l'item correspondant au code fourni, ou {@code null} si
     *         aucune réponse valide n'est reçue ou en cas d'erreur. La valeur est déterminée à partir du corps de la réponse HTTP.
     * @throws HttpClientErrorException si une erreur HTTP survient lors de l'appel au service REST. Les erreurs sont
     *                                  loguées et traitées par la méthode {@link #handleException(HttpClientErrorException, String)}.
     */
    public ItemResponse getItem(String code) {
        LOGGER.debug("Entry in getItem with parameter : code={}", code);
        try {
            String url = String.format(restUtils.getResourceUrl(), code);
            HttpEntity<String> entity = restUtils.entityHeaderWithoutToken();

            ResponseEntity<ItemResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, ItemResponse.class);
            LOGGER.info("GET {} : status : {}", url, response.getStatusCode());

            return response.getBody();

        } catch (HttpClientErrorException e) {
            LOGGER.error("Erreur lors de l'appel au service REST : {}", e.getMessage(), e);
            handleException(e, restUtils.getItemUrl());
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
     * @throws RuntimeException si le code d'état HTTP est 404 (Not Found), avec un message d'erreur spécifique.
     * @throws HttpClientErrorException pour tout autre code d'état HTTP, relancée avec le code et le message d'origine.
     */
    private void handleException(HttpClientErrorException e, String url) {
        switch (e.getStatusCode().value()) {
            case 404:
                if(url.equals(restUtils.getItemUrl())) {
                    throw new RuntimeException(ErrorCode.RESOURCE_NOT_FOUND.getReason(), e);
                } else {
                    throw new RuntimeException(ErrorCode.RESOURCES_NOT_FOUND.getReason(), e);
                }
            default:
                throw new HttpClientErrorException(e.getStatusCode(), e.getMessage());
        }
    }
}
