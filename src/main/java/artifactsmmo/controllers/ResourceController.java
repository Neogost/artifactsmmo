package artifactsmmo.controllers;

import artifactsmmo.models.entity.Resource;
import artifactsmmo.services.ResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static artifactsmmo.utils.ValidationUtils.*;

@RestController
public class ResourceController {

    @Autowired
    private ResourceService resourceService;
    private  static final Logger LOGGER = LoggerFactory.getLogger(ResourceController.class);


    /**
     * Retrieves a list of all resources using default parameters for the query.
     *
     * This method provides a convenience wrapper to fetch resources with preset default
     * values for the query parameters:
     * - `drop` is set to {@code null}
     * - `maxLevel` is set to {@code 1}
     * - `minLevel` is set to {@code 30}
     * - `page` is set to {@code 1}
     * - `size` is set to {@code 100}
     * - `skill` is set to {@code null}
     *
     * The method calls {@link #getResources(String, int, int, int, int, String)} with these default
     * values to perform the resource retrieval.
     *
     * <p>
     * If the method encounters an {@link IllegalArgumentException} due to invalid arguments,
     * it logs the error and rethrows the exception. For other runtime exceptions, it logs
     * the error and wraps it in a {@link RuntimeException} with a custom message before throwing.
     * </p>
     *
     * @return A list of {@link Resource} objects representing all resources retrieved with
     *         the default parameters.
     *
     * @throws IllegalArgumentException if the default parameters are invalid or cause
     *         the underlying method to throw this exception.
     * @throws RuntimeException if any other runtime errors occur during the resource retrieval.
     */
    public List<Resource> getAllResources() {
        LOGGER.info("Getting all resources of the game with default parameters");

        try {
            return getResources(null, 1, 30, 1, 100, null);
        } catch (IllegalArgumentException e) {
            LOGGER.error("Invalid argument provided: {}", e.getMessage());
            throw e; // Relance l'exception pour l'appeler
        } catch (Exception e) {
            LOGGER.error("Error fetching resources: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to fetch resources", e);
        }
    }


    /**
     * Récupère une liste de ressources en fonction des critères spécifiés.
     * <p>
     * Cette méthode permet de filtrer et de paginer les ressources du jeu en fonction des paramètres suivants :
     * <ul>
     *     <li><b>drop</b> : le type de drop à filtrer (peut être nul ou une chaîne vide)</li>
     *     <li><b>minLevel</b> : le niveau minimum des ressources (doit être un entier positif)</li>
     *     <li><b>maxLevel</b> : le niveau maximum des ressources (doit être un entier positif et supérieur ou égal à minLevel)</li>
     *     <li><b>page</b> : le numéro de la page à retourner (doit être un entier positif)</li>
     *     <li><b>pageSize</b> : le nombre de ressources par page (doit être un entier positif)</li>
     *     <li><b>skill</b> : la compétence associée à filtrer (peut être nul ou une chaîne vide)</li>
     * </ul>
     * </p>
     * <p>
     * Les paramètres sont validés avant l'exécution de la requête. En cas d'erreur de validation ou d'échec lors de la récupération des ressources,
     * une exception {@link ResponseStatusException} est lancée avec le statut HTTP 500.
     * </p>
     *
     * @param drop le type de drop pour filtrer les ressources, peut être nul ou une chaîne vide.
     * @param minLevel le niveau minimum des ressources à inclure, doit être un entier positif.
     * @param maxLevel le niveau maximum des ressources à inclure, doit être un entier positif et supérieur ou égal à minLevel.
     * @param page le numéro de la page à retourner, doit être un entier positif.
     * @param pageSize le nombre de ressources par page, doit être un entier positif.
     * @param skill la compétence à utiliser pour filtrer les ressources, peut être nul ou une chaîne vide.
     * @return une liste de ressources correspondant aux critères spécifiés.
     * @throws ResponseStatusException si une erreur se produit lors de la récupération des ressources, avec un statut HTTP 500.
     */
    public List<Resource> getResources(String drop, int minLevel, int maxLevel, int page, int pageSize, String skill) {
        LOGGER.info("Getting resources of the game");

        try {
            validateDrop(drop);
            validateMinLevel(minLevel);
            validateMaxLevel(maxLevel);
            validateLevelsParam(minLevel, maxLevel);
            validatePage(page);
            validatePageSize(pageSize);
            validateSkillForResources(skill);

            return resourceService.getAllResources(drop, minLevel, maxLevel, page, pageSize, skill);
        } catch (Exception e) {
            LOGGER.error("Error fetching resources: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to fetch resources", e);
        }
    }

    /**
     * Récupère une ressource en fonction du code spécifié.
     * <p>
     * Cette méthode permet de récupérer une ressource unique identifiée par le code fourni. Le code est validé avant d'effectuer la
     * requête. En cas d'erreur de validation ou d'échec lors de la récupération de la ressource, une exception {@link ResponseStatusException}
     * est lancée avec le statut HTTP 500.
     * </p>
     *
     * @param code le code unique de la ressource à récupérer. Ce paramètre ne peut pas être nul ou vide.
     * @return la ressource correspondant au code spécifié.
     * @throws ResponseStatusException si une erreur se produit lors de la récupération de la ressource, avec un statut HTTP 500.
     */
    public Resource getResource(String code) {
        LOGGER.info("Getting resource {}", code);
        try {
            validateDrop(code);

            return resourceService.getResource(code);
        } catch (Exception e) {
            LOGGER.error("Error fetching resources: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to fetch resources", e);
        }
    }

}
