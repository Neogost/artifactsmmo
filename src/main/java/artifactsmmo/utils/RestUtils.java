package artifactsmmo.utils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import artifactsmmo.business.AppConfig;
import org.springframework.http.MediaType;

import java.util.Map;


public class RestUtils {

    /* %s = name of the character */
    String CHARACTER_URL = "characters/%s";
    String CHARACTERS_URL = "characters";
    String CHARACTERS_DELETE_URL = "characters/delete";
    String CHARACTERS_CREATE_URL = "characters/create";


    String MY_CHARACTERS_URL = "my/characters";
    String MY_CHARACTERS_LOGS_URL = "my/logs";
    String MY_CHARACTER_DELETE_ITEM_URL = "my/%s/action/delete";
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
    String MY_CHARACTER_EXCHANGE_TASK = "my/%s/action/task/exchange";
    /* %s = name of the character */
    String MY_CHARACTER_CRAFTING = "my/%s/action/crafting";
    String MY_CHARACTER_GE_SELL_ITEM = "my/%s/action/ge/sell";
    String MY_CHARACTER_GE_BUY_ITEM = "my/%s/action/ge/buy";
    String MY_CHARACTER_WITHDRAW_GOLD_BANK = "my/%s/action/bank/withdraw/gold";
    String MY_CHARACTER_WITHDRAW_BANK = "my/%s/action/bank/withdraw";
    String MY_CHARACTER_DEPOSITE_GOLD_BANK = "my/%s/action/bank/deposite/gold";
    String MY_CHARACTER_RECYCLING = "my/%s/action/recycling/";
    String MY_CHARACTER_UNEQUIP_ITEM = "my/%s/action/unequip/";
    String MY_CHARACTER_EQUIP_ITEM = "my/%s/action/equip/";


    String MY_ACCOUNT_BANK_ITEMS = "my/bank/items";
    String MY_ACCOUNT_BANK_GOLD = "my/bank/gold";
    String MY_ACCOUNT_CHANGE_PASSWORD = "my/change_password";

    String GE_ITEMS = "ge/";
    String GE_ITEM = "ge/%s";

    /* %s = name of the character */
    String MY_CHARACTER_GATHERING = "my/%s/action/gathering";

    String RESOURCES = "resources/";
    String RESOURCE = "resource/%s";

    String MONSTERS = "monsters/";
    String MONSTER = "monsters/%s";

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
        return new HttpEntity<>(body, headers);
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

    public String getMyCharacterAcceptNewTaskUrl() {
        return getDomain() + MY_CHARACTER_NEW_TASK;
    }

    public String getCharacterUrl() {
        return getDomain() + CHARACTER_URL;
    }

    public String getAllCharacterUrl() {
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
        return getDomain() + GE_ITEMS;
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
        return getDomain() + MY_ACCOUNT_CHANGE_PASSWORD;
    }

    public String getStatusUrl() {
        return getDomain();
    }

    public String getMyCharactersLogsUrl() {
        return getDomain() + MY_CHARACTERS_LOGS_URL;
    }

    public String getMyCharacterDeleteItemUrl() {
        return getDomain() + MY_CHARACTER_DELETE_ITEM_URL;
    }

    public String getMyCharacterExchangeTaskUrl() {
        return getDomain() + MY_CHARACTER_EXCHANGE_TASK;
    }

    public String getMyCharacterGeSellItemUrl() {
        return getDomain() + MY_CHARACTER_GE_SELL_ITEM;
    }

    public String getMyCharacterGeBuyItemUrl() {
        return getDomain() + MY_CHARACTER_GE_BUY_ITEM;
    }

    public String getMyCharacterWithdrawGoldBankUrl() {
        return getDomain() + MY_CHARACTER_WITHDRAW_GOLD_BANK;
    }

    public String getMyCharacterDepositeGoldBankUrl() {
        return getDomain() + MY_CHARACTER_DEPOSITE_GOLD_BANK;
    }
    public String getMyCharacterWithdrawBankUrl() {
        return getDomain() + MY_CHARACTER_WITHDRAW_BANK;
    }

    public String getMyCharacterRecyclingUrl() {
        return getDomain() + MY_CHARACTER_RECYCLING;
    }

    public String getMyCharacterUnequipItemUrl() {
        return getDomain() + MY_CHARACTER_UNEQUIP_ITEM;
    }
    public String getMyCharacterEquipItemUrl() {
        return getDomain() + MY_CHARACTER_EQUIP_ITEM;
    }
}
