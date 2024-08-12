package artifactsmmo.services;

import artifactsmmo.models.entity.SimpleItem;
import artifactsmmo.models.response.BankItemsResponse;
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
public class MyAccountService {

    @Autowired
    private RestTemplate restTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(MyAccountService.class);
    private final RestUtils restUtils = new RestUtils();

    public List<SimpleItem> getBankItems(String codeItem, int page, int size) {
        List<SimpleItem> items = new ArrayList<>();
        try {
            HttpEntity<String> entity = restUtils.entityHeader();


            String url = UriComponentsBuilder.fromHttpUrl(restUtils.getMyBankItems())
                    .queryParamIfPresent("codeItem", Optional.ofNullable(codeItem))
                    .queryParamIfPresent("page", Optional.of(page))
                    .queryParamIfPresent("size", Optional.of(size))
                    .encode()
                    .toUriString();

            ResponseEntity<BankItemsResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, BankItemsResponse.class);
            LOGGER.info("{} status : {}", url, response.getStatusCode());

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null && response.getBody().getItems() != null) {
                BankItemsResponse bankItemsResponse = response.getBody();
                items.addAll(bankItemsResponse.getItems());

                // Get All Items in bank items account
                if(bankItemsResponse.getPage() < bankItemsResponse.getPages()) {
                    items.addAll(getBankItems(codeItem, ++page, size));
                }
            }
            return items;
        } catch (HttpClientErrorException e){
            LOGGER.error("Error during getting bank items");
            throw new RuntimeException("Failed to fetch bank items", e);

        }
    }
}
