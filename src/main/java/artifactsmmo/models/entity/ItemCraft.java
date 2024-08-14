package artifactsmmo.models.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemCraft {

    @JsonProperty("code")
    private String code;

    @JsonProperty("quantity")
    private int quantity;
}
