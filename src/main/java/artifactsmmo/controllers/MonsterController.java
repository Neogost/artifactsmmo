package artifactsmmo.controllers;

import artifactsmmo.models.entity.Monster;
import artifactsmmo.services.MonsterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static artifactsmmo.utils.ValidationUtils.*;

@RestController
public class MonsterController {

    @Autowired
    private MonsterService monsterService;
    private static final Logger LOGGER = LoggerFactory.getLogger(MonsterController.class);

    /**
     * Récupère une liste de tous les monstres du jeu en utilisant des paramètres par défaut.
     * <p>
     * Cette méthode permet de récupérer tous les monstres du jeu en utilisant les paramètres par défaut suivants :
     * <ul>
     *     <li><b>drop</b> : <code>null</code> (pas de filtre sur le type de drop)</li>
     *     <li><b>maxLevel</b> : 30 (niveau maximum des monstres)</li>
     *     <li><b>minLevel</b> : 1 (niveau minimum des monstres)</li>
     *     <li><b>page</b> : 1 (première page)</li>
     *     <li><b>size</b> : 50 (nombre de monstres par page)</li>
     * </ul>
     * </p>
     *
     * @return une liste d'objets {@link Monster} représentant tous les monstres du jeu avec les paramètres par défaut.
     */
    public List<Monster> getAllMonsters() {
        LOGGER.info("Getting the all monsters with default parameters");
        try {
            return getAllMonsters(null, 30, 1, 1, 50);
        } catch (RuntimeException e) {
            LOGGER.error("Error fetching monsters with default parameters : {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Récupère une liste paginée de tous les monstres du jeu en fonction des critères spécifiés.
     * <p>
     * Cette méthode permet de filtrer et de paginer les monstres du jeu en fonction des paramètres suivants :
     * <ul>
     *     <li><b>drop</b> : le type de drop que les monstres doivent avoir.</li>
     *     <li><b>minLevel</b> : le niveau minimum des monstres à inclure.</li>
     *     <li><b>maxLevel</b> : le niveau maximum des monstres à inclure, doit être supérieur ou égal à <code>minLevel</code>.</li>
     *     <li><b>page</b> : le numéro de la page à récupérer.</li>
     *     <li><b>size</b> : le nombre de monstres par page.</li>
     * </ul>
     * Tous les paramètres sont validés avant l'exécution de la requête. Si une validation échoue ou si une erreur survient lors de la récupération des monstres,
     * une exception {@link RuntimeException} est lancée.
     * </p>
     *
     * @param drop     le type de drop pour filtrer les monstres.
     * @param maxLevel le niveau maximum des monstres à inclure, doit être supérieur ou égal à <code>minLevel</code>.
     * @param minLevel le niveau minimum des monstres à inclure.
     * @param page     le numéro de la page à récupérer, doit être un entier positif.
     * @param size     le nombre de monstres à récupérer par page, doit être un entier positif.
     * @return une liste d'objets {@link Monster} correspondant aux critères spécifiés.
     * @throws RuntimeException si une erreur se produit lors de la récupération des monstres.
     */
    public List<Monster> getAllMonsters(String drop, int maxLevel, int minLevel, int page, int size) {
        LOGGER.info("Getting the all monsters.");
        try {

            validateDrop(drop);
            validateMinLevel(minLevel);
            validateMaxLevel(maxLevel);
            validateLevelsParam(minLevel, maxLevel);
            validatePage(page);
            validatePageSize(size);

            return monsterService.getAllMonsters(null, 30, 1, 1, 50);
        } catch (RuntimeException e) {
            LOGGER.error("Error fetching monsters : {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }


    /**
     * Récupère un monstre spécifique en fonction du code fourni.
     * <p>
     * Cette méthode permet de récupérer un monstre identifié par un code unique.
     * Le code du monstre est validé avant d'effectuer la récupération. Si une validation échoue ou si une erreur survient lors de la récupération du monstre,
     * une exception {@link RuntimeException} est lancée.
     * </p>
     *
     * @param code le code unique du monstre à récupérer. Ce paramètre ne peut pas être nul ou vide.
     * @return l'objet {@link Monster} correspondant au code spécifié.
     * @throws RuntimeException si une erreur se produit lors de la validation du code ou de la récupération du monstre.
     */
    public Monster getMonster(String code) {
        LOGGER.info("Getting the monster {}.", code);
        try {
            validateMonsterCode(code);
            return monsterService.getMonster(code);
        } catch (RuntimeException e) {
            LOGGER.error("Error fetching monster : {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

}
