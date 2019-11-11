package start;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import start.data.RideLog;

public class XmlParserTest {
	public static void main(String[] args)  {
		startXml();
		createXml();
	}
	
	private static void startXml() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder;
		
		try {
			documentBuilder = factory.newDocumentBuilder();
			
			File file = new File("gpxFile.gpx");
			Document doc = documentBuilder.parse(file);

			doc.getDocumentElement().normalize();

			NodeList nodeList = doc.getElementsByTagName("gpx");
			System.out.println(nodeList.getLength());
			
			XPath xpath = XPathFactory.newInstance().newXPath();

			
			NodeList nodeTrk = (NodeList) xpath.evaluate("//gpx/trk/trkseg/trkpt", doc, XPathConstants.NODESET);
			System.out.println("metadata.length  ::" + nodeTrk.getLength());


			NodeList node2 = (NodeList) xpath.evaluate("//gpx/wpt", doc, XPathConstants.NODESET);
			System.out.println("metadata.length  ::" + node2.getLength());
			for (int i = 0; i < node2.getLength(); ++i) {
				if (node2.item(i).getNodeType() != Node.ELEMENT_NODE)
					continue; // 실제 NODE만 취급합니다
//				System.out.println("getTextContent ::" + node2.item(i).getTextContent()); // 얻은 값을 원하는 대로 다루면 되겠지요
				NamedNodeMap nodeMap = node2.item(i).getAttributes();
				System.out.println("lat ::" + nodeMap.getNamedItem("lat").getNodeValue());

			}



			
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				NodeList childNodeList = node.getChildNodes();

				RideLog rideLog = extractRideLog(childNodeList);
//				dataset.addValue(rideLog.watt, series, rideLog.time);
			}
			
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private static RideLog extractRideLog(NodeList trackNode) {
		RideLog log = new RideLog();
		System.out.println("trackNode.getLength()  ::"+trackNode.getLength());
		for (int i = 0; i < trackNode.getLength(); i++) {
			Node node = trackNode.item(i);
			String name = node.getNodeName();
			String value = node.getTextContent();
			System.out.println("trackNode.name  ::"+name );
			if ("Time".equals(name)) {
				log.time = value;
			} else if ("DistanceMeters".equals(name)) {
				log.distance = Double.parseDouble(value);
			} else if ("HeartRateBpm".equals(name)) {
				log.hr = Integer.parseInt(value);
			} else if ("Cadence".equals(name)) {
				log.cadence = Integer.parseInt(value);
			} else if ("Extensions".equals(name)) {
				NodeList extensionList = node.getChildNodes();
				for (int j = 0; j < extensionList.getLength(); j++) {
					node = extensionList.item(j);
					name = node.getNodeName();

					if ("TPX".equals(name)) {
						NodeList extensionChildList = node.getChildNodes();
						for (int k = 0; k < extensionChildList.getLength(); k++) {
							Node extensionNode = extensionChildList.item(k);
							name = extensionNode.getNodeName();
							if ("Speed".equals(name)) {
								log.speed = Double.parseDouble(extensionNode.getTextContent());
							} else if ("Watts".equals(name)) {
								log.watt = Double.parseDouble(extensionNode.getTextContent());
							}
						}
					}
				}
			}
		}

		return log;
	}
	private static void createXml() {
		Document document;
		try {
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Node root = document.createElement("JCompany");
			document.appendChild(root);
			{
				Element people1 = document.createElement("employee");
				people1.setAttribute("id", "1");
				people1.setAttribute("part", "devlopment");
				root.appendChild(people1);
				{
					Element name = document.createElement("name");
					name.appendChild(document.createTextNode("Gildong Hong"));
					people1.appendChild(name);
				}
				{
					Element age = document.createElement("age");
					age.appendChild(document.createTextNode("25"));
					people1.appendChild(age);
				}
			} // Document 저장
			DOMSource xmlDOM = new DOMSource(document);
			StreamResult xmlFile = new StreamResult(new File("saved.xml"));
			TransformerFactory.newInstance().newTransformer().transform(xmlDOM, xmlFile);

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
