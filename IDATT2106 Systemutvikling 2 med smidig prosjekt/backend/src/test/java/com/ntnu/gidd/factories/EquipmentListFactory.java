package com.ntnu.gidd.factories;

import com.ntnu.gidd.model.Equipment;
import org.springframework.beans.factory.FactoryBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static com.ntnu.gidd.utils.StringRandomizer.getRandomString;

public class EquipmentListFactory implements FactoryBean<List<Equipment>> {
    Random random = new Random();

    @Override
    public List<Equipment> getObject() throws Exception {
        List<Equipment> equipmentList = new ArrayList<>();
        for (int i = 0; i < random.nextInt(3) + 2; i++){
            equipmentList.add(Equipment.builder()
                    .id(UUID.randomUUID())
                    .name(getRandomString(10))
                    .amount(random.nextInt(5))
                    .build());
        }
        return equipmentList;
    }

    @Override
    public Class<?> getObjectType() {
        return null;
    }
}
