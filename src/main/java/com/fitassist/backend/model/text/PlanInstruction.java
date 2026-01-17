package com.fitassist.backend.model.text;

import com.fitassist.backend.model.plan.Plan;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("PLAN_INSTRUCTION")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlanInstruction extends TextBase {

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
