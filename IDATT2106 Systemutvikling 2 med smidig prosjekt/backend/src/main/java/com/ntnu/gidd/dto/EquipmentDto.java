package com.ntnu.gidd.dto;

import com.ntnu.gidd.model.Equipment;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class EquipmentDto {
    private String name;
    private int amount;
}
