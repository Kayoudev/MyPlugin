package fr.myplugin.utils.city;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CityInviteManager {

    private static final Map<UUID, UUID> pendingInvites = new HashMap<>();

    public static boolean invitePlayer(UUID invited, UUID mayor) {
        pendingInvites.put(invited, mayor);
        return true;
    }

    public static boolean hasInvite(UUID invited) {
        return pendingInvites.containsKey(invited);
    }

    public static UUID getInviter(UUID invited) {
        return pendingInvites.get(invited);
    }

    public static void removeInvite(UUID invited) {
        pendingInvites.remove(invited);
    }
}
