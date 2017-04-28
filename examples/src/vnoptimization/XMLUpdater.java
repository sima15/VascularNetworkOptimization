package vnoptimization;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class XMLUpdater {
	
	static String filepath ;
	public static void setPath(String path){
		filepath = path;
	}

	/**
	 * Changes 6 parameter values in the protocol.xml file which exists in the 
	 * filepath given
	 * @param map A hashMap of parameter names and updated values
	 */
	public static void updateParameter(Map<String, Double> map){

	try {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(filepath);
		

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		StreamResult result = new StreamResult(new File(filepath));
		
		
		//Chemotactic Strength with attract and gradient
		NodeList company1 = doc.getElementsByTagName("chemotactic");
		String cstrengthA = Double.toString((double) map.get("chemotactic Strength With Attract"));
		String cstrengthG = Double.toString((double) map.get("chemotactic Strength With Gradient"));
        for (int i = 0; i < company1.getLength(); i++) {
			Node node = company1.item(i);
			Element eElement = (Element) node;
			if (eElement.getAttribute("withSolute").equals("Attract")){
				System.out.println("current Chem attract: "+ eElement.getTextContent()+"\t New value = "+ cstrengthA);
				eElement.setAttribute("strength", cstrengthA);
				DOMSource source = new DOMSource(doc);
				transformer.transform(source, result);
			}
			else if (eElement.getAttribute("withSolute").equals("Gradient")){
				System.out.println("current Chem gradient: "+ eElement.getTextContent()+ "\t New value = "+ cstrengthG);
				eElement.setAttribute("strength", cstrengthG);
				DOMSource source = new DOMSource(doc);
				transformer.transform(source, result);
			}
		}
        
		
		// Vessel and pipe muMax and vessel K;
        NodeList company2 = doc.getElementsByTagName("param");
		String VesselMu = Double.toString((double) map.get("Vessel muMax"));
		String pipeMu = Double.toString((double) map.get("Pipe muMax"));
		String VesselK = Double.toString((double) map.get("Vessel K"));
		
		for (int i = 0; i < company2.getLength(); i++) {
			Node node = company2.item(i);
			Element eElement = (Element) node;
			if (eElement.getAttribute("name").equals("muMax")){
				System.out.println("current V muMax: "+ eElement.getTextContent()+ "\t New value = "+ VesselMu);
				eElement.setTextContent(VesselMu);
				DOMSource source = new DOMSource(doc);
				transformer.transform(source, result);
				break;
			}
		}
		int index =0;
		for (int i = 0; i < company2.getLength(); i++) {
			Node node = company2.item(i);
			Element eElement = (Element) node;
			if (eElement.getAttribute("name").equals("muMax")){
				index++;
				if(index<=1) continue;
				System.out.println("current Pipe muMax: "+ eElement.getTextContent()+"\t New value = "+ pipeMu);
				eElement.setTextContent(pipeMu);
				DOMSource source = new DOMSource(doc);
				transformer.transform(source, result);
				break;
			}
			
		}
		
		for (int i = 0; i < company2.getLength(); i++) {
			Node node = company2.item(i);
			Element eElement = (Element) node;
			if (eElement.getAttribute("name").equals("Ks")){
				System.out.println("current Ks: "+ eElement.getTextContent()+ "\t New value = "+ VesselK);
				eElement.setTextContent(VesselK);
				DOMSource source = new DOMSource(doc);
				transformer.transform(source, result);
				break;
			}
		}
				
		// Pipe and vessel beta
		NodeList company3 = doc.getElementsByTagName("solute");
		String VesselBeta = Double.toString((double) map.get("Vessel Beta"));
		String pipeBeta = Double.toString((double) map.get("Pipe Beta"));
		
		
		Node node1 = company3.item(1);
		Element eElement1 = (Element) node1;
		NodeList l1 = eElement1.getElementsByTagName("param");
		Element eElement2 = (Element) l1.item(2);
		System.out.println("current vesel beta: "+ eElement2.getTextContent()+ "\t New value = "+VesselBeta);
		eElement2.setTextContent(VesselBeta);
		DOMSource source = new DOMSource(doc);
		transformer.transform(source, result);
		
		Node node3 = company3.item(2);
		Element eElement3 = (Element) node3;
		NodeList l3 = eElement3.getElementsByTagName("param");
		Element eElement4 = (Element) l3.item(2);
		System.out.println("current pipe beta: "+ eElement4.getTextContent()+ "\t New value = "+ pipeBeta);
		eElement4.setTextContent(pipeBeta);
		source = new DOMSource(doc);
		transformer.transform(source, result);
		
		//Tight junction
		NodeList company4 = doc.getElementsByTagName("param");
		
		String attachCreateFactor = Double.toString((double) map.get("attachCreateFactor"));
		String attachDestroyFactor = Double.toString((double) map.get("attachDestroyFactor"));
		String tightJunctionToBoundaryStrength = Double.toString((double) map.get("tightJunctionToBoundaryStrength"));
        
		for (int i = 0; i < company4.getLength(); i++) {
			Node node = company4.item(i);
			Element eElement = (Element) node;
			if (eElement.getAttribute("name").equals("attachCreateFactor")){
				System.out.println("current attachCreateFactor : "+ eElement.getTextContent()+"\t New value = "+ attachCreateFactor);
				eElement.setTextContent(attachCreateFactor);
				
			}
			else if (eElement.getAttribute("name").equals("attachDestroyFactor")){
				System.out.println("current attachDestroyFactor: "+ eElement.getTextContent()+ "\t New value = "+ attachDestroyFactor);
				eElement.setTextContent(attachDestroyFactor);
			}
			else if(eElement.getAttribute("name").equals("tightJunctionToBoundaryStrength")){
				System.out.println("current tightJunctionToBoundaryStrength: "+ eElement.getTextContent()+ "\t New value = "+ tightJunctionToBoundaryStrength);
				eElement.setTextContent( tightJunctionToBoundaryStrength);
			}
		}
		source = new DOMSource(doc);
		transformer.transform(source, result);
		
	} catch (ParserConfigurationException pce) {
		pce.printStackTrace();
	} catch (IOException ioe) {
		ioe.printStackTrace();
	} catch (SAXException sae) {
		sae.printStackTrace();
	} catch (TransformerException te){
		te.printStackTrace();
	}
}

		
//		NodeList company2 = doc.getElementsByTagName("reaction");
//		String VesselMu = Double.toString((double) map.get("Vessel muMax"));
//		String pipeMu = Double.toString((double) map.get("Pipe muMax"));
//		String VesselK = Double.toString((double) map.get("Vessel K"));
//		
//		for (int i = 0; i < company2.getLength(); i++) {
//			Node node = company2.item(i);
//			Element eElement = (Element) node;
//			if (eElement.getAttribute("name").equals("AttractSecretion")) {
//				NodeList subComp1 = eElement.getElementsByTagName("param");
//				for(int j=0; j<subComp1.getLength(); j++){
//					Node node2 = subComp1.item(i);
//					System.out.println(node2.getNodeName());
//					Element eElement2 = (Element) node2;
//					System.out.println("Param name: " + eElement2.getAttribute("name"));
//					if(eElement2.getAttribute("name").equals("muMax")){
//					
//						System.out.println("current vessel muMax: or "+eElement2.getTagName() + 
//								eElement2.getTextContent());
//						System.out.println("New value = "+ VesselMu);
//						eElement2.setTextContent(VesselMu);
//						DOMSource source = new DOMSource(doc);
//						transformer.transform(source, result);
//						break;
//					}
//				}
//			}
//			break;
//		}
//			
//		for (int i = 0; i < company2.getLength(); i++) {
//			Node node = company2.item(i);
//			Element eElement = (Element) node;
//			if (eElement.getAttribute("name").equals("AttractSecretion")) {
//				NodeList subComp1 = eElement.getElementsByTagName("param");
//				for(int j=0; j<subComp1.getLength(); j++){
//					Node node2 = subComp1.item(i);
//					System.out.println(node2.getNodeName());
//					Element eElement2 = (Element) node2;
//					System.out.println("Param name: " + eElement2.getAttribute("name"));
//					if(eElement2.getAttribute("name").equals("Ks")){
//						System.out.println("current Ks: "+eElement2.getTagName() + 
//								eElement2.getTextContent());
//						System.out.println("New value = "+ VesselK);
//						eElement2.setTextContent(VesselK);
//						DOMSource source = new DOMSource(doc);
//						transformer.transform(source, result);
//						break;
//					}
//			
//				}
//			}
//			break;
//		}
//		for (int i = 0; i < company2.getLength(); i++) {
//			Node node = company2.item(i);
//			Element eElement = (Element) node;
//			if (eElement.getAttribute("name").equals("GradientSecretion")){
//				NodeList subComp1 = eElement.getElementsByTagName("param");
//				for(int j=0; j<subComp1.getLength(); j++){
//					Node node2 = subComp1.item(i);
//					Element eElement2 = (Element) node2;
//					if(eElement2.getAttribute("name").equals("muMax")){
//						System.out.println("current pipeMu: "+ eElement2.getTextContent());
//						System.out.println("New value = "+ pipeMu);
//						eElement2.setTextContent(pipeMu);
//						DOMSource source = new DOMSource(doc);
//						transformer.transform(source, result);
//						break;
//					}
//				}				
//			}
//			break;
//		}
//		source = new DOMSource(doc);
//		transformer.transform(source, result);
//		System.out.println("vesselmu, PipeMu and Ks updated.");
//	
//		// Pipe and vessel beta
//		NodeList company3 = doc.getElementsByTagName("solute");
//		String VesselBeta = Double.toString((double) map.get("Vessel Beta"));
//		String pipeBeta = Double.toString((double) map.get("Pipe Beta"));
//		
		
//		for (int i = 0; i < company3.getLength(); i++) {
//			Node node = company3.item(i);
//			Element eElement = (Element) node;
//			if (eElement.getAttribute("name").equals("Attract")) {
//				NodeList subComp1 = eElement.getElementsByTagName("param");
//				for(int j=0; j<subComp1.getLength(); j++){
//					Node node2 = subComp1.item(i);
//					Element eElement2 = (Element) node2;
//					if(eElement2.getAttribute("name").equals("decayRate")){
//						System.out.println("current vessel decay rate: "+ eElement2.getTextContent());
//						System.out.println("New value = "+ VesselBeta);
//						eElement2.setTextContent(VesselBeta);
//						DOMSource source = new DOMSource(doc);
//						transformer.transform(source, result);
//					}
//				}
//			}
//			else if (eElement.getAttribute("name").equals("Gradient")){
//				NodeList subComp1 = eElement.getElementsByTagName("param");
//				for(int j=0; j<subComp1.getLength(); j++){
//					Node node2 = subComp1.item(i);
//					Element eElement2 = (Element) node2;
//					if(eElement2.getAttribute("name").equals("decayRate")){
//						System.out.println("current pipe decay rate: "+ eElement2.getTextContent());
//						System.out.println("New value = "+ pipeBeta);
//						eElement2.setTextContent(pipeBeta);
//							DOMSource source = new DOMSource(doc);
//							transformer.transform(source, result);
//					}
//				}				
//			}
//		}
//			source = new DOMSource(doc);
//			transformer.transform(source, result);

		
}
