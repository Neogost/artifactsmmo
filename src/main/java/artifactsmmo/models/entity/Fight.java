package artifactsmmo.models.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Fight {

    @JsonProperty("xp")
    private int xp;

    @JsonProperty("gold")
    private int gold;

    @JsonProperty("drops")
    private List<Drop> drops;

    @JsonProperty("turns")
    private int turns;

    @JsonProperty("monster_blocked_hits")
    private BlockedHits monsterBlockedHits;

    @JsonProperty("player_blocked_hits")
    private BlockedHits playerBlockedHits;

    @JsonProperty("logs")
    private List<String> logs;

    @JsonProperty("result")
    private String result;

}
