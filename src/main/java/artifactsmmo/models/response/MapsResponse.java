package artifactsmmo.models.response;

import artifactsmmo.models.entity.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MapsResponse {

    private List<Map> maps;
    private int total;
    private int page;
    private int size;
    private int pages;


}