package source.code.model.text;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.model.plan.Plan;

@Entity
@DiscriminatorValue("PLAN_INSTRUCTION")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PlanInstruction extends TextBase {
    private String title;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private Plan plan;

    public static PlanInstruction of(short number, String title, String text, Plan plan) {
        PlanInstruction instruction = new PlanInstruction();
        instruction.setOrderIndex(number);
        instruction.setTitle(title);
        instruction.setText(text);
        instruction.setPlan(plan);
        return instruction;
    }

    public static PlanInstruction of(Integer id, Plan plan) {
        PlanInstruction instruction = new PlanInstruction();
        instruction.setId(id);
        instruction.setPlan(plan);
        return instruction;
    }
}
