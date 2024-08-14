package artifactsmmo.utils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import artifactsmmo.business.AppConfig;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;

import java.util.Map;


public class RestUtils {

    /* %s = name of the character */
    String CHARACTER_URL = "characters/%s";
    String CHARACTERS_URL = "characters";
    String CHARACTERS_DELETE_URL = "characters/delete";
    String CHARACTERS_CREATE_URL = "characters/create";


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
    String MY_ACCOUNT_BANK_GOLD = "my/bank/gold";
    String MY_ACCOUNT_CHANGE_PASSWORD = "my/change_password";

    String GE_ITEMS = "ge/";
    String GE_ITEM = "ge/%s";

    /* %s = name of the character */
    String MY_CHARACTER_GATHERING = "my/%s/action/gathering";

    String RESOURCES = "resources/";
    String RESOURCE = "resource/{code}";

    String MONSTERS = "monsters/";
    String MONSTER = "monsters/{code}";

    String ITEMS = "items/";
    /* %s = code */
    String ITEM = "items/%s";

    String EVENTS = "events/";

    String ACCOUNT_CREATE = "accounts/create";

    String MAPS = "maps/";
    // X et Y
    String MAP = "maps/%s/%s";

    String TOKEN_GENERATE = "token/";

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
    public String getMapsUrl() {
        return getDomain() + MAPS;
    }

    public String getMyCharacterMoveUrl() {
        return getDomain() + MY_CHARACTER_MOVE;
    }

    public String getMyCharacterNewTaskUrl() {
        return getDomain() + MY_CHARACTER_NEW_TASK;
    }
    public String getCharacterUrl() {
        return getDomain() + CHARACTERS_URL;
    }
    public String getAllCharacterUrl() {
        return getDomain() + CHARACTER_URL;
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

    public String getResourceUrl() {
        return getDomain() + RESOURCE;
    }

    public String getMapUrl() {
        return getDomain() + MAP;
    }

    public String getMonsterUrl() {
        return getDomain() + MONSTER;
    }

    public String getEventsUrl() {
        return getDomain() + EVENTS;
    }
    public String getGeItemsUrl() {
        return  getDomain() + GE_ITEMS;
    }

    public String getGeItemUrl() {
        return getDomain() + GE_ITEM;
    }

    public String getAccountCreateUrl() {
        return getDomain() + ACCOUNT_CREATE;
    }

    public String getTokenUrl() {
        return getDomain() + TOKEN_GENERATE;
    }

    public String getCharacterDeleteUrl() {
        return getDomain() + CHARACTERS_DELETE_URL;
    }

    public String getCharacterCreateUrl() {
        return getDomain() + CHARACTERS_CREATE_URL;
    }

    public String getMyAccountBankGoldUrl() {
        return getDomain() + MY_ACCOUNT_BANK_GOLD;
    }

    public String getMyAccountChangePasswordUrl() {
        return  getDomain() + MY_ACCOUNT_CHANGE_PASSWORD;
    }
    public String getStatusUrl() {
        return  getDomain();
    }
}
