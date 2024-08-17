package artifactsmmo.controllers;

import artifactsmmo.enums.CharacterSort;
import artifactsmmo.enums.Skin;
import artifactsmmo.models.entity.Character;
import artifactsmmo.services.CharacterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static artifactsmmo.utils.ValidationUtils.*;

@RestController
public class CharacterController {

    private  static final Logger LOGGER = LoggerFactory.getLogger(CharacterController.class);
    @Autowired
    private CharacterService characterService;

    /**
     * Récupère un personnage spécifique en fonction du nom fourni.
     * <p>
     * Cette méthode permet de récupérer un personnage identifié par son nom. Le nom du personnage est validé avant de procéder à la récupération.
     * En cas d'erreur de validation ou lors de la récupération du personnage, une exception {@link RuntimeException} est lancée.
     * </p>
     *
     * @param name le nom unique du personnage à récupérer. Ce paramètre ne peut pas être nul ou vide.
     * @return l'objet {@link Character} correspondant au nom spécifié.
     * @throws RuntimeException si une erreur se produit lors de la validation du nom ou de la récupération du personnage.
     */
    public Character getCharacter(String name) {
        LOGGER.info("{} status updated", name);
        try {
            validateCharacterName(name);

            return characterService.getCharacter(name);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Récupère une liste paginée de personnages en fonction des critères spécifiés.
     * <p>
     * Cette méthode permet de récupérer tous les personnages en utilisant les critères suivants :
     * <ul>
     *     <li><b>page</b> : le numéro de la page à récupérer.</li>
     *     <li><b>size</b> : le nombre de personnages à récupérer par page.</li>
     *     <li><b>sort</b> : le critère de tri des personnages, basé sur le type de tri spécifié.</li>
     * </ul>
     * Les paramètres sont validés avant la récupération des personnages. En cas d'erreur lors de la validation ou de la récupération,
     * une exception {@link RuntimeException} est lancée.
     * </p>
     *
     * @param page le numéro de la page à récupérer, doit être un entier positif.
     * @param size le nombre de personnages à récupérer par page, doit être un entier positif.
     * @param sort le critère de tri des personnages, spécifié par une instance de {@link CharacterSort}. Ce paramètre ne peut pas être nul.
     * @return une liste d'objets {@link Character} correspondant aux critères spécifiés.
     * @throws RuntimeException si une erreur se produit lors de la validation des paramètres ou de la récupération des personnages.
     */
    public List<Character> getCharacters(int page, int size, CharacterSort sort) {
        LOGGER.info("Getting all characters status");
        try {
            validatePage(page);
            validatePageSize(size);
            validateCharacterSort(sort);

            return characterService.getAllCharacters(page, size, sort.getValue());
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Récupère une liste de tous les personnages en utilisant des paramètres par défaut.
     * <p>
     * Cette méthode permet de récupérer tous les personnages avec les paramètres de pagination et de tri par défaut.
     * Les paramètres par défaut utilisés sont :
     * <ul>
     *     <li><b>page</b> : 1 (première page)</li>
     *     <li><b>size</b> : 100 (nombre de personnages par page)</li>
     *     <li><b>sort</b> : <code>null</code> (aucun tri spécifique appliqué)</li>
     * </ul>
     * </p>
     *
     * @return une liste d'objets {@link Character} représentant tous les personnages, en utilisant les paramètres par défaut.
     */
    public List<Character> getAllCharacters() {
        LOGGER.info("Getting all characters with default parameters");
        return getCharacters(1,100,null);
    }

    /**
     * Creates a new character with the specified name and skin.
     * <p>
     * This method logs the creation process, validates the character's name,
     * and then delegates the creation operation to the {@code characterService}.
     * The created character's information is returned upon successful creation.
     * </p>
     *
     * @param name the name of the character to be created.
     *             Must not be null or empty.
     * @param skin the skin type for the character.
     *             Must not be null and should be one of the predefined {@link Skin} values.
     * @return the created {@link Character} object.
     * @throws IllegalArgumentException if the character's name is invalid (e.g., null or empty).
     * @throws IllegalArgumentException if a character with the specified name already exists.
     */
    public Character createCharacter(String name, Skin skin) {
        LOGGER.info("Creating new character named {}", name);
        validateCharacterName(name);
        return characterService.createCharacter(name, skin.getValue());
    }

    /**
     * Deletes a character by its name.
     * <p>
     * This method logs the deletion process, validates the character's name,
     * and then delegates the deletion operation to the {@code characterService}.
     * If the character is found and deleted successfully, the deleted character's
     * information is returned.
     * </p>
     *
     * @param name the name of the character to be deleted.
     *             Must not be null or empty.
     * @return the deleted {@link Character} object if found and successfully deleted.
     * @throws IllegalArgumentException if the character's name is invalid (e.g., null or empty).
     * @throws IllegalArgumentException if no character with the specified name exists.
     */
    public Character deleteCharacter(String name) {
        LOGGER.info("Deleting character named {}", name);
        validateCharacterName(name);
        return characterService.deleteCharacter(name);
    }

}
