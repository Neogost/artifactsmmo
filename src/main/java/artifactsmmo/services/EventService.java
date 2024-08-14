package artifactsmmo.services;

import artifactsmmo.enums.ErrorCode;
import artifactsmmo.models.entity.Event;
import artifactsmmo.models.response.EventsResponse;
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
public class EventService {


    @Autowired
    private RestTemplate restTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(EventService.class);
    private final RestUtils restUtils = new RestUtils();

    /**
     * Retrieves a paginated list of all events.
     * <p>
     * This method performs a paginated request to the event resources endpoint,
     * collecting events until all pages are retrieved or an error occurs.
     * </p>
     *
     * @param page the initial page number to retrieve (starting from 0).
     * @param size the number of events to retrieve per page.
     * @return a list of {@link Event} objects representing the events retrieved.
     *         If no events are found or an error occurs, an empty list is returned.
     *
     * @throws HttpClientErrorException if an HTTP error occurs during the request.
     * @throws RuntimeException if a specific error occurs as defined in the handleException method.
     */
    public List<Event> getAllEvents(int page, int size) {
        LOGGER.debug("Entry in getAllEvents with parameters : page={}, size={}",
                page, size);

        List<Event> events = new ArrayList<>();
        try {
            HttpEntity<String> entity = restUtils.entityHeaderWithoutToken();

            while (true) {
                // Create URL with params
                String url = UriComponentsBuilder.fromHttpUrl(restUtils.getEventsUrl())
                        .queryParamIfPresent("page", Optional.of(page))
                        .queryParamIfPresent("size", Optional.of(size))
                        .encode()
                        .toUriString();

                ResponseEntity<EventsResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, EventsResponse.class);
                LOGGER.info("GET {} : status : {}", url, response.getStatusCode());


                if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                    break; // Break if the response is not OK or body is null
                }

                EventsResponse eventsResponse = response.getBody();
                events.addAll(eventsResponse.getEvents());

                // Verify if we continue to the next page
                if (eventsResponse.getPage() >= eventsResponse.getPages()) {
                    break; // Get out if we are on the last page
                }

                page++; // Increment page to the next iteration
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("Erreur lors de l'appel au service REST : {}", e.getMessage(), e);
            handleException(e, restUtils.getEventsUrl());
        }
        return events;
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
                throw new RuntimeException(ErrorCode.EVENTS_NOT_FOUND.getReason(), e);
            default:
                throw new HttpClientErrorException(e.getStatusCode(), e.getMessage());
        }
    }

}
