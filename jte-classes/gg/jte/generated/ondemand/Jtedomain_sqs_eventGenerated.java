package gg.jte.generated.ondemand;
public final class Jtedomain_sqs_eventGenerated {
	public static final String JTE_NAME = "domain_sqs_event.jte";
	public static final int[] JTE_LINE_INFO = {0,0,0,0,6,6,6,7,7,8,8,14,14,14,0,1,2,2,2,2};
	public static void render(gg.jte.TemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, String messageId, String body, String timestamp) {
		jteOutput.writeContent("{\n  \"Records\": [\n    {\n      \"messageId\": \"");
		jteOutput.writeUserContent(messageId);
		jteOutput.writeContent("\",\n      \"body\": ");
		jteOutput.writeUserContent(body);
		jteOutput.writeContent(",\n      \"timestamp\": \"");
		jteOutput.writeUserContent(timestamp);
		jteOutput.writeContent("\",\n      \"eventSource\": \"aws:sqs\",\n      \"eventSourceARN\": \"arn:aws:sqs:us-east-1:000000000000:domain-event-queue\"\n    }\n  ]\n}\n");
	}
	public static void renderMap(gg.jte.TemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		String messageId = (String)params.get("messageId");
		String body = (String)params.get("body");
		String timestamp = (String)params.get("timestamp");
		render(jteOutput, jteHtmlInterceptor, messageId, body, timestamp);
	}
}
