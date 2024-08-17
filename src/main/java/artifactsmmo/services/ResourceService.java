package artifactsmmo.services;

import artifactsmmo.enums.ErrorCode;
import artifactsmmo.models.entity.Resource;
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
public class ResourceService {

    @Autowired
    private RestTemplate restTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceService.class);
    private final RestUtils restUtils = new RestUtils();

    /**
     * Récupère une liste de ressources à partir d'un service REST en tenant compte des filtres et de la pagination.
     * Cette méthode envoie des requêtes GET à une API REST pour obtenir des ressources en fonction des critères fournis.
     * Si la réponse contient plusieurs pages, elle récupère toutes les pages de manière itérative jusqu'à ce que toutes
     * les ressources soient collectées.
     *
     * @param drop     Filtre optionnel basé sur le type de drop des ressources (peut être null).
     * @param maxLevel Filtre optionnel pour le niveau maximum des ressources (valeur par défaut : Integer.MAX_VALUE).
     * @param minLevel Filtre optionnel pour le niveau minimum des ressources (valeur par défaut : 0).
     * @param page     Numéro de page pour la pagination (doit être supérieur ou égal à 1).
     * @param size     Nombre d'éléments par page (doit être supérieur à 0, valeur par défaut : 50).
     * @param skill    Filtre optionnel basé sur les compétences des ressources (peut être null).
     * @return Une liste de {@link Resource} contenant toutes les ressources correspondant aux critères de recherche.
     *         Si aucune ressource n'est trouvée ou si une erreur se produit, une liste vide est retournée.
     * @throws HttpClientErrorException si une erreur HTTP survient lors de l'appel au service REST. Les erreurs sont
     *                                  loguées et traitées par la méthode {@link #handleException(HttpClientErrorException, String)}.
     */

    public List<Resource> getAllResources(String drop, int maxLevel, int minLevel, int page, int size, String skill) {
        LOGGER.debug("Entry in getAllResources with parameters : drop={}, maxLevel={}, minLevel={}, page={}, size={}, skill={}",
                drop, maxLevel, minLevel, page, size, skill);

        List<Resource> resources = new ArrayList<>();
        try {
            HttpEntity<String> entity = restUtils.entityHeaderWithoutToken();

            while (true) {
                // Create URL with params
                String url = UriComponentsBuilder.fromHttpUrl(restUtils.getResourcesUrl())
                        .queryParamIfPresent("drop", Optional.ofNullable(drop))
                        .queryParamIfPresent("max_level", Optional.of(maxLevel))
                        .queryParamIfPresent("min_level", Optional.of(minLevel))
                        .queryParamIfPresent("page", Optional.of(page))
                        .queryParamIfPresent("size", Optional.of(size))
                        .queryParamIfPresent("skill", Optional.ofNullable(skill))
                        .encode()
                        .toUriString();

                ResponseEntity<ResourcesResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, ResourcesResponse.class);
                LOGGER.info("GET {} : status : {}", url, response.getStatusCode());


                if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                    break; // Break if the response is not OK or body is null
                }

                ResourcesResponse resourcesResponse = response.getBody();
                resources.addAll(resourcesResponse.getResources());

                // Verify if we continue to the next page
                if (resourcesResponse.getPage() >= resourcesResponse.getPages()) {
                    break; // Get out if we are on the last page
                }

                page++; // Increment page to the next iteration
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("Erreur lors de l'appel au service REST : {}", e.getMessage(), e);
            handleException(e, restUtils.getResourcesUrl());
        }
        return resources;
    }

    /**
     * Récupère une ressource spécifique à partir de son code via un appel à un service REST.
     *
     * <p>Cette méthode envoie une requête HTTP GET à l'URL du service REST construite en utilisant le code
     * fourni en paramètre. Elle récupère la réponse, extrait l'objet ressource s'il est présent, et le retourne.
     * En cas d'erreur HTTP, une gestion des exceptions est effectuée pour gérer et loguer l'erreur.</p>
     *
     * @param code le code unique de la ressource à récupérer.
     * @return la ressource associée au code spécifié, ou {@code null} si la ressource n'est pas trouvée ou en cas d'erreur.
     *
     * @throws HttpClientErrorException si une erreur HTTP survient lors de l'appel au service REST.
     */
    public Resource getResource(String code) {
        LOGGER.debug("Entry in getResource with parameter : code={}", code);

        try {
            String url = String.format(restUtils.getResourceUrl(), code);
            HttpEntity<String> entity = restUtils.entityHeaderWithoutToken();

            ResponseEntity<ResourceResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, ResourceResponse.class);
            LOGGER.info("GET {} : status : {}", url, response.getStatusCode());

            return Optional.ofNullable(response.getBody())
                    .map(ResourceResponse::getResource)
                    .orElse(null);

        } catch (HttpClientErrorException e) {
            LOGGER.error("Erreur lors de l'appel au service REST : {}", e.getMessage(), e);
            handleException(e, restUtils.getResourceUrl());
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
                if(url.equals(restUtils.getResourceUrl())) {
                    throw new RuntimeException(ErrorCode.RESOURCE_NOT_FOUND.getReason(), e);
                } else {
                    throw new RuntimeException(ErrorCode.RESOURCES_NOT_FOUND.getReason(), e);
                }
            default:
                throw new HttpClientErrorException(e.getStatusCode(), e.getMessage());
        }
    }

}
