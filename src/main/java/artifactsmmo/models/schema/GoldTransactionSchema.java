package artifactsmmo.models.schema;

import artifactsmmo.models.entity.Bank;
import artifactsmmo.models.entity.Character;
import artifactsmmo.models.entity.Cooldown;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GoldTransactionSchema {

    @JsonProperty("cooldown")
    private Cooldown cooldown;

    @JsonProperty("bank")
    private Bank bank;

    @JsonProperty("character")
    private Character character;
}
