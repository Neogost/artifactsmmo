package artifactsmmo.controllers;

import artifactsmmo.models.entity.Resource;
import artifactsmmo.services.ResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ResourceController {

    @Autowired
    private ResourceService resourceService;
    private  static final Logger LOGGER = LoggerFactory.getLogger(ResourceController.class);

    public List<Resource> getAllResources() {
        try {
            LOGGER.info("Getting the all resources of the game");
            return resourceService.getAllRessources(null, 30, 1, null, 1, 50, null);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}
