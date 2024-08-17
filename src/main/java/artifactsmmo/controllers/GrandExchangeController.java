package artifactsmmo.controllers;

import artifactsmmo.models.entity.GE;
import artifactsmmo.services.GrandExchangeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static artifactsmmo.utils.ValidationUtils.*;

@RestController
public class GrandExchangeController {

    @Autowired
    private GrandExchangeService grandExchangeService;
    private static final Logger LOGGER = LoggerFactory.getLogger(GrandExchangeController.class);

    /**
     * Récupère un article du Grand Exchange en fonction du code spécifié.
     * <p>
     * Cette méthode permet de récupérer un article unique du Grand Exchange (GE) identifié par le code fourni.
     * Le code de l'article est validé avant de procéder à la récupération. En cas d'erreur lors de la validation ou de l'obtention de l'article,
     * une exception {@link ResponseStatusException} est lancée avec un statut HTTP 500.
     * </p>
     *
     * @param code le code unique de l'article à récupérer depuis le Grand Exchange. Ce paramètre ne peut pas être nul ou vide.
     * @return l'article correspondant au code spécifié sous forme d'objet {@link GE}.
     * @throws ResponseStatusException si une erreur se produit lors de la récupération de l'article, avec un statut HTTP 500.
     */
    public GE getGeItem(String code) {
        LOGGER.info("Getting item {} from Grand Exchange.", code);
        try {
            validateItemCode(code);

            return grandExchangeService.getGeItem(code);
        } catch (Exception e) {
            LOGGER.error("Error fetching ge item: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to fetch resources", e);
        }
    }

    /**
     * Récupère une liste paginée d'articles du Grand Exchange.
     * <p>
     * Cette méthode permet de récupérer tous les articles du Grand Exchange (GE) sous forme de liste paginée.
     * Le numéro de page et la taille de la page sont validés avant de procéder à la récupération des articles.
     * En cas d'erreur lors de la validation ou de l'obtention des articles, une exception {@link ResponseStatusException}
     * est lancée avec un statut HTTP 500.
     * </p>
     *
     * @param page le numéro de la page à récupérer, doit être un entier positif.
     * @param size le nombre d'articles à récupérer par page, doit être un entier positif.
     * @return une liste d'objets {@link GE} représentant les articles du Grand Exchange pour la page spécifiée.
     * @throws ResponseStatusException si une erreur se produit lors de la récupération des articles, avec un statut HTTP 500.
     */
    public List<GE> getAllGeItems(int page, int size) {
        LOGGER.info("Getting all items from Grand Exchange.");
        try {
            validatePage(page);
            validatePageSize(size);

            return grandExchangeService.getAllGeItems(page, size);
        } catch (Exception e) {
            LOGGER.error("Error fetching all ge items: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to fetch resources", e);
        }
    }

    /**
     * Récupère une liste d'articles du Grand Exchange avec des paramètres par défaut.
     * <p>
     * Cette méthode permet de récupérer tous les articles du Grand Exchange (GE) en utilisant des paramètres par défaut pour la pagination.
     * Par défaut, elle récupère la première page avec une taille de page de 100 articles.
     * </p>
     *
     * @return une liste d'objets {@link GE} représentant les articles du Grand Exchange.
     */
    public List<GE> getAllGeItems() {
        LOGGER.info("Getting all items from Grand Exchange with default parameters.");
        return getAllGeItems(1, 100);
    }
}
