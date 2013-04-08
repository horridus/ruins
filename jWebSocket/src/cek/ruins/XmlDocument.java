package cek.ruins;

import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;

public class XmlDocument {
	private Document doc;

	public XmlDocument(String document) throws DocumentException {
		this.doc = DocumentHelper.parseText(document);
	}

	public Document document() {
		return this.doc;
	}

	public String getTrimmedText(String xpath, Node parent) throws DocumentException {
		return this.getTrimmedText(xpath, parent, null);
	}

	public String getTrimmedText(String xpath, Node parent, String def) throws DocumentException {
		Node node;

		if (parent != null)
			node = parent.selectSingleNode(xpath);
		else
			node = this.doc.selectSingleNode(xpath);

		if (node == null)
			if (def == null)
				throw new DocumentException("'Required' xpath not found " + xpath);
			else
				return def;
		else
			return node.getText().trim();
	}

	public List<?> selectNodes(String xpathExpression) {
		return this.doc.selectNodes(xpathExpression);
	}

	public String getAttributeValue(String xpath, String defValue) {
        String val = getAttributeValue(xpath);
        if (val == null)
            return defValue;
        else
            return val;
    }

	public String getAttributeValue(String xpath) {
        Attribute at = (Attribute) this.doc.selectSingleNode(xpath);
        if (at == null)
            return null;
        else
            return at.getValue();
    }
}
