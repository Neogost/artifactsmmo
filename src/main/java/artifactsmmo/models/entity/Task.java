package artifactsmmo.models.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Task {
    private String code;
    private String type;
    private int total;
}
