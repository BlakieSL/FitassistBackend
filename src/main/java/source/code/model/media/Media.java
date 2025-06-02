package source.code.model.media;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.helper.Enum.model.MediaConnectedEntity;

@Entity
@Table(name = "media")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(nullable = false)
    private String imageName;

    @NotNull
    @Enumerated(EnumType.STRING)
    private MediaConnectedEntity parentType;

    @NotNull
    @Column(name = "parent_id", nullable = false)
    private Integer parentId;

    public static Media of (Integer id, MediaConnectedEntity parentType, Integer parentId) {
        Media media = new Media();
        media.setId(id);
        media.setParentType(parentType);
        media.setParentId(parentId);
        return media;
    }
}
