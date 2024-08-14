package artifactsmmo.services;

import artifactsmmo.enums.ErrorCode;
import artifactsmmo.models.entity.Monster;
import artifactsmmo.models.response.MonsterResponse;
import artifactsmmo.models.response.MonstersResponse;
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
public class MonsterService {


    @Autowired
    private RestTemplate restTemplate;


    private static final Logger LOGGER = LoggerFactory.getLogger(MonsterService.class);
    private final RestUtils restUtils = new RestUtils();

    /**
     * Retrieves a list of {@link Monster} objects from the REST service based on the provided filters and pagination parameters.
     *
     * <p>This method builds a URL with the provided filter parameters (such as drop, level range, and pagination)
     * and makes a series of GET requests to the REST service to fetch all the relevant monsters. The results are
     * aggregated and returned as a list of {@link Monster} objects. The method handles pagination by checking
     * the current page and total pages in the response, continuing the requests until all pages are fetched or
     * an error occurs.</p>
     *
     * @param drop      the drop code to filter monsters by the item they drop, or {@code null} to ignore this filter.
     * @param maxLevel  the maximum level of monsters to retrieve, or {@code 0} to ignore this filter.
     * @param minLevel  the minimum level of monsters to retrieve, or {@code 0} to ignore this filter.
     * @param page      the starting page number for pagination.
     * @param size      the number of monsters per page.
     * @return a list of {@link Monster} objects matching the provided filters and pagination criteria, or an empty list if none are found.
     * @throws HttpClientErrorException if there is an HTTP error during the request.
     */
    public List<Monster> getAllMonsters(String drop,int maxLevel, int minLevel, int page, int size) {
        LOGGER.debug("Entry in getAllMonsters with parameters : drop={}, maxLevel={}, minLevel={}, page={}, size={}",
                drop, maxLevel, minLevel, page, size);

        List<Monster> monsters = new ArrayList<>();
        try {
            HttpEntity<String> entity = restUtils.entityHeaderWithoutToken();

            while(true) {

                String url = UriComponentsBuilder.fromHttpUrl(restUtils.getMonstersUrl())
                        .queryParamIfPresent("drop", Optional.ofNullable(drop))
                        .queryParamIfPresent("max_level", Optional.of(maxLevel))
                        .queryParamIfPresent("min_level", Optional.of(minLevel))
                        .queryParamIfPresent("page", Optional.of(page))
                        .queryParamIfPresent("size", Optional.of(size))
                        .encode()
                        .toUriString();

                ResponseEntity<MonstersResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, MonstersResponse.class);
                LOGGER.info("GET {} status : {}", url, response.getStatusCode());


                if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                    break; // Break if the response is not OK or body is null
                }

                MonstersResponse monstersResponse = response.getBody();
                monsters.addAll(monstersResponse.getMonsters());

                // Verify if we continue to the next page
                if (monstersResponse.getPage() >= monstersResponse.getPages()) {
                    break; // Get out if we are on the last page
                }

                page++; // Increment page to the next iteration
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("Erreur lors de l'appel au service REST : {}", e.getMessage(), e);
            handleException(e, restUtils.getMonstersUrl());
        }
        return monsters;
    }

    /**
     * Retrieves a {@link Monster} object from the REST service based on the provided monster code.
     *
     * <p>This method constructs the appropriate URL using the provided monster code and makes a GET
     * request to the REST service to fetch the monster details. If the response is successful and
     * contains a valid body, the {@link Monster} object is returned. In case of an error during the
     * REST call, the exception is logged and handled accordingly.</p>
     *
     * @param code the unique identifier code of the monster to be retrieved.
     * @return the {@link Monster} object corresponding to the provided code, or {@code null} if the
     *         monster is not found or an error occurs during the REST call.
     * @throws HttpClientErrorException if there is an HTTP error during the request.
     */
    public Monster getMonster(String code) {
        LOGGER.debug("Entry in getMonster with parameter : code={}", code);

        try {
            String url = String.format(restUtils.getMonsterUrl(), code);
            HttpEntity<String> entity = restUtils.entityHeaderWithoutToken();

            ResponseEntity<MonsterResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, MonsterResponse.class);
            LOGGER.info("GET {} : status : {}", url, response.getStatusCode());

            return Optional.ofNullable(response.getBody())
                    .map(MonsterResponse::getMonster)
                    .orElse(null);

        } catch (HttpClientErrorException e) {
            LOGGER.error("Erreur lors de l'appel au service REST : {}", e.getMessage(), e);
            handleException(e, restUtils.getMonsterUrl());
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
                if(url.equals(restUtils.getMonsterUrl())) {
                    throw new RuntimeException(ErrorCode.MONSTER_NOT_FOUND.getReason(), e);
                } else {
                    throw new RuntimeException(ErrorCode.MONSTERS_NOT_FOUND.getReason(), e);
                }
            default:
                throw new HttpClientErrorException(e.getStatusCode(), e.getMessage());
        }
    }
}
