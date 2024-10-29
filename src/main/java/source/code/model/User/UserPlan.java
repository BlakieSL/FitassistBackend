package source.code.model.User;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.model.Plan.Plan;

@Entity
@Table(name = "user_plan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPlan implements BaseUserEntity{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "plan_id", nullable = false)
  private Plan plan;

  @NotNull
  @Column(nullable = false)
  private short type;

  public static UserPlan createWithUserPlanType(
          User user, Plan plan, short type) {

    UserPlan userPlan = new UserPlan();
    userPlan.setUser(user);
    userPlan.setPlan(plan);
    userPlan.setType(type);

    return userPlan;
  }
}
