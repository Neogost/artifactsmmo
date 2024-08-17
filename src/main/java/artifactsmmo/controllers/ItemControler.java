package artifactsmmo.controllers;

import artifactsmmo.enums.CraftSkill;
import artifactsmmo.enums.Type;
import artifactsmmo.models.entity.Item;
import artifactsmmo.models.response.ItemResponse;
import artifactsmmo.services.ItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static artifactsmmo.utils.ValidationUtils.*;

@RestController
public class ItemControler {

    @Autowired
    private ItemService itemService;
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemControler.class);

    /**
     * Retrieves all items using default parameters for pagination and filtering.
     * <p>
     * This method logs the retrieval process and calls the {@code itemService} with default values
     * for all filtering and pagination parameters. The default parameters used are:
     * <ul>
     *     <li>Filter by code: {@code null}</li>
     *     <li>Filter by name: {@code null}</li>
     *     <li>Page size: {@code 30}</li>
     *     <li>Page number: {@code 1}</li>
     *     <li>Sort field: {@code null}</li>
     *     <li>Sort order: {@code 1}</li>
     *     <li>Additional filter: {@code 50}</li>
     *     <li>Another parameter: {@code null}</li>
     * </ul>
     * </p>
     *
     * @return a {@link List} of {@link Item} objects representing all available items.
     */
    public List<Item> getAllItems() {
        LOGGER.info("Getting the all items with default parameters.");
        try {
            return itemService.getAllItems(null, null, 30, 1, null, 1, 50, null);
        } catch (RuntimeException e) {
            LOGGER.error("Error fetching items with default parameters : {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves a list of items based on the provided filtering and pagination parameters.
     * <p>
     * This method logs the retrieval request, validates the input parameters, and delegates
     * the operation to the {@code itemService} to fetch the items. The filtering and pagination
     * parameters allow fine-grained control over the items retrieved.
     * </p>
     *
     * @param craftMaterial the code for the craft material to filter items by.
     *                      Can be {@code null} to include all materials.
     * @param craftSkill    the {@link CraftSkill} required for the items.
     *                      Can be {@code null} to include all skills.
     * @param maxLevel      the maximum level of items to retrieve.
     *                      Must be a non-negative integer.
     * @param minLevel      the minimum level of items to retrieve.
     *                      Must be a non-negative integer and less than or equal to {@code maxLevel}.
     * @param name          the name to filter items by.
     *                      Can be {@code null} to include items with any name.
     * @param page          the page number to retrieve.
     *                      Must be a positive integer.
     * @param size          the number of items per page.
     *                      Must be a positive integer.
     * @param type          the {@link Type} of items to filter by.
     *                      Can be {@code null} to include all types.
     * @return a {@link List} of {@link Item} objects that match the specified filters and pagination parameters.
     * @throws IllegalArgumentException if any of the parameters are invalid, such as negative page numbers, sizes, or levels,
     *                                  or if {@code minLevel} is greater than {@code maxLevel}.
     */
    public List<Item> getAllItems(String craftMaterial, CraftSkill craftSkill, int maxLevel, int minLevel, String name, int page, int size, Type type) {
        LOGGER.info("Getting the all items.");
        try {
            validatePage(page);
            validatePageSize(size);
            validateMinLevel(minLevel);
            validateMaxLevel(maxLevel);
            validateLevelsParam(minLevel, maxLevel);
            validateCharacterName(name);
            validateCraftMaterialCode(craftMaterial);

            return itemService.getAllItems(craftMaterial, craftSkill.getValue(), maxLevel, minLevel, name, page, size, type.getValue());
        } catch (RuntimeException e) {
            LOGGER.error("Error fetching items : {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves a specific item based on its code.
     * <p>
     * This method logs the retrieval request, validates the provided item code,
     * and then delegates the operation to {@code itemService} to fetch the item.
     * </p>
     *
     * @param code the code of the item to retrieve.
     *             Must not be {@code null} or empty.
     * @return an {@link ItemResponse} object representing the item with the specified code.
     * @throws IllegalArgumentException if the item code is {@code null} or empty.
     */
    public ItemResponse getItem(String code) {
        LOGGER.info("Getting item {}", code);
        try {
            validateItemCode(code);

            return itemService.getItem(code);
        } catch (RuntimeException e) {
            LOGGER.error("Error fetching item : {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

}
