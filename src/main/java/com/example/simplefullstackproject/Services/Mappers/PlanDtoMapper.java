package com.example.simplefullstackproject.Services.Mappers;

import com.example.simplefullstackproject.Dtos.PlanDto;
import com.example.simplefullstackproject.Models.Plan;
import org.springframework.stereotype.Service;

@Service
public class PlanDtoMapper {
    public PlanDto map(Plan plan){
        return new PlanDto(
                plan.getId(),
                plan.getName(),
                plan.getDescription(),
                plan.getText()
        );
    }

    public Plan map(PlanDto request){
        Plan plan = new Plan();
        plan.setName(request.getName());
        plan.setDescription(request.getDescription());
        plan.setText(request.getText());
        return plan;
    }
}
