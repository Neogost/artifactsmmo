package artifactsmmo.models.response;

import artifactsmmo.models.schema.GeTransactionListSchema;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyCharacterGeBuyItemResponse {

    @JsonProperty("data")
    private GeTransactionListSchema geTransactionListSchema;

}
