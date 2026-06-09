package com.example.common;

import com.example.common.models.BankAccount;
import com.example.common.models.Person;
import com.example.common.models.Transaction;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.TemplateOutput;
import gg.jte.output.StringOutput;
import gg.jte.resolve.DirectoryCodeResolver;

import java.nio.file.Path;
import java.util.Map;

public final class JteTemplateEngine {

    private final TemplateEngine engine;

    private static final String ENV_VAR = "JTE_TEMPLATES";

    public JteTemplateEngine() {
        var path = System.getenv(ENV_VAR);
        if (path == null || path.isBlank()) {
            throw new IllegalStateException(
                "Environment variable " + ENV_VAR + " must be set to an absolute path (e.g. /home/user/project/common/src/main/jte)"
            );
        }
        this.engine = TemplateEngine.create(
            new DirectoryCodeResolver(Path.of(path)),
            ContentType.Plain
        );
    }

    public JteTemplateEngine(Path templateRoot) {
        this.engine = TemplateEngine.create(
            new DirectoryCodeResolver(templateRoot),
            ContentType.Plain
        );
    }

    public String render(String templateName, Map<String, Object> params) {
        TemplateOutput output = new StringOutput();
        engine.render(templateName, params, output);
        return output.toString();
    }

    public String renderSqsEvent(String messageId, String body, String timestamp) {
        return render("sqsevent.jte", Map.of(
            "messageId", messageId,
            "body", body,
            "timestamp", timestamp
        ));
    }

    public String renderTransaction(String id, double amount, String description, String timestamp) {
        return render("transaction.jte", Map.of(
            "id", id,
            "amount", amount,
            "description", description,
            "timestamp", timestamp
        ));
    }

    public String renderS3Notification(String bucketName, String objectKey, String eventTime) {
        return render("s3notification.jte", Map.of(
            "bucketName", bucketName,
            "objectKey", objectKey,
            "eventTime", eventTime
        ));
    }

    public String renderPerson(Person person) {
        return render("person.jte", Map.of("person", person));
    }

    public String renderBankAccount(BankAccount bankAccount) {
        return render("bankaccount.jte", Map.of("bankAccount", bankAccount));
    }

    public String renderDomainTransaction(Transaction transaction) {
        return render("domain_transaction.jte", Map.of("transaction", transaction));
    }

    public String renderDomainSqsEvent(String messageId, String body, String timestamp) {
        return render("domain_sqs_event.jte", Map.of(
            "messageId", messageId,
            "body", body,
            "timestamp", timestamp
        ));
    }
}
