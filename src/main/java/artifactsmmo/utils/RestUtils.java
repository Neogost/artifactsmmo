package artifactsmmo.utils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import artifactsmmo.business.AppConfig;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;

import java.util.Map;


public class RestUtils {

    /* %s = name of the character */
    String CHARACTERS_URL = "characters/%s";


    String MY_CHARACTERS_URL = "my/characters";
    /* %s = name of the character */
    String MY_CHARACTER_FIGHT = "my/%s/action/fight";
    /* %s = name of the character */
    String MY_CHARACTER_BANK_DEPOSIT = "my/%s/action/bank/deposit";
    /* %s = name of the character */
    String MY_CHARACTER_MOVE = "my/%s/action/move";
    /* %s = name of the character */
    String MY_CHARACTER_NEW_TASK = "my/%s/action/task/new";
    /* %s = name of the character */
    String MY_CHARACTER_COMPLETE_TASK = "my/%s/action/task/complete";
    /* %s = name of the character */
    String MY_CHARACTER_CRAFTING = "my/%s/action/crafting";


    String MY_ACCOUNT_BANK_ITEMS = "my/bank/items";


    /* %s = name of the character */
    String MY_CHARACTER_GATHERING = "my/%s/action/gathering";

    String RESOURCES = "resources/";

    String MONSTERS = "monsters/";

    String ITEMS = "items/";
    /* %s = code */
    String ITEM = "items/%s";


    String ALL_MAPS = "maps/";

    public HttpEntity<Object> entityHeader(Map body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + AppConfig.getToken());
        return new HttpEntity<>(body,headers);
    }


    public HttpEntity<String> entityHeaderWithoutToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(headers);
    }
    public HttpEntity<String> entityHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + AppConfig.getToken());
        return new HttpEntity<>(headers);
    }

    public String getDomain() {
        return AppConfig.getDomain();
    }

    public String getMyCharactersUrl() {
        return getDomain() + MY_CHARACTERS_URL;
    }
    public String getAllMapsUrl() {
        return getDomain() + ALL_MAPS;
    }

    public String getMyCharacterMoveUrl() {
        return getDomain() + MY_CHARACTER_MOVE;
    }

    public String getMyCharacterNewTaskUrl() {
        return getDomain() + MY_CHARACTER_NEW_TASK;
    }
    public String getCharacter() {
        return getDomain() + CHARACTERS_URL;
    }
    public String getMyCharacterFightUrl() {
        return getDomain() + MY_CHARACTER_FIGHT;
    }
    public String getMyCharacterCompleteTaskUrl() {
        return getDomain() + MY_CHARACTER_COMPLETE_TASK;
    }

    public String getMyCharacterBankDepositUrl() {
        return getDomain() + MY_CHARACTER_BANK_DEPOSIT;
    }

    public String getItemsUrl() {
        return getDomain() + ITEMS;
    }

    public String getMonstersUrl() {
        return getDomain() + MONSTERS;
    }

    public String getResourcesUrl() {
        return getDomain() + RESOURCES;
    }

    public String getItemUrl() {
        return getDomain() + ITEM;
    }

    public String getMyCharacterGatheringUrl() {
        return getDomain() + MY_CHARACTER_GATHERING;
    }

    public String getMyBankItems() {
        return getDomain() + MY_ACCOUNT_BANK_ITEMS;
    }

    public String getMyCharacterCraftingUrl() {
        return getDomain() + MY_CHARACTER_CRAFTING;
    }
}
