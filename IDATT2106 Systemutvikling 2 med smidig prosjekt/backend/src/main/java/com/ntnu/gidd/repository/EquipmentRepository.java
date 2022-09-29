package com.ntnu.gidd.repository;

import com.ntnu.gidd.model.Equipment;
import com.ntnu.gidd.model.UUIDModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EquipmentRepository extends JpaRepository<Equipment, UUID> {
}
