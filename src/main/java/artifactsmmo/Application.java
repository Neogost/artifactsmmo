package artifactsmmo;

import artifactsmmo.controllers.*;
import artifactsmmo.enums.*;
import artifactsmmo.models.entity.*;
import artifactsmmo.models.entity.Character;
import artifactsmmo.models.entity.Map;
import artifactsmmo.models.schema.SingleItemSchema;
import artifactsmmo.utils.DateUtils;
import artifactsmmo.utils.ItemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.*;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class Application {

    private static File database = new File("./database.txt");
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    private static ApplicationContext context;
    private static MyCharactersController myCharactersController;
    private static MapController mapController;
    private static CharacterController characterController;
    private static ItemControler itemControler;
    private static MonsterController monsterController;
    private static ResourceController resourceController;
    private static MyAccountController myAccountController;
    private static AccountController accountController;

    private static Map taskMasterPosition;
    private static Map bankPosition;
    private static Map weaponcraftingPosition;
    private static Map workshopMiningPosition;

    private static List<Map> maps;
    private static List<Item> items;

    private static Memory memory;

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
        accountController = context.getBean(AccountController.class);

        getServerStatus();

        maps = mapController.getAllMaps();
        items = itemControler.getAllItems();

        // Find the TaskMaster on the map
        taskMasterPosition = maps.stream().filter(m -> m.getContent() != null
                && ContentType.TASKS_MASTER == m.getContent().getType()).findFirst().orElseThrow(NoSuchElementException::new);

        // Find the bank on the map
        bankPosition = maps.stream().filter(m -> m.getContent() != null
                && ContentType.BANK == m.getContent().getType()).findFirst().orElseThrow(NoSuchElementException::new);

        // Find the weapon crafting on the map
        weaponcraftingPosition = maps.stream().filter(m -> m.getContent() != null
                && m.getContent().getCode().equals(Skill.WEAPONCRAFTING.getValue())).findFirst().orElseThrow(NoSuchElementException::new);

        // Find the weapon crafting on the map
        workshopMiningPosition = maps.stream().filter(m -> m.getContent() != null
                && m.getContent().getType() == ContentType.WORKSHOP
                && m.getContent().getCode().equals(Skill.MINING.getValue())).findFirst().orElseThrow(NoSuchElementException::new);

        // All the game is here
        gameLoop(context);

    }

    private static void getServerStatus() throws InterruptedException {
        Server server = accountController.getStatus();
        LOGGER.info("======================================================");
        LOGGER.info("State of the server : {}", server.getStatus());
        LOGGER.info("Version : {}", server.getVersion());
        LOGGER.info("Number of characters online : {}", server.getCharactersOnline());
        for (Announcement announcement : server.getAnnouncements()) {
            LOGGER.info("Date of creation : {}", announcement.getCreatedAt());
            LOGGER.info("Annonce : {}", announcement.getMessage());
        }
        LOGGER.info("Next wipe : {}", server.getNextWipe());
        LOGGER.info("======================================================");
        // Sleep to take time to read
        Thread.sleep(5000);
    }

    public static void gameLoop(ApplicationContext context) throws InterruptedException {
        List<Character> characters = myCharactersController.getMyCharacters();

        // TODO : Extract data from file database.txt
        //memory = readFromFile();

        if (memory == null) {
            memory = new Memory();
            memory.initialiseAllMemory(characters);
        }
        // 1 Thread by character
        ExecutorService executorService = Executors.newFixedThreadPool(characters.size());
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (Character character : characters) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    // Save data and transmit it to another loop
                    // All action from the character
                    characterGameLoop(character, memory);
                    // TODO : Save data in file database.txt
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
        CharacterMemory characterMemory = memory.getCharacterMemory(character.getName());
        do {
            // update character data
            character = characterController.getCharacter(name);
            waitUntilAvailable(character);

            // Take a task
            if (!character.haveTask()) {
                myCharactersController.moveAndTakeNewTask(character, taskMasterPosition);
            }

            // Can equip best item ?


            // Choose a job
            if (!characterMemory.haveJob()) {
                chooseJob(character, characterMemory);
            }

            // FIXME : A retirer
            if (characterMemory.getJob() == null) {
                LOGGER.error("{} haven't job.", name);
                //break;
            } else {
                LOGGER.info("{} is a {}.", name, characterMemory.getJob().toString());

                // Target : craft this item.
                ItemCraft item = itemCraftToDo(characterMemory.getCraftPart());
                if (item == null) {
                    Craft craft = Optional.ofNullable(itemControler.getItem(item.getCode()).getItem().getCraft()).orElse(new Craft());
                    switch (characterMemory.getJob()) {
                        case FIGHTER:
                            break;
                        case MINING:

                            if (character.getMiningLevel() >= item.getLevel()) {

                                // si c'est un minerai a aller chercher
                                if (item.getSubType().equals("mining")) {
                                }
                                // Item a craft
                                else {
                                    // Get craft to do item
                                    // Tout est dans l'inventaire
                                    if (character.isInInventory(craft, item.getQuantityTotalNeeded())) {
                                        myCharactersController.moveAndCraft(character, workshopMiningPosition, new ItemSimple(item.getCode(), item.getQuantityTotalNeeded()));
                                        characterMemory.setJob(null);
                                    }
                                    // Aller chercher les items.
                                    else {
                                        getItemsCraftFromBank(character, craft, item);
                                    }
                                }
                            }
                            break;
                        case WEAPONCRAFTING:
                            // have suffisant level
                            if (character.getWeaponcraftingLevel() >= item.getLevel()) {
                                if (character.isInInventory(craft, item.getQuantityTotalNeeded())) {
                                    myCharactersController.moveAndCraft(character, weaponcraftingPosition, new ItemSimple(item.getCode(), item.getQuantityTotalNeeded()));
                                    characterMemory.setJob(null);
                                } else {
                                    getItemsCraftFromBank(character, craft, item);
                                }
                            }
                        default:
                            characterMemory.setJob(null);
                            LOGGER.warn("{} haven't works to do.", name);
                    }
                }
            }
            // TODO : Save all memory data to next execution of the application.
           // writeToFile();
        } while (true);
    }


    public static void getItemsCraftFromBank(Character character, Craft craft, ItemCraft itemToDo) throws InterruptedException {
        for (ItemSimple itemToWithdraw : craft.getItems()) {
            ItemInventory itemInventory = character.getInventory().stream().filter(i -> i.getCode().equals(itemToWithdraw.getCode())).findFirst().orElse(null);
            if (itemInventory != null) {
                itemToWithdraw.setQuantity(itemToWithdraw.getQuantity() * itemToDo.getQuantityTotalNeeded() - itemInventory.getQuantity());
            } else {
                itemToWithdraw.setQuantity(itemToWithdraw.getQuantity() * itemToDo.getQuantityTotalNeeded());
            }

            myCharactersController.moveAndWithdrawBankItem(character, bankPosition, itemToWithdraw);
            itemToDo.transfertFromBank(itemToWithdraw.getQuantity());
        }
    }

    private static void chooseJob(Character character, CharacterMemory characterMemory) {
        String monsterCode = character.getTask();
        Monster monster = monsterController.getMonster(monsterCode);

        // Can do the quest ?
        switch (character.getTaskType()) {
            case MONSTERS:
                // Can beat the monster ?
                boolean victory = character.simulateFight(monster);
                if (victory) {
                    characterMemory.setJob(Job.TASK_FIGHTER);
                }
                break;
            default:
                LOGGER.error("{} have a no supported task.", character.getName());

        }
        if (characterMemory.haveJob()) {
            LOGGER.info("{} can do the task. {} is now a {}", character.getName(), character.getName(), characterMemory.getJob());
            return;
        }
        LOGGER.info("{} can't do the task. Try to find item in inventory.", character.getName());

        // Find Item in inventory who upgrade character stuff
        // Save item to equip


        LOGGER.info("{} can't equip new item. Try to craft something.");
        // Find thing to do to upgrade stuff
        improveStuff(character, characterMemory, monster);

    }

    public static void improveStuff(Character character, CharacterMemory characterMemory, Monster targetedMonster) {

        // Weapon
        List<ItemFightSimulation> weapons = ItemUtils.simulateWeaponOnMonster(items, character, targetedMonster);
        ItemFightSimulation bestWeapon = weapons.stream().max(Comparator.comparing(ItemFightSimulation::getDamageDealOnTarget)).orElseThrow(NoSuchElementException::new);

        List<ItemCraft> craftsPart = decomposeCraft(bestWeapon, null, 0);
        LOGGER.info("{} have to craft {} items to get {}", character.getName(), craftsPart.size(), bestWeapon.getName());
        // Comparer avec l'inventaire + bank
        findInInventoryAndBankCraftPart(character, craftsPart);

        // Save
        characterMemory.setTargetCraft(itemControler.getItem(bestWeapon.getCode()).getItem());
        characterMemory.setCraftPart(craftsPart);
        // Partir a la recherche des composants restants

        ItemCraft priority = itemCraftToDo(craftsPart);

        if (priority != null && priority.getSkill() != null) {
            characterMemory.setJob(Job.fromValue(priority.getSkill().getValue()));
        }
    }

    /**
     * Sélectionne l'élément `ItemCraft` à traiter en priorité parmi une liste donnée.
     *
     * <p>Cette méthode prend une liste d'objets `ItemCraft`, filtre ceux dont la quantité totale est
     * inférieure à la quantité totale nécessaire, et retourne celui ayant la priorité la plus élevée.</p>
     *
     * @param craftsPart Une liste d'objets `ItemCraft` représentant les éléments à vérifier.
     * @return L'objet `ItemCraft` avec la plus haute priorité parmi ceux dont la quantité totale est inférieure à la quantité totale nécessaire.
     * @throws NoSuchElementException si aucun élément ne correspond aux critères de filtrage.
     */
    private static ItemCraft itemCraftToDo(List<ItemCraft> craftsPart) {
        return craftsPart.stream().filter(c -> c.getQuantityTotal() < c.getQuantityTotalNeeded()).max(Comparator.comparing(c -> c.getPriority())).orElse(null);
    }


    private static void findInInventoryAndBankCraftPart(Character character, List<ItemCraft> craftsPart) {
        for (ItemCraft itemCraft : craftsPart) {
            ItemSimple itemInBank = myAccountController.getBankItem(itemCraft.getCode());
            ItemInventory itemInInventory = character.getInventory().stream().filter(i -> i.getCode().equals(itemCraft.getCode())).findFirst().orElse(null);

            int quantityInInventory = itemInInventory != null ? itemInInventory.getQuantity() : 0;
            int quantityInBank = itemInBank != null ? itemInBank.getQuantity() : 0;

            itemCraft.setQuantityInBank(quantityInBank);
            itemCraft.setQuantityInInventory(quantityInInventory);
            LOGGER.info("Item {} found in {} quantity in Inventory and {} quantity in Bank. Total : {}",
                    itemCraft.getCode(),
                    itemCraft.getQuantityInInventory(),
                    itemCraft.getQuantityInBank(),
                    itemCraft.getQuantityTotal());
        }
    }


    public static List<ItemCraft> decomposeCraft(Item itemToCraft, Craft craft, int priority) {
        List<ItemCraft> items = new ArrayList<>();
        if (itemToCraft != null) {
            items.add(new ItemCraft(itemToCraft.getCode(),
                    itemToCraft.getLevel(),
                    itemToCraft.getCraft().getSkill(),
                    itemToCraft.getItemType(),
                    itemToCraft.getSubtype(),
                    0,
                    0,
                    1,
                    priority++));
            craft = itemToCraft.getCraft();
        }
        for (ItemSimple itemSimple : craft.getItems()) {
            SingleItemSchema singleItemSchema = itemControler.getItem(itemSimple.getCode());
            Item item = singleItemSchema.getItem();

            items.add(new ItemCraft(itemSimple.getCode(),
                    item.getLevel(),
                    item.getCraft() != null ? item.getCraft().getSkill() : null,
                    item.getItemType(),
                    item.getSubtype(),
                    0,
                    0,
                    itemSimple.getQuantity(),
                    priority));

            if (item.getCraft() != null) {
                items.addAll(decomposeCraft(null, item.getCraft(), ++priority));
            }
        }

        return items;
    }


    private static void waitUntilAvailable(Character character) throws InterruptedException {

        long cooldown = DateUtils.zonedDateTimeDifference(character.getCooldownExpiration(), ZonedDateTime.now(), ChronoUnit.SECONDS);
        if (cooldown > 0) {
            long wait = character.getCooldown();
            LOGGER.info("{} is in cooldown for {} secondes.", character.getName(), wait);
            Thread.sleep(wait * 1000);
        }
    }


    public static void writeToFile() {
        try (FileOutputStream fos = new FileOutputStream(database);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(memory);
            oos.flush();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);

        } catch (NotSerializableException nse) {
            LOGGER.error("Error during save memory in file {}", database.getName());
            //do something
        } catch (IOException eio) {
            //do something
        }
    }


    public static Memory readFromFile() {
        Memory data = null;

        try  {
            FileInputStream fis = new FileInputStream(database);
            ObjectInputStream ois = new ObjectInputStream(fis);
            data = (Memory) ois.readObject();
            ois.close();
            fis.close();
        } catch (FileNotFoundException e) {
            LOGGER.warn("Database not found");
            return null;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return data;
    }
}