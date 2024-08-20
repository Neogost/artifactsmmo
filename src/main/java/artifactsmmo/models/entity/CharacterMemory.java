package artifactsmmo.models.entity;

import artifactsmmo.enums.Job;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class CharacterMemory implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(CharacterMemory.class);
    private Character character;
    private Job job;
    private Item targetCraft;
    private List<ItemCraft> craftPart;



    public CharacterMemory(Character character) {
        LOGGER.info("Creation of the memory of character {}", character.getName());
        this.character = character;
    }

    public boolean haveJob() {
        return job != null;
    }
}
