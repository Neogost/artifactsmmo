package artifactsmmo.controllers;

import artifactsmmo.models.entity.Gold;
import artifactsmmo.models.entity.ItemSimple;
import artifactsmmo.services.MyAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static artifactsmmo.utils.ValidationUtils.*;

@RestController
public class MyAccountController {

    @Autowired
    private MyAccountService myAccountService;
    private static final Logger LOGGER = LoggerFactory.getLogger(MyAccountController.class);


    /**
     * Retrieves all bank items using default parameters.
     * <p>
     * This method logs the retrieval process and fetches the bank items
     * by delegating the operation to {@code getBankItems} with default pagination
     * and filtering parameters.
     * </p>
     *
     * @return a {@link List} of {@link ItemSimple} representing the items in the bank.
     */
    public List<ItemSimple> getBankItems() {
        LOGGER.info("Getting the all bank items with default parameters");
        try {
            return getBankItems(null, 1, 50);
        } catch (RuntimeException e) {
            LOGGER.error("Error fetching items in bank with default parameters: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }

    }

    /**
     * Retrieves bank items based on the provided parameters.
     * <p>
     * This method logs the retrieval process, validates the input parameters,
     * and then delegates the operation to the {@code myAccountService} to fetch the bank items.
     * The items are returned in a paginated format.
     * </p>
     *
     * @param code the optional item code to filter the bank items.
     *             Can be {@code null} to retrieve all items.
     * @param page the page number to retrieve.
     *             Must be a positive integer.
     * @param size the number of items per page.
     *             Must be a positive integer.
     * @return a {@link List} of {@link ItemSimple} representing the items in the bank for the specified page.
     * @throws IllegalArgumentException if the item code is invalid, or if the page or size parameters are not positive integers.
     */
    public List<ItemSimple> getBankItems(String code, int page, int size) {
        LOGGER.info("Getting the all bank items");
        try {
            validateItemCode(code);
            validatePage(page);
            validatePageSize(size);

            return myAccountService.getBankItems(code, page, size);
        } catch (RuntimeException e) {
            LOGGER.error("Error fetching items in bank : {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves the total amount of gold in the bank.
     * <p>
     * This method logs the retrieval process and delegates the operation
     * to the {@code myAccountService} to fetch the current amount of gold stored in the bank.
     * </p>
     *
     * @return the {@link Gold} object representing the total amount of gold in the bank.
     */
    public Gold getBankGolds() {
        LOGGER.info("Getting the amount of Gold in bank");
        try {
            return myAccountService.getBankGold();
        } catch (RuntimeException e) {
            LOGGER.error("Error fetching golds in bank : {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Changes the user's password.
     * <p>
     * This method logs the request to change the password, validates the new password,
     * and then delegates the operation to the {@code myAccountService} to perform the password change.
     * The new password is then returned upon successful update.
     * </p>
     *
     * @param password the new password to be set.
     *                 Must not be null or invalid according to password validation rules.
     * @return a confirmation message or the new password, depending on the implementation of {@code myAccountService}.
     * @throws IllegalArgumentException if the password is invalid.
     */
    public String changePassword(String password) {
        LOGGER.info("Request to change the password");
        try {
            validatePassword(password);
            return myAccountService.changePassword(password);
        } catch (RuntimeException e) {
            LOGGER.error("Error fetching request to change password : {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
