package source.code.model.daily;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.model.user.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "daily_cart")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DailyCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(nullable = false)
    private LocalDate date;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "dailyCart",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            orphanRemoval = true)
    private final List<DailyActivityItem> dailyActivityItems = new ArrayList<>();

    @OneToMany(mappedBy = "dailyCart",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            orphanRemoval = true)
    private final List<DailyFoodItem> dailyFoodItems = new ArrayList<>();

    public static DailyCart createForToday(User user) {
        DailyCart dailyCart = new DailyCart();
        dailyCart.setDate(LocalDate.now());
        dailyCart.setUser(user);
        return dailyCart;
    }
}
