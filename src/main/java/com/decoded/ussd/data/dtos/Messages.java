package com.decoded.ussd.data.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Messages {
    private List<Destination> destinations = new ArrayList<>();
    private String from;
    private String text;
}
