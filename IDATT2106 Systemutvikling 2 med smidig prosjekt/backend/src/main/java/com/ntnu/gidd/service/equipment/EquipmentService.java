package com.ntnu.gidd.service.equipment;

import com.ntnu.gidd.dto.EquipmentDto;
import com.ntnu.gidd.model.Equipment;

import java.util.List;
import java.util.UUID;

public interface EquipmentService {
    List<Equipment> saveAndReturnEquipments(List<EquipmentDto> equipmentDtos);
}
