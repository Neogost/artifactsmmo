package artifactsmmo.controllers;

import artifactsmmo.models.entity.Event;
import artifactsmmo.services.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static artifactsmmo.utils.ValidationUtils.validatePage;
import static artifactsmmo.utils.ValidationUtils.validatePageSize;

@RestController
public class EventController {

    @Autowired
    private EventService eventService;
    private static final Logger LOGGER = LoggerFactory.getLogger(EventController.class);

    /**
     * Récupère une liste paginée de tous les événements.
     * <p>
     * Cette méthode permet de récupérer tous les événements sous forme de liste paginée.
     * Le numéro de page et la taille de la page sont validés avant la récupération des événements.
     * En cas d'erreur lors de la validation ou de l'obtention des événements, une exception {@link ResponseStatusException}
     * est lancée avec un statut HTTP 500.
     * </p>
     *
     * @param page le numéro de la page à récupérer, doit être un entier positif.
     * @param size le nombre d'événements à récupérer par page, doit être un entier positif.
     * @return une liste d'objets {@link Event} représentant les événements pour la page spécifiée.
     * @throws ResponseStatusException si une erreur se produit lors de la récupération des événements, avec un statut HTTP 500.
     */
    public List<Event> getAllEvents(int page, int size) {
        LOGGER.info("Getting all events.");
        try {
            validatePage(page);
            validatePageSize(size);

            return eventService.getAllEvents(page, size);
        } catch (Exception e) {
            LOGGER.error("Error fetching all events: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to fetch resources", e);
        }
    }

    /**
     * Récupère une liste de tous les événements avec des paramètres par défaut.
     * <p>
     * Cette méthode permet de récupérer tous les événements en utilisant des paramètres par défaut pour la pagination.
     * Par défaut, elle récupère la première page avec une taille de page de 100 événements.
     * </p>
     *
     * @return une liste d'objets {@link Event} représentant tous les événements.
     */
    public List<Event> getAllEvents() {
        LOGGER.info("Getting all events with default parameters");
        try {
            return getAllEvents(1, 100);
        } catch (RuntimeException e) {
            LOGGER.error("Error fetching events with default parameters : {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
