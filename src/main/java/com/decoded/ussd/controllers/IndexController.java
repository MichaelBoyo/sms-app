package com.decoded.ussd.controllers;

import com.decoded.ussd.data.Menu;
import com.decoded.ussd.services.MenuService;
import com.decoded.ussd.services.UssdRoutingService;
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
    private MenuService menuService;

    @Autowired
    private UssdRoutingService ussdRoutingService;

    @PostMapping(path = "menus")
    public Map<String, Menu> menusLoad() throws IOException {
        log.info("menus end point");
        return menuService.loadMenus();
    }

    /**
     * @return
     */
    @GetMapping(path = "")
    public String index() {
        return "Your have reached us";
    }


    @PostMapping(path = "ussd")
    public String ussdIngress(@RequestParam String sessionId, @RequestParam String serviceCode,
                              @RequestParam String phoneNumber, @RequestParam String text) {
        try {
            return ussdRoutingService.menuLevelRouter(sessionId, serviceCode, phoneNumber, text);
        } catch (IOException e) {
            return "END " + e.getMessage();
        }
    }
}
