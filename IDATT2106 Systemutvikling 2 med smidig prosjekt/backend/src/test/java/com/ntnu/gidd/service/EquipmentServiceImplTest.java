package com.ntnu.gidd.service;

import com.ntnu.gidd.dto.EquipmentDto;
import com.ntnu.gidd.factories.EquipmentListFactory;
import com.ntnu.gidd.model.Equipment;
import com.ntnu.gidd.repository.EquipmentRepository;
import com.ntnu.gidd.service.equipment.EquipmentService;
import com.ntnu.gidd.service.equipment.EquipmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EquipmentServiceImplTest {

    @InjectMocks
    private EquipmentServiceImpl equipmentService;

    @Mock
    private EquipmentRepository equipmentRepository;

    List<Equipment> equipments;

    ModelMapper modelMapper = new ModelMapper();

    @BeforeEach
    void setUp() throws Exception {
        equipments = new EquipmentListFactory().getObject();
    }

    @Test
    void testSaveAndReturnEquipmentsReturnsEquipments(){
        when(equipmentRepository.saveAll(any(List.class))).thenReturn(equipments);

        List<EquipmentDto> equipmentDtos = equipments.stream()
                .map(s -> modelMapper.map(s, EquipmentDto.class)).collect(Collectors.toList());

        List<Equipment> equipmentsFound = equipmentService.saveAndReturnEquipments(equipmentDtos);
        assertThat(equipmentsFound).isEqualTo(equipments);
    }
}
