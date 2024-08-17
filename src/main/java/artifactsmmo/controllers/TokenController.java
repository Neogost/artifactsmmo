package artifactsmmo.controllers;

import artifactsmmo.services.ResourceService;
import artifactsmmo.services.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class TokenController {


    @Autowired
    private TokenService tokenService;
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenController.class);


    /**
     * Génère un jeton (token) d'authentification pour l'utilisateur spécifié.
     * <p>
     * Cette méthode permet de générer un nouveau jeton d'authentification pour un utilisateur en fonction de son nom d'utilisateur et de son mot de passe.
     * Si l'opération échoue pour une raison quelconque, une exception {@link ResponseStatusException} est lancée avec le statut HTTP 500.
     * </p>
     *
     * @param username le nom d'utilisateur pour lequel le jeton doit être généré. Ce paramètre ne peut pas être nul ou vide.
     * @param password le mot de passe de l'utilisateur. Ce paramètre ne peut pas être nul ou vide.
     * @return une chaîne de caractères représentant le jeton généré.
     * @throws ResponseStatusException si une erreur se produit lors de la génération du jeton, avec un statut HTTP 500.
     */
    public String generateToken(String username, String password) {
        LOGGER.info("Generating new token for {}.", username);
        try {
            return tokenService.generateToken(username, password);
        } catch (Exception e) {
            LOGGER.error("Error fetching token: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to fetch resources", e);
        }
    }
}
