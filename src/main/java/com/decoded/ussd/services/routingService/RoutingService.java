package com.decoded.ussd.services.routingService;

import com.decoded.ussd.data.models.Menu;
import com.decoded.ussd.data.models.MenuOption;
import com.decoded.ussd.data.models.UssdSession;

import java.io.IOException;
import java.util.Map;

public interface RoutingService {
    String menuLevelRouter(String sessionId, String serviceCode, String phoneNumber, String text)
            throws IOException;

    String getNextMenuItem(UssdSession session, Map<String, Menu> menus) throws IOException;

    String getMenu(String menuLevel) throws IOException;

    String processMenuOption(UssdSession session, MenuOption menuOption) throws IOException;

    String processMenuOptionResponses(MenuOption menuOption, UssdSession session);

    String replaceVariable(Map<String, String> variablesMap, String response);

    UssdSession updateSessionMenuLevel(UssdSession session, String menuLevel);

    UssdSession checkAndSetSession(String sessionId, String serviceCode, String phoneNumber, String text);
}
