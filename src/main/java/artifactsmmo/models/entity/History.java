package artifactsmmo.models.entity;


import artifactsmmo.Application;
import artifactsmmo.enums.CraftSkill;
import artifactsmmo.enums.FightResult;
import artifactsmmo.enums.Job;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Getter
@Setter
public class History {

    private static final Logger LOGGER = LoggerFactory.getLogger(History.class);

    private Job job = Job.FIGHTER;
    private List<FightResult> lastsFight = new ArrayList<>();
    private Monster questMonster;
    private int MAX_FIGHT_QUEST_HISTORY = 2;

    private Item craftItemToDO;
    private List<Item> itemFromMiningJob;
    private List<Item> itemFromWoodcuttingJob;
    private List<Item> itemFromCookingJob;
    private List<Item> itemFromWeaponcraftingJob;
    private List<Item> itemFromGearcraftingJob;
    private List<Item> itemFromJewelrycrafting;
    private List<Item> itemFromMob;


    public void addFightQuest(String fightResult) {
        if(lastsFight.size() > MAX_FIGHT_QUEST_HISTORY) {
            lastsFight.remove(0);
        }
        lastsFight.add(FightResult.fromValue(fightResult));
    }

    public boolean canDoQuest() {
        if(lastsFight.size() <= MAX_FIGHT_QUEST_HISTORY / 2)
            return true;
        long victory = lastsFight.stream().filter(f -> f.getValue().equals(FightResult.WIN.getValue())).count();
        return victory > (lastsFight.size() / 2);
    }

    public long ratioVictory(){
        long victory = lastsFight.stream().filter(f -> f.getValue().equals(FightResult.WIN.getValue())).count();
        long lose = lastsFight.stream().filter(f -> f.getValue().equals(FightResult.LOSE.getValue())).count();
        return victory/lose;
    }

    public  void updateJob() {
        if(!canDoQuest() && job == Job.FIGHTER) {
            job = null;
        }
    }

    public void setItemNeededToCraft(List<Item> items) {
        itemFromMiningJob = items.stream().filter(i -> i.getSubtype().equals(CraftSkill.MINING.getValue())).toList();
        itemFromCookingJob = items.stream().filter(i -> i.getSubtype().equals(CraftSkill.COOKING.getValue())).toList();
        itemFromWoodcuttingJob = items.stream().filter(i -> i.getSubtype().equals(CraftSkill.WOODCUTTING.getValue())).toList();
        // TODO : Comprendre et corriger comment savoir a quel catégorie de métier appartien les items "Ressource/Bar"
        itemFromWeaponcraftingJob = items.stream().filter(i -> i.getSubtype().equals(CraftSkill.WEAPONCRAFTING.getValue())
                                    || i.getSubtype().equals("bar")).toList();
        itemFromJewelrycrafting = items.stream().filter(i -> i.getSubtype().equals(CraftSkill.JEWELRYCRAFTING.getValue())).toList();
        itemFromGearcraftingJob = items.stream().filter(i -> i.getSubtype().equals(CraftSkill.GEARCRAFTING.getValue())).toList();
        itemFromMob = items.stream().filter(i -> i.getSubtype().equals("mob")).toList();
    }

    public List<Item> getItemNeededToCraft() {
        List<Item> items = new ArrayList<>();
        items.addAll(itemFromJewelrycrafting);
        items.addAll(itemFromWeaponcraftingJob);
        items.addAll(itemFromGearcraftingJob);
        items.addAll(itemFromCookingJob);
        items.addAll(itemFromMiningJob);
        items.addAll(itemFromWoodcuttingJob);
        items.addAll(itemFromMob);
        return items;
    }

    public Item prioriseItemToCraft() {
        if(!itemFromMiningJob.isEmpty()) {
            return itemFromMiningJob.stream().findFirst().orElseThrow(NoSuchElementException::new);
        }
        else if(!itemFromCookingJob.isEmpty()) {
            return itemFromCookingJob.stream().findFirst().orElseThrow(NoSuchElementException::new);
        }
        else if(!itemFromWoodcuttingJob.isEmpty()) {
            return itemFromWoodcuttingJob.stream().findFirst().orElseThrow(NoSuchElementException::new);
        }
        else if(!itemFromGearcraftingJob.isEmpty()) {
            return itemFromGearcraftingJob.stream().findFirst().orElseThrow(NoSuchElementException::new);
        }
        else if(!itemFromJewelrycrafting.isEmpty()) {
            return itemFromJewelrycrafting.stream().findFirst().orElseThrow(NoSuchElementException::new);
        }
        else if(!itemFromWeaponcraftingJob.isEmpty()) {
            return itemFromWeaponcraftingJob.stream().findFirst().orElseThrow(NoSuchElementException::new);
        }
        else if(!itemFromMob.isEmpty()) {
            return itemFromMob.stream().findFirst().orElseThrow(NoSuchElementException::new);
        }
        return null;
    }


    public void takeAJob(String name) {
        if(!itemFromMiningJob.isEmpty()) {
            job =  Job.MINING;
        }
        else if(!itemFromCookingJob.isEmpty()) {
            job = Job.COOKING;
        }
        else if(!itemFromWoodcuttingJob.isEmpty()) {
            job = Job.WOODCUTTING;
        }
        else if(!itemFromGearcraftingJob.isEmpty()) {
            job = Job.GEARCRAFTING;
        }
        else if(!itemFromJewelrycrafting.isEmpty()) {
            job = Job.JEWELRYCRAFTING;
        }
        else if(!itemFromWeaponcraftingJob.isEmpty()) {
            job = Job.WEAPONCRAFTING;
        }
        else if(!itemFromMob.isEmpty()) {
            job = Job.FIGHTER;
        }
    }
}
