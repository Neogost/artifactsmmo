package artifactsmmo.controllers;

import artifactsmmo.models.entity.Monster;
import artifactsmmo.services.MonsterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MonsterController {

    @Autowired
    private MonsterService monsterService;
    private  static final Logger LOGGER = LoggerFactory.getLogger(MonsterController.class);

    public List<Monster> getAllMonsters() {
        try {
            LOGGER.info("Getting the all monsters of the game");
            return monsterService.getAllMonsters(null, 30, 1, 1, 50);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

}
