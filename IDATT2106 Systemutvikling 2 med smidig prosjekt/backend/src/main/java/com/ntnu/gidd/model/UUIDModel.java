package com.ntnu.gidd.model;


import com.fasterxml.uuid.Generators;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@MappedSuperclass
@EqualsAndHashCode(of = {"id"}, callSuper = false)
public class UUIDModel extends TimeStampedModel{
    @Id
    @Column(columnDefinition = "CHAR(32)")
    private UUID id = Generators.randomBasedGenerator().generate();
}
