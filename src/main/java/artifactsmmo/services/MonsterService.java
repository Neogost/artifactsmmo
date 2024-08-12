package artifactsmmo.services;

import artifactsmmo.models.entity.Item;
import artifactsmmo.models.entity.Monster;
import artifactsmmo.models.response.ItemsResponse;
import artifactsmmo.models.response.MonstersResponse;
import artifactsmmo.utils.RestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MonsterService {


    @Autowired
    private RestTemplate restTemplate;


    private static final Logger LOGGER = LoggerFactory.getLogger(MonsterService.class);
    private final RestUtils restUtils = new RestUtils();

    public List<Monster> getAllMonsters(String drop,int maxLevel, int minLevel, int page, int size) {
        List<Monster> monsters = new ArrayList<>();
        try {
            HttpEntity<String> entity = restUtils.entityHeaderWithoutToken();


            String url = UriComponentsBuilder.fromHttpUrl(restUtils.getMonstersUrl())
                    .queryParamIfPresent("drop", Optional.ofNullable(drop))
                    .queryParamIfPresent("max_level", Optional.of(maxLevel))
                    .queryParamIfPresent("min_level", Optional.of(minLevel))
                    .queryParamIfPresent("page", Optional.of(page))
                    .queryParamIfPresent("size", Optional.of(size))
                    .encode()
                    .toUriString();

            ResponseEntity<MonstersResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, MonstersResponse.class);
            LOGGER.info("{} status : {}", url, response.getStatusCode());

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null && response.getBody().getMonsters() != null) {
                MonstersResponse monstersResponse = response.getBody();
                monsters.addAll(monstersResponse.getMonsters());

                // Get All Monsters from other pages
                if(monstersResponse.getPage() < monstersResponse.getPages()) {
                    monsters.addAll(getAllMonsters(drop, maxLevel, minLevel, ++page, size));
                }
            }
            return monsters;
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Failed to fetch monsters", e);
        }
    }
}
