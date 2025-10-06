package io.github.followsclosely.warehouse.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import org.javers.core.metamodel.annotation.ValueObject;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ValueObject
@ToString(of = {"id", "name"}, includeFieldNames = false)
public class LegoSet {
    @Id
    private String id;
    private String name;
    private int releaseYear;
    @ManyToOne
    @JoinColumn(name = "theme_id")
    private LegoTheme legoTheme;
    private int partCount;
    private String imageUrl;
}
