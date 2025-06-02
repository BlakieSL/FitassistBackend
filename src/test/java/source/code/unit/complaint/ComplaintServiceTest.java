package source.code.unit.complaint;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import source.code.dto.request.complaint.ComplaintCreateDto;
import source.code.exception.RecordNotFoundException;
import source.code.helper.Enum.model.ComplaintSubClass;
import source.code.helper.user.AuthorizationUtil;
import source.code.mapper.complaint.ComplaintMapper;
import source.code.model.forum.CommentComplaint;
import source.code.model.forum.ComplaintBase;
import source.code.model.forum.ThreadComplaint;
import source.code.repository.CommentComplaintRepository;
import source.code.repository.ThreadComplaintRepository;
import source.code.service.implementation.complaint.ComplaintServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ComplaintServiceTest {
    private static final int USER_ID = 1;
    private static final int COMPLAINT_ID = 1;
    private static final int INVALID_COMPLAINT_ID = 999;

    @Mock
    private ComplaintMapper complaintMapper;
    @Mock
    private CommentComplaintRepository commentComplaintRepository;
    @Mock
    private ThreadComplaintRepository threadComplaintRepository;
    @InjectMocks
    private ComplaintServiceImpl complaintService;

    private ComplaintCreateDto createDto;
    private CommentComplaint commentComplaint;
    private ThreadComplaint threadComplaint;
    private MockedStatic<AuthorizationUtil> mockedAuthorizationUtil;

    @BeforeEach
    void setUp() {
        createDto = new ComplaintCreateDto();
        commentComplaint = new CommentComplaint();
        threadComplaint = new ThreadComplaint();
        mockedAuthorizationUtil = mockStatic(AuthorizationUtil.class);
    }

    @AfterEach
    void tearDown() {
        if (mockedAuthorizationUtil != null) {
            mockedAuthorizationUtil.close();
        }
    }

    @Test
    void createComplaint_shouldCreateCommentComplaint() {
        createDto.setSubClass(ComplaintSubClass.COMMENT_COMPLAINT);
        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
        when(complaintMapper.toCommentComplaint(createDto, USER_ID)).thenReturn(commentComplaint);

        complaintService.createComplaint(createDto);

        verify(commentComplaintRepository).save(commentComplaint);
    }

    @Test
    void createComplaint_shouldCreateThreadComplaint() {
        createDto.setSubClass(ComplaintSubClass.THREAD_COMPLAINT);
        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
        when(complaintMapper.toThreadComplaint(createDto, USER_ID)).thenReturn(threadComplaint);

        complaintService.createComplaint(createDto);

        verify(threadComplaintRepository).save(threadComplaint);
    }

    @Test
    void resolveComplaint_shouldResolveCommentComplaint() {
        when(commentComplaintRepository.findById(COMPLAINT_ID))
                .thenReturn(Optional.of(commentComplaint));

        complaintService.resolveComplaint(COMPLAINT_ID);

        assertEquals(ComplaintBase.Status.RESOLVED, commentComplaint.getStatus());
        verify(commentComplaintRepository).findById(COMPLAINT_ID);
    }

    @Test
    void resolveComplaint_shouldThrowExceptionWhenComplaintNotFound() {
        when(commentComplaintRepository.findById(INVALID_COMPLAINT_ID))
                .thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> complaintService
                .resolveComplaint(INVALID_COMPLAINT_ID));
        verify(commentComplaintRepository).findById(INVALID_COMPLAINT_ID);
    }
}