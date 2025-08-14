package source.code.helper.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailTemplateData {
    private String recipientName;
    private String recipientEmail;
    private Map<String, Object> variables;
    
    public static EmailTemplateData of(String recipientName, String recipientEmail) {
        return new EmailTemplateData(recipientName, recipientEmail, Map.of());
    }
    
    public static EmailTemplateData of(String recipientName, String recipientEmail, Map<String, Object> variables) {
        return new EmailTemplateData(recipientName, recipientEmail, variables);
    }
}