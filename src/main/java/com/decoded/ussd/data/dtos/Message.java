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
public class Message {

    //    private Messages messages;
    private String from;
    private List<Destination> destinations = new ArrayList<>();

    private String text;



}
