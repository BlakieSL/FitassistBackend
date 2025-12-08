package source.code.unit.helpers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.dto.response.activity.ActivitySummaryDto;
import source.code.dto.response.plan.PlanSummaryDto;
import source.code.model.media.Media;
import source.code.service.declaration.aws.AwsS3Service;
import source.code.service.implementation.helpers.ImageUrlPopulationServiceImpl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ImageUrlPopulationServiceTest {

    @Mock
    private AwsS3Service s3Service;

    @InjectMocks
    private ImageUrlPopulationServiceImpl imagePopulationService;

    @Test
    void populateAuthorAndEntityImages_ShouldPopulateUrlsForSingleDto() {
        PlanSummaryDto dto = new PlanSummaryDto();
        dto.setAuthorImageName("author.jpg");
        dto.setFirstImageName("plan.jpg");

        when(s3Service.getImage("author.jpg")).thenReturn("https://s3.amazonaws.com/author.jpg");
        when(s3Service.getImage("plan.jpg")).thenReturn("https://s3.amazonaws.com/plan.jpg");

        imagePopulationService.populateAuthorAndEntityImages(
            dto,
            PlanSummaryDto::getAuthorImageName,
            PlanSummaryDto::setAuthorImageUrl,
            PlanSummaryDto::getFirstImageName,
            PlanSummaryDto::setFirstImageUrl
        );

        assertEquals("https://s3.amazonaws.com/author.jpg", dto.getAuthorImageUrl());
        assertEquals("https://s3.amazonaws.com/plan.jpg", dto.getFirstImageUrl());
        verify(s3Service, times(2)).getImage(anyString());
    }

    @Test
    void populateAuthorAndEntityImages_ShouldHandleNullAuthorImageName() {
        PlanSummaryDto dto = new PlanSummaryDto();
        dto.setAuthorImageName(null);
        dto.setFirstImageName("plan.jpg");

        when(s3Service.getImage("plan.jpg")).thenReturn("https://s3.amazonaws.com/plan.jpg");

        imagePopulationService.populateAuthorAndEntityImages(
            dto,
            PlanSummaryDto::getAuthorImageName,
            PlanSummaryDto::setAuthorImageUrl,
            PlanSummaryDto::getFirstImageName,
            PlanSummaryDto::setFirstImageUrl
        );

        assertNull(dto.getAuthorImageUrl());
        assertEquals("https://s3.amazonaws.com/plan.jpg", dto.getFirstImageUrl());
        verify(s3Service, times(1)).getImage(anyString());
    }

    @Test
    void populateAuthorAndEntityImages_ShouldHandleNullEntityImageName() {
        PlanSummaryDto dto = new PlanSummaryDto();
        dto.setAuthorImageName("author.jpg");
        dto.setFirstImageName(null);

        when(s3Service.getImage("author.jpg")).thenReturn("https://s3.amazonaws.com/author.jpg");

        imagePopulationService.populateAuthorAndEntityImages(
            dto,
            PlanSummaryDto::getAuthorImageName,
            PlanSummaryDto::setAuthorImageUrl,
            PlanSummaryDto::getFirstImageName,
            PlanSummaryDto::setFirstImageUrl
        );

        assertEquals("https://s3.amazonaws.com/author.jpg", dto.getAuthorImageUrl());
        assertNull(dto.getFirstImageUrl());
        verify(s3Service, times(1)).getImage(anyString());
    }

    @Test
    void populateAuthorImage_ShouldPopulateAuthorUrl() {
        PlanSummaryDto dto = new PlanSummaryDto();
        dto.setAuthorImageName("author.jpg");

        when(s3Service.getImage("author.jpg")).thenReturn("https://s3.amazonaws.com/author.jpg");

        imagePopulationService.populateAuthorImage(
            dto,
            PlanSummaryDto::getAuthorImageName,
            PlanSummaryDto::setAuthorImageUrl
        );

        assertEquals("https://s3.amazonaws.com/author.jpg", dto.getAuthorImageUrl());
        verify(s3Service, times(1)).getImage("author.jpg");
    }

    @Test
    void populateAuthorImage_ShouldNotCallS3WhenNameIsNull() {
        PlanSummaryDto dto = new PlanSummaryDto();
        dto.setAuthorImageName(null);

        imagePopulationService.populateAuthorImage(
            dto,
            PlanSummaryDto::getAuthorImageName,
            PlanSummaryDto::setAuthorImageUrl
        );

        assertNull(dto.getAuthorImageUrl());
        verify(s3Service, never()).getImage(anyString());
    }

    @Test
    void populateFirstImageFromMediaList_ShouldPopulateImageFromMediaList() {
        ActivitySummaryDto dto = new ActivitySummaryDto();

        Media media = mock(Media.class);
        when(media.getImageName()).thenReturn("activity1.jpg");
        List<Media> mediaList = new ArrayList<>();
        mediaList.add(media);

        when(s3Service.getImage("activity1.jpg")).thenReturn("https://s3.amazonaws.com/activity1.jpg");

        imagePopulationService.populateFirstImageFromMediaList(
            dto,
            mediaList,
            Media::getImageName,
            ActivitySummaryDto::setImageName,
            ActivitySummaryDto::setFirstImageUrl
        );

        assertEquals("activity1.jpg", dto.getImageName());
        assertEquals("https://s3.amazonaws.com/activity1.jpg", dto.getFirstImageUrl());
        verify(s3Service, times(1)).getImage("activity1.jpg");
    }

    @Test
    void populateFirstImageFromMediaList_ShouldHandleEmptyMediaList() {
        ActivitySummaryDto dto = new ActivitySummaryDto();
        List<Media> mediaList = new ArrayList<>();

        imagePopulationService.populateFirstImageFromMediaList(
            dto,
            mediaList,
            Media::getImageName,
            ActivitySummaryDto::setImageName,
            ActivitySummaryDto::setFirstImageUrl
        );

        assertNull(dto.getImageName());
        assertNull(dto.getFirstImageUrl());
        verify(s3Service, never()).getImage(anyString());
    }

    @Test
    void populateFirstImageFromMediaList_ShouldHandleNullMediaList() {
        ActivitySummaryDto dto = new ActivitySummaryDto();

        imagePopulationService.populateFirstImageFromMediaList(
            dto,
            null,
            Media::getImageName,
            ActivitySummaryDto::setImageName,
            ActivitySummaryDto::setFirstImageUrl
        );

        assertNull(dto.getImageName());
        assertNull(dto.getFirstImageUrl());
        verify(s3Service, never()).getImage(anyString());
    }

    @Test
    void populateFirstImageFromMediaList_ShouldHandleNullImageNameInMedia() {
        ActivitySummaryDto dto = new ActivitySummaryDto();

        Media media = mock(Media.class);
        when(media.getImageName()).thenReturn(null);
        List<Media> mediaList = new ArrayList<>();
        mediaList.add(media);

        imagePopulationService.populateFirstImageFromMediaList(
            dto,
            mediaList,
            Media::getImageName,
            ActivitySummaryDto::setImageName,
            ActivitySummaryDto::setFirstImageUrl
        );

        assertNull(dto.getImageName());
        assertNull(dto.getFirstImageUrl());
        verify(s3Service, never()).getImage(anyString());
    }

    @Test
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
            dto1,
            mediaList1,
            Media::getImageName,
            ActivitySummaryDto::setImageName,
            ActivitySummaryDto::setFirstImageUrl
        );

        imagePopulationService.populateFirstImageFromMediaList(
            dto2,
            mediaList2,
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
