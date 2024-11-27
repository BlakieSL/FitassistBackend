package source.code.service.rateLimiter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import source.code.config.RedissonRateLimitConfig;
import source.code.service.implementation.rateLimiter.RedissonRateLimiterServiceImpl;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RedissonRateLimiterServiceTest {

    @Mock
    private RedissonClient redissonClient;
    @Mock
    private RedissonRateLimitConfig rateLimitConfig;
    @Mock
    private RRateLimiter rateLimiter;

    @InjectMocks
    private RedissonRateLimiterServiceImpl rateLimiterService;

    private int userId;
    private String key;

    @BeforeEach
    void setUp() {
        userId = 1;
        key = "testKey";
    }

    @Test
    void isAllowed_shouldReturnTrueWhenRateLimiterAllows() {
        when(redissonClient.getRateLimiter(anyString())).thenReturn(rateLimiter);
        when(rateLimiter.tryAcquire(1)).thenReturn(true);

        assertTrue(rateLimiterService.isAllowed(userId));
        assertTrue(rateLimiterService.isAllowed(key));
    }

    @Test
    void isAllowed_shouldReturnFalseWhenRateLimiterDoesNotAllow() {
        when(redissonClient.getRateLimiter(anyString())).thenReturn(rateLimiter);
        when(rateLimiter.tryAcquire(1)).thenReturn(false);

        assertFalse(rateLimiterService.isAllowed(userId));
        assertFalse(rateLimiterService.isAllowed(key));
    }

    @Test
    void createRateLimiterForUserId_shouldSetRateCorrectly() {
        when(redissonClient.getRateLimiter(anyString())).thenReturn(rateLimiter);
        when(rateLimitConfig.getUserRate()).thenReturn(8);
        when(rateLimitConfig.getUserInterval()).thenReturn(1);

        rateLimiterService.isAllowed(userId);

        verify(rateLimiter).trySetRate(
                eq(RateType.OVERALL),
                eq(8L),
                eq(1L),
                eq(RateIntervalUnit.MINUTES)
        );
    }

    @Test
    void createRateLimiterForKey_shouldSetRateCorrectly() {
        when(redissonClient.getRateLimiter(anyString())).thenReturn(rateLimiter);
        when(rateLimitConfig.getKeyRate()).thenReturn(5);
        when(rateLimitConfig.getKeyInterval()).thenReturn(1);

        rateLimiterService.isAllowed(key);

        verify(rateLimiter).trySetRate(
                eq(RateType.OVERALL),
                eq(5L),
                eq(1L),
                eq(RateIntervalUnit.MINUTES)
        );
    }
}