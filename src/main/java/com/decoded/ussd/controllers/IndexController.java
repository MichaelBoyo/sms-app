package com.decoded.ussd.controllers;

import com.decoded.ussd.data.models.Menu;
import com.decoded.ussd.services.routingService.RoutingService;
import com.decoded.ussd.services.menuService.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@CrossOrigin("*")
@Slf4j
public class IndexController {

    @Autowired
    private MenuService MenuService;

    @Autowired
    private RoutingService routingService;

    @PostMapping(path = "menus")
    public Map<String, Menu> menusLoad() throws IOException {
        log.info("menus end point");
        return MenuService.loadMenus();
    }


    @PostMapping
    public String ussdIngress(@RequestParam String sessionId, @RequestParam String serviceCode,
                              @RequestParam String phoneNumber, @RequestParam String text) {
        try {
            return routingService.menuLevelRouter(sessionId, serviceCode, phoneNumber, text);
        } catch (IOException e) {
            return "END " + e.getMessage();
        }
    }
}
