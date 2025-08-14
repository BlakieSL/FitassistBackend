package source.code.helper.email;

public enum EmailTemplate {
    WELCOME("welcome", "Welcome to FitAssist!"),
    PASSWORD_RESET("password-reset", "Reset Your Password"),
    NOTIFICATION("notification", "You have a new notification"),
    ACCOUNT_VERIFICATION("account-verification", "Verify Your Account"),
    WORKOUT_REMINDER("workout-reminder", "Time for Your Workout!"),
    PLAN_SHARED("plan-shared", "A Workout Plan Has Been Shared With You");

    private final String templateName;
    private final String defaultSubject;

    EmailTemplate(String templateName, String defaultSubject) {
        this.templateName = templateName;
        this.defaultSubject = defaultSubject;
    }

    public String getTemplateName() {
        return templateName;
    }

    public String getDefaultSubject() {
        return defaultSubject;
    }
}