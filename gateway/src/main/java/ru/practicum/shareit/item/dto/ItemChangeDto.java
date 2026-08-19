package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ItemChangeDto {
    private String name;
    private String description;
    @JsonProperty("available")
    private Boolean isAvailable;
}
