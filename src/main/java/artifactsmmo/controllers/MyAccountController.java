package artifactsmmo.controllers;

import artifactsmmo.models.entity.ItemSimple;
import artifactsmmo.services.MyAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MyAccountController {

    @Autowired
    private MyAccountService myAccountService;
    private  static final Logger LOGGER = LoggerFactory.getLogger(MyAccountController.class);


    public List<ItemSimple> getBankItems() {
        try {
            LOGGER.info("Getting the all bank items");
            return myAccountService.getBankItems(null, 1,50);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

    }

}
