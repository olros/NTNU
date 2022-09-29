package com.ntnu.gidd.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@EqualsAndHashCode(callSuper = true)
public class Equipment extends UUIDModel {

    @ManyToOne
    private Activity activity;

    @NotNull
    String name;

    int amount;
}
