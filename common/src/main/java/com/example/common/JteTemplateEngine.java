package com.example.common;

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
        return render("sqsevent", Map.of(
            "messageId", messageId,
            "body", body,
            "timestamp", timestamp
        ));
    }

    public String renderTransaction(String id, double amount, String description, String timestamp) {
        return render("transaction", Map.of(
            "id", id,
            "amount", amount,
            "description", description,
            "timestamp", timestamp
        ));
    }

    public String renderS3Notification(String bucketName, String objectKey, String eventTime) {
        return render("s3notification", Map.of(
            "bucketName", bucketName,
            "objectKey", objectKey,
            "eventTime", eventTime
        ));
    }
}
