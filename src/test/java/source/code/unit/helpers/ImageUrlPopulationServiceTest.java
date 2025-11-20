package source.code.unit.helpers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.dto.response.activity.ActivitySummaryDto;
import source.code.dto.response.plan.PlanSummaryDto;
import source.code.dto.response.recipe.RecipeSummaryDto;
import source.code.model.media.Media;
import source.code.service.declaration.aws.AwsS3Service;
import source.code.service.implementation.helpers.ImageUrlPopulationServiceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ImageUrlPopulationServiceTest {

    @Mock
    private AwsS3Service s3Service;

    private ImageUrlPopulationServiceImpl imagePopulationService;

    @BeforeEach
    void setUp() {
        imagePopulationService = new ImageUrlPopulationServiceImpl(s3Service);
    }

    @Test
    @DisplayName("populateAuthorAndEntityImagesForList - Should populate URLs for all PlanSummaryDto in list")
    void populateAuthorAndEntityImagesForList_ShouldPopulateUrlsForAllPlanDtos() {
        PlanSummaryDto dto1 = new PlanSummaryDto();
        dto1.setAuthorImageName("author1.jpg");
        dto1.setFirstImageName("plan1.jpg");

        PlanSummaryDto dto2 = new PlanSummaryDto();
        dto2.setAuthorImageName("author2.jpg");
        dto2.setFirstImageName("plan2.jpg");

        PlanSummaryDto dto3 = new PlanSummaryDto();
        dto3.setAuthorImageName(null);
        dto3.setFirstImageName("plan3.jpg");

        List<PlanSummaryDto> dtos = Arrays.asList(dto1, dto2, dto3);

        when(s3Service.getImage("author1.jpg")).thenReturn("https://s3.amazonaws.com/author1.jpg");
        when(s3Service.getImage("plan1.jpg")).thenReturn("https://s3.amazonaws.com/plan1.jpg");
        when(s3Service.getImage("author2.jpg")).thenReturn("https://s3.amazonaws.com/author2.jpg");
        when(s3Service.getImage("plan2.jpg")).thenReturn("https://s3.amazonaws.com/plan2.jpg");
        when(s3Service.getImage("plan3.jpg")).thenReturn("https://s3.amazonaws.com/plan3.jpg");

        imagePopulationService.populateAuthorAndEntityImagesForList(
            dtos,
            PlanSummaryDto::getAuthorImageName,
            PlanSummaryDto::setAuthorImageUrl,
            PlanSummaryDto::getFirstImageName,
            PlanSummaryDto::setFirstImageUrl
        );

        assertEquals("https://s3.amazonaws.com/author1.jpg", dto1.getAuthorImageUrl());
        assertEquals("https://s3.amazonaws.com/plan1.jpg", dto1.getFirstImageUrl());
        assertEquals("https://s3.amazonaws.com/author2.jpg", dto2.getAuthorImageUrl());
        assertEquals("https://s3.amazonaws.com/plan2.jpg", dto2.getFirstImageUrl());
        assertNull(dto3.getAuthorImageUrl());
        assertEquals("https://s3.amazonaws.com/plan3.jpg", dto3.getFirstImageUrl());
        verify(s3Service, times(5)).getImage(anyString());
    }

    @Test
    @DisplayName("populateAuthorAndEntityImagesForList - Should populate URLs for all RecipeSummaryDto in list")
    void populateAuthorAndEntityImagesForList_ShouldPopulateUrlsForAllRecipeDtos() {
        RecipeSummaryDto dto1 = new RecipeSummaryDto();
        dto1.setAuthorImageName("author1.jpg");
        dto1.setFirstImageName("recipe1.jpg");

        RecipeSummaryDto dto2 = new RecipeSummaryDto();
        dto2.setAuthorImageName("author2.jpg");
        dto2.setFirstImageName(null);

        List<RecipeSummaryDto> dtos = Arrays.asList(dto1, dto2);

        when(s3Service.getImage("author1.jpg")).thenReturn("https://s3.amazonaws.com/author1.jpg");
        when(s3Service.getImage("recipe1.jpg")).thenReturn("https://s3.amazonaws.com/recipe1.jpg");
        when(s3Service.getImage("author2.jpg")).thenReturn("https://s3.amazonaws.com/author2.jpg");

        imagePopulationService.populateAuthorAndEntityImagesForList(
            dtos,
            RecipeSummaryDto::getAuthorImageName,
            RecipeSummaryDto::setAuthorImageUrl,
            RecipeSummaryDto::getFirstImageName,
            RecipeSummaryDto::setFirstImageUrl
        );

        assertEquals("https://s3.amazonaws.com/author1.jpg", dto1.getAuthorImageUrl());
        assertEquals("https://s3.amazonaws.com/recipe1.jpg", dto1.getFirstImageUrl());
        assertEquals("https://s3.amazonaws.com/author2.jpg", dto2.getAuthorImageUrl());
        assertNull(dto2.getFirstImageUrl());
        verify(s3Service, times(3)).getImage(anyString());
    }

    @Test
    @DisplayName("populateAuthorAndEntityImagesForList - Should handle empty list")
    void populateAuthorAndEntityImagesForList_ShouldHandleEmptyList() {
        List<PlanSummaryDto> dtos = Arrays.asList();

        imagePopulationService.populateAuthorAndEntityImagesForList(
            dtos,
            PlanSummaryDto::getAuthorImageName,
            PlanSummaryDto::setAuthorImageUrl,
            PlanSummaryDto::getFirstImageName,
            PlanSummaryDto::setFirstImageUrl
        );

        verify(s3Service, never()).getImage(anyString());
    }

    @Test
    @DisplayName("populateAuthorImageForList - Should populate author URLs for all DTOs in list")
    void populateAuthorImageForList_ShouldPopulateAuthorUrlsForAllDtos() {
        PlanSummaryDto dto1 = new PlanSummaryDto();
        dto1.setAuthorImageName("author1.jpg");

        PlanSummaryDto dto2 = new PlanSummaryDto();
        dto2.setAuthorImageName("author2.jpg");

        PlanSummaryDto dto3 = new PlanSummaryDto();
        dto3.setAuthorImageName(null);

        List<PlanSummaryDto> dtos = Arrays.asList(dto1, dto2, dto3);

        when(s3Service.getImage("author1.jpg")).thenReturn("https://s3.amazonaws.com/author1.jpg");
        when(s3Service.getImage("author2.jpg")).thenReturn("https://s3.amazonaws.com/author2.jpg");

        imagePopulationService.populateAuthorImageForList(
            dtos,
            PlanSummaryDto::getAuthorImageName,
            PlanSummaryDto::setAuthorImageUrl
        );

        assertEquals("https://s3.amazonaws.com/author1.jpg", dto1.getAuthorImageUrl());
        assertEquals("https://s3.amazonaws.com/author2.jpg", dto2.getAuthorImageUrl());
        assertNull(dto3.getAuthorImageUrl());
        verify(s3Service, times(2)).getImage(anyString());
    }

    @Test
    @DisplayName("populateAuthorImageForList - Should not call S3 service when all image names are null")
    void populateAuthorImageForList_ShouldNotCallS3WhenAllNamesAreNull() {
        PlanSummaryDto dto1 = new PlanSummaryDto();
        dto1.setAuthorImageName(null);

        PlanSummaryDto dto2 = new PlanSummaryDto();
        dto2.setAuthorImageName(null);

        List<PlanSummaryDto> dtos = Arrays.asList(dto1, dto2);

        imagePopulationService.populateAuthorImageForList(
            dtos,
            PlanSummaryDto::getAuthorImageName,
            PlanSummaryDto::setAuthorImageUrl
        );

        assertNull(dto1.getAuthorImageUrl());
        assertNull(dto2.getAuthorImageUrl());
        verify(s3Service, never()).getImage(anyString());
    }

    @Test
    @DisplayName("populateFirstImageFromMediaList - Should populate image from mediaList")
    void populateFirstImageFromMediaList_ShouldPopulateImageFromMediaList() {
        ActivitySummaryDto dto = new ActivitySummaryDto();

        Media media = mock(Media.class);
        when(media.getImageName()).thenReturn("activity1.jpg");
        List<Media> mediaList = new ArrayList<>();
        mediaList.add(media);

        when(s3Service.getImage("activity1.jpg")).thenReturn("https://s3.amazonaws.com/activity1.jpg");

        imagePopulationService.populateFirstImageFromMediaList(
            List.of(dto),
            d -> mediaList,
            Media::getImageName,
            ActivitySummaryDto::setImageName,
            ActivitySummaryDto::setFirstImageUrl
        );

        assertEquals("activity1.jpg", dto.getImageName());
        assertEquals("https://s3.amazonaws.com/activity1.jpg", dto.getFirstImageUrl());
        verify(s3Service, times(1)).getImage("activity1.jpg");
    }

    @Test
    @DisplayName("populateFirstImageFromMediaList - Should handle empty mediaList")
    void populateFirstImageFromMediaList_ShouldHandleEmptyMediaList() {
        ActivitySummaryDto dto = new ActivitySummaryDto();
        List<Media> mediaList = new ArrayList<>();

        imagePopulationService.populateFirstImageFromMediaList(
            List.of(dto),
            d -> mediaList,
            Media::getImageName,
            ActivitySummaryDto::setImageName,
            ActivitySummaryDto::setFirstImageUrl
        );

        assertNull(dto.getImageName());
        assertNull(dto.getFirstImageUrl());
        verify(s3Service, never()).getImage(anyString());
    }

    @Test
    @DisplayName("populateFirstImageFromMediaList - Should handle null mediaList")
    void populateFirstImageFromMediaList_ShouldHandleNullMediaList() {
        ActivitySummaryDto dto = new ActivitySummaryDto();

        imagePopulationService.populateFirstImageFromMediaList(
            List.of(dto),
            d -> null,
            Media::getImageName,
            ActivitySummaryDto::setImageName,
            ActivitySummaryDto::setFirstImageUrl
        );

        assertNull(dto.getImageName());
        assertNull(dto.getFirstImageUrl());
        verify(s3Service, never()).getImage(anyString());
    }

    @Test
    @DisplayName("populateFirstImageFromMediaList - Should handle null imageName in media")
    void populateFirstImageFromMediaList_ShouldHandleNullImageNameInMedia() {
        ActivitySummaryDto dto = new ActivitySummaryDto();

        Media media = mock(Media.class);
        when(media.getImageName()).thenReturn(null);
        List<Media> mediaList = new ArrayList<>();
        mediaList.add(media);

        imagePopulationService.populateFirstImageFromMediaList(
            List.of(dto),
            d -> mediaList,
            Media::getImageName,
            ActivitySummaryDto::setImageName,
            ActivitySummaryDto::setFirstImageUrl
        );

        assertNull(dto.getImageName());
        assertNull(dto.getFirstImageUrl());
        verify(s3Service, never()).getImage(anyString());
    }

    @Test
    @DisplayName("populateFirstImageFromMediaList - Should populate for multiple DTOs")
    void populateFirstImageFromMediaList_ShouldPopulateForMultipleDtos() {
        ActivitySummaryDto dto1 = new ActivitySummaryDto();
        ActivitySummaryDto dto2 = new ActivitySummaryDto();

        Media media1 = mock(Media.class);
        when(media1.getImageName()).thenReturn("activity1.jpg");
        List<Media> mediaList1 = new ArrayList<>();
        mediaList1.add(media1);

        Media media2 = mock(Media.class);
        when(media2.getImageName()).thenReturn("activity2.jpg");
        List<Media> mediaList2 = new ArrayList<>();
        mediaList2.add(media2);

        when(s3Service.getImage("activity1.jpg")).thenReturn("https://s3.amazonaws.com/activity1.jpg");
        when(s3Service.getImage("activity2.jpg")).thenReturn("https://s3.amazonaws.com/activity2.jpg");

        imagePopulationService.populateFirstImageFromMediaList(
                List.of(dto1),
            d -> mediaList1,
            Media::getImageName,
            ActivitySummaryDto::setImageName,
            ActivitySummaryDto::setFirstImageUrl
        );

        imagePopulationService.populateFirstImageFromMediaList(
                List.of(dto2),
            d -> mediaList2,
            Media::getImageName,
            ActivitySummaryDto::setImageName,
            ActivitySummaryDto::setFirstImageUrl
        );

        assertEquals("activity1.jpg", dto1.getImageName());
        assertEquals("https://s3.amazonaws.com/activity1.jpg", dto1.getFirstImageUrl());
        assertEquals("activity2.jpg", dto2.getImageName());
        assertEquals("https://s3.amazonaws.com/activity2.jpg", dto2.getFirstImageUrl());
        verify(s3Service, times(2)).getImage(anyString());
    }
}
