package artifactsmmo.services;


import artifactsmmo.enums.ErrorCode;
import artifactsmmo.models.entity.Character;
import artifactsmmo.models.response.*;
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

/**
 * Service pour la gestion des opérations liées aux personnages via des appels à un service REST.
 * <p>
 * La classe {@link CharacterService} fournit des méthodes pour créer, supprimer, récupérer un personnage spécifique,
 * et obtenir tous les personnages avec pagination. Elle utilise le client {@link RestTemplate} pour communiquer avec
 * le service REST, gère les exceptions HTTP et log les informations pertinentes pour le débogage et le suivi.
 * </p>
 */
@Service
public class CharacterService {


    @Autowired
    private RestTemplate restTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(CharacterService.class);
    private final RestUtils restUtils = new RestUtils();

    /**
     * Crée un nouveau personnage en envoyant une requête POST à un service REST avec les informations fournies.
     * <p>
     * Cette méthode prend en entrée le nom et le skin du personnage à créer, crée le corps de la requête avec ces informations,
     * et envoie une requête POST au service REST pour créer le personnage. Si la requête est réussie et que la réponse contient
     * un personnage, ce dernier est retourné. En cas d'erreur HTTP ou si la réponse est vide, la méthode renvoie `null`.
     * </p>
     *
     * @param name le nom du personnage à créer
     * @param skin le skin du personnage à créer
     * @return un objet {@link Character} représentant le personnage créé, ou `null` si la création échoue ou si le personnage n'est pas trouvé
     * @throws HttpClientErrorException si une erreur HTTP se produit lors de l'appel au service REST
     */
    public Character createCharacter(String name, String skin) {
        LOGGER.debug("Entry in createCharacter with parameter : name={}, skin={}", name, skin);

        try {
            // Create the request body as a MultiValueMap
            Map<String, String> body = new HashMap<>();
            body.put("name", name);
            body.put("skin", skin);

            String url = String.format(restUtils.getCharacterCreateUrl());
            HttpEntity<Object> entity = restUtils.entityHeader(body);

            ResponseEntity<CharacterCreateResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, CharacterCreateResponse.class);
            LOGGER.info("POST {} : status : {}", url, response.getStatusCode());

            return Optional.ofNullable(response.getBody())
                    .map(CharacterCreateResponse::getCharacter)
                    .orElse(null);

        } catch (HttpClientErrorException e) {
            LOGGER.error("Erreur lors de l'appel au service REST : {}", e.getMessage(), e);
            handleException(e, restUtils.getCharacterCreateUrl());
            return null;
        }
    }

    /**
     * Supprime un personnage en envoyant une requête POST à un service REST avec le nom du personnage fourni.
     * <p>
     * Cette méthode prend en entrée le nom du personnage à supprimer, crée le corps de la requête avec ce nom,
     * et envoie une requête POST au service REST pour supprimer le personnage. Si la requête est réussie et que
     * la réponse contient un personnage, ce dernier est retourné. En cas d'erreur HTTP ou si la réponse est vide,
     * la méthode renvoie `null`.
     * </p>
     *
     * @param name le nom du personnage à supprimer
     * @return un objet {@link Character} représentant le personnage supprimé, ou `null` si la suppression échoue ou si le personnage n'est pas trouvé
     * @throws HttpClientErrorException si une erreur HTTP se produit lors de l'appel au service REST
     */
    public Character deleteCharacter(String name) {
        LOGGER.debug("Entry in deleteCharacter with parameter : name={}", name);

        try {

            String url = restUtils.getCharacterDeleteUrl();
            // Create the request body as a MultiValueMap
            Map<String, Object> body = new HashMap<>();
            body.put("name", name);

            HttpEntity<Object> entity = restUtils.entityHeader(body);

            ResponseEntity<CharacterDeleteResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, CharacterDeleteResponse.class);
            LOGGER.info("POST {} : status : {}", url, response.getStatusCode());

            return Optional.ofNullable(response.getBody())
                    .map(CharacterDeleteResponse::getCharacter)
                    .orElse(null);

        } catch (HttpClientErrorException e) {
            LOGGER.error("Erreur lors de l'appel au service REST : {}", e.getMessage(), e);
            handleException(e, restUtils.getCharacterDeleteUrl());
            return null;
        }
    }
    /**
     * Récupère un personnage en envoyant une requête GET à un service REST avec le nom du personnage fourni.
     * <p>
     * Cette méthode prend en entrée le nom d'un personnage, construit l'URL de la requête en utilisant ce nom,
     * puis envoie une requête GET au service REST pour récupérer les détails du personnage. Si la requête réussit,
     * le personnage correspondant est renvoyé. En cas d'erreur HTTP, une exception est gérée, et `null` est retourné.
     * </p>
     *
     * @param name le nom du personnage à récupérer
     * @return un objet {@link Character} représentant le personnage récupéré, ou `null` en cas d'erreur ou si le personnage n'est pas trouvé
     * @throws HttpClientErrorException si une erreur HTTP se produit lors de l'appel au service REST
     */
    public Character getCharacter(String name) {
        LOGGER.debug("Entry in getCharacter with parameter : name={}", name);

        try {
            String url = String.format(restUtils.getCharacterUrl(), name);
            HttpEntity<String> entity = restUtils.entityHeaderWithoutToken();

            ResponseEntity<CharacterResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, CharacterResponse.class);
            LOGGER.info("GET {} status : {}", url, response.getStatusCode());

            return Optional.ofNullable(response.getBody())
                    .map(CharacterResponse::getCharacter)
                    .orElse(null);
        } catch (HttpClientErrorException e) {
            LOGGER.error("Erreur lors de l'appel au service REST : {}", e.getMessage(), e);
            handleException(e, restUtils.getCharacterUrl());
            return null;
        }
    }

    /**
     * Récupère tous les personnages en envoyant des requêtes paginées à un service REST.
     * <p>
     * Cette méthode permet de récupérer une liste de personnages à partir d'un service REST en utilisant la pagination.
     * Les paramètres de page, taille et tri sont utilisés pour construire l'URL de la requête.
     * La méthode continue de paginer tant que toutes les pages ne sont pas récupérées ou qu'une erreur survient.
     * </p>
     *
     * @param page le numéro de la page à récupérer, indexé à partir de 0
     * @param size le nombre d'éléments par page
     * @param sort le critère de tri pour les résultats
     * @return une liste de {@link Character} représentant tous les personnages récupérés
     * @throws HttpClientErrorException si une erreur HTTP se produit lors de l'appel au service REST
     */
    public List<Character> getAllCharacters(int page, int size, String sort) {
        LOGGER.debug("Entry in getAllCharacters with parameter : page={}, size={}, sort={}", page, size, sort);
        List<Character> characters = new ArrayList<>();
        try {
            HttpEntity<String> entity = restUtils.entityHeaderWithoutToken();

            while (true) {
                // Create URL with params
                String url = UriComponentsBuilder.fromHttpUrl(restUtils.getAllCharacterUrl())
                        .queryParamIfPresent("page", Optional.of(page))
                        .queryParamIfPresent("size", Optional.of(size))
                        .queryParamIfPresent("sort", Optional.of(size))
                        .encode()
                        .toUriString();

                ResponseEntity<CharactersResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, CharactersResponse.class);
                LOGGER.info("GET {} : status : {}", url, response.getStatusCode());


                if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                    break; // Break if the response is not OK or body is null
                }

                CharactersResponse charactersResponse = response.getBody();
                characters.addAll(charactersResponse.getCharacters());

                // Verify if we continue to the next page
                if (charactersResponse.getPage() >= charactersResponse.getPages()) {
                    break; // Get out if we are on the last page
                }

                page++; // Increment page to the next iteration
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("Erreur lors de l'appel au service REST : {}", e.getMessage(), e);
            handleException(e, restUtils.getAllCharacterUrl());
        }
        return characters;
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
    private void handleException(HttpClientErrorException e, String url) {
        switch (e.getStatusCode().value()) {
            case 404:
                if (url.equals(restUtils.getCharacterUrl())) {
                    throw new RuntimeException(ErrorCode.CHARACTER_NOT_FOUND.getReason(), e);
                } else if (url.equals(restUtils.getCharacterUrl())) {
                    throw new RuntimeException(ErrorCode.CHARACTERS_NOT_FOUND.getReason(), e);
                }
            case 494:
                throw new RuntimeException(ErrorCode.NAME_ALREADY_USED.getReason(), e);
            case 495:
                throw new RuntimeException(ErrorCode.MAX_CHARACTER.getReason(), e);
            case 498:
                throw new RuntimeException(ErrorCode.CHARACTER_DELETE_NOT_FOUND.getReason(), e);
            default:
                throw new HttpClientErrorException(e.getStatusCode(), e.getMessage());
        }
    }
}