package artifactsmmo.controllers;

import artifactsmmo.enums.ErrorCode;
import artifactsmmo.models.schema.*;
import artifactsmmo.models.entity.Character;
import artifactsmmo.models.entity.ItemInventory;
import artifactsmmo.models.response.*;
import artifactsmmo.models.entity.Map;
import artifactsmmo.services.CharacterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import artifactsmmo.services.MyCharactersService;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@RestController
public class MyCharactersController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyCharactersController.class);
    @Autowired
    private MyCharactersService myCharactersService;
    @Autowired
    private CharacterService characterService;

    public List<Character> getMyCharacters() {
        LOGGER.info("Getting all my characters");
        try {
            return myCharactersService.getMyCharacters();
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public CharacterMovementDataSchema move(String name, int x, int y) {
        LOGGER.info("Moving {} to 'x,y' : {},{}", name, x, y);
        try {
            return myCharactersService.move(name, x, y);
        } catch (HttpClientErrorException e) {
            LOGGER.warn(name + " waiting.");
            return null;
        }
    }


    public CharacterMovementDataSchema moveToMap(String name, Map map) {
        LOGGER.info("Moving {} to Map('x,y') : {},{}", name, map.getX(), map.getX());
        try {
            return myCharactersService.move(name, map.getX(), map.getY());
        } catch (HttpClientErrorException e) {
            LOGGER.warn(name + " waiting.");
            return null;
        }
    }


    public TaskDataSchema acceptNewTask(String name) {
        LOGGER.info("{} accept a new task", name);
        try {
            return myCharactersService.acceptNewTask(name);
        } catch (HttpClientErrorException e) {
            LOGGER.warn(name + " waiting.");
            return null;
        }
    }

    public BankItemSchema bankDeposit(String name, ItemInventory item, int quantity) throws InterruptedException {

        LOGGER.info("{} depose the item {} in {} quantity in bank !", name, item.getCode(), quantity);
        try {
            return myCharactersService.bankDeposit(name, item.getCode(), quantity);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == ErrorCode.MISSING_ITEM.getCode()) {
                LOGGER.error(name + " as a missing item or insufficient quantity");
            }
            else if (e.getStatusCode().value() == ErrorCode.TRANSACTION_ALREADY_IN_PROGRESS.getCode()) {
                LOGGER.warn("{} can't depost {} item, someone is already in transaction with it", name, item.getCode());
                Thread.sleep(500);
                bankDeposit(name, item, quantity);
            }
        }
        return null;
    }


    public SkillDataSchema crafting(String name, String code, int quantity) throws InterruptedException {

        LOGGER.info("{} crafting the item {} in {} quantity in bank !", name, code, quantity);
        try {
            return myCharactersService.crafting(name, code, quantity);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == ErrorCode.MISSING_ITEM.getCode()) {
                LOGGER.error(name + " as a missing item or insufficient quantity");
            } else {
                LOGGER.error("{} can't craft the item {}. Something when wrong.", name, code);
            }
        }
        return null;
    }

    public CharacterFightDataSchema fight(String name) {
        LOGGER.info("{} fight !", name);
        try {
            return myCharactersService.fight(name);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == ErrorCode.CHARACTER_INVENTORY_FULL.getCode()) {
                LOGGER.error(name + " as his inventory full.");
            }
        }
        return null;
    }

    public TaskDataSchema moveAndTakeNewTask(Character character, Map map) throws InterruptedException {
        LOGGER.info("Moving {} to 'x,y' : {},{} and take a new task", character.getName(), map.getX(), map.getY());
        if (character.getX() != map.getX() || character.getY() != map.getY()) {
            CharacterMovementDataSchema result = moveToMap(character.getName(), map);
            int cooldown = result.getCooldown().getRemainingSeconds();
            Thread.sleep(cooldown * 1000);
        }
        return acceptNewTask(character.getName());
    }

    public TaskRewardDataSchema completeTask(String name) {
        LOGGER.info("{} complete a quest ! ", name);
        return myCharactersService.completeTask(name);
    }

    public MyCharacterGatheringResponse gathering(String name) throws InterruptedException {
        LOGGER.info("{} gather resource !", name);
        try {
            myCharactersService.gathering(name);
        } catch (HttpClientErrorException e){
            if(e.getStatusCode().value() == ErrorCode.CHARACTER_IN_COOLDOWN.getCode()){
                Character character = characterService.getCharacter(name);
                Thread.sleep(character.getCooldown() * 1000);
                return gathering(name);
            }
        }
        return null;
    }

}
