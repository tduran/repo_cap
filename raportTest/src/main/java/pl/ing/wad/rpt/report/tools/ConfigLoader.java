package pl.ing.wad.rpt.report.tools;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class ConfigLoader {

    private NodeList nlist;
    private Document doc;
    
    public ConfigLoader() {}

    public void loadConfigFile(String filePath) throws Exception {
        DocumentBuilderFactory dbfact = DocumentBuilderFactory.newInstance();
        DocumentBuilder dbuild = dbfact.newDocumentBuilder();
        try {
            doc = dbuild.parse(new File(filePath));
            doc.getDocumentElement().getNodeName();
            return;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public String getElementValue(String elementName) {
        nlist = doc.getElementsByTagName(elementName);

        if (nlist.getLength() == 0) {
            return "";
        }

        return nlist.item(0).getTextContent();
    }
}
