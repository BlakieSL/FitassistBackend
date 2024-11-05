package source.code.mapper.text;


import org.mapstruct.*;
import source.code.dto.Request.Text.ExerciseInstructionUpdateDto;
import source.code.dto.Request.Text.ExerciseTipUpdateDto;
import source.code.dto.Request.Text.PlanInstructionUpdateDto;
import source.code.dto.Request.Text.RecipeInstructionUpdateDto;
import source.code.dto.Response.Text.ExerciseInstructionResponseDto;
import source.code.dto.Response.Text.ExerciseTipResponseDto;
import source.code.dto.Response.Text.PlanInstructionResponseDto;
import source.code.dto.Response.Text.RecipeInstructionResponseDto;
import source.code.model.Text.ExerciseInstruction;
import source.code.model.Text.ExerciseTip;
import source.code.model.Text.PlanInstruction;
import source.code.model.Text.RecipeInstruction;

@Mapper(componentModel = "spring")
public abstract class TextMapper {
    public abstract ExerciseInstructionResponseDto toExerciseInstructionResponseDto(ExerciseInstruction instruction);

    public abstract ExerciseTipResponseDto toExerciseTipResponseDto(ExerciseTip tip);

    public abstract RecipeInstructionResponseDto toRecipeInstructionResponseDto(RecipeInstruction instruction);

    public abstract PlanInstructionResponseDto toPlanInstructionResponseDto(PlanInstruction instruction);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "exercise", ignore = true)
    public abstract void updateExerciseInstruction(@MappingTarget ExerciseInstruction instruction,
                                                   ExerciseInstructionUpdateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "exercise", ignore = true)
    public abstract void updateExerciseTip(@MappingTarget ExerciseTip tip,
                                           ExerciseTipUpdateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "recipe", ignore = true)
    public abstract void updateRecipeInstruction(@MappingTarget RecipeInstruction instruction,
                                                 RecipeInstructionUpdateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "plan", ignore = true)
    public abstract void updatePlanInstruction(@MappingTarget PlanInstruction instruction,
                                               PlanInstructionUpdateDto dto);
}
