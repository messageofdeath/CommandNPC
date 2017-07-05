package me.messageofdeath.commandnpc.Utilities.CooldownManager;

import java.util.UUID;

public class Cooldown {

    private UUID uuid;
    private int npcID;
    private int commandID;

    public Cooldown(UUID uuid, int npcID, int commandID) {
        this.uuid = uuid;
        this.npcID = npcID;
        this.commandID = commandID;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getNpcID() {
        return npcID;
    }

    public int getCommandID() {
        return commandID;
    }
}
