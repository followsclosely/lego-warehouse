package io.github.followsclosely.warehouse.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class LegoColor {
    @Id
    private String id;
    private String name;
    private String rgb;
    private Boolean transparent;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "lego_color_provider", joinColumns = @JoinColumn(name = "color_id"))
    private List<LegoColorProvider> providers = new ArrayList<>();

}
