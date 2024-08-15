package artifactsmmo.models.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
@Getter
@Setter
@ToString
public class MyCharactersResponse {

    private List<Character> characters;

}