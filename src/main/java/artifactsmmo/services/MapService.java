package artifactsmmo.services;

import artifactsmmo.models.entity.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import artifactsmmo.models.response.MapResponse;
import artifactsmmo.utils.RestUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@Service
public class MapService {

    @Autowired
    private RestTemplate restTemplate;

    private  static final Logger LOGGER = LoggerFactory.getLogger(MapService.class);
    private final RestUtils restUtils = new RestUtils();

    public List<Map> getAllMaps(String content_type, String content_code, int page, int size){
        try {
            HttpEntity<String> entity = restUtils.entityHeader();

            String url = UriComponentsBuilder.fromHttpUrl(restUtils.getAllMapsUrl())
                    .queryParamIfPresent("content_type", Optional.ofNullable(content_type))
                    .queryParamIfPresent("content_code", Optional.ofNullable(content_code))
                    .queryParam("page", page)
                    .queryParam("size", size)
                    .encode()
                    .toUriString();


            ResponseEntity<MapResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, MapResponse.class);
            LOGGER.info("{} status : {}",url, response.getStatusCode() );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null && response.getBody().getData() != null) {
                MapResponse mapResponse = response.getBody();
                List<Map> maps = new ArrayList<>();
                maps.addAll(mapResponse.getData());
                // Get Alls maps from all pages
                if(mapResponse.getPage() < mapResponse.getPages()) {
                    maps.addAll(getAllMaps(content_type, content_code, ++page, size));
                }

                return maps;
            } else if(response.getStatusCode() == HttpStatus.NOT_FOUND){
                LOGGER.warn("No maps found");
                return null;
            } else {
                throw new RuntimeException("Failed to fetch characters: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch characters", e);
        }
    }
}
