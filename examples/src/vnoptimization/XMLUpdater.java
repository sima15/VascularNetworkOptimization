package vnoptimization;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
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
	
	/**
	 * The path to the protocol file where values can be updated based on the given 
	 * values in the parameterMap
	 */
	static String filepath ;
	static Map<String, Double> parameterMap; 
	static Document doc;
	static Transformer transformer;
	static StreamResult result;
	
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
			
			parameterMap = map;
			
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.parse(filepath);
	
	        TransformerFactory transformerFactory = TransformerFactory.newInstance();
			transformer = transformerFactory.newTransformer();
			result = new StreamResult(new File(filepath));
			
			updateChemStrength();
			updateMuAndK();
			updateBetas();
			updateTightJunctions();
			
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
	
	/**
	 * Updates Chemotactic Strength with attract and gradient inside the corresponding protocol file
	 * @throws TransformerException
	 */
	static void updateChemStrength() throws TransformerException{
		NodeList company1 = doc.getElementsByTagName("chemotactic");
		String cstrengthA = Double.toString((double) parameterMap.get("chemotactic Strength With Attract"));
		String cstrengthG = Double.toString((double) parameterMap.get("chemotactic Strength With Gradient"));
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
	}
	
	/**
	 * Updates vascular and circulatory cells production rate inside the corresponding protocol file
	 * @throws TransformerException
	 */
	static void updateMuAndK() throws TransformerException{
		 NodeList company2 = doc.getElementsByTagName("param");
			String VesselMu = Double.toString((double) parameterMap.get("Vessel muMax"));
			String pipeMu = Double.toString((double) parameterMap.get("Pipe muMax"));
			String VesselK = Double.toString((double) parameterMap.get("Vessel K"));
			
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
	}
	
	/**
	 * Updates decay rate of vascular and circulatory cells' secretion inside the corresponding protocol file
	 * @throws TransformerException
	 */
	static void updateBetas() throws TransformerException{
		NodeList company3 = doc.getElementsByTagName("solute");
		String VesselBeta = Double.toString((double) parameterMap.get("Vessel Beta"));
		String pipeBeta = Double.toString((double) parameterMap.get("Pipe Beta"));
		
		
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
	}
	
	/**
	 * Updates tight junction values of vascular and circulatory cells' secretion inside the corresponding protocol file
	 * @throws TransformerException
	 */
	static void updateTightJunctions() throws TransformerException{
		NodeList company4 = doc.getElementsByTagName("param");
		
		String attachCreateFactor = Double.toString((double) parameterMap.get("attachCreateFactor"));
		String attachDestroyFactor = Double.toString((double) parameterMap.get("attachDestroyFactor"));
		String tightJunctionToBoundaryStrength = Double.toString((double) parameterMap.get("tightJunctionToBoundaryStrength"));
        
		boolean createF = false;
		boolean destroyF = false;
		boolean boundaryF = false;
		
		for (int i = 0; i < company4.getLength(); i++) {
			Node node = company4.item(i);
			Element eElement = (Element) node;
			if (eElement.getAttribute("name").equals("attachCreateFactor")){
				if(!createF)
					System.out.println("current attachCreateFactor : "+ eElement.getTextContent()+"\t New value = "+ attachCreateFactor);
				eElement.setTextContent(attachCreateFactor);
				createF = true;
			}
			else if (eElement.getAttribute("name").equals("attachDestroyFactor")){
				if(!destroyF)
					System.out.println("current attachDestroyFactor: "+ eElement.getTextContent()+ "\t New value = "+ attachDestroyFactor);
				eElement.setTextContent(attachDestroyFactor);
				destroyF = true;
			}
			else if(eElement.getAttribute("name").equals("tightJunctionToBoundaryStrength")){
				if(!boundaryF)
					System.out.println("current tightJunctionToBoundaryStrength: "+ eElement.getTextContent()+ "\t New value = "
					+ tightJunctionToBoundaryStrength);
				eElement.setTextContent( tightJunctionToBoundaryStrength);
				boundaryF = true;
			}
		}
		DOMSource source = new DOMSource(doc);
		transformer.transform(source, result);
	}
}
