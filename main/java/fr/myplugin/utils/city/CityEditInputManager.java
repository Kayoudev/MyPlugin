package fr.myplugin.utils.city;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CityEditInputManager {

    public static final Map<UUID, Boolean> waitingForCityNameInput = new HashMap<>();
    public static final Map<UUID, Boolean> waitingForCityDescriptionInput = new HashMap<>();
    public static final Map<UUID, Boolean> waitingForDeputyInput = new HashMap<>();
    public static final Map<UUID, Boolean> waitingForCitizenInvite = new HashMap<>();
    public static Map<UUID, Boolean> waitingForCitizenRemoval = new HashMap<>();
}
