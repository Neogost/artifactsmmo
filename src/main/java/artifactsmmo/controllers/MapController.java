package artifactsmmo.controllers;

import artifactsmmo.enums.ContentType;
import artifactsmmo.models.entity.Map;
import artifactsmmo.services.MapService;
import artifactsmmo.utils.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static artifactsmmo.utils.ValidationUtils.*;

@RestController
public class MapController {

    @Autowired
    private MapService mapService;
    private static final Logger LOGGER = LoggerFactory.getLogger(MapController.class);

    /**
     * Retrieves all maps using default parameters for pagination and filtering.
     * <p>
     * This method logs the retrieval process and calls the overloaded {@code getAllMaps}
     * method with default values for filtering and pagination. The default parameters
     * used are {@code null} for filters, page number {@code 1}, and page size {@code 100}.
     * </p>
     *
     * @return a {@link List} of {@link Map} objects representing all available maps.
     */
    public List<Map> getAllMaps(ContentType type, String contentCode, int page, int size) {
        LOGGER.info("Getting the all of the map.");
        try {
            validatePage(page);
            validatePageSize(size);
            validateContentCode(contentCode);
            String typeValue = type == null ? null : type.getValue();
            return mapService.getAllMaps(typeValue, contentCode, page, size);
        } catch (
                RuntimeException e) {
            LOGGER.error("Error fetching maps : {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }


    /**
     * Retrieves all maps using default parameters.
     * <p>
     * This method logs the request to retrieve all maps and delegates the operation
     * to the {@code getAllMaps} method with default pagination and filtering parameters.
     * </p>
     *
     * @return a {@link List} of {@link Map} objects representing all available maps.
     */
    public List<Map> getAllMaps() {
        LOGGER.info("Getting the all of the map with default parameters.");
        try {
            return getAllMaps(null, null, 1, 100);
        } catch (RuntimeException e) {
            LOGGER.error("Error fetching maps with default parameters: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves the map data for a specified coordinate.
     * <p>
     * This method logs the request to retrieve the map at the specified coordinates,
     * and then delegates the operation to the {@code mapService} to fetch the map data.
     * </p>
     *
     * @param x the x-coordinate of the map to retrieve.
     * @param y the y-coordinate of the map to retrieve.
     * @return the {@link Map} object representing the map data at the specified coordinates.
     */
    public Map getMap(int x, int y) {
        LOGGER.info("Getting the map in (x,y)={},{}", x, y);
        try {
            return mapService.getMap(x, y);
        } catch (RuntimeException e) {
            LOGGER.error("Error fetching map : {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
