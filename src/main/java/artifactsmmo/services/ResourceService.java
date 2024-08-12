package artifactsmmo.services;

import artifactsmmo.models.entity.Resource;
import artifactsmmo.models.response.ResourcesResponse;
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
public class ResourceService {

    @Autowired
    private RestTemplate restTemplate;


    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceService.class);
    private final RestUtils restUtils = new RestUtils();

    public List<Resource> getAllRessources(String drop, int maxLevel, int minLevel, String name, int page, int size, String skill) {
        List<Resource> resources = new ArrayList<>();
        try {
            HttpEntity<String> entity = restUtils.entityHeaderWithoutToken();


            String url = UriComponentsBuilder.fromHttpUrl(restUtils.getResourcesUrl())
                    .queryParamIfPresent("drop", Optional.ofNullable(drop))
                    .queryParamIfPresent("max_level", Optional.of(maxLevel))
                    .queryParamIfPresent("min_level", Optional.of(minLevel))
                    .queryParamIfPresent("name", Optional.ofNullable(name))
                    .queryParamIfPresent("page", Optional.of(page))
                    .queryParamIfPresent("size", Optional.of(size))
                    .queryParamIfPresent("skill", Optional.ofNullable(skill))
                    .encode()
                    .toUriString();

            ResponseEntity<ResourcesResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, ResourcesResponse.class);
            LOGGER.info("{} status : {}", url, response.getStatusCode());
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null && response.getBody().getResources() != null) {
                ResourcesResponse resourcesResponse = response.getBody();
                resources.addAll(resourcesResponse.getResources());

                // Get All items from other pages
                if(resourcesResponse.getPage() < resourcesResponse.getPages()) {
                    resources.addAll(getAllRessources(drop, maxLevel, minLevel, name, ++page, size, skill));
                }
            }
            return resources;
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Failed to fetch characters", e);
        }
    }
}
