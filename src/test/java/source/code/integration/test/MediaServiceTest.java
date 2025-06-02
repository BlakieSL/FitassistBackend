package source.code.integration.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import source.code.integration.containers.MySqlRedisContainers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;


@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
public class MediaServiceTest extends MySqlRedisContainers {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


}
