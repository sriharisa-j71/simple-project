package gg.jte.generated.ondemand;
public final class JtepersonGenerated {
	public static final String JTE_NAME = "person.jte";
	public static final int[] JTE_LINE_INFO = {0,0,0,0,2,2,2,3,3,4,4,6,6,6,0,0,0,0};
	public static void render(gg.jte.TemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, com.example.common.models.Person person) {
		jteOutput.writeContent("{\n  \"name\": \"");
		jteOutput.writeUserContent(person.name());
		jteOutput.writeContent("\",\n  \"email\": \"");
		jteOutput.writeUserContent(person.email());
		jteOutput.writeContent("\",\n  \"age\": ");
		jteOutput.writeUserContent(person.age());
		jteOutput.writeContent("\n}\n");
	}
	public static void renderMap(gg.jte.TemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		com.example.common.models.Person person = (com.example.common.models.Person)params.get("person");
		render(jteOutput, jteHtmlInterceptor, person);
	}
}
