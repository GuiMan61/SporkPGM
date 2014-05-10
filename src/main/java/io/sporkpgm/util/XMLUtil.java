package io.sporkpgm.util;

import org.jdom2.Element;

public class XMLUtil {

	public static String getElementOrParentValue(Element element, String attribute) {
		return (element.getAttributeValue(attribute) != null ? element.getAttributeValue(attribute) : element.getParentElement().getAttributeValue(attribute));
	}

}
