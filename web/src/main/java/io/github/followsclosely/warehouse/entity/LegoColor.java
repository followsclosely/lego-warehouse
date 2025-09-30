package io.github.followsclosely.warehouse.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class LegoColor {
    @Id
    private String id;
    private String name;
    private String rgb;
    private Boolean transparent;
}
