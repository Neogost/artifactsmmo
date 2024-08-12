package artifactsmmo.controllers;

import artifactsmmo.models.entity.Item;
import artifactsmmo.models.response.ItemResponse;
import artifactsmmo.services.ItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ItemControler {

    @Autowired
    private ItemService itemService;
    private  static final Logger LOGGER = LoggerFactory.getLogger(ItemControler.class);

    public List<Item> getAllItems() {
        try {
            LOGGER.info("Getting the all items of the game");
            return itemService.getAllItems(null, null, 30, 1, null, 1, 50, null);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public ItemResponse getItem(String code) {
        try {
            LOGGER.info("Getting item {}", code);
            return itemService.getItem(code);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

    }

}
