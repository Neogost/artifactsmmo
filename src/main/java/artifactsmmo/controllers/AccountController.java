package artifactsmmo.controllers;

import artifactsmmo.models.entity.Server;
import artifactsmmo.models.response.StatusResponse;
import artifactsmmo.services.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static artifactsmmo.utils.ValidationUtils.validatePassword;
import static artifactsmmo.utils.ValidationUtils.validateUsername;

@RestController
public class AccountController {


    @Autowired
    private AccountService accountController;
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountController.class);

    public void createAccount(String username, String password, String email) {
        LOGGER.info("Request to create the account {}  with the email {}", username, email);
        try {
            validateUsername(username);
            validatePassword(password);

            accountController.createAccount(username, password, email);
        } catch (Exception e) {
            LOGGER.error("Error fetching account creation response: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to fetch creation account response", e);

        }
    }

    /**
     * Retrieves the current status of the servers.
     * <p>
     * This method logs the request to get the server status and then delegates the operation
     * to {@code accountController} to fetch the current status of the servers.
     * </p>
     *
     * @return a {@link Server} object representing the current status of the servers.
     */
    public Server getStatus() {
        LOGGER.info("Getting status of the servers");
        try {
            return accountController.getStatus();
        } catch (RuntimeException e) {
            LOGGER.error("Error fetching server information : {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
