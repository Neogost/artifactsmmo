package artifactsmmo.controllers;

import artifactsmmo.enums.Slot;
import artifactsmmo.exception.CharacterActionAlreadyInProgressException;
import artifactsmmo.exception.CharacterInCooldownException;
import artifactsmmo.exception.CharacterInventoryMissingItemException;
import artifactsmmo.exception.CharacterTransitionAlreadyInProgressException;
import artifactsmmo.models.entity.*;
import artifactsmmo.models.entity.Character;
import artifactsmmo.models.schema.*;
import artifactsmmo.services.CharacterService;
import artifactsmmo.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import artifactsmmo.services.MyCharactersService;
import org.springframework.web.client.HttpClientErrorException;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static artifactsmmo.utils.ValidationUtils.*;

@RestController
public class MyCharactersController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyCharactersController.class);
    @Autowired
    private MyCharactersService myCharactersService;
    @Autowired
    private CharacterService characterService;

    /**
     * Retrieves a list of the current user's characters.
     * <p>
     * This method logs the request to get all characters associated with the current user.
     * It delegates the operation to {@code myCharactersService} to fetch the list of characters.
     * If an error occurs during the retrieval process, it logs the error and rethrows the exception.
     * </p>
     *
     * @return a {@link List} of {@link Character} objects representing all characters associated with the current user.
     * @throws RuntimeException if an error occurs while fetching the characters, such as an issue with the service call.
     */
    public List<Character> getMyCharacters() {
        LOGGER.info("Getting all my characters");
        try {
            return myCharactersService.getMyCharacters();
        } catch (RuntimeException e) {
            LOGGER.error("Error fetching my characters : {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Récupère tous les journaux (logs) des personnages avec des paramètres par défaut.
     * <p>
     * Cette méthode obtient tous les journaux des personnages en utilisant des paramètres par défaut pour la pagination. Elle
     * appelle une méthode interne avec une page de départ de 1 et une taille de page de 100. En cas d'erreur lors de la récupération
     * des journaux, elle gère les exceptions en les enregistrant et en les relançant sous forme de `RuntimeException`.
     * </p>
     *
     * @return Une liste d'objets `LogSchema` contenant les journaux de tous les personnages.
     * @throws RuntimeException Si une erreur survient lors de la récupération des journaux.
     */
    public List<LogSchema> getAllCharactersLogs() {
        LOGGER.info("Getting all my characters logs with default parameters.");
        try {
            return getAllCharactersLogs(1,100);
        } catch (RuntimeException e) {
            LOGGER.error("Error fetching my characters : {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Récupère les journaux (logs) des personnages en fonction de la pagination spécifiée.
     * <p>
     * Cette méthode récupère les journaux des personnages en utilisant les paramètres de pagination fournis. Elle valide les
     * paramètres de page et de taille de page avant d'appeler le service approprié pour obtenir les données. En cas d'erreur lors
     * de la récupération des journaux, la méthode enregistre l'erreur et la relance sous forme de `RuntimeException`.
     * </p>
     *
     * @param page Le numéro de la page à récupérer, utilisé pour la pagination des résultats. Doit être supérieur à 0.
     * @param size Le nombre d'éléments par page, utilisé pour la pagination des résultats. Doit être supérieur à 0.
     * @return Une liste d'objets `LogSchema` contenant les journaux des personnages pour la page et la taille spécifiées.
     * @throws IllegalArgumentException Si les paramètres `page` ou `size` ne sont pas valides (par exemple, s'ils sont inférieurs ou égaux à 0).
     * @throws RuntimeException Si une erreur survient lors de la récupération des journaux.
     */
    public List<LogSchema> getAllCharactersLogs(int page, int size) {
        LOGGER.info("Getting all my characters logs.");
        try {
            validatePage(page);
            validatePageSize(size);
            return myCharactersService.getAllCharactersLog(page, size);
        } catch (RuntimeException e) {
            LOGGER.error("Error fetching my characters logs : {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }


    /**
     * Moves a character to the specified coordinates.
     * <p>
     * This method logs the movement request, validates the character's name, and attempts to move the character
     * to the specified coordinates (x, y). If the character is currently in cooldown, it waits until the cooldown
     * period expires and then tries to move the character again. If any runtime exception occurs during the process,
     * it logs the error and rethrows the exception.
     * </p>
     *
     * @param name the name of the character to move. Must not be {@code null} or empty.
     * @param x    the x-coordinate to move the character to.
     * @param y    the y-coordinate to move the character to.
     * @return a {@link CharacterMovementDataSchema} object containing the result of the movement operation.
     * @throws InterruptedException if the thread is interrupted while sleeping during cooldown.
     * @throws RuntimeException     if an error occurs during the movement process or while fetching character data.
     */
    public CharacterMovementDataSchema move(String name, int x, int y) throws InterruptedException {
        LOGGER.info("{} moving to 'x,y' : {},{}.", name, x, y);
        try {
            validateCharacterName(name);
            return myCharactersService.move(name, x, y);
        } catch (CharacterInCooldownException e) {
            LOGGER.warn("Impossible to move, character {} was in cooldown.", name);
            waitCooldown(name);
            // Try to move again
            return move(name, x, y);
        } catch (CharacterActionAlreadyInProgressException e) {
            LOGGER.error("Impossible to move, character {} have already an action in progress.", name);
            throw new RuntimeException(e);
        } catch (RuntimeException e) {
            LOGGER.error("{} : Error during movement : {}", name, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }


    /**
     * Moves a character to the specified map coordinates.
     * <p>
     * This method logs the request to move the character to the coordinates of the specified map.
     * It delegates the movement operation to the {@code move} method, which handles the movement logic.
     * If an error occurs during the movement process, such as an HTTP client error or an interruption,
     * it logs the error and rethrows it as a {@code RuntimeException}.
     * </p>
     *
     * @param name the name of the character to move. Must not be {@code null} or empty.
     * @param map  the {@link Map} object representing the destination coordinates. Must not be {@code null}.
     *             The map's {@code getX()} and {@code getY()} methods provide the target coordinates.
     * @return a {@link CharacterMovementDataSchema} object containing the result of the movement operation.
     * @throws RuntimeException if an error occurs during the movement process, such as an HTTP client error
     *                          or if interrupted while performing the movement.
     */
    public CharacterMovementDataSchema moveToMap(String name, Map map) {
        LOGGER.info("{} moving to Map('x,y') : Map({},{}).", name, map.getX(), map.getX());
        try {
            return move(name, map.getX(), map.getY());
        } catch (HttpClientErrorException | InterruptedException e) {
            LOGGER.error("{} : Error during movement to a specifique map: {}", name, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }


    /**
     * Accepts a new task for the specified character.
     * <p>
     * This method logs the request to accept a new task for the character, validates the character's name,
     * and attempts to accept the task by delegating to {@code myCharactersService}.
     * If the character is in cooldown, it waits for the cooldown period to expire and retries the task acceptance.
     * If the character already has an action in progress, it logs an error and throws a {@code RuntimeException}.
     * If an HTTP client error occurs during the process, it logs the error and throws a {@code RuntimeException}.
     * </p>
     *
     * @param name the name of the character accepting the new task. Must not be {@code null} or empty.
     * @return a {@link TaskDataSchema} object containing the result of the task acceptance operation.
     * @throws RuntimeException if an error occurs during the task acceptance, such as:
     *                          - The character already has an action in progress ({@link CharacterActionAlreadyInProgressException}).
     *                          - An HTTP client error occurs ({@link HttpClientErrorException}).
     */
    public TaskDataSchema acceptNewTask(String name) throws InterruptedException {
        LOGGER.info("{} accept a new task.", name);
        try {
            validateCharacterName(name);

            return myCharactersService.acceptNewTask(name);
        } catch (CharacterInCooldownException e) {
            LOGGER.warn("Impossible to accept a new task, character {} was in cooldown.", name);
            waitCooldown(name);
            // Try to do it again
            return acceptNewTask(name);
        } catch (CharacterActionAlreadyInProgressException e) {
            LOGGER.error("Impossible to accept a new task, character {} have already an action in progress.", name);
            throw new RuntimeException(e);
        } catch (HttpClientErrorException e) {
            LOGGER.error("{} :  Error during acceptation of a new task : {}", name, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Deposits a specified quantity of an item into the bank for a given character.
     * <p>
     * This method logs the deposit request, validates the character's name, item code, and quantity,
     * and then attempts to deposit the item into the bank using {@code myCharactersService}.
     * If the character is in cooldown, it waits for the cooldown period to expire and retries the deposit.
     * If the character is already engaged in an action or if there are issues with the item or quantity,
     * it logs the errors and throws appropriate exceptions. If an HTTP client error occurs, it logs the error
     * and throws a {@code RuntimeException}.
     * </p>
     *
     * @param name     the name of the character depositing the item. Must not be {@code null} or empty.
     * @param item     the {@link ItemInventory} object representing the item to deposit. Must not be {@code null}.
     *                 The item's code is used to identify the item.
     * @param quantity the quantity of the item to deposit. Must be a positive integer.
     * @return a {@link BankItemSchema} object containing the result of the deposit operation.
     * @throws InterruptedException if the thread is interrupted while waiting for the cooldown.
     * @throws RuntimeException     if an error occurs during the deposit process, such as:
     *                              - The character already has an action in progress ({@link CharacterActionAlreadyInProgressException}).
     *                              - The item is missing or there is insufficient quantity ({@link CharacterInventoryMissingItemException}).
     *                              - A transaction is already in progress with this item/gold ({@link CharacterTransitionAlreadyInProgressException}).
     *                              - An HTTP client error occurs ({@link HttpClientErrorException}).
     */
    public BankItemSchema deposeItemInBank(String name, ItemInventory item, int quantity) throws InterruptedException {
        LOGGER.info("{} depose the item {} in {} quantity in bank.", name, item.getCode(), quantity);
        try {
            validateCharacterName(name);
            validateItemCode(item.getCode());
            validateQuantity(quantity);

            return myCharactersService.depositBank(name, item.getCode(), quantity);
        } catch (CharacterInCooldownException e) {
            LOGGER.warn("Impossible to depose item in bank, character {} was in cooldown.", name);
            waitCooldown(name);
            // Try to do it again
            return deposeItemInBank(name, item, quantity);
        } catch (CharacterActionAlreadyInProgressException e) {
            LOGGER.error("Impossible to depose item in bank, character {} have already an action in progress.", name);
            throw new RuntimeException(e);
        } catch (CharacterInventoryMissingItemException e) {
            LOGGER.error("Impossible to depose item in bank, character {} as a missing item or insufficient quantity", name);
            throw new RuntimeException(e);
        } catch (CharacterTransitionAlreadyInProgressException e) {
            LOGGER.error("Impossible to depose item in bank, an transaction is already in progress with this item/gold {} in bank", item.getCode());
            throw new RuntimeException(e);
        } catch (HttpClientErrorException e) {
            LOGGER.error("{} : Error during deposit item in bank : {}", name, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Dépose une quantité spécifiée d'or dans la banque pour un personnage donné.
     * <p>
     * Cette méthode permet à un personnage de déposer une certaine quantité d'or dans la banque. Elle valide le nom du personnage
     * et la quantité d'or avant de procéder au dépôt. Si le personnage est en cooldown ou si une autre action est en cours,
     * la méthode gère ces exceptions et réessaie le dépôt si nécessaire. Des exceptions spécifiques sont également gérées pour
     * les problèmes d'inventaire et les transactions en cours.
     * </p>
     *
     * @param name     Le nom du personnage qui tente de déposer de l'or dans la banque.
     * @param quantity La quantité d'or à déposer dans la banque.
     * @return Un objet `GoldTransactionSchema` contenant les détails de la transaction de dépôt d'or.
     * @throws InterruptedException Si le thread est interrompu pendant l'attente du cooldown.
     * @throws RuntimeException     Si une erreur survient lors du dépôt, notamment en cas de cooldown du personnage,
     *                              d'action déjà en cours, de problème d'inventaire, ou de transaction en cours.
     */
    public GoldTransactionSchema deposeGoldInBank(String name, int quantity) throws InterruptedException {
        LOGGER.info("{} depose golds in {} quantity in bank.", name, quantity);
        try {
            validateCharacterName(name);
            validateQuantity(quantity);

            return myCharactersService.depositBankGold(name, quantity);
        } catch (CharacterInCooldownException e) {
            LOGGER.warn("Impossible to depose golds in bank, character {} was in cooldown.", name);
            waitCooldown(name);
            // Try to do it again
            return deposeGoldInBank(name, quantity);
        } catch (CharacterActionAlreadyInProgressException e) {
            LOGGER.error("Impossible to depose golds in bank, character {} have already an action in progress.", name);
            throw new RuntimeException(e);
        } catch (CharacterInventoryMissingItemException e) {
            LOGGER.error("Impossible to depose golds in bank, character {} as a missing item or insufficient quantity", name);
            throw new RuntimeException(e);
        } catch (CharacterTransitionAlreadyInProgressException e) {
            LOGGER.error("Impossible to depose golds in bank, an transaction is already in progress with this gold in bank");
            throw new RuntimeException(e);
        } catch (HttpClientErrorException e) {
            LOGGER.error("{} : Error during deposit golds in bank : {}", name, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }


    /**
     * Initiates a fight for the specified character.
     * <p>
     * This method logs the request to initiate a fight for the character, validates the character's name,
     * and attempts to start the fight using {@code myCharactersService}.
     * If the character is in cooldown, it waits for the cooldown period to expire and retries the fight operation.
     * If the character is already engaged in an action, it logs the error and throws a {@code RuntimeException}.
     * If an HTTP client error occurs during the process, it logs the error and throws a {@code RuntimeException}.
     * </p>
     *
     * @param name the name of the character initiating the fight. Must not be {@code null} or empty.
     * @return a {@link CharacterFightDataSchema} object containing the result of the fight operation.
     * @throws RuntimeException     if an error occurs during the fight operation, such as:
     *                              - The character is already engaged in another action ({@link CharacterActionAlreadyInProgressException}).
     *                              - An HTTP client error occurs ({@link HttpClientErrorException}).
     * @throws InterruptedException if the thread is interrupted while waiting for the cooldown period.
     */
    public CharacterFightDataSchema fight(String name) throws InterruptedException {
        LOGGER.info("{} fight !", name);
        try {
            validateCharacterName(name);

            return myCharactersService.fight(name);
        } catch (CharacterInCooldownException e) {
            LOGGER.warn("Impossible to fight, character {} was in cooldown.", name);
            waitCooldown(name);
            // Try to do it again
            return fight(name);
        } catch (CharacterActionAlreadyInProgressException e) {
            LOGGER.error("Impossible to fight, character {} have already an action in progress.", name);
            throw new RuntimeException(e);
        } catch (HttpClientErrorException e) {
            LOGGER.error("{} : Error during fight : {}", name, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Moves the specified character to the given map coordinates if necessary, and then assigns a new task to the character.
     * <p>
     * This method first logs the action of moving the character to the target map coordinates and taking a new task.
     * It checks if movement is required by comparing the character's current position with the target map coordinates.
     * If movement is needed, it performs the move operation and waits for any cooldown period to expire before proceeding.
     * After ensuring the character is at the desired location, it assigns a new task to the character.
     * </p>
     *
     * @param character the {@link Character} to move and assign a new task. Must not be {@code null}.
     * @param map       the {@link Map} object representing the destination coordinates. Must not be {@code null}.
     * @return a {@link TaskDataSchema} object containing the result of the task assignment operation.
     * @throws InterruptedException if the thread is interrupted while waiting for the cooldown.
     */
    public TaskDataSchema moveAndTakeNewTask(Character character, Map map) throws InterruptedException {
        LOGGER.info("{} moving to 'x,y' : {},{} and take a new task", character.getName(), map.getX(), map.getY());

        if (validateIsRequireMovement(character, map)) {
            CharacterMovementDataSchema result = moveToMap(character.getName(), map);
            waitCooldown(result.getCooldown().getRemainingSeconds());
        }
        return acceptNewTask(character.getName());
    }


    public BankItemSchema moveAndWithdrawBankItem(Character character, Map map, ItemSimple item) throws InterruptedException {
        LOGGER.info("{} moving to 'x,y' : {},{} and withdraw item {} in {} quantity from bank",
                character.getName(), map.getX(), map.getY(), item.getCode(), item.getQuantity());

        if (validateIsRequireMovement(character, map)) {
            CharacterMovementDataSchema result = moveToMap(character.getName(), map);
            waitCooldown(result.getCooldown().getRemainingSeconds());
        }
        return withdrawItemFromBank(character.getName(), item);
    }

    public SkillDataSchema moveAndCraft(Character character, Map map, ItemSimple item) throws InterruptedException {
        LOGGER.info("{} moving to 'x,y' : {},{} and craft item {}", character.getName(), map.getX(), map.getY(), item.getCode());

        if (validateIsRequireMovement(character, map)) {
            CharacterMovementDataSchema result = moveToMap(character.getName(), map);
            waitCooldown(result.getCooldown().getRemainingSeconds());
        }
        return craft(character.getName(), item);
    }


    public SkillDataSchema moveAndGathering(Character character, Map map) throws InterruptedException {
        LOGGER.info("{} moving to 'x,y' : {},{} and gather", character.getName(), map.getX(), map.getY());

        if (validateIsRequireMovement(character, map)) {
            CharacterMovementDataSchema result = moveToMap(character.getName(), map);
            waitCooldown(result.getCooldown().getRemainingSeconds());
        }
        return gathering(character.getName());
    }


    /**
     * Completes a task for the specified character and returns the associated reward.
     * <p>
     * This method logs the action of completing a task for the character, validates the character's name,
     * and attempts to complete the task using {@code myCharactersService}.
     * If the character is in cooldown, it waits for the cooldown period to expire and retries the completion.
     * If the character is already engaged in another action, it logs the error and throws a {@code RuntimeException}.
     * If an HTTP client error occurs during the process, it logs the error and throws a {@code RuntimeException}.
     * </p>
     *
     * @param name the name of the character completing the task. Must not be {@code null} or empty.
     * @return a {@link TaskRewardDataSchema} object containing the reward data for the completed task.
     * @throws RuntimeException     if an error occurs during task completion, such as:
     *                              - The character is already engaged in another action ({@link CharacterActionAlreadyInProgressException}).
     *                              - An HTTP client error occurs ({@link HttpClientErrorException}).
     * @throws InterruptedException if the thread is interrupted while waiting for the cooldown.
     */
    public TaskRewardDataSchema completeTask(String name) throws InterruptedException {
        LOGGER.info("{} complete a task ! ", name);
        try {
            validateCharacterName(name);

            return myCharactersService.completeTask(name);
        } catch (CharacterInCooldownException e) {
            LOGGER.warn("Impossible to complete task, character {} was in cooldown.", name);
            waitCooldown(name);
            // Try to do it again
            return completeTask(name);
        } catch (CharacterActionAlreadyInProgressException e) {
            LOGGER.error("Impossible to complete task, character {} have already an action in progress.", name);
            throw new RuntimeException(e);
        } catch (HttpClientErrorException e) {
            LOGGER.error("{} : Error during completing task : {}", name, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Tente de rassembler une ressource pour un personnage donné.
     * <p>
     * Cette méthode tente d'effectuer une action de récolte pour le personnage spécifié par son nom.
     * En cas de succès, elle retourne un objet `SkillDataSchema` représentant les données de la compétence
     * associée à la récolte. En cas d'échec dû à un cooldown ou à une action déjà en cours, la méthode gère
     * ces exceptions et réessaie la récolte si nécessaire.
     * </p>
     *
     * @param name Le nom du personnage qui tente de rassembler une ressource.
     * @return Un objet `SkillDataSchema` contenant les données de la compétence liée à la récolte.
     * @throws InterruptedException Si le thread est interrompu pendant l'attente du cooldown.
     * @throws RuntimeException     Si une erreur survient lors de l'action de récolte, notamment
     *                              si le personnage a déjà une action en cours ou en cas d'erreur HTTP.
     */
    public SkillDataSchema gathering(String name) throws InterruptedException {
        LOGGER.info("{} gather resource.", name);
        try {
            validateCharacterName(name);

            return myCharactersService.gathering(name);
        } catch (CharacterInCooldownException e) {
            LOGGER.warn("Impossible to gather the resource, character {} was in cooldown.", name);
            waitCooldown(name);
            // Try to do it again
            return gathering(name);
        } catch (CharacterActionAlreadyInProgressException e) {
            LOGGER.error("Impossible to gather the resource, character {} have already an action in progress.", name);
            throw new RuntimeException(e);
        } catch (HttpClientErrorException e) {
            LOGGER.error("{} : Error during gathering resource : {}", name, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Équipe un item dans un slot spécifique pour un personnage donné.
     * <p>
     * Cette méthode tente d'équiper un item pour le personnage spécifié par son nom dans un slot particulier.
     * Si le personnage est en cooldown ou si une autre action est déjà en cours, la méthode gère ces exceptions
     * et réessaie l'équipement si nécessaire.
     * </p>
     *
     * @param name Le nom du personnage qui tente d'équiper l'item.
     * @param item L'objet `Item` à équiper.
     * @param slot Le slot où l'item doit être équipé.
     * @return Un objet `EquipRequestSchema` contenant les détails de l'action d'équipement.
     * @throws InterruptedException Si le thread est interrompu pendant l'attente du cooldown.
     * @throws RuntimeException     Si une erreur survient lors de l'équipement de l'item, notamment
     *                              si le personnage a déjà une action en cours ou en cas d'erreur HTTP.
     */
    public EquipRequestSchema equipItem(String name, Item item, Slot slot) throws InterruptedException {
        LOGGER.info("{} equip  items {} in the slot {}.", name, item.getName(), slot);
        try {
            validateCharacterName(name);
            validateItemCode(item.getCode());


            return myCharactersService.equipItem(name, item.getCode(), slot.getValue());
        } catch (CharacterInCooldownException e) {
            LOGGER.warn("Impossible to equip the item, character {} was in cooldown.", name);
            waitCooldown(name);
            // Try to do it again
            return equipItem(name, item, slot);
        } catch (CharacterActionAlreadyInProgressException e) {
            LOGGER.error("Impossible to equip the item, character {} have already an action in progress.", name);
            throw new RuntimeException(e);
        } catch (HttpClientErrorException e) {
            LOGGER.error("{} : Error during equipment of an item : {}", name, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Retire un item d'un slot spécifique pour un personnage donné.
     * <p>
     * Cette méthode tente de retirer un item équipé par un personnage spécifié dans un slot particulier.
     * Si le personnage est en cooldown ou si une autre action est déjà en cours, la méthode gère ces exceptions
     * et réessaie l'action si nécessaire.
     * </p>
     *
     * @param name Le nom du personnage qui tente de retirer l'item.
     * @param slot Le slot d'où l'item doit être retiré.
     * @return Un objet `EquipRequestSchema` contenant les détails de l'action de déséquipement.
     * @throws InterruptedException Si le thread est interrompu pendant l'attente du cooldown.
     * @throws RuntimeException     Si une erreur survient lors du retrait de l'item, notamment
     *                              si le personnage a déjà une action en cours ou en cas d'erreur HTTP.
     */
    public EquipRequestSchema unequipItem(String name, Slot slot) throws InterruptedException {
        LOGGER.info("{} unequip items in the slot {}.", name, slot);
        try {
            validateCharacterName(name);


            return myCharactersService.unequipItem(name, slot.getValue());
        } catch (CharacterInCooldownException e) {
            LOGGER.warn("Impossible to unequip the item, character {} was in cooldown.", name);
            waitCooldown(name);
            // Try to do it again
            return unequipItem(name, slot);
        } catch (CharacterActionAlreadyInProgressException e) {
            LOGGER.error("Impossible to unequip the item, character {} have already an action in progress.", name);
            throw new RuntimeException(e);
        } catch (HttpClientErrorException e) {
            LOGGER.error("{} : Error during unequipment of an item : {}", name, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Tente de fabriquer un item pour un personnage donné.
     * <p>
     * Cette méthode tente de fabriquer un item spécifique en une quantité définie pour un personnage spécifié par son nom.
     * Elle valide les paramètres tels que le nom du personnage, la quantité d'items et le code de l'item avant de procéder à la fabrication.
     * Si le personnage est en cooldown ou si une autre action est déjà en cours, la méthode gère ces exceptions et réessaie la fabrication si nécessaire.
     * </p>
     *
     * @param name       Le nom du personnage qui tente de fabriquer l'item.
     * @param itemSimple L'objet `ItemSimple` contenant le code de l'item et la quantité à fabriquer.
     * @return Un objet `SkillDataSchema` contenant les détails de la compétence utilisée pour fabriquer l'item.
     * @throws InterruptedException Si le thread est interrompu pendant l'attente du cooldown.
     * @throws RuntimeException     Si une erreur survient lors de la fabrication, notamment si le personnage a déjà une action en cours ou en cas d'erreur HTTP.
     */
    public SkillDataSchema craft(String name, ItemSimple itemSimple) throws InterruptedException {
        LOGGER.info("{} try to craft item {} in {} quantity.", name, itemSimple.getCode(), itemSimple.getQuantity());

        try {
            validateCharacterName(name);
            validateQuantity(itemSimple.getQuantity());
            validateItemCode(itemSimple.getCode());

            return myCharactersService.crafting(name, itemSimple.getCode(), itemSimple.getQuantity());
        } catch (CharacterInCooldownException e) {
            LOGGER.warn("Impossible to craft the item, character {} was in cooldown.", name);
            waitCooldown(name);
            // Try to do it again
            return craft(name, itemSimple);
        } catch (CharacterActionAlreadyInProgressException e) {
            LOGGER.error("Impossible to craft the item, character {} have already an action in progress.", name);
            throw new RuntimeException(e);
        } catch (HttpClientErrorException e) {
            LOGGER.error("{} : Error during craft of an item : {}", name, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Recycle un item pour un personnage donné.
     * <p>
     * Cette méthode tente de recycler un item spécifique en une quantité définie pour un personnage spécifié par son nom.
     * Elle valide les paramètres tels que le nom du personnage, la quantité de l'item et le code de l'item avant de procéder au recyclage.
     * Si le personnage est en cooldown ou si une autre action est déjà en cours, la méthode gère ces exceptions et réessaie le recyclage si nécessaire.
     * </p>
     *
     * @param name       Le nom du personnage qui tente de recycler l'item.
     * @param itemSimple L'objet `ItemSimple` contenant le code de l'item et la quantité à recycler.
     * @return Un objet `RecyclingDataSchema` contenant les détails de l'action de recyclage.
     * @throws InterruptedException Si le thread est interrompu pendant l'attente du cooldown.
     * @throws RuntimeException     Si une erreur survient lors du recyclage, notamment en cas de cooldown du personnage,
     *                              d'action déjà en cours, ou d'erreur HTTP.
     */
    public RecyclingDataSchema recycle(String name, ItemSimple itemSimple) throws InterruptedException {
        LOGGER.info("{} recycle item {} in {} quantity.", name, itemSimple.getCode(), itemSimple.getQuantity());

        try {
            validateCharacterName(name);
            validateQuantity(itemSimple.getQuantity());
            validateItemCode(itemSimple.getCode());

            return myCharactersService.recycling(name, itemSimple.getCode(), itemSimple.getQuantity());
        } catch (CharacterInCooldownException e) {
            LOGGER.warn("Impossible to recycle the item, character {} was in cooldown.", name);
            waitCooldown(name);
            // Try to do it again
            return recycle(name, itemSimple);
        } catch (CharacterActionAlreadyInProgressException e) {
            LOGGER.error("Impossible to recycle the item, character {} have already an action in progress.", name);
            throw new RuntimeException(e);
        } catch (HttpClientErrorException e) {
            LOGGER.error("{} : Error during recycling of an item : {}", name, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }


    /**
     * Retire un item d'une banque pour un personnage donné.
     * <p>
     * Cette méthode permet à un personnage de retirer une certaine quantité d'un item spécifique depuis la banque.
     * Avant de procéder au retrait, la méthode valide le nom du personnage, la quantité de l'item et le code de l'item.
     * Si le personnage est en cooldown ou si une autre action est déjà en cours, la méthode gère ces exceptions et réessaie le retrait si nécessaire.
     * </p>
     *
     * @param name       Le nom du personnage qui tente de retirer l'item de la banque.
     * @param itemSimple L'objet `ItemSimple` contenant le code de l'item et la quantité à retirer.
     * @return Un objet `BankItemSchema` contenant les détails du retrait de l'item de la banque.
     * @throws InterruptedException Si le thread est interrompu pendant l'attente du cooldown.
     * @throws RuntimeException     Si une erreur survient lors du retrait de l'item, notamment en cas de cooldown du personnage,
     *                              d'action déjà en cours, ou d'erreur HTTP.
     */
    public BankItemSchema withdrawItemFromBank(String name, ItemSimple itemSimple) throws InterruptedException {
        LOGGER.info("{} withdraw item {} in {} quantity from the bank.", name, itemSimple.getCode(), itemSimple.getQuantity());

        try {
            validateCharacterName(name);
            validateQuantity(itemSimple.getQuantity());
            validateItemCode(itemSimple.getCode());

            return myCharactersService.withdrawBank(name, itemSimple.getCode(), itemSimple.getQuantity());
        } catch (CharacterInCooldownException e) {
            LOGGER.warn("Impossible to withdraw the item from the bank, character {} was in cooldown.", name);
            waitCooldown(name);
            // Try to do it again
            return withdrawItemFromBank(name, itemSimple);
        } catch (CharacterActionAlreadyInProgressException e) {
            LOGGER.error("Impossible to withdraw the item from the bank, character {} have already an action in progress.", name);
            throw new RuntimeException(e);
        } catch (HttpClientErrorException e) {
            LOGGER.error("{} : Error during withdraw of an item from the bank : {}", name, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Retire une quantité spécifiée d'or de la banque pour un personnage donné.
     * <p>
     * Cette méthode permet à un personnage de retirer une certaine quantité d'or depuis la banque. Elle valide les paramètres
     * tels que le nom du personnage et la quantité d'or avant de procéder au retrait. Si le personnage est en cooldown ou si
     * une autre action est déjà en cours, la méthode gère ces exceptions et réessaie le retrait si nécessaire.
     * </p>
     *
     * @param name     Le nom du personnage qui tente de retirer de l'or de la banque.
     * @param quantity La quantité d'or à retirer de la banque.
     * @return Un objet `GoldTransactionSchema` contenant les détails de la transaction de retrait d'or.
     * @throws InterruptedException Si le thread est interrompu pendant l'attente du cooldown.
     * @throws RuntimeException     Si une erreur survient lors du retrait, notamment en cas de cooldown du personnage,
     *                              d'action déjà en cours, ou d'erreur HTTP.
     */
    public GoldTransactionSchema withdrawGoldFromBank(String name, int quantity) throws InterruptedException {
        LOGGER.info("{} withdraw golds in {} quantity from the bank.", name, quantity);

        try {
            validateCharacterName(name);
            validateQuantity(quantity);

            return myCharactersService.withdrawBankGold(name, quantity);
        } catch (CharacterInCooldownException e) {
            LOGGER.warn("Impossible to withdraw golds from the bank, character {} was in cooldown.", name);
            waitCooldown(name);
            // Try to do it again
            return withdrawGoldFromBank(name, quantity);
        } catch (CharacterActionAlreadyInProgressException e) {
            LOGGER.error("Impossible to withdraw golds from the bank, character {} have already an action in progress.", name);
            throw new RuntimeException(e);
        } catch (HttpClientErrorException e) {
            LOGGER.error("{} : Error during withdraw of golds from the bank : {}", name, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Achète un item à la Grand Exchange pour un personnage donné.
     * <p>
     * Cette méthode permet à un personnage d'acheter un item spécifique à la Grand Exchange pour un prix donné. Elle valide les paramètres
     * tels que le nom du personnage, le code de l'item, la quantité d'item à acheter, et le prix avant de procéder à l'achat. Si le personnage
     * est en cooldown ou si une autre action est déjà en cours, la méthode gère ces exceptions et réessaie l'achat si nécessaire.
     * </p>
     *
     * @param name  Le nom du personnage qui tente d'acheter l'item à la Grand Exchange.
     * @param item  L'objet `ItemSimple` contenant le code de l'item et la quantité à acheter.
     * @param price Le prix auquel l'item est acheté à la Grand Exchange.
     * @return Un objet `GeTransactionListSchema` contenant les détails de la transaction d'achat à la Grand Exchange.
     * @throws InterruptedException Si le thread est interrompu pendant l'attente du cooldown.
     * @throws RuntimeException     Si une erreur survient lors de l'achat, notamment en cas de cooldown du personnage,
     *                              d'action déjà en cours, ou d'erreur HTTP.
     */
    public GeTransactionListSchema buyGrandExchangeItem(String name, ItemSimple item, int price) throws InterruptedException {
        LOGGER.info("{} buy item {} in {} quantity from the Grand Exchange.", name, item.getCode(), price);

        try {
            validateCharacterName(name);
            validateItemCode(item.getCode());
            validateQuantityForGrandExchangeTransaction(item.getQuantity());
            validatePrice(price);

            return myCharactersService.geBuyItem(name, item.getCode(), item.getQuantity(), price);
        } catch (CharacterInCooldownException e) {
            LOGGER.warn("Impossible to buy item {} from the Grand Exchange, character {} was in cooldown.", name, item.getCode());
            waitCooldown(name);
            // Try to do it again
            return buyGrandExchangeItem(name, item, price);
        } catch (CharacterActionAlreadyInProgressException e) {
            LOGGER.error("Impossible to buy item {} from the Grand Exchange, character {} have already an action in progress.", name, item.getCode());
            throw new RuntimeException(e);
        } catch (HttpClientErrorException e) {
            LOGGER.error("{} : Error during buying item from the Grand Echange : {}", name, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Vends un item à la Grand Exchange pour un personnage donné.
     * <p>
     * Cette méthode permet à un personnage de vendre un item spécifique à la Grand Exchange pour un prix donné. Elle valide les paramètres
     * tels que le nom du personnage, le code de l'item, la quantité d'item à vendre, et le prix avant de procéder à la vente. Si le personnage
     * est en cooldown ou si une autre action est déjà en cours, la méthode gère ces exceptions et réessaie la vente si nécessaire.
     * </p>
     *
     * @param name  Le nom du personnage qui tente de vendre l'item à la Grand Exchange.
     * @param item  L'objet `ItemSimple` contenant le code de l'item et la quantité à vendre.
     * @param price Le prix auquel l'item est vendu à la Grand Exchange.
     * @return Un objet `GeTransactionListSchema` contenant les détails de la transaction de vente à la Grand Exchange.
     * @throws InterruptedException Si le thread est interrompu pendant l'attente du cooldown.
     * @throws RuntimeException     Si une erreur survient lors de la vente, notamment en cas de cooldown du personnage,
     *                              d'action déjà en cours, ou d'erreur HTTP.
     */
    public GeTransactionListSchema sellGrandExchangeItem(String name, ItemSimple item, int price) throws InterruptedException {
        LOGGER.info("{} sell item {} in {} quantity from the Grand Exchange.", name, item.getCode(), price);

        try {
            validateCharacterName(name);
            validateItemCode(item.getCode());
            validateQuantityForGrandExchangeTransaction(item.getQuantity());
            validatePrice(price);

            return myCharactersService.geSellItem(name, item.getCode(), item.getQuantity(), price);
        } catch (CharacterInCooldownException e) {
            LOGGER.warn("Impossible to sell item {} from the Grand Exchange, character {} was in cooldown.", name, item.getCode());
            waitCooldown(name);
            // Try to do it again
            return sellGrandExchangeItem(name, item, price);
        } catch (CharacterActionAlreadyInProgressException e) {
            LOGGER.error("Impossible to sell item {} from the Grand Exchange, character {} have already an action in progress.", name, item.getCode());
            throw new RuntimeException(e);
        } catch (HttpClientErrorException e) {
            LOGGER.error("{} : Error during selling item from the Grand Echange : {}", name, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Supprime un item pour un personnage donné.
     * <p>
     * Cette méthode permet de supprimer une certaine quantité d'un item spécifique pour un personnage. Elle valide les paramètres
     * tels que le nom du personnage, le code de l'item, et la quantité à supprimer avant d'effectuer l'opération. En cas de cooldown
     * ou si une autre action est déjà en cours pour le personnage, la méthode gère ces exceptions et réessaie la suppression si nécessaire.
     * </p>
     *
     * @param name Le nom du personnage qui tente de supprimer l'item.
     * @param item L'objet `ItemSimple` contenant le code de l'item et la quantité à supprimer.
     * @return Un objet `DeleteItemSchema` contenant les détails de la suppression de l'item.
     * @throws InterruptedException Si le thread est interrompu pendant l'attente du cooldown.
     * @throws RuntimeException Si une erreur survient lors de la suppression, notamment en cas de cooldown du personnage,
     *                          d'action déjà en cours, ou d'erreur HTTP.
     */
    public DeleteItemSchema deleteItem(String name, ItemSimple item) throws InterruptedException {
        LOGGER.info("{} delete item {} in {} quantity.", name, item.getCode(), item.getQuantity());

        try {
            validateCharacterName(name);
            validateItemCode(item.getCode());
            validateQuantity(item.getQuantity());

            return myCharactersService.deleteItem(name, item.getCode(), item.getQuantity());
        } catch (CharacterInCooldownException e) {
            LOGGER.warn("Impossible to delete item {}, character {} was in cooldown.", name, item.getCode());
            waitCooldown(name);
            // Try to do it again
            return deleteItem(name, item);
        } catch (CharacterActionAlreadyInProgressException e) {
            LOGGER.error("Impossible to delete item {}, character {} have already an action in progress.", name, item.getCode());
            throw new RuntimeException(e);
        } catch (HttpClientErrorException e) {
            LOGGER.error("{} : Error during deleting item : {}", name, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
    /**
     * Attend la fin du cooldown pour un personnage donné avant de continuer.
     * <p>
     * Cette méthode vérifie si un personnage spécifié est en cooldown et, le cas échéant, met le thread en pause
     * pour la durée restante du cooldown. La durée du cooldown est calculée en secondes, et si elle est positive,
     * le thread est suspendu pour cette durée.
     * </p>
     *
     * @param name Le nom du personnage pour lequel vérifier et attendre le cooldown.
     * @throws InterruptedException Si le thread est interrompu pendant l'attente du cooldown.
     */
    private void waitCooldown(String name) throws InterruptedException {
        Character characterUpdated = characterService.getCharacter(name);

        long cooldown = DateUtils.zonedDateTimeDifference(characterUpdated.getCooldownExpiration(), ZonedDateTime.now(), ChronoUnit.SECONDS);
        waitCooldown(cooldown);
    }

    /**
     * Met en pause l'exécution du thread pour la durée spécifiée en secondes.
     * <p>
     * Cette méthode suspend l'exécution du programme en mettant le thread en pause pour un nombre de secondes donné.
     * Si le nombre de secondes est supérieur à zéro, le thread est suspendu pour cette durée. Si la valeur est nulle
     * ou négative, la méthode n'a aucun effet.
     * </p>
     *
     * @param seconds La durée en secondes pendant laquelle le thread doit être suspendu.
     * @throws InterruptedException Si le thread est interrompu pendant l'attente.
     */
    private void waitCooldown(long seconds) throws InterruptedException {
        if (seconds > 0) {
            Thread.sleep(seconds * 1000);
        }
    }
}
