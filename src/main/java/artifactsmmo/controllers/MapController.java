package artifactsmmo.controllers;

import artifactsmmo.models.entity.Map;
import artifactsmmo.services.MapService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MapController {

    @Autowired
    private MapService mapService;
    private  static final Logger LOGGER = LoggerFactory.getLogger(MapController.class);

    public List<Map> getAllMaps(String content_type, String content_code, int page, int size) {
        try {
            LOGGER.info("Getting the all of the map.");
            return mapService.getAllMaps(content_type, content_code,page, size);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}
