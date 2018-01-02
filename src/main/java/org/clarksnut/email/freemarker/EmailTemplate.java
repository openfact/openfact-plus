package org.clarksnut.email.freemarker;

public class EmailTemplate {

    private String subject;
    private String textBody;
    private String htmlBody;

    public EmailTemplate(String subject, String textBody, String htmlBody) {
        this.subject = subject;
        this.textBody = textBody;
        this.htmlBody = htmlBody;
    }

    public String getSubject() {
        return subject;
    }

    public String getTextBody() {
        return textBody;
    }

    public String getHtmlBody() {
        return htmlBody;
    }

}
