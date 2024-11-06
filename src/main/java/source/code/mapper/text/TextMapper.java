package source.code.mapper.text;


import org.mapstruct.*;
import source.code.dto.Request.text.ExerciseInstructionUpdateDto;
import source.code.dto.Request.text.ExerciseTipUpdateDto;
import source.code.dto.Request.text.PlanInstructionUpdateDto;
import source.code.dto.Request.text.RecipeInstructionUpdateDto;
import source.code.dto.Response.text.ExerciseInstructionResponseDto;
import source.code.dto.Response.text.ExerciseTipResponseDto;
import source.code.dto.Response.text.PlanInstructionResponseDto;
import source.code.dto.Response.text.RecipeInstructionResponseDto;
import source.code.model.text.ExerciseInstruction;
import source.code.model.text.ExerciseTip;
import source.code.model.text.PlanInstruction;
import source.code.model.text.RecipeInstruction;

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
