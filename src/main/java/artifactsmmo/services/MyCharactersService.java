package artifactsmmo.services;

import artifactsmmo.enums.ErrorCode;
import artifactsmmo.models.schema.SkillDataSchema;
import artifactsmmo.models.entity.ItemInventory;
import artifactsmmo.models.response.*;
import artifactsmmo.models.entity.MyCharactersList;
import artifactsmmo.utils.RestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;


@Service
public class MyCharactersService {


    @Autowired
    private RestTemplate restTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(MyCharactersService.class);
    private final RestUtils restUtils = new RestUtils();


    public MyCharactersList getMyCharacters() {
        try {

            HttpEntity<String> entity = restUtils.entityHeader();
            ResponseEntity<MyCharactersList> response = restTemplate.exchange(restUtils.getMyCharactersUrl(), HttpMethod.GET, entity, MyCharactersList.class);

            LOGGER.info("{} status : {}", restUtils.getMyCharactersUrl(), response.getStatusCode());

            return response.getBody();
        } catch (HttpClientErrorException e) {
            LOGGER.info("{} status : {}", restUtils.getMyCharactersUrl(), e.getStatusCode());
            handleHttpClientErrorException(e);
        }
        return null;
    }

    public CharacterResponse move(String name, @RequestBody int x, @RequestBody int y) {
        // Create the request body as a MultiValueMap
        Map<String, Integer> body = new HashMap<>();
        body.put("x", x);
        body.put("y", y);
        String url = String.format(restUtils.getMyCharacterMoveUrl(), name);

        HttpEntity<Object> entity = restUtils.entityHeader(body);
        try {
            ResponseEntity<CharacterResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, CharacterResponse.class);

            LOGGER.info("{} status : {}", url, response.getStatusCode());
            return response.getBody();
        } catch (HttpClientErrorException e) {
            LOGGER.info("{} status : {}", url, e.getStatusCode());
            handleHttpClientErrorException(e);

        }
        return null;
    }

    public NewTaskResponse acceptNewTask(String name) {

        String url = String.format(restUtils.getMyCharacterNewTaskUrl(), name);
        HttpEntity<String> entity = restUtils.entityHeader();
        try {
            ResponseEntity<NewTaskResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, NewTaskResponse.class);
            LOGGER.info("{} status : {}", url, response.getStatusCode());
            return response.getBody();
        } catch (HttpClientErrorException e) {

            LOGGER.info("{} status : {}", url, e.getStatusCode());
            handleHttpClientErrorException(e);

        }

        return null;
    }
    public FightResponse fight(String name) {
        String url = String.format(restUtils.getMyCharacterFightUrl(), name);
        HttpEntity<String> entity = restUtils.entityHeader();

        try {
            ResponseEntity<FightResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, FightResponse.class);
            LOGGER.info("{} status : {}", url, response.getStatusCode());
            return response.getBody();
        } catch (HttpClientErrorException e) {
            LOGGER.info("{} status : {}", url, e.getStatusCode());
            handleHttpClientErrorException(e);
        }
        return null;

    }


    public TaskRewardResponse completeTask(String name) {
        String url = String.format(restUtils.getMyCharacterCompleteTaskUrl(), name);
        HttpEntity<String> entity = restUtils.entityHeader();

        try {
            ResponseEntity<TaskRewardResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, TaskRewardResponse.class);
            LOGGER.info("{} status : {}", url, response.getStatusCode());
            return response.getBody();
        } catch (HttpClientErrorException e) {
            LOGGER.info("{} status : {}", url, e.getStatusCode());
            handleHttpClientErrorException(e);
        }
        return null;

    }


    public GatheringResponse gathering(String name) {
        String url = String.format(restUtils.getMyCharacterGatheringUrl(), name);
        HttpEntity<String> entity = restUtils.entityHeader();

        try {
            ResponseEntity<GatheringResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, GatheringResponse.class);
            LOGGER.info("{} status : {}", url, response.getStatusCode());
            return response.getBody();
        } catch (HttpClientErrorException e) {
            LOGGER.info("{} status : {}", url, e.getStatusCode());
            handleHttpClientErrorException(e);
        } catch (RestClientException e) {
            LOGGER.error("The structure of object is incorrect : {}", e.getMessage());
            throw new RuntimeException(e);

        }
        return null;
    }



    public SkillDataSchema crafting(String name, String code, int quantity) {
        String url = String.format(restUtils.getMyCharacterCraftingUrl(), name);

        Map<String, Object> body = new HashMap<>();
        body.put("code", code);
        body.put("quantity", quantity);

        HttpEntity<Object> entity = restUtils.entityHeader(body);

        try {
            ResponseEntity<SkillDataSchema> response = restTemplate.exchange(url, HttpMethod.POST, entity, SkillDataSchema.class);
            LOGGER.info("{} status : {}", url, response.getStatusCode());
            return response.getBody();
        } catch (HttpClientErrorException e) {
            LOGGER.info("{} status : {}", url, e.getStatusCode());
            handleHttpClientErrorException(e);
        }
        return null;

    }

    public BankItemResponse bankDeposit(String name, ItemInventory item, int quantity) {
        String url = String.format(restUtils.getMyCharacterBankDepositUrl(), name);

        // Create the request body as a MultiValueMap
        Map<String, Object> body = new HashMap<>();
        body.put("code", item.getCode());
        body.put("quantity", quantity);

        HttpEntity<Object> entity = restUtils.entityHeader(body);

        try {
            ResponseEntity<BankItemResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, BankItemResponse.class);
            LOGGER.info("{} status : {}", url, response.getStatusCode());
            return response.getBody();
        } catch (HttpClientErrorException e) {
            LOGGER.info("{} status : {}", url, e.getStatusCode());
            handleHttpClientErrorException(e);
        }
        return null;

    }

    private void handleHttpClientErrorException(HttpClientErrorException e) {
        Map<Integer, ErrorCode> errorCodeMap = Map.of(
                ErrorCode.ACTION_ALREADY_IN_PROGRESS.getCode(), ErrorCode.ACTION_ALREADY_IN_PROGRESS,
                ErrorCode.CHARACTER_ALREADY_HAS_TASK.getCode(), ErrorCode.CHARACTER_ALREADY_HAS_TASK,
                ErrorCode.CHARACTER_IN_COOLDOWN.getCode(), ErrorCode.CHARACTER_IN_COOLDOWN,
                ErrorCode.CHARACTER_NOT_FOUND.getCode(), ErrorCode.CHARACTER_NOT_FOUND,
                ErrorCode.ENTITY_NOT_FOUND.getCode(), ErrorCode.ENTITY_NOT_FOUND,
                ErrorCode.MAP_NOT_FOUND.getCode(), ErrorCode.MAP_NOT_FOUND,
                ErrorCode.CHARACTER_AT_DESTINATION.getCode(), ErrorCode.CHARACTER_AT_DESTINATION,
                ErrorCode.MISSING_ITEM.getCode(), ErrorCode.MISSING_ITEM
        );

        ErrorCode errorCode = errorCodeMap.get(e.getStatusCode().value());
        if (errorCode != null) {
            throw new HttpClientErrorException(e.getStatusCode(), errorCode.getReason());
        }

        throw e; // Rethrow if no matching error code
    }


}
