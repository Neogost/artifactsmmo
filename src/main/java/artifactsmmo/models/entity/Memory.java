package artifactsmmo.models.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class Memory implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(Memory.class);
    private static List<CharacterMemory> myCharactersMemory = Collections.synchronizedList(new ArrayList<>());


    public void initialiseAllMemory(List<Character> characters) {
        LOGGER.info("Initialise the memory of the application.");
        for (Character character : characters) {
            CharacterMemory characterMemory = new CharacterMemory(character);
            myCharactersMemory.add(characterMemory);
        }
    }

    public CharacterMemory getCharacterMemory(String name) {
        return myCharactersMemory.stream().filter(c -> c.getCharacter().getName().equals(name)).findFirst().orElseThrow(NoSuchElementException::new);
    }


}
