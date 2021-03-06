package cyoastudio.data;

import java.util.*;

import cyoastudio.templating.*;

public class Project {
	private String title = "";
	private Template template;
	private Map<String, Object> style;
	private List<Section> sections = new ArrayList<>();
	private String css = "";
	private Settings settings = new Settings();

	public Project() {
		this.template = Template.defaultTemplate();
		this.style = Style.defaultStyle();
	}

	public Template getTemplate() {
		return template;
	}

	public void changeTemplate(Template template, Map<String, Object> style) {
		this.template = template;
		this.style = style;
	}

	public Map<String, Object> getStyleOptions() {
		return style;
	}

	public void setStyle(Map<String, Object> style) {
		this.style = style;
	}

	public List<Section> getSections() {
		return sections;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCss() {
		return css;
	}

	public void setCss(String css) {
		this.css = css;
	}

	public Settings getSettings() {
		return settings;
	}
}
