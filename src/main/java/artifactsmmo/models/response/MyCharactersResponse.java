package artifactsmmo.models.response;

import artifactsmmo.models.entity.Character;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
@Getter
@Setter
@ToString
public class MyCharactersResponse {

    @JsonProperty("data")
    private List<Character> characters;

}