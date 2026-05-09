package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Item {
    private Long id;
    private Long ownerId;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    @JsonProperty("available")
    private Boolean isAvailable;
}
