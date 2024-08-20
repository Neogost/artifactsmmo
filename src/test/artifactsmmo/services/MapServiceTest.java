package src.test.artifactsmmo.services;

import artifactsmmo.models.entity.Map;
import artifactsmmo.services.MapService;
import artifactsmmo.utils.RestUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertNotNull;


@SpringBootTest
public class MapServiceTest {

    private MapService mapService;

    @Autowired
    private RestTemplate restTemplate;


    @Before
    public void setup() {
        this.mapService = new MapService(restTemplate);

    }

    @Test
    public void testGetMap_Success() {
        int x = 10;
        int y = 20;

        // Appel réel à l'API
        Map result = mapService.getMap(x, y);

        // Vérification du résultat
        assertNotNull(result);
        // Ajoutez des assertions supplémentaires selon ce que vous attendez de l'API
    }

}
