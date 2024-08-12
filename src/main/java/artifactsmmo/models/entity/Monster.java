package artifactsmmo.models.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public  class Monster {

    @JsonProperty("name")
    private String name;

    @JsonProperty("code")
    private String code;

    @JsonProperty("level")
    private int level;

    @JsonProperty("hp")
    private int hp;

    @JsonProperty("attack_fire")
    private int attackFire;

    @JsonProperty("attack_earth")
    private int attackEarth;

    @JsonProperty("attack_water")
    private int attackWater;

    @JsonProperty("attack_air")
    private int attackAir;

    @JsonProperty("res_fire")
    private int resFire;

    @JsonProperty("res_earth")
    private int resEarth;

    @JsonProperty("res_water")
    private int resWater;

    @JsonProperty("res_air")
    private int resAir;

    @JsonProperty("min_gold")
    private int minGold;

    @JsonProperty("max_gold")
    private int maxGold;

    @JsonProperty("drops")
    private List<Drop> drops;
}