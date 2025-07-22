package net.chaolux.createterminal.common.client;

public class ClientAdvancementCache {
    private static boolean dragonKillUnlock=false;
    public static void setDragonKill(boolean value) {
        dragonKillUnlock=value;
    }

    public static boolean hasDragonKill() {
        return dragonKillUnlock;
    }
}
