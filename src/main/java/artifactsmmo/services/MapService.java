package artifactsmmo.services;

import artifactsmmo.enums.ErrorCode;
import artifactsmmo.models.entity.Map;
import artifactsmmo.models.response.MapResponse;
import artifactsmmo.models.response.MapsResponse;
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
public class MapService {

    @Autowired
    private RestTemplate restTemplate;

    private  static final Logger LOGGER = LoggerFactory.getLogger(MapService.class);
    private final RestUtils restUtils = new RestUtils();

    /**
     * Récupère toutes les cartes disponibles en paginant à travers les pages du service REST.
     *
     * <p>Cette méthode envoie des requêtes HTTP GET pour récupérer des cartes depuis le service REST. Les requêtes sont
     * effectuées de manière itérative en utilisant la pagination pour obtenir toutes les cartes disponibles. Les paramètres
     * de la requête incluent le type de contenu, le code de contenu, la page courante et la taille de la page.</p>
     *
     * <p>Les requêtes sont envoyées jusqu'à ce que toutes les pages soient récupérées ou qu'une réponse invalide soit
     * reçue. Les cartes récupérées sont ajoutées à une liste, qui est ensuite retournée.</p>
     *
     * @param contentType le type de contenu à filtrer dans les requêtes, ou {@code null} pour ne pas filtrer par type de contenu.
     * @param contentCode le code de contenu à filtrer dans les requêtes, ou {@code null} pour ne pas filtrer par code de contenu.
     * @param page le numéro de la page actuelle à récupérer, généralement initialisé à 0 pour la première page.
     * @param size le nombre d'éléments par page.
     * @return une liste contenant toutes les cartes récupérées à partir du service REST. La liste est vide si aucune carte
     *         n'est trouvée ou en cas d'erreur.
     *
     * @throws HttpClientErrorException si une erreur HTTP survient lors de l'appel au service REST. Les erreurs sont
     *                                  loguées et traitées par la méthode {@link #handleException(HttpClientErrorException, String)}.
     */
    public List<Map> getAllMaps(String contentType, String contentCode, int page, int size) {
        LOGGER.debug("Entry in getAllMaps with parameters : contentType={}, contentCode={}, page={}, size={}",
                contentType, contentCode, page, size);

        List<Map> maps = new ArrayList<>();
        try {
            HttpEntity<String> entity = restUtils.entityHeaderWithoutToken();

            while (true) {
                // Create URL with params
                String url = UriComponentsBuilder.fromHttpUrl(restUtils.getMapsUrl())
                        .queryParamIfPresent("contentType", Optional.ofNullable(contentType))
                        .queryParamIfPresent("contentCode", Optional.of(contentCode))
                        .queryParamIfPresent("page", Optional.of(page))
                        .queryParamIfPresent("size", Optional.of(size))
                        .encode()
                        .toUriString();

                ResponseEntity<MapsResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, MapsResponse.class);
                LOGGER.info("GET {} : status : {}", url, response.getStatusCode());


                if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                    break; // Break if the response is not OK or body is null
                }

                MapsResponse mapsResponse = response.getBody();
                maps.addAll(mapsResponse.getMaps());

                // Verify if we continue to the next page
                if (mapsResponse.getPage() >= mapsResponse.getPages()) {
                    break; // Get out if we are on the last page
                }

                page++; // Increment page to the next iteration
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("Erreur lors de l'appel au service REST : {}", e.getMessage(), e);
            handleException(e, restUtils.getMapsUrl());
        }
        return maps;
    }

    /**
     * Récupère une carte spécifique en fonction des coordonnées fournies.
     *
     * <p>Cette méthode envoie une requête HTTP GET pour obtenir une carte depuis le service REST en utilisant les coordonnées
     * spécifiées. Les coordonnées sont passées en tant que paramètres dans l'URL. La réponse est ensuite traitée pour extraire
     * les données de la carte.</p>
     *
     * <p>La méthode utilise un en-tête HTTP sans jeton d'authentification. En cas d'erreur HTTP, une gestion appropriée est
     * effectuée par la méthode {@link #handleException(HttpClientErrorException, String)}.</p>
     *
     * @param x la coordonnée X de la carte à récupérer.
     * @param y la coordonnée Y de la carte à récupérer.
     * @return l'objet {@link Map} correspondant aux coordonnées fournies, ou {@code null} si aucune carte n'est trouvée ou
     *         en cas d'erreur. La valeur est déterminée à partir du corps de la réponse HTTP.
     * @throws HttpClientErrorException si une erreur HTTP survient lors de l'appel au service REST. Les erreurs sont
     *                                  loguées et traitées par la méthode {@link #handleException(HttpClientErrorException, String)}.
     */
    public Map getMap(int x, int y) {
        LOGGER.debug("Entry in getMap with parameter : x={}, y={}", x, y);

        try {
            String url = String.format(restUtils.getMapUrl(), x, y);
            HttpEntity<String> entity = restUtils.entityHeaderWithoutToken();

            ResponseEntity<MapResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, MapResponse.class);
            LOGGER.info("GET {} : status : {}", url, response.getStatusCode());

            return Optional.ofNullable(response.getBody())
                    .map(MapResponse::getMap)
                    .orElse(null);

        } catch (HttpClientErrorException e) {
            LOGGER.error("Erreur lors de l'appel au service REST : {}", e.getMessage(), e);
            handleException(e, restUtils.getMapUrl());
            return null;
        }
    }

    private void handleException(HttpClientErrorException e, String url) {
        switch (e.getStatusCode().value()) {
            case 404:
                if(url.equals(restUtils.getMapUrl())) {
                    throw new RuntimeException(ErrorCode.MAP_NOT_FOUND.getReason(), e);
                } else {
                    throw new RuntimeException(ErrorCode.MAPS_NOT_FOUND.getReason(), e);
                }
            default:
                throw new HttpClientErrorException(e.getStatusCode(), e.getMessage());
        }
    }
}
