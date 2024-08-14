package artifactsmmo.models.response;

import artifactsmmo.models.entity.Gold;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoldResponse {

    @JsonProperty("data")
    private Gold gold;
}
