package com.decoded.ussd.services.menuService;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.decoded.ussd.data.models.Menu;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuService implements iMenuService {

    private final ResourceLoader resourceLoader;


    @Override
    public Map<String, Menu> loadMenus() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Resource resource = resourceLoader.getResource("classpath:menu.json");
        InputStream input = resource.getInputStream();
        String json = readFromInputStream(input);
        return objectMapper.readValue(json, new TypeReference<>() {
        });
    }

}
