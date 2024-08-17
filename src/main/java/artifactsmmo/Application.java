package artifactsmmo;

import artifactsmmo.controllers.*;
import artifactsmmo.enums.ContentType;
import artifactsmmo.enums.Job;
import artifactsmmo.enums.Type;
import artifactsmmo.models.entity.*;
import artifactsmmo.models.entity.Character;
import artifactsmmo.models.response.*;
import artifactsmmo.models.schema.BankItemSchema;
import artifactsmmo.models.schema.CharacterFightDataSchema;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    private static ApplicationContext context;
    private static MyCharactersController myCharactersController;
    private static MapController mapController;
    private static CharacterController characterController;
    private static ItemControler itemControler;
    private static MonsterController monsterController;
    private static ResourceController resourceController;
    private static MyAccountController myAccountController;

    private static Map taskMasterPosition;
    private static Map bankPosition;
    private static Map weaponcraftingPosition;
    private static List<Map> maps;
    private static List<Item> items;
    private static List<Monster> monsters;
    private static List<Resource> resources;
    private static List<ItemSimple> bankItems;


    /**
     * Point d'entrée principal de l'application.
     * <p>
     * Cette méthode initialise le contexte Spring et récupère les différents beans nécessaires au bon fonctionnement de l'application.
     * Elle charge toutes les cartes, objets et monstre du jeu
     * Enfin, elle lance la boucle principale du jeu.
     *
     * @param args les arguments de la ligne de commande (pas utilisés ici)
     * @throws InterruptedException si la boucle principale du jeu est interrompue
     *
     *                              <p>Les étapes principales effectuées par cette méthode sont :</p>
     *                              <ul>
     *                                  <li>Initialisation du contexte Spring à partir de la classe {@code Application}.</li>
     *                                  <li>Récupération des contrôleurs nécessaires (MapController, ItemController, MyCharactersController, CharacterController, MonsterController).</li>
     *                                  <li>Chargement de toutes les cartes, objets et monstre du jeu.</li>
     *                                  <li>Lancement de la boucle principale du jeu via la méthode {@code gameLoop()}.</li>
     *                              </ul>
     */
    public static void main(String[] args) throws InterruptedException {
        context = SpringApplication.run(Application.class, args);
        mapController = context.getBean(MapController.class);
        itemControler = context.getBean(ItemControler.class);
        monsterController = context.getBean(MonsterController.class);
        myCharactersController = context.getBean(MyCharactersController.class);
        characterController = context.getBean(CharacterController.class);
        resourceController = context.getBean(ResourceController.class);
        myAccountController = context.getBean(MyAccountController.class);

        // Charge all Maps
        maps = getMaps();
        // Charge all items
        items = getAllItems();
        // Charge all monsters
        monsters = getAllMonsters();
        // Charge all resources
        resources = getAllResources();
        // Charge all bank items
        bankItems = getAllBankItems();

        // Find the TaskMaster on the map
        taskMasterPosition = maps.stream().filter(m -> m.getContent() != null
                && ContentType.TASKS_MASTER.getValue().equals(m.getContent().getType())).findFirst().orElseThrow(NoSuchElementException::new);

        // Find the bank on the map
        bankPosition = maps.stream().filter(m -> m.getContent() != null
                && ContentType.BANK.getValue().equals(m.getContent().getType())).findFirst().orElseThrow(NoSuchElementException::new);

        // Find the weapon crafting on the map
        weaponcraftingPosition = maps.stream().filter(m -> m.getContent() != null
            && m.getContent().getCode().equals("weaponcrafting")).findFirst().orElseThrow(NoSuchElementException::new);

        // All the game is here
        gameLoop(context);

    }

    /**
     * Lance la boucle principale du jeu en exécutant une boucle de jeu pour chaque personnage du joueur dans des threads séparés.
     *
     * @param context le contexte de l'application Spring utilisé pour récupérer les personnages du joueur
     * @throws InterruptedException si un thread est interrompu pendant l'exécution
     *
     *                              <p>Cette méthode fonctionne comme suit :</p>
     *                              <ul>
     *                                  <li>Récupère la liste des personnages du joueur via le contexte Spring.</li>
     *                                  <li>Crée un pool de threads avec un nombre fixe de threads égal au nombre de personnages du joueur.</li>
     *                                  <li>Pour chaque personnage, lance une tâche asynchrone dans un thread séparé. Cette tâche exécute une boucle de jeu dédiée au personnage.</li>
     *                                  <li>Chaque tâche asynchrone sauvegarde les données de l'historique du personnage et appelle la méthode {@code characterGameLoop} pour gérer les actions du personnage.</li>
     *                                  <li>Attend que toutes les tâches asynchrones soient terminées en utilisant {@code CompletableFuture.allOf().join()}.</li>
     *                                  <li>Ferme le pool de threads une fois toutes les tâches terminées.</li>
     *                              </ul>
     *
     *                              <p>Cette méthode permet de gérer plusieurs personnages simultanément en parallèle, ce qui améliore les performances de l'application
     *                              lorsque plusieurs personnages doivent être contrôlés de manière indépendante.</p>
     */
    public static void gameLoop(ApplicationContext context) throws InterruptedException {
        List<Character> characters = getMyCharacters();

        // 1 Thread by character
        ExecutorService executorService = Executors.newFixedThreadPool(characters.size());
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (Character character : characters) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    // Save data and transmit it to another loop
                    Memory memory = new Memory();
                    // All action from the character
                    characterGameLoop(character, memory);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }, executorService);
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executorService.shutdown();
    }


    private static void characterGameLoop(Character character, Memory memory) throws InterruptedException {
        String name = character.getName();
        // update character data
        character = characterController.getCharacter(name);
        // update bank data
        bankItems = getAllBankItems();

        // Wait until character available
        waitUntilAvailable(character);

        // Need to deposit item in bank ?
        if (character.inventoryFull()) {
            deposeItemInBank(character);
        }

        // The character have no mission, take once
        if (character.getTaskTotal() == 0) {
            // The character starting by asking a quest to the TaskMaster
            myCharactersController.moveAndTakeNewTask(character, taskMasterPosition);
        }
        // The character have 1 mission and it's not finish
        else if (character.taskInProgress()
                && memory.canDoQuest() &&
                memory.getJob() == Job.FIGHTER) {

            performQuestAction(character, memory);

        }
        // The mission is complete
        else if (character.taskComplete()) {
            completeQuest(character);
        }
        // Can't do the quest do something else to be stronger
        else {
            LOGGER.info("{} lose a lot of fight ({}% win), need to do something else.", name, memory.ratioVictory());

            // Select an item to craft
            if (memory.getCraftItemToDO() == null) {
                LOGGER.info("{} find an item to craft to be stronger !", name);
                findCraftableItem(character, memory);

            }

            // Est ce que tout les composants sont dans mon inventaire ou en banque ?
            // Est ce que le premier item qui doit etre fait a tout ces composants ?$
            // TODO : Revoir toute cette logique, il faut les quantité de chaque item !!
            Item itemToDo = memory.prioriseItemToCraft();
            if (itemToDo.getCraft() != null) {
                LOGGER.debug("{} inspect the task to do");
                List<ItemInventory> allItemsNeededCollected = new ArrayList<>();
                int quantityOfPart = itemToDo.getCraft().getItems().size();
                for (ItemCraft itemCraft : itemToDo.getCraft().getItems()) {
                    String code  = itemCraft.getCode();
                    int quantity = itemCraft.getQuantity();
                    ItemInventory itemInventory = character.getInventory().stream().filter(i -> i.getCode().equals(code) && i.getQuantity() == quantity).findFirst().orElse(null);
                    if(itemInventory != null) {
                        LOGGER.debug("{} and this item {} have {} done !", name, itemToDo.getCode(), itemInventory.getCode());
                        allItemsNeededCollected.add(itemInventory);
                    }
                }
                if(quantityOfPart == allItemsNeededCollected.size()) {
                    LOGGER.info("Item {} ready to be crafted", itemToDo.getCode());
                    if(itemToDo.getType().equals("weapon")) {
                        if(!character.onMap(weaponcraftingPosition)) {
                            myCharactersController.moveToMap(name, weaponcraftingPosition);
                        } else {
                            myCharactersController.craft(name, new ItemSimple(itemToDo.getCode(), 1));
                        }
                    }
                }
            }
            // Do a job to complete the item
            LOGGER.info("{} is going to collect ressources", name);
            if (memory.getJob() == null) {
                // Priorisation of collect job
                memory.takeAJob(name);
                LOGGER.info("{} is a {} now !", name, memory.getJob().toString());
            }

            // Do the job
            if (memory.getJob() != Job.FIGHTER) {
                jobSkill(character, memory);
            } else {
                jobFight(character, memory);
            }
        }

        characterGameLoop(character, memory);
    }

    private static void jobFight(Character character, Memory memory) throws InterruptedException {
        Item itemToCreate = memory.prioriseItemToCraft();

        // What monster trop the item
        Monster monster = monsters.stream().filter(
                m -> m.getDrops().stream().anyMatch(d -> d.getCode().equals(itemToCreate.getCode()))
        ).findFirst().orElseThrow(NoSuchElementException::new);

        // Get where the item come from
        Map mapToCollect = maps.stream().filter(
                m -> m.getContent() != null && m.getContent().getCode().equals(monster.getCode())
        ).findFirst().orElseThrow(NoSuchElementException::new);

        if (!character.onMap(mapToCollect)) {
            myCharactersController.moveToMap(character.getName(), mapToCollect);
        } else {
            myCharactersController.fight(character.getName());
        }
    }

    private static void jobSkill(Character character, Memory memory) throws InterruptedException {

        Item itemToCreate = memory.prioriseItemToCraft();
        // Get what resource is needed
        Resource resource = resources.stream().filter(
                r -> r.getDrops().stream().anyMatch(d -> d.getCode().equals(itemToCreate.getCode()))
        ).findFirst().orElseThrow(NoSuchElementException::new);

        // Get where the resource come from
        Map mapToCollect = maps.stream().filter(
                m -> m.getContent() != null && m.getContent().getCode().equals(resource.getCode())
        ).findFirst().orElseThrow(NoSuchElementException::new);

        if (!character.onMap(mapToCollect)) {
            myCharactersController.moveToMap(character.getName(), mapToCollect);
        } else {
            myCharactersController.gathering(character.getName());
        }
    }

    private static void findCraftableItem(Character character, Memory memory) {

        // Find weapon than I can use to beat the monster
        Monster monster = memory.getQuestMonster();
        List<Item> itemInMyRange = weaponInMyRange(character);

        // Estime the best weapons
        List<ItemAdvanced> itemAdvanced = simulateItemsOnMonster(itemInMyRange, character, monster);
        ItemAdvanced itemSelected = itemAdvanced.stream().max(Comparator.comparing(ItemAdvanced::getDamageDealOnTarget)).orElseThrow(NoSuchElementException::new);
        LOGGER.info("{} is the best item to do to beat {} with {} damages", itemSelected.getName(), itemSelected.getTarget().getName(), itemSelected.getDamageDealOnTarget());

        // Extract composent of the items
        List<Item> craftItemNeeded = craftComposent(itemSelected.getCraft());

        // Save data
        memory.setCraftItemToDO(itemSelected);
        memory.setItemNeededToCraft(craftItemNeeded);
    }

    private static List<Item> weaponInMyRange(Character character) {
        return items.stream().filter(
                i -> i.getLevel() <= character.getLevel()
                        && i.getType().equals(Type.WEAPON.getValue())
        ).toList();
    }


    private static List<ItemAdvanced> simulateItemsOnMonster(@NotNull List<Item> items, @NotNull Character character, @NotNull Monster monster) {
        List<ItemAdvanced> itemAdvanceds = new ArrayList<>();
        for (Item item : items) {
            double damageDeal = 0;
            // Simulate each effect of each item on the monster
            for (Effect effect : item.getEffects()) {
                switch (effect.getName()) {
                    case "attack_air":
                        damageDeal += effect.getValue() + (effect.getValue() * (character.getDmgAir() * 0.01)) * (1 - monster.getResAir() * 0.01);
                        break;
                    case "attack_earth":
                        damageDeal += (effect.getValue() + (effect.getValue() * (character.getDmgEarth() * 0.01))) * (1 - monster.getResEarth() * 0.01);
                        break;
                    case "attack_water":
                        damageDeal += (effect.getValue() + (effect.getValue() * (character.getDmgWater() * 0.01))) * (1 - monster.getResWater() * 0.01);
                        break;
                    case "attack_fire":
                        damageDeal += (effect.getValue() + (effect.getValue() * (character.getDmgFire() * 0.01))) * (1 - monster.getResFire() * 0.01);
                        break;
                    default:
                        LOGGER.warn("Effect {} not supported", effect.getName());
                }
            }
            itemAdvanceds.add(new ItemAdvanced(item, monster, damageDeal));
            LOGGER.info("Item {} deal {} damages against {}.", item.getCode(), damageDeal, monster.getName());
        }
        return itemAdvanceds;
    }

    private static List<Item> craftComposent(Craft craft) {
        List<Item> items = new ArrayList<>();
        for (ItemCraft itemPart : craft.getItems()) {
            ItemResponse response = itemControler.getItem(itemPart.getCode());
            Item item = response.getSingleItemSchema().getItem();
            items.add(item);
            if (item.getCraft() != null) {
                items.addAll(craftComposent(item.getCraft()));
            }
        }
        return items;
    }

    private static void deposeItemInBank(Character character) throws InterruptedException {

        // Is not in the bank
        if (!character.onMap(bankPosition)) {
            myCharactersController.moveToMap(character.getName(), bankPosition);
        }
        // Is in the bank
        else {
            deposeAllInBank(character);
        }
    }

    private static void completeQuest(Character character) throws InterruptedException {

        if (!character.onMap(taskMasterPosition)) {
            myCharactersController.moveToMap(character.getName(), taskMasterPosition);
        } else {
            myCharactersController.completeTask(character.getName());
        }
    }

    private static void waitUntilAvailable(Character character) throws InterruptedException {

        int cooldown = character.getCooldown();
        if (cooldown > 0 && character.getCooldownExpiration().isAfter(ZonedDateTime.now())) {
            LOGGER.info("{} cooldown : {}sec", character.getName(), cooldown);
            Thread.sleep(cooldown * 1000);
        }
        LOGGER.info("{} quest {} / {}", character.getName(), character.getTaskProgress(), character.getTaskTotal());

    }

    private static void performQuestAction(Character character, Memory memory) throws InterruptedException {
        String name = character.getName();
        switch (character.getTaskType()) {
            case "monsters":
                Map monsterMap = maps.stream().filter(m -> m.getContent() != null && character.getTask().equals(m.getContent().getCode())).findFirst().orElseThrow(NoSuchElementException::new);
                Monster monster = monsters.stream().filter(m -> m.getCode().equals(character.getTask())).findFirst().orElseThrow(NoSuchElementException::new);
                // Save the monster information for this quest
                memory.setQuestMonster(monster);

                // If not on map, go to
                if (!character.onMap(monsterMap)) {
                    myCharactersController.moveToMap(name, monsterMap);
                }
                // On the map, fight !
                else {
                    CharacterFightDataSchema response = myCharactersController.fight(name);
                    String fightResult = response.getFight().getResult();
                    // Save data
                    memory.addFightQuest(fightResult);
                    memory.updateJob();

                    int xp = response.getFight().getXp();
                    LOGGER.info("{} {} the fight against {} and win {} xp.", name, fightResult, monster.getName(), xp);

                }
                break;
            default:
                LOGGER.warn("Default choice for task assignation");
        }
    }


    private static void deposeAllInBank(Character character) throws InterruptedException {
        for (ItemInventory item : character.getInventory()) {
            if (item.getQuantity() > 0) {
                BankItemSchema response = myCharactersController.deposeItemInBank(character.getName(), item, item.getQuantity());
                int cooldown = response.getCooldown().getRemainingSeconds();
                Thread.sleep(cooldown * 1000);
            }
        }
    }


    private static List<Map> getMaps() {
        List<Map> maps = mapController.getAllMaps(null, null, 1, 100);
        if (CollectionUtils.isEmpty(maps))
            LOGGER.error("Maps is empty");
        return maps;
    }

    private static List<Character> getMyCharacters() {
        return myCharactersController.getMyCharacters();

    }


    private static List<Item> getAllItems() {
        List<Item> items = itemControler.getAllItems();
        if (CollectionUtils.isEmpty(items))
            LOGGER.error("Items is empty");
        return items;
    }

    private static List<Monster> getAllMonsters() {
        List<Monster> monsters = monsterController.getAllMonsters();
        if (CollectionUtils.isEmpty(monsters))
            LOGGER.error("Monsters is empty");
        return monsters;
    }

    public static List<Resource> getAllResources() {
        List<Resource> resources = resourceController.getAllResources();
        if (CollectionUtils.isEmpty(resources))
            LOGGER.error("Resources is empty");
        return resources;
    }

    public static List<ItemSimple> getAllBankItems() {
        List<ItemSimple> itemSimples = myAccountController.getBankItems();
        if (CollectionUtils.isEmpty(itemSimples))
            LOGGER.warn("bank items is empty");
        return itemSimples;
    }
}