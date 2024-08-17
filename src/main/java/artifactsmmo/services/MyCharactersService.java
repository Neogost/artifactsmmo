package artifactsmmo.services;

import artifactsmmo.enums.ErrorCode;
import artifactsmmo.exception.CharacterActionAlreadyInProgressException;
import artifactsmmo.exception.CharacterInCooldownException;
import artifactsmmo.exception.CharacterInventoryMissingItemException;
import artifactsmmo.exception.CharacterTransitionAlreadyInProgressException;
import artifactsmmo.models.entity.Character;
import artifactsmmo.models.schema.*;
import artifactsmmo.models.response.*;
import artifactsmmo.models.entity.MyCharactersResponse;
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

import java.util.*;


@Service
public class MyCharactersService {


    @Autowired
    private RestTemplate restTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(MyCharactersService.class);
    private final RestUtils restUtils = new RestUtils();

    /**
     * Récupère la liste des personnages associés au compte utilisateur en envoyant une requête GET à un service REST.
     * <p>
     * Cette méthode construit l'URL pour la requête à l'aide d'un utilitaire de configuration,
     * envoie une requête GET pour obtenir les personnages, et retourne une liste d'objets {@link Character}.
     * La méthode utilise des entêtes HTTP pour authentifier la requête. En cas de succès, elle renvoie la liste des personnages
     * extraits de la réponse. En cas d'échec ou d'erreur HTTP, une exception est gérée et `null` est retourné.
     * </p>
     *
     * @return une liste d'objets {@link Character} représentant les personnages associés au compte utilisateur,
     * ou `null` en cas d'erreur ou si la liste des personnages ne peut être récupérée.
     * @throws HttpClientErrorException si une erreur HTTP se produit lors de l'appel au service REST.
     */
    public List<Character> getMyCharacters() {
        LOGGER.debug("Entry in getMyCharacters");
        try {
            String url = restUtils.getMyCharactersUrl();
            HttpEntity<String> entity = restUtils.entityHeader();
            ResponseEntity<MyCharactersResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, MyCharactersResponse.class);

            LOGGER.info("GET {} status : {}", restUtils.getMyCharactersUrl(), response.getStatusCode());

            return Objects.requireNonNull(response.getBody()).getCharacters();
        } catch (HttpClientErrorException e) {
            LOGGER.error("Erreur lors de l'appel au service REST : {}", e.getMessage(), e);
            handleException(e, restUtils.getGeItemUrl());
            return null;
        }
    }

    /**
     * Déplace un personnage à une nouvelle position en envoyant une requête POST à un service REST.
     * <p>
     * Cette méthode envoie les coordonnées de déplacement (x, y) pour le personnage spécifié par son nom.
     * Elle construit le corps de la requête avec les coordonnées et envoie une requête POST au service REST pour
     * mettre à jour la position du personnage. La réponse est ensuite analysée pour extraire les données de mouvement
     * du personnage, qui sont retournées. En cas d'erreur HTTP, une exception est gérée et `null` est retourné.
     * </p>
     *
     * @param name le nom du personnage à déplacer
     * @param x    la nouvelle coordonnée X pour le personnage
     * @param y    la nouvelle coordonnée Y pour le personnage
     * @return un objet {@link CharacterMovementDataSchema} contenant les données de mouvement du personnage,
     * ou `null` en cas d'échec ou si les données de mouvement ne peuvent être récupérées.
     * @throws HttpClientErrorException si une erreur HTTP se produit lors de l'appel au service REST.
     */
    public CharacterMovementDataSchema move(String name, int x, int y) {
        LOGGER.debug("Entry in move with parameters : name={}, x={}, y={}", name, x, y);

        try {
            // Create the request body as a MultiValueMap
            Map<String, Integer> body = new HashMap<>();
            body.put("x", x);
            body.put("y", y);

            String url = String.format(restUtils.getMyCharacterMoveUrl(), name);
            HttpEntity<Object> entity = restUtils.entityHeader(body);

            ResponseEntity<MyCharacterMoveResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, MyCharacterMoveResponse.class);
            LOGGER.info("GET {} status : {}", url, response.getStatusCode());

            return Objects.requireNonNull(response.getBody()).getCharacterMovementDataSchema();
        } catch (HttpClientErrorException e) {
            LOGGER.error("Erreur lors de l'appel au service REST : {}", e.getMessage(), e);
            handleException(e, restUtils.getMyCharacterMoveUrl());
            return null;
        }
    }

    /**
     * Supprime un ou plusieurs objets d'un personnage spécifié en envoyant une requête POST au service REST.
     *
     * <p>Cette méthode prend en entrée le nom du personnage, le code de l'objet à supprimer, et la quantité
     * à supprimer. Elle construit un corps de requête contenant le code de l'objet et la quantité,
     * puis envoie une requête POST à l'URL de suppression d'objet du personnage.</p>
     *
     * <p>En cas de succès, la méthode retourne un objet {@link DeleteItemSchema} qui contient les détails
     * de la suppression de l'objet. Si une erreur HTTP est rencontrée, elle est capturée, loguée,
     * et une gestion spécifique des exceptions est déclenchée. En cas d'échec ou de réponse vide,
     * la méthode retourne {@code null}.</p>
     *
     * @param name     le nom du personnage dont l'objet doit être supprimé
     * @param code     le code de l'objet à supprimer
     * @param quantity la quantité de l'objet à supprimer
     * @return un objet {@link DeleteItemSchema} contenant les informations de suppression de l'objet, ou {@code null} si la suppression a échoué
     */
    public DeleteItemSchema deleteItem(String name, String code, int quantity) {
        LOGGER.debug("Entry in deleteItem with parameter : name={}, code={}, quantity={}", name, code, quantity);

        try {
            Map<String, Object> body = new HashMap<>();
            body.put("code", code);
            body.put("quantity", quantity);

            String url = String.format(restUtils.getMyCharacterDeleteItemUrl(), name);
            HttpEntity<Object> entity = restUtils.entityHeader(body);

            ResponseEntity<MyCharacterDeleteItemResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, MyCharacterDeleteItemResponse.class);
            LOGGER.info("POST {} : status : {}", url, response.getStatusCode());

            return Optional.ofNullable(response.getBody())
                    .map(MyCharacterDeleteItemResponse::getDeleteItemSchema)
                    .orElse(null);

        } catch (HttpClientErrorException e) {
            LOGGER.error("Erreur lors de l'appel au service REST : {}", e.getMessage(), e);
            handleException(e, restUtils.getMyCharacterDeleteItemUrl());
            return null;
        }
    }

    /**
     * Récupère l'ensemble des journaux de tous les personnages en paginant à travers les pages disponibles.
     * <p>
     * Cette méthode effectue une requête GET vers l'API pour obtenir les journaux de tous les personnages. Elle utilise la pagination pour récupérer les données, et continue de récupérer les pages suivantes jusqu'à ce que toutes les pages soient obtenues ou qu'une réponse non valide soit reçue.
     * </p>
     *
     * @param page Le numéro de la page à récupérer (commence à 1).
     * @param size Le nombre d'éléments par page.
     * @return Une liste de {@link LogSchema} représentant les journaux de tous les personnages. La liste sera vide si aucune donnée n'est trouvée ou en cas d'erreur.
     * @throws HttpClientErrorException Si une erreur HTTP est rencontrée lors de la communication avec le service REST.
     * @see LogSchema
     * @see MyCharactersLogsResponse
     */
    public List<LogSchema> getAllCharactersLog(int page, int size) {
        LOGGER.debug("Entry in getAllCharactersLog with parameter : page={}, size={}", page, size);

        List<LogSchema> logs = new ArrayList<>();
        try {
            HttpEntity<String> entity = restUtils.entityHeader();

            while (true) {
                // Create URL with params
                String url = UriComponentsBuilder.fromHttpUrl(restUtils.getMyCharactersLogsUrl())
                        .queryParamIfPresent("page", Optional.of(page))
                        .queryParamIfPresent("size", Optional.of(size))
                        .encode()
                        .toUriString();

                ResponseEntity<MyCharactersLogsResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, MyCharactersLogsResponse.class);
                LOGGER.info("GET {} : status : {}", url, response.getStatusCode());


                if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                    break; // Break if the response is not OK or body is null
                }

                MyCharactersLogsResponse myCharactersLogsResponse = response.getBody();
                logs.addAll(myCharactersLogsResponse.getLogs());

                // Verify if we continue to the next page
                if (myCharactersLogsResponse.getPage() >= myCharactersLogsResponse.getPages()) {
                    break; // Get out if we are on the last page
                }

                page++; // Increment page to the next iteration
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("Erreur lors de l'appel au service REST : {}", e.getMessage(), e);
            handleException(e, restUtils.getGeItemsUrl());
        }
        return logs;
    }

    /**
     * Accepte une nouvelle tâche pour un personnage spécifié en envoyant une requête POST au service REST.
     *
     * <p>Cette méthode prend en entrée le nom du personnage pour lequel une nouvelle tâche doit être acceptée.
     * Elle construit l'URL à partir du nom du personnage et envoie une requête POST pour accepter la nouvelle tâche.</p>
     *
     * <p>En cas de succès, la méthode retourne un objet {@link TaskDataSchema} contenant les informations de la nouvelle tâche acceptée.
     * Si une erreur HTTP est rencontrée, elle est capturée, loguée, et une gestion spécifique des exceptions est déclenchée.
     * En cas d'échec ou de réponse vide, la méthode retourne {@code null}.</p>
     *
     * @param name le nom du personnage pour lequel une nouvelle tâche doit être acceptée
     * @return un objet {@link TaskDataSchema} contenant les informations de la nouvelle tâche acceptée, ou {@code null} en cas d'échec
     */
    public TaskDataSchema acceptNewTask(String name) {
        LOGGER.debug("Entry in acceptNewTask with parameters : name={}",
                name);

        try {
            String url = String.format(restUtils.getMyCharacterAcceptNewTaskUrl(), name);
            HttpEntity<String> entity = restUtils.entityHeader();

            ResponseEntity<MyCharacterAcceptNewTaskResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, MyCharacterAcceptNewTaskResponse.class);
            LOGGER.info("POST {} status : {}", url, response.getStatusCode());

            return Objects.requireNonNull(response.getBody()).getTaskDataSchema();
        } catch (HttpClientErrorException e) {
            LOGGER.error("Erreur lors de l'appel au service REST : {}", e.getMessage(), e);
            handleException(e, restUtils.getMyCharacterAcceptNewTaskUrl());
            return null;
        }
    }

    /**
     * Lance un combat pour un personnage spécifié en envoyant une requête POST au service REST.
     *
     * <p>Cette méthode prend en entrée le nom du personnage qui doit engager un combat.
     * Elle construit l'URL correspondante à partir du nom du personnage et envoie une requête POST pour démarrer le combat.</p>
     *
     * <p>En cas de succès, la méthode retourne un objet {@link CharacterFightDataSchema} contenant les informations relatives au combat.
     * Si une erreur HTTP survient, elle est capturée, loguée, et une gestion spécifique des exceptions est exécutée.
     * En cas d'échec ou de réponse vide, la méthode retourne {@code null}.</p>
     *
     * @param name le nom du personnage qui doit engager un combat
     * @return un objet {@link CharacterFightDataSchema} contenant les informations relatives au combat, ou {@code null} en cas d'échec
     */
    public CharacterFightDataSchema fight(String name) {
        LOGGER.debug("Entry in fight with parameters : name={}",
                name);

        try {
            String url = String.format(restUtils.getMyCharacterFightUrl(), name);
            HttpEntity<String> entity = restUtils.entityHeader();

            ResponseEntity<MyCharacterFightResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, MyCharacterFightResponse.class);
            LOGGER.info("POST {} status : {}", url, response.getStatusCode());

            return Objects.requireNonNull(response.getBody()).getCharacterFightDataSchema();
        } catch (HttpClientErrorException e) {
            LOGGER.error("Erreur lors de l'appel au service REST : {}", e.getMessage(), e);
            handleException(e, restUtils.getMyCharacterFightUrl());
            return null;
        }
    }

    /**
     * Marque une tâche comme complétée pour un personnage spécifié en envoyant une requête POST au service REST.
     *
     * <p>Cette méthode prend en entrée le nom du personnage pour lequel la tâche doit être complétée.
     * Elle construit l'URL correspondante à partir du nom du personnage et envoie une requête POST pour signaler l'achèvement de la tâche.</p>
     *
     * <p>En cas de succès, la méthode retourne un objet {@link TaskRewardDataSchema} contenant les informations relatives aux récompenses obtenues
     * pour la tâche complétée. Si une erreur HTTP survient, elle est capturée, loguée, et une gestion spécifique des exceptions est exécutée.
     * En cas d'échec ou de réponse vide, la méthode retourne {@code null}.</p>
     *
     * @param name le nom du personnage pour lequel la tâche doit être complétée
     * @return un objet {@link TaskRewardDataSchema} contenant les informations relatives aux récompenses obtenues pour la tâche complétée, ou {@code null} en cas d'échec
     */
    public TaskRewardDataSchema completeTask(String name) {
        LOGGER.debug("Entry in completeTask with parameter : name={}", name);

        try {
            String url = String.format(restUtils.getMyCharacterCompleteTaskUrl(), name);
            HttpEntity<String> entity = restUtils.entityHeader();

            ResponseEntity<MyCharacterTaskRewardResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, MyCharacterTaskRewardResponse.class);
            LOGGER.info("POST {} status : {}", url, response.getStatusCode());

            return Objects.requireNonNull(response.getBody()).getTaskRewardDataSchema();
        } catch (HttpClientErrorException e) {
            LOGGER.error("Erreur lors de l'appel au service REST : {}", e.getMessage(), e);
            handleException(e, restUtils.getMyCharacterCompleteTaskUrl());
            return null;
        }
    }

    /**
     * Effectue une action de collecte pour un personnage spécifié en envoyant une requête POST au service REST.
     *
     * <p>Cette méthode prend en entrée le nom du personnage pour lequel l'action de collecte doit être réalisée.
     * Elle construit l'URL correspondante à partir du nom du personnage et envoie une requête POST pour effectuer l'action de collecte.</p>
     *
     * <p>En cas de succès, la méthode retourne un objet {@link SkillDataSchema} contenant les informations relatives aux compétences acquises ou mises à jour
     * lors de l'action de collecte. Si une erreur HTTP survient, elle est capturée, loguée, et une gestion spécifique des exceptions est exécutée.
     * En cas d'échec ou de réponse vide, la méthode retourne {@code null}.</p>
     *
     * @param name le nom du personnage pour lequel l'action de collecte doit être réalisée
     * @return un objet {@link SkillDataSchema} contenant les informations relatives aux compétences acquises ou mises à jour lors de l'action de collecte, ou {@code null} en cas d'échec
     */
    public SkillDataSchema gathering(String name) {
        LOGGER.debug("Entry in gathering with parameter : name={}", name);

        try {
            String url = String.format(restUtils.getMyCharacterGatheringUrl(), name);
            HttpEntity<String> entity = restUtils.entityHeader();

            ResponseEntity<MyCharacterGatheringResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, MyCharacterGatheringResponse.class);
            LOGGER.info("POST {} status : {}", url, response.getStatusCode());

            return Objects.requireNonNull(response.getBody()).getSkillDataSchema();
        } catch (HttpClientErrorException e) {
            LOGGER.error("Erreur lors de l'appel au service REST : {}", e.getMessage(), e);
            handleException(e, restUtils.getMyCharacterGatheringUrl());
            return null;
        }
    }

    /**
     * Effectue une action d'artisanat pour un personnage spécifié en envoyant une requête POST au service REST.
     *
     * <p>Cette méthode prend en entrée le nom du personnage, le code de l'objet à fabriquer, ainsi que la quantité de cet objet.
     * Elle construit l'URL correspondante à partir du nom du personnage et envoie une requête POST avec ces paramètres pour effectuer l'action d'artisanat.</p>
     *
     * <p>En cas de succès, la méthode retourne un objet {@link SkillDataSchema} contenant les informations relatives aux compétences acquises ou mises à jour
     * lors de l'action d'artisanat. Si une erreur HTTP survient, elle est capturée, loguée, et une gestion spécifique des exceptions est exécutée.
     * En cas d'échec ou de réponse vide, la méthode retourne {@code null}.</p>
     *
     * @param name     le nom du personnage pour lequel l'action d'artisanat doit être réalisée
     * @param code     le code de l'objet à fabriquer
     * @param quantity la quantité de l'objet à fabriquer
     * @return un objet {@link SkillDataSchema} contenant les informations relatives aux compétences acquises ou mises à jour lors de l'action d'artisanat, ou {@code null} en cas d'échec
     */
    public SkillDataSchema crafting(String name, String code, int quantity) {
        LOGGER.debug("Entry in crafting with parameter : name={}, code={}, quantity={}", name, code, quantity);

        try {
            Map<String, Object> body = new HashMap<>();
            body.put("code", code);
            body.put("quantity", quantity);
            String url = String.format(restUtils.getMyCharacterCraftingUrl(), name);


            HttpEntity<Object> entity = restUtils.entityHeader(body);
            ResponseEntity<MyCharacterCraftingResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, MyCharacterCraftingResponse.class);
            LOGGER.info("POST {} status : {}", url, response.getStatusCode());

            return response.getBody().getSkillDataSchema();
        } catch (HttpClientErrorException e) {
            LOGGER.error("Erreur lors de l'appel au service REST : {}", e.getMessage(), e);
            handleException(e, restUtils.getMyCharacterCraftingUrl());
            return null;
        }
    }

    /**
     * Dépose un objet spécifique dans la banque d'un personnage en envoyant une requête POST au service REST.
     *
     * <p>Cette méthode prend en entrée le nom du personnage, le code de l'objet à déposer, ainsi que la quantité de cet objet.
     * Elle construit l'URL correspondante à partir du nom du personnage et envoie une requête POST avec ces paramètres pour effectuer le dépôt dans la banque.</p>
     *
     * <p>En cas de succès, la méthode retourne un objet {@link BankItemSchema} contenant les informations relatives à l'objet déposé en banque.
     * Si une erreur HTTP survient, elle est capturée, loguée, et une gestion spécifique des exceptions est exécutée.
     * En cas d'échec ou de réponse vide, la méthode retourne {@code null}.</p>
     *
     * @param name     le nom du personnage pour lequel l'action de dépôt en banque doit être réalisée
     * @param itemCode le code de l'objet à déposer en banque
     * @param quantity la quantité de l'objet à déposer en banque
     * @return un objet {@link BankItemSchema} contenant les informations relatives à l'objet déposé en banque, ou {@code null} en cas d'échec
     */
    public BankItemSchema depositBank(String name, String itemCode, int quantity) {
        LOGGER.debug("Entry in depositeBank with parameter : name={}, item={}, quantity={}", name, itemCode, quantity);

        try {
            Map<String, Object> body = new HashMap<>();
            body.put("code", itemCode);
            body.put("quantity", quantity);

            String url = String.format(restUtils.getMyCharacterBankDepositUrl(), name);
            HttpEntity<Object> entity = restUtils.entityHeader(body);

            ResponseEntity<MyCharacterDepositBankResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, MyCharacterDepositBankResponse.class);
            LOGGER.info("POST {} status : {}", url, response.getStatusCode());

            return response.getBody().getBankItemSchema();
        } catch (HttpClientErrorException e) {
            LOGGER.error("Erreur lors de l'appel au service REST : {}", e.getMessage(), e);
            handleException(e, restUtils.getMyCharacterBankDepositUrl());
            return null;
        }

    }

    /**
     * Effectue l'échange d'une tâche pour un personnage spécifié par son nom.
     * <p>
     * Cette méthode envoie une requête POST à l'URL de l'échange de tâche du personnage
     * pour effectuer l'échange et recevoir une récompense de tâche.
     * </p>
     *
     * @param name Le nom du personnage pour lequel l'échange de tâche doit être effectué.
     * @return Un objet {@link TaskRewardDataSchema} contenant les données de la récompense de la tâche, ou {@code null} si la réponse est vide.
     * @throws HttpClientErrorException en cas d'erreur lors de l'appel au service REST.
     */
    public TaskRewardDataSchema taskExchange(String name) {
        LOGGER.debug("Entry in taskExchange with parameter : name={}", name);

        try {
            String url = String.format(restUtils.getMyCharacterExchangeTaskUrl(), name);
            HttpEntity<String> entity = restUtils.entityHeaderWithoutToken();

            ResponseEntity<MyCharacterTaskExchangeResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, MyCharacterTaskExchangeResponse.class);
            LOGGER.info("POST {} : status : {}", url, response.getStatusCode());

            return Optional.ofNullable(response.getBody())
                    .map(MyCharacterTaskExchangeResponse::getTaskRewardDataSchema)
                    .orElse(null);

        } catch (HttpClientErrorException e) {
            LOGGER.error("Erreur lors de l'appel au service REST : {}", e.getMessage(), e);
            handleException(e, restUtils.getMyCharacterExchangeTaskUrl());
            return null;
        }
    }

    /**
     * Executes a request to sell an item and retrieves the transaction list schema.
     *
     * <p>This method sends a POST request to the server to sell an item based on the provided parameters.
     * The request body contains the item code, quantity, and price, and is associated with a character name.
     * The response is expected to be of type {@link MyCharacterGeSellItemResponse}, from which the transaction
     * list schema is extracted and returned.</p>
     *
     * @param name     The name of the character performing the transaction.
     * @param code     The code of the item to be sold.
     * @param quantity The quantity of the item to be sold.
     * @param price    The price per unit of the item.
     * @return {@link GeTransactionListSchema} containing the transaction details, or {@code null} if the response is not available or an error occurs.
     * @throws IllegalArgumentException if any of the input parameters are invalid or if the URL format is incorrect.
     */
    public GeTransactionListSchema geSellItem(String name, String code, int quantity, int price) {
        LOGGER.debug("Entry in GeSellItem with parameter : name={}, code={}, quantity={}, price={}", name, code, quantity, price);

        try {
            Map<String, Object> body = new HashMap<>();
            body.put("code", code);
            body.put("quantity", quantity);
            body.put("price", price);

            String url = String.format(restUtils.getMyCharacterGeSellItemUrl(), name);
            HttpEntity<Object> entity = restUtils.entityHeader(body);

            ResponseEntity<MyCharacterGeSellItemResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, MyCharacterGeSellItemResponse.class);
            LOGGER.info("POST {} : status : {}", url, response.getStatusCode());

            return Optional.ofNullable(response.getBody())
                    .map(MyCharacterGeSellItemResponse::getGeTransactionListSchema)
                    .orElse(null);

        } catch (HttpClientErrorException e) {
            LOGGER.error("Erreur lors de l'appel au service REST : {}", e.getMessage(), e);
            handleException(e, restUtils.getMyCharacterGeSellItemUrl());
            return null;
        }
    }

    /**
     * Effectue l'achat d'un article dans le jeu et retourne les détails de la transaction.
     * <p>
     * Cette méthode envoie une requête POST à un service REST pour acheter un article en spécifiant
     * le nom du personnage, le code de l'article, la quantité désirée et le prix de l'article. Elle
     * récupère ensuite la réponse du service, qui contient les détails de la transaction effectuée.
     *
     * @param name     Le nom du personnage effectuant l'achat.
     * @param code     Le code de l'article à acheter.
     * @param quantity La quantité de l'article à acheter.
     * @param price    Le prix de l'article par unité.
     * @return Un objet {@link GeTransactionListSchema} contenant les détails de la transaction si la
     * requête est réussie, ou {@code null} en cas d'erreur.
     * @throws HttpClientErrorException Si une erreur HTTP se produit lors de l'appel au service REST.
     */
    public GeTransactionListSchema geBuyItem(String name, String code, int quantity, int price) {
        LOGGER.debug("Entry in GeBuyItem with parameter : name={}, code={}, quantity={}, price={}", name, code, quantity, price);

        try {
            Map<String, Object> body = new HashMap<>();
            body.put("code", code);
            body.put("quantity", quantity);
            body.put("price", price);

            String url = String.format(restUtils.getMyCharacterGeBuyItemUrl(), name);
            HttpEntity<Object> entity = restUtils.entityHeader(body);

            ResponseEntity<MyCharacterGeBuyItemResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, MyCharacterGeBuyItemResponse.class);
            LOGGER.info("POST {} : status : {}", url, response.getStatusCode());

            return Optional.ofNullable(response.getBody())
                    .map(MyCharacterGeBuyItemResponse::getGeTransactionListSchema)
                    .orElse(null);

        } catch (HttpClientErrorException e) {
            LOGGER.error("Erreur lors de l'appel au service REST : {}", e.getMessage(), e);
            handleException(e, restUtils.getMyCharacterGeBuyItemUrl());
            return null;
        }
    }

    /**
     * Effectue un retrait de la banque pour obtenir une certaine quantité d'or.
     *
     * <p>Cette méthode envoie une requête POST au service REST pour retirer de l'or de la banque. Le nom du personnage et
     * la quantité d'or à retirer sont spécifiés dans le corps de la requête. La méthode retourne un objet {@link GoldTransactionSchema}
     * représentant les détails de la transaction si la requête est réussie. En cas d'erreur lors de l'appel au service REST,
     * elle gère l'exception et retourne {@code null}.</p>
     *
     * @param name     Le nom du personnage à partir duquel l'or est retiré.
     * @param quantity La quantité d'or à retirer de la banque.
     * @return Un objet {@link GoldTransactionSchema} contenant les détails de la transaction, ou {@code null} si la réponse est vide ou en cas d'erreur.
     * @throws HttpClientErrorException Si une erreur HTTP se produit lors de l'appel au service REST.
     */
    public GoldTransactionSchema withdrawBankGold(String name, int quantity) {
        LOGGER.debug("Entry in withdrawBankGold with parameter : name={}, quantity={}", name, quantity);

        try {
            Map<String, Object> body = new HashMap<>();
            body.put("quantity", quantity);

            String url = String.format(restUtils.getMyCharacterWithdrawGoldBankUrl(), name);
            HttpEntity<Object> entity = restUtils.entityHeader(body);

            ResponseEntity<MyCharacterWithdrawGoldBankResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, MyCharacterWithdrawGoldBankResponse.class);
            LOGGER.info("POST {} : status : {}", url, response.getStatusCode());

            return Optional.ofNullable(response.getBody())
                    .map(MyCharacterWithdrawGoldBankResponse::getGoldTransactionSchema)
                    .orElse(null);
        } catch (HttpClientErrorException e) {
            LOGGER.error("Erreur lors de l'appel au service REST : {}", e.getMessage(), e);
            handleException(e, restUtils.getMyCharacterWithdrawGoldBankUrl());
            return null;
        }
    }

    /**
     * Withdraws an item from the bank for a specified character.
     *
     * <p>This method sends a POST request to withdraw a specific quantity of an item from the bank
     * for a given character. It constructs the request body with the item code and quantity to be
     * withdrawn, then makes a call to the REST service. The response, if successful, contains
     * details of the bank item schema.</p>
     *
     * @param name     The name of the character from whose bank the item will be withdrawn.
     * @param code     The code of the item to be withdrawn.
     * @param quantity The quantity of the item to be withdrawn.
     * @return A {@link BankItemSchema} object containing the details of the withdrawn item,
     * or {@code null} if the response is empty or an error occurs.
     * @throws HttpClientErrorException If an error occurs during the REST call, such as client-side
     *                                  issues (e.g., invalid parameters or unauthorized access).
     * @see MyCharacterWithdrawBankResponse
     * @see BankItemSchema
     */
    public BankItemSchema withdrawBank(String name, String code, int quantity) {
        LOGGER.debug("Entry in withdrawBank with parameter : name={}, code={}, quantity={}", name, code, quantity);

        try {
            Map<String, Object> body = new HashMap<>();
            body.put("code", code);
            body.put("quantity", quantity);

            String url = String.format(restUtils.getMyCharacterWithdrawBankUrl(), name);
            HttpEntity<Object> entity = restUtils.entityHeader(body);

            ResponseEntity<MyCharacterWithdrawBankResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, MyCharacterWithdrawBankResponse.class);
            LOGGER.info("POST {} : status : {}", url, response.getStatusCode());

            return Optional.ofNullable(response.getBody())
                    .map(MyCharacterWithdrawBankResponse::getBankItemSchema)
                    .orElse(null);
        } catch (HttpClientErrorException e) {
            LOGGER.error("Erreur lors de l'appel au service REST : {}", e.getMessage(), e);
            handleException(e, restUtils.getMyCharacterWithdrawBankUrl());
            return null;
        }
    }

    /**
     * Initiates a recycling operation for a specified item and retrieves the recycling data schema.
     * <p>
     * This method sends a POST request to the recycling endpoint with the provided item details.
     * The request body includes the item code and the quantity to be recycled. Upon receiving the response,
     * it extracts the {@link RecyclingDataSchema} from the response body.
     * </p>
     * <p>
     * In case of an HTTP error, the exception is logged and handled appropriately. If the response body
     * is null or if an exception occurs, the method returns {@code null}.
     * </p>
     *
     * @param name     the name of the character performing the recycling operation
     * @param code     the code of the item to be recycled
     * @param quantity the quantity of the item to be recycled
     * @return the {@link RecyclingDataSchema} containing the result of the recycling operation, or {@code null} if the response body is empty or an error occurs
     */
    public RecyclingDataSchema recycling(String name, String code, int quantity) {
        LOGGER.debug("Entry in recycling with parameter : name={}, code={}, quantity={}", name, code, quantity);

        try {
            Map<String, Object> body = new HashMap<>();
            body.put("code", code);
            body.put("quantity", quantity);

            String url = String.format(restUtils.getMyCharacterRecyclingUrl(), name);
            HttpEntity<Object> entity = restUtils.entityHeader(body);

            ResponseEntity<MyCharacterRecyclingResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, MyCharacterRecyclingResponse.class);
            LOGGER.info("POST {} : status : {}", url, response.getStatusCode());

            return Optional.ofNullable(response.getBody())
                    .map(MyCharacterRecyclingResponse::getRecyclingDataSchema)
                    .orElse(null);
        } catch (HttpClientErrorException e) {
            LOGGER.error("Erreur lors de l'appel au service REST : {}", e.getMessage(), e);
            handleException(e, restUtils.getMyCharacterRecyclingUrl());
            return null;
        }
    }

    /**
     * Deposits a specified quantity of gold into the bank for the character identified by name.
     *
     * <p>This method sends a POST request to the appropriate REST endpoint to deposit gold into the bank.
     * The response contains the details of the gold transaction, encapsulated in a {@link GoldTransactionSchema} object.
     * If the operation fails due to a client-side error, the exception is logged, and null is returned.
     *
     * @param name     the name of the character for whom the gold is to be deposited
     * @param quantity the amount of gold to deposit into the bank
     * @return a {@link GoldTransactionSchema} object containing details of the transaction,
     * or null if the operation failed
     * @see RestUtils#getMyCharacterDepositeGoldBankUrl()
     * @see MyCharacterDepositGoldBankResponse#getGoldTransactionSchema()
     */
    public GoldTransactionSchema depositBankGold(String name, int quantity) {
        LOGGER.debug("Entry in depositBankGold with parameter : name={}, quantity={}", name, quantity);

        try {
            Map<String, Object> body = new HashMap<>();
            body.put("quantity", quantity);

            String url = String.format(restUtils.getMyCharacterDepositeGoldBankUrl(), name);
            HttpEntity<Object> entity = restUtils.entityHeader(body);

            ResponseEntity<MyCharacterDepositGoldBankResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, MyCharacterDepositGoldBankResponse.class);
            LOGGER.info("POST {} : status : {}", url, response.getStatusCode());

            return Optional.ofNullable(response.getBody())
                    .map(MyCharacterDepositGoldBankResponse::getGoldTransactionSchema)
                    .orElse(null);
        } catch (HttpClientErrorException e) {
            LOGGER.error("Erreur lors de l'appel au service REST : {}", e.getMessage(), e);
            handleException(e, restUtils.getMyCharacterDepositeGoldBankUrl());
            return null;
        }
    }

    /**
     * Unequips an item from a specific slot for a given character.
     *
     * <p>This method sends a POST request to the specified REST endpoint to unequip an item
     * from the character's slot. If the unequipping operation is successful, it returns the
     * resulting {@link EquipRequestSchema} object. In case of an error, it handles the exception
     * and returns {@code null}.
     *
     * @param name The name of the character from whom the item should be unequipped.
     *             This parameter is used to format the request URL.
     * @param slot The slot from which the item should be unequipped, such as "weapon_slot"
     *             or "helmet_slot".
     * @return The {@link EquipRequestSchema} containing details of the unequipped item
     * if the operation is successful; {@code null} otherwise.
     * @throws HttpClientErrorException if there is an error during the HTTP request.
     * @see MyCharacterUnequipItemResponse#getEquipRequestSchema()
     */
    public EquipRequestSchema unequipItem(String name, String slot) {
        LOGGER.debug("Entry in unequipItem with parameter : name={}, slot={}", name, slot);

        try {
            Map<String, Object> body = new HashMap<>();
            body.put("slot", slot);

            String url = String.format(restUtils.getMyCharacterUnequipItemUrl(), name);
            HttpEntity<Object> entity = restUtils.entityHeader(body);

            ResponseEntity<MyCharacterUnequipItemResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, MyCharacterUnequipItemResponse.class);
            LOGGER.info("POST {} : status : {}", url, response.getStatusCode());

            return Optional.ofNullable(response.getBody())
                    .map(MyCharacterUnequipItemResponse::getEquipRequestSchema)
                    .orElse(null);
        } catch (HttpClientErrorException e) {
            LOGGER.error("Erreur lors de l'appel au service REST : {}", e.getMessage(), e);
            handleException(e, restUtils.getMyCharacterUnequipItemUrl());
            return null;
        }
    }

    /**
     * Equips an item to a specific slot for a given character.
     *
     * <p>This method sends a POST request to the specified REST endpoint to equip an item
     * to the character's slot. If the equipping operation is successful, it returns the
     * resulting {@link EquipRequestSchema} object. In case of an error, it handles the exception
     * and returns {@code null}.
     *
     * @param name The name of the character for whom the item should be equipped.
     *             This parameter is used to format the request URL.
     * @param code The code of the item that should be equipped.
     * @param slot The slot to which the item should be equipped, such as "weapon_slot"
     *             or "helmet_slot".
     * @return The {@link EquipRequestSchema} containing details of the equipped item
     *         if the operation is successful; {@code null} otherwise.
     *
     * @throws HttpClientErrorException if there is an error during the HTTP request.
     *
     * @see MyCharacterEquipItemResponse#getEquipRequestSchema()
     */
    public EquipRequestSchema equipItem(String name, String code, String slot) {
        LOGGER.debug("Entry in equipItem with parameter : name={}, code={}, slot={}", name, code, slot);

        try {
            Map<String, Object> body = new HashMap<>();
            body.put("code", code);
            body.put("slot", slot);

            String url = String.format(restUtils.getMyCharacterEquipItemUrl(), name);
            HttpEntity<Object> entity = restUtils.entityHeader(body);

            ResponseEntity<MyCharacterEquipItemResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, MyCharacterEquipItemResponse.class);
            LOGGER.info("POST {} : status : {}", url, response.getStatusCode());

            return Optional.ofNullable(response.getBody())
                    .map(MyCharacterEquipItemResponse::getEquipRequestSchema)
                    .orElse(null);
        } catch (HttpClientErrorException e) {
            LOGGER.error("Erreur lors de l'appel au service REST : {}", e.getMessage(), e);
            handleException(e, restUtils.getMyCharacterEquipItemUrl());
            return null;
        }
    }


    /**
     * Gère les exceptions {@link HttpClientErrorException} en fonction du code d'état HTTP.
     *
     * <p>Cette méthode traite les exceptions HTTP en lançant des exceptions spécifiques selon le code d'état de la réponse HTTP.
     * Si le code d'état est 404 (Not Found), une exception RuntimeException est lancée avec un message d'erreur spécifique.
     * Pour tous les autres codes d'état HTTP, une {@link HttpClientErrorException} est relancée avec le même code d'état et message.</p>
     *
     * @param e l'exception {@link HttpClientErrorException} à traiter.
     * @throws RuntimeException         si le code d'état HTTP est 404 (Not Found), avec un message d'erreur spécifique.
     * @throws HttpClientErrorException pour tout autre code d'état HTTP, relancée avec le code et le message d'origine.
     */
    private void handleException(HttpClientErrorException e, String url) {
        switch (e.getStatusCode().value()) {
            case 404:
                if (url.equals(restUtils.getMyCharactersUrl())) {
                    throw new RuntimeException(ErrorCode.CHARACTERS_NOT_FOUND.getReason(), e);
                } else if (url.equals(restUtils.getMyCharacterMoveUrl())) {
                    throw new RuntimeException(ErrorCode.MAP_NOT_FOUND.getReason(), e);
                } else if (url.equals(restUtils.getMyCharactersLogsUrl())) {
                    throw new RuntimeException(ErrorCode.LOGS_NOT_FOUND.getReason(), e);
                } else if (url.equals(restUtils.getMyCharacterCraftingUrl())) {
                    throw new RuntimeException(ErrorCode.CRAFT_NOT_FOUND.getReason(), e);
                } else if (url.equals(restUtils.getMyCharacterBankDepositUrl())
                        || url.equals(restUtils.getMyCharacterGeSellItemUrl())
                        || url.equals(restUtils.getMyCharacterWithdrawBankUrl())
                        || url.equals(restUtils.getMyCharacterRecyclingUrl())
                        || url.equals(restUtils.getMyCharacterUnequipItemUrl())
                        || url.equals(restUtils.getMyCharacterEquipItemUrl())) {
                    throw new RuntimeException(ErrorCode.ITEM_NOT_FOUND.getReason(), e);
                }
            case 460:
                throw new RuntimeException(ErrorCode.INSUFFICIENT_GOLD_BANK.getReason(), e);
            case 461:
                throw new CharacterTransitionAlreadyInProgressException(ErrorCode.TRANSACTION_ALREADY_IN_PROGRESS.getReason(), e);
            case 473:
                throw new RuntimeException(ErrorCode.ITEM_CANNOT_BE_RECYCLED.getReason(), e);
            case 478:
                throw new CharacterInventoryMissingItemException(ErrorCode.MISSING_ITEM.getReason(), e);
            case 480:
                throw new RuntimeException(ErrorCode.NO_STOCK_ITEM.getReason(), e);
            case 482:
                throw new RuntimeException(ErrorCode.NO_ITEM_AT_PRICE.getReason(), e);
            case 483:
                throw new RuntimeException(ErrorCode.TRANSACTION_ALREADY_IN_PROGRESS_ITEM.getReason(), e);
            case 485:
                throw new RuntimeException(ErrorCode.ITEM_ALREADY_EQUIPPED.getReason(), e);
            case 486:
                throw new CharacterActionAlreadyInProgressException(ErrorCode.ACTION_ALREADY_IN_PROGRESS.getReason(), e);
            case 489:
                throw new RuntimeException(ErrorCode.CHARACTER_ALREADY_HAS_TASK.getReason(), e);
            case 490:
                throw new RuntimeException(ErrorCode.CHARACTER_AT_DESTINATION.getReason(), e);
            case 491:
                throw new RuntimeException(ErrorCode.SLOT_EMPTY.getReason(), e);
            case 492:
                throw new RuntimeException(ErrorCode.INSUFFICIENT_GOLD.getReason(), e);
            case 493:
                throw new RuntimeException(ErrorCode.NOT_SKILL_LEVEL_REQUIRE.getReason(), e);
            case 496:
                throw new RuntimeException(ErrorCode.CHARACTER_LEVEL_INSUFFICIENT.getReason(), e);
            case 497:
                throw new RuntimeException(ErrorCode.CHARACTER_INVENTORY_FULL.getReason(), e);
            case 498:
                throw new RuntimeException(ErrorCode.CHARACTER_NOT_FOUND.getReason(), e);
            case 499:
                throw new CharacterInCooldownException(ErrorCode.CHARACTER_IN_COOLDOWN.getReason(), e);
            case 598:
                if (url.equals(restUtils.getMyCharacterAcceptNewTaskUrl()) || url.equals(restUtils.getMyCharacterExchangeTaskUrl())) {
                    throw new RuntimeException(ErrorCode.TASKMASTER_MAP_NOT_FOUND.getReason(), e);
                } else if (url.equals(restUtils.getMyCharacterFightUrl())) {
                    throw new RuntimeException(ErrorCode.MONSTER_MAP_NOT_FOUND.getReason(), e);
                } else if (url.equals(restUtils.getMyCharacterGatheringUrl())) {
                    throw new RuntimeException(ErrorCode.RESOURCE_MAP_NOT_FOUND.getReason(), e);
                } else if (url.equals(restUtils.getMyCharacterCraftingUrl())
                        || url.equals(restUtils.getMyCharacterRecyclingUrl())) {
                    throw new RuntimeException(ErrorCode.WORKSHOP_MAP_NOT_FOUND.getReason(), e);
                } else if (url.equals(restUtils.getMyCharacterBankDepositUrl())
                        || url.equals(restUtils.getMyCharacterWithdrawGoldBankUrl())
                        || url.equals(restUtils.getMyCharacterWithdrawBankUrl())
                        || url.equals(restUtils.getMyCharacterDepositeGoldBankUrl())) {
                    throw new RuntimeException(ErrorCode.BANK_MAP_NOT_FOUND.getReason(), e);
                } else if (url.equals(restUtils.getMyCharacterGeSellItemUrl()) || url.equals(restUtils.getMyCharacterGeBuyItemUrl())) {
                    throw new RuntimeException(ErrorCode.GRAND_EXCHANGE_MAP_NOT_FOUND.getReason(), e);
                }
            default:
                throw new HttpClientErrorException(e.getStatusCode(), e.getMessage());
        }
    }

}
