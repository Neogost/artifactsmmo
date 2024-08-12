package artifactsmmo.services;


import artifactsmmo.models.entity.Character;
import artifactsmmo.models.response.FindCharacterResponse;
import artifactsmmo.utils.RestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CharacterService {


    @Autowired
    private RestTemplate restTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(CharacterService.class);
    private final RestUtils restUtils = new RestUtils();


    public Character getCharacter(String name) {
        try {
            HttpEntity<String> entity = restUtils.entityHeaderWithoutToken();
            String url = String.format(restUtils.getCharacter(), name);
            ResponseEntity<FindCharacterResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, FindCharacterResponse.class);
            LOGGER.info("{} status : {}", url, response.getStatusCode());

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody().getCharacter();
            } else if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                LOGGER.warn("Character not found");
                return null;
            } else {
                throw new RuntimeException("Failed to fetch characters: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch characters", e);
        }
    }
}