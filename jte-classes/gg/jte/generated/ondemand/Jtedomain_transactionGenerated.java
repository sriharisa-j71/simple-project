package gg.jte.generated.ondemand;
public final class Jtedomain_transactionGenerated {
	public static final String JTE_NAME = "domain_transaction.jte";
	public static final int[] JTE_LINE_INFO = {0,0,0,0,2,2,2,3,3,4,4,5,5,6,6,8,8,9,9,10,10,12,12,13,13,14,14,18,18,18,0,0,0,0};
	public static void render(gg.jte.TemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, com.example.common.models.Transaction transaction) {
		jteOutput.writeContent("{\n  \"id\": \"");
		jteOutput.writeUserContent(transaction.id());
		jteOutput.writeContent("\",\n  \"amount\": ");
		jteOutput.writeUserContent(transaction.amount());
		jteOutput.writeContent(",\n  \"description\": \"");
		jteOutput.writeUserContent(transaction.description());
		jteOutput.writeContent("\",\n  \"timestamp\": \"");
		jteOutput.writeUserContent(transaction.timestamp());
		jteOutput.writeContent("\",\n  \"merchant\": \"");
		jteOutput.writeUserContent(transaction.merchant());
		jteOutput.writeContent("\",\n  \"account\": {\n    \"accountNumber\": \"");
		jteOutput.writeUserContent(transaction.account().accountNumber());
		jteOutput.writeContent("\",\n    \"balance\": ");
		jteOutput.writeUserContent(transaction.account().balance());
		jteOutput.writeContent(",\n    \"accountType\": \"");
		jteOutput.writeUserContent(transaction.account().accountType());
		jteOutput.writeContent("\",\n    \"accountHolder\": {\n      \"name\": \"");
		jteOutput.writeUserContent(transaction.account().accountHolder().name());
		jteOutput.writeContent("\",\n      \"email\": \"");
		jteOutput.writeUserContent(transaction.account().accountHolder().email());
		jteOutput.writeContent("\",\n      \"age\": ");
		jteOutput.writeUserContent(transaction.account().accountHolder().age());
		jteOutput.writeContent("\n    }\n  }\n}\n");
	}
	public static void renderMap(gg.jte.TemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		com.example.common.models.Transaction transaction = (com.example.common.models.Transaction)params.get("transaction");
		render(jteOutput, jteHtmlInterceptor, transaction);
	}
}
