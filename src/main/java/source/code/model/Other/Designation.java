package source.code.model.Other;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "designation")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Designation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(nullable = false)
    private String tableName;

    @NotNull
    @Column(nullable = false)
    private String columnName;

    @NotNull
    @Column(nullable = false)
    private short shortValue;

    @NotNull
    @Column(nullable = false)
    private String description;
}
