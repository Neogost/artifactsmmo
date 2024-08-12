package artifactsmmo.services;

import artifactsmmo.models.entity.Item;
import artifactsmmo.models.response.FindCharacterResponse;
import artifactsmmo.models.response.ItemResponse;
import artifactsmmo.models.response.ItemsResponse;
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
public class ItemService {


    @Autowired
    private RestTemplate restTemplate;


    private static final Logger LOGGER = LoggerFactory.getLogger(ItemService.class);
    private final RestUtils restUtils = new RestUtils();

    public List<Item> getAllItems(String craftMaterial, String craftSkill, int maxLevel, int minLevel, String name, int page, int size, String type) {
        List<Item> items = new ArrayList<>();
        try {
            HttpEntity<String> entity = restUtils.entityHeaderWithoutToken();


            String url = UriComponentsBuilder.fromHttpUrl(restUtils.getItemsUrl())
                    .queryParamIfPresent("craft_material", Optional.ofNullable(craftMaterial))
                    .queryParamIfPresent("craft_skill", Optional.ofNullable(craftSkill))
                    .queryParamIfPresent("max_level", Optional.of(maxLevel))
                    .queryParamIfPresent("min_level", Optional.of(minLevel))
                    .queryParamIfPresent("name", Optional.ofNullable(name))
                    .queryParamIfPresent("page", Optional.of(page))
                    .queryParamIfPresent("size", Optional.of(size))
                    .queryParamIfPresent("type", Optional.ofNullable(type))
                    .encode()
                    .toUriString();

            ResponseEntity<ItemsResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, ItemsResponse.class);
            LOGGER.info("{} status : {}", url, response.getStatusCode());
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null && response.getBody().getItems() != null) {
                ItemsResponse itemsResponse = response.getBody();
                items.addAll(itemsResponse.getItems());

                // Get All items from other pages
                if(itemsResponse.getPage() < itemsResponse.getPages()) {
                    items.addAll(getAllItems(craftMaterial, craftSkill, maxLevel, minLevel, name, ++page, size, type));
                }
            }
            return items;
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Failed to fetch characters", e);
        }
    }

    public ItemResponse getItem(String code) {
        try {
            HttpEntity<String> entity = restUtils.entityHeaderWithoutToken();
            String url = String.format(restUtils.getItemUrl(), code);

            ResponseEntity<ItemResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, ItemResponse.class);
            LOGGER.info("{} status : {}", url, response.getStatusCode());
            return response.getBody();
        }catch ( HttpClientErrorException e) {
            throw new RuntimeException("Failed to fetch characters", e);
        }
    }
}
