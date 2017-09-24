package cyoastudio.templating;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.slf4j.*;

import cyoastudio.data.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class ProjectConverter {

	public static Map<String, Object> convert(Project p) {
		return convert(p, true, 0, p.getSections().size());
	}

	public static Map<String, Object> convert(Project p, boolean includeTitle, int start, int end) {
		Map<String, Object> data = new HashMap<>();

		if (includeTitle)
			data.put("title", p.getTitle());
		if (start <= end && end <= p.getSections().size()) {
			List<Section> sections = p.getSections().subList(start, end);
			List<Map<String, Object>> dataList = sections.stream()
					.map(ProjectConverter::convert).collect(Collectors.toList());

			// Add ids to everything
			for (int i = 0; i < sections.size(); i++) {
				dataList.get(i).put("id", "section-" + String.valueOf(i));
				Section s = sections.get(i);
				for (int j = 0; j < s.getOptions().size(); j++) {
					List<Map<String, Object>> optionDataList = (List<Map<String, Object>>) dataList.get(i)
							.get("options");
					optionDataList.get(j).put("id", "option-" + String.valueOf(i) + "-" + String.valueOf(j));
				}
			}
			data.put("sections", dataList);
		}
		data.put("customCss", p.getCss());

		return data;
	}

	public static Map<String, Object> convert(Section s) {
		Map<String, Object> data = new HashMap<String, Object>();

		data.put("title", Markdown.render(s.getTitle()));
		data.put("description", Markdown.render(s.getDescription()));
		data.put("classes", s.getClasses().concat(" "));

		List<Map<String, Object>> optionDataList = new ArrayList<>();
		int lowerRollBound = 1;
		for (Option o : s.getOptions()) {
			Map<String, Object> optionData = ProjectConverter.convert(o);
			if (s.isRollable() && o.getRollRange() > 0) {
				int higherRollBound = lowerRollBound + o.getRollRange() - 1;
				optionData.put("rollRange", convertRange(lowerRollBound, higherRollBound));
				lowerRollBound = higherRollBound + 1;
			}
			optionDataList.add(optionData);
		}
		data.put("options", optionDataList);

		data.put("numPerRow", s.getOptionsPerRow());
		switch (s.getImagePositioning()) {
		case ALTERNATING:
			data.put("imageClass", "alternating-images");
			break;
		case TOP:
			data.put("imageClass", "top-images");
			break;
		case LEFT:
			data.put("imageClass", "left-images");
			break;
		case RIGHT:
			data.put("imageClass", "right-images");
			break;
		}

		return data;
	}

	public static Map<String, Object> convert(Option o) {
		Map<String, Object> data = new HashMap<String, Object>();

		data.put("title", Markdown.render(o.getTitle()));
		data.put("description", Markdown.render(o.getDescription()));
		if (o.getImage() != null)
			data.put("image", convert(o.getImage()));
		data.put("classes", o.getClasses().concat(" "));
		data.put("cost", o.getCost());

		return data;
	}

	public static Map<String, Object> convertStyle(Map<String, Object> style) {
		Map<String, Object> data = new HashMap<String, Object>();

		for (Entry<String, Object> i : style.entrySet()) {
			String key = i.getKey();
			Object value = i.getValue();
			String repr;
			if (value instanceof Color) {
				repr = convert((Color) value);
			} else if (value instanceof Image) {
				if (style.containsKey(key + "Color")) {
					data.put(key + "Blended",
							convert(((Image) value).blend((Color) style.get(key + "Color"))));
				}
				repr = convert((Image) value);
			} else if (value instanceof String) {
				repr = (String) value;
			} else if (value instanceof Font) {
				repr = ((Font) value).getFamily();
			} else if (value == null) {
				repr = "null";
				Logger logger = LoggerFactory.getLogger(ProjectConverter.class);
				logger.warn("Null value in key {}", key);
			} else {
				repr = value.toString();
				Logger logger = LoggerFactory.getLogger(ProjectConverter.class);
				logger.warn("Unknown value type in style options");
			}
			data.put(key, repr);
		}

		return data;
	}

	public static String convert(Image i) {
		return "data:image/png;base64," + i.toBase64();
	}

	public static String convert(Color c) {
		return "rgba(" +
				Integer.toString((int) (c.getRed() * 255)) + ", " +
				Integer.toString((int) (c.getGreen() * 255)) + ", " +
				Integer.toString((int) (c.getBlue() * 255)) + ", " +
				Double.toString(c.getOpacity()) + ")";
	}

	public static String convertRange(int low, int high) {
		assert low <= high;

		if (low == high)
			return String.valueOf(low);
		else if (low == high - 1)
			return String.valueOf(low) + ", " + String.valueOf(high);
		else
			return String.valueOf(low) + "-" + String.valueOf(high);
	}
}
