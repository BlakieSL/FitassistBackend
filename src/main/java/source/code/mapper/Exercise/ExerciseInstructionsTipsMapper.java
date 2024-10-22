package source.code.mapper.Exercise;


import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import source.code.dto.request.Exercise.ExerciseInstructionUpdateDto;
import source.code.dto.request.Exercise.ExerciseTipUpdateDto;
import source.code.dto.response.ExerciseInstructionResponseDto;
import source.code.dto.response.ExerciseTipResponseDto;
import source.code.model.Exercise.ExerciseInstruction;
import source.code.model.Exercise.ExerciseTip;

@Mapper(componentModel = "spring")
public abstract class ExerciseInstructionsTipsMapper {
  public abstract ExerciseInstructionResponseDto toInstructionResponseDto(ExerciseInstruction instruction);
  public abstract ExerciseTipResponseDto toTipResponseDto(ExerciseTip tip);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  public abstract void updateInstruction(@MappingTarget ExerciseInstruction instruction,
                                         ExerciseInstructionUpdateDto dto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  public abstract void updateTip(@MappingTarget ExerciseTip tip,
                                 ExerciseTipUpdateDto dto);
}
