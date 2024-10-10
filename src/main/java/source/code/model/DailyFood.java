package source.code.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "daily_food")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DailyFood {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(nullable = false)
    private LocalDate date;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "dailyFoodFood",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            orphanRemoval = true)
    @JsonManagedReference
    private final List<DailyFoodItem> dailyFoodItems = new ArrayList<>();

    public static DailyFood createForToday(User user) {
        DailyFood dailyFood = new DailyFood();
        dailyFood.setDate(LocalDate.now());
        dailyFood.setUser(user);
        return dailyFood;
    }
}
