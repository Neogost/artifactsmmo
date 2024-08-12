package artifactsmmo.models.Schema;

import artifactsmmo.models.entity.GE;
import artifactsmmo.models.entity.Item;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SingleItemSchema {

    @JsonProperty("item")
    private Item item;

    @JsonProperty("ge")
    private GE ge;
}
