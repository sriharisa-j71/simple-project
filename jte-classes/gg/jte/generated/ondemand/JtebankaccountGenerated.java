package gg.jte.generated.ondemand;
public final class JtebankaccountGenerated {
	public static final String JTE_NAME = "bankaccount.jte";
	public static final int[] JTE_LINE_INFO = {0,0,0,0,2,2,2,3,3,4,4,6,6,7,7,8,8,11,11,11,0,0,0,0};
	public static void render(gg.jte.TemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, com.example.common.models.BankAccount bankAccount) {
		jteOutput.writeContent("{\n  \"accountNumber\": \"");
		jteOutput.writeUserContent(bankAccount.accountNumber());
		jteOutput.writeContent("\",\n  \"balance\": ");
		jteOutput.writeUserContent(bankAccount.balance());
		jteOutput.writeContent(",\n  \"accountType\": \"");
		jteOutput.writeUserContent(bankAccount.accountType());
		jteOutput.writeContent("\",\n  \"accountHolder\": {\n    \"name\": \"");
		jteOutput.writeUserContent(bankAccount.accountHolder().name());
		jteOutput.writeContent("\",\n    \"email\": \"");
		jteOutput.writeUserContent(bankAccount.accountHolder().email());
		jteOutput.writeContent("\",\n    \"age\": ");
		jteOutput.writeUserContent(bankAccount.accountHolder().age());
		jteOutput.writeContent("\n  }\n}\n");
	}
	public static void renderMap(gg.jte.TemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		com.example.common.models.BankAccount bankAccount = (com.example.common.models.BankAccount)params.get("bankAccount");
		render(jteOutput, jteHtmlInterceptor, bankAccount);
	}
}
