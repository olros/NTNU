package com.ntnu.gidd.model;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.util.UUID;


@Data
@ToString(of = {"jti", "isValid"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class RefreshToken {
    @Id
    @Type(type="uuid-char")
    private UUID jti;

    private boolean isValid;

    @OneToOne
    private RefreshToken next;

}
