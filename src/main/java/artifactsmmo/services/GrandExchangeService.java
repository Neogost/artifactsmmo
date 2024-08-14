package artifactsmmo.services;

import artifactsmmo.enums.ErrorCode;
import artifactsmmo.models.entity.GE;
import artifactsmmo.models.entity.Item;
import artifactsmmo.models.entity.Resource;
import artifactsmmo.models.response.GeItemResponse;
import artifactsmmo.models.response.GeItemsResponse;
import artifactsmmo.models.response.ResourceResponse;
import artifactsmmo.models.response.ResourcesResponse;
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
public class GrandExchangeService {

    @Autowired
    private RestTemplate restTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(GrandExchangeService.class);
    private final RestUtils restUtils = new RestUtils();

    /**
     * Récupère une liste paginée d'items GE (Grand Exchange) à partir d'un service REST.
     *
     * <p>Cette méthode envoie une requête HTTP GET pour récupérer les items GE en fonction des paramètres de pagination
     * spécifiés. Elle parcourt les pages de résultats jusqu'à ce qu'elle atteigne la dernière page ou qu'une réponse
     * invalide soit reçue.</p>
     *
     * @param page le numéro de la page à récupérer (commence généralement à 0)
     * @param size le nombre d'items à récupérer par page
     * @return une liste d'objets {@link GE} récupérés depuis le service REST. Si une erreur survient ou si aucune
     * donnée n'est disponible, une liste vide est retournée.
     *
     * @throws HttpClientErrorException si une erreur HTTP survient lors de l'appel au service REST
     */
    public List<GE> getAllGeItems(int page, int size) {
        LOGGER.debug("Entry in getAllGeItems with parameters : page={}, size={}",
                page, size);

        List<GE> geItems = new ArrayList<>();
        try {
            HttpEntity<String> entity = restUtils.entityHeaderWithoutToken();

            while (true) {
                // Create URL with params
                String url = UriComponentsBuilder.fromHttpUrl(restUtils.getResourcesUrl())
                        .queryParamIfPresent("page", Optional.of(page))
                        .queryParamIfPresent("size", Optional.of(size))
                        .encode()
                        .toUriString();

                ResponseEntity<GeItemsResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, GeItemsResponse.class);
                LOGGER.info("GET {} : status : {}", url, response.getStatusCode());


                if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                    break; // Break if the response is not OK or body is null
                }

                GeItemsResponse geItemsResponse = response.getBody();
                geItems.addAll(geItemsResponse.getGeItems());

                // Verify if we continue to the next page
                if (geItemsResponse.getPage() >= geItemsResponse.getPages()) {
                    break; // Get out if we are on the last page
                }

                page++; // Increment page to the next iteration
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("Erreur lors de l'appel au service REST : {}", e.getMessage(), e);
            handleException(e, restUtils.getGeItemsUrl());
        }
        return geItems;
    }

    /**
     * Retrieves a Grand Exchange (GE) item by its unique code.
     *
     * <p>This method makes a RESTful API call to retrieve the details of a specific GE item using its
     * unique code. The method constructs the URL with the provided code, sends a GET request, and
     * processes the response to extract the GE item details.</p>
     *
     * @param code The unique code of the GE item to retrieve.
     * @return The {@link GE} item object corresponding to the provided code, or {@code null} if the item
     *         is not found or if an error occurs during the request.
     * @throws RuntimeException If a specific error condition is met, such as a 404 Not Found status.
     *         Other HTTP client errors are also logged and handled appropriately.
     */
    public GE getGeItem(String code) {
        LOGGER.debug("Entry in getGeItem with parameter : code={}", code);

        try {
            String url = String.format(restUtils.getGeItemUrl(), code);
            HttpEntity<String> entity = restUtils.entityHeaderWithoutToken();

            ResponseEntity<GeItemResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, GeItemResponse.class);
            LOGGER.info("GET {} : status : {}", url, response.getStatusCode());

            return Optional.ofNullable(response.getBody())
                    .map(GeItemResponse::getGeItem)
                    .orElse(null);

        } catch (HttpClientErrorException e) {
            LOGGER.error("Erreur lors de l'appel au service REST : {}", e.getMessage(), e);
            handleException(e, restUtils.getGeItemUrl());
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
                if(url.equals(restUtils.getGeItemUrl())) {
                    throw new RuntimeException(ErrorCode.GE_ITEM_NOT_FOUND.getReason(), e);
                } else {
                    throw new RuntimeException(ErrorCode.GE_ITEMS_NOT_FOUND.getReason(), e);
                }
            default:
                throw new HttpClientErrorException(e.getStatusCode(), e.getMessage());
        }
    }
}
