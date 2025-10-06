package io.github.followsclosely.warehouse.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.javers.core.metamodel.annotation.ValueObject;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ValueObject
@ToString(of = {"id", "name"}, includeFieldNames = false)
public class LegoTheme {
    @Id
    private String id;
    private String name;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private LegoTheme parent;

    @Transient
    @Builder.Default
    private List<LegoTheme> children = new ArrayList<>();
}
