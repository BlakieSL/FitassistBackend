package source.code.mapper.Text;


import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import source.code.dto.request.Text.ExerciseInstructionUpdateDto;
import source.code.dto.request.Text.ExerciseTipUpdateDto;
import source.code.dto.request.Text.PlanInstructionUpdateDto;
import source.code.dto.request.Text.RecipeInstructionUpdateDto;
import source.code.dto.response.Text.ExerciseInstructionResponseDto;
import source.code.dto.response.Text.ExerciseTipResponseDto;
import source.code.dto.response.Text.PlanInstructionResponseDto;
import source.code.dto.response.Text.RecipeInstructionResponseDto;
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
  public abstract void updateExerciseInstruction(@MappingTarget ExerciseInstruction instruction,
                                                 ExerciseInstructionUpdateDto dto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  public abstract void updateExerciseTip(@MappingTarget ExerciseTip tip,
                                         ExerciseTipUpdateDto dto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  public abstract void updateRecipeInstruction(@MappingTarget RecipeInstruction instruction,
                                               RecipeInstructionUpdateDto dto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  public abstract void updatePlanInstruction(@MappingTarget PlanInstruction instruction,
                                             PlanInstructionUpdateDto dto);
}
