package artifactsmmo.models.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ItemInventory {
    @JsonProperty("slot")
    private int slot;
    @JsonProperty("code")
    private String code;
    @JsonProperty("quantity")
    private int quantity;

}