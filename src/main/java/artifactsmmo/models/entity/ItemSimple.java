package artifactsmmo.models.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ItemSimple {

    @JsonProperty("code")
    private String code;

    @JsonProperty("quantity")
    private int quantity;
}
