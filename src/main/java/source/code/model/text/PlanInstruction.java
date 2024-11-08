package source.code.model.text;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
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
public class PlanInstruction extends TextBase {
    private String title;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private Plan plan;

    public static PlanInstruction createWithIdAndPlan(int id, Plan plan) {
        PlanInstruction instruction = new PlanInstruction();
        instruction.setId(id);
        instruction.setPlan(plan);
        return instruction;
    }

    public static PlanInstruction createWithNumberTitleText(short number, String title, String text) {
        PlanInstruction instruction = new PlanInstruction();
        instruction.setNumber(number);
        instruction.setTitle(title);
        instruction.setText(text);
        return instruction;
    }
}
