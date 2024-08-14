package artifactsmmo.models.response;

import artifactsmmo.models.schema.SingleItemSchema;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemResponse {


    @JsonProperty("data")
    private SingleItemSchema singleItemSchema;
}
