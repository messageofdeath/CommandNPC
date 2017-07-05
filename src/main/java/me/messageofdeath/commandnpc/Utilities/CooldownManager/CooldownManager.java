package me.messageofdeath.commandnpc.Utilities.CooldownManager;

import java.util.ArrayList;
import java.util.UUID;

public class CooldownManager {

    private ArrayList<Cooldown> cooldowns;

    public CooldownManager() {
        cooldowns = new ArrayList<>();
    }

    public void addCooldown(Cooldown cooldown) {
        if(cooldowns.stream().noneMatch(search -> search.getUuid() == cooldown.getUuid())) {
            cooldowns.add(cooldown);
        }
    }

    public void removeCooldown(UUID uuid) {
        cooldowns.stream().filter(search -> search.getUuid() == uuid).findFirst().ifPresent(cooldowns::remove);
    }

    public boolean hasCooldown(UUID uuid) {
        return cooldowns.stream().anyMatch(search -> search.getUuid() == uuid);
    }

    public Cooldown getCooldown(UUID uuid) {
        return cooldowns.stream().filter(search -> search.getUuid() == uuid).findFirst().orElse(null);
    }

    public Cooldown[] getCooldowns(UUID uuid) {
        return cooldowns.stream().filter(search -> search.getUuid() == uuid).toArray(Cooldown[]::new);
    }

    public ArrayList<Cooldown> getCooldowns() {
        return cooldowns;
    }
}
