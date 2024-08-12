package artifactsmmo.controllers;

import artifactsmmo.models.entity.Character;
import artifactsmmo.services.CharacterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CharacterController {

    private  static final Logger LOGGER = LoggerFactory.getLogger(CharacterController.class);
    @Autowired
    private CharacterService CharacterService;

    public Character getCharacter(String name) {
        try {
            LOGGER.info("{} status updated", name);
            return CharacterService.getCharacter(name);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}
