package artifactsmmo.models.schema;

import artifactsmmo.models.entity.Character;
import artifactsmmo.models.entity.Cooldown;
import artifactsmmo.models.entity.Transaction;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GeTransactionListSchema {

    @JsonProperty("cooldown")
    private Cooldown cooldown;

    @JsonProperty("transaction")
    private Transaction transaction;

    @JsonProperty("character")
    private Character character;
}
