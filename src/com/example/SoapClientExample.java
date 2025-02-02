package com.example;

import javax.xml.soap.*;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

public class SoapClientExample
{
	private  boolean  belavia      = true;
	
    private  String   namespaceURI = null;    
    private  String   soapUrl      = null;
    private  String   serviceName  = null;

    private  String   namespace    = null;
    private  String   soapAction   = null;

    private  boolean  useXSLT      = true;
    
	public SoapClientExample()
	{
		setSoapParams();
		callSoapWebService(soapUrl, soapAction);
	}
	private void setSoapParams()
	{
		if (belavia) {
		    namespaceURI = "http://webservices.belavia.by";
		    soapUrl      = "http://86.57.245.235/TimeTable/Service.asmx";
		    serviceName  = "GetAirportsList";  
		} else {
		    namespaceURI = "http://www.webserviceX.NET";
		    soapUrl      = "http://www.webservicex.net/uszip.asmx";
		    serviceName  = "GetInfoByCity";
		}
	    namespace  = "ns"; // Namespace";
	    soapAction = namespaceURI + "/" + serviceName;
	}
    private void createSoapEnvelope(SOAPMessage soapMessage) throws SOAPException 
    {
        SOAPPart soapPart = soapMessage.getSOAPPart();

        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration(namespace, namespaceURI);
/*
            Constructed SOAP Request Message:
            <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/"
                               xmlns:myNamespace="http://www.webserviceX.NET">
                <SOAP-ENV:Header/>
                <SOAP-ENV:Body>
                    <myNamespace:GetInfoByCity>
                        <myNamespace:USCity>New York</myNamespace:USCity>
                    </myNamespace:GetInfoByCity>
                </SOAP-ENV:Body>
            </SOAP-ENV:Envelope>
*/

        // SOAP Body
        SOAPBody soapBody = envelope.getBody();
    	SOAPElement soapBodyElem;
    	SOAPElement soapBodyElem1;
        if (belavia) {
        	soapBody.addChildElement(serviceName, namespace);
/*
        	serviceName = "GetTimeTable"; 
        	soapBodyElem  = soapBody.addChildElement(serviceName, namespace);
        	soapBodyElem1 = soapBodyElem.addChildElement("Airport", namespace);
        	soapBodyElem1.addTextNode("AER");

        	SOAPElement soapBodyElem2 = soapBodyElem.addChildElement("Type", namespace);
        	soapBodyElem2.addTextNode("Departure");
        	
        	SOAPElement soapBodyElem3 = soapBodyElem.addChildElement("ViewDate", namespace);
        	soapBodyElem3.addTextNode("2017-10-13");
*/
        } else {
        	soapBodyElem  = soapBody.addChildElement(serviceName, namespace);
        	soapBodyElem1 = soapBodyElem.addChildElement("USCity", namespace);
        	soapBodyElem1.addTextNode("New York");
        }
    }
	private SOAPMessage createSOAPRequest(String soapAction) throws Exception
	{
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();

        createSoapEnvelope(soapMessage);

        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", soapAction);

        soapMessage.saveChanges();

        // ������ XML ������ �������
        System.out.println("Request SOAP Message:");
        soapMessage.writeTo(System.out);
        System.out.println("\n");

        return soapMessage;
    }	
    private void callSoapWebService(String destination, String soapAction) 
    {
    	SOAPConnectionFactory soapFactory  = null;
    	SOAPConnection        soapConnect  = null;
    	SOAPMessage           soapRequest  = null;
    	SOAPMessage           soapResponse = null;
        try {
            // �������� SOAP Connection
        	soapFactory = SOAPConnectionFactory.newInstance();
            soapConnect = soapFactory.createConnection();

            // �������� SOAP Message ��� ��������
            soapRequest  = createSOAPRequest(soapAction);
            // ��������� SOAP Message
            soapResponse = soapConnect.call(soapRequest, destination);

            if (!useXSLT) {
            	// ������ SOAP Response
            	System.out.println("Response SOAP Message:");
            	soapResponse.writeTo(System.out);
            	System.out.println();
            } else
            	printSOAPMessage (soapResponse);

            soapConnect.close();
        } catch (Exception e) {
            System.err.println("\nError occurred while sending SOAP Request to Server!\n"
            		         + "Make sure you have the correct endpoint URL and SOAPAction!\n");
            e.printStackTrace();
        }
    }
    private void printSOAPMessage (SOAPMessage soapResponse)
    {
    	TransformerFactory transformerFactory;
    	Transformer        transformer;
        try {
	        // �������� XSLT-����������
	        transformerFactory = TransformerFactory.newInstance();
	        transformer = transformerFactory.newTransformer();
	        // ��������� ����������� ������
	        Source content;
	        content = soapResponse.getSOAPPart().getContent();
	        // ����������� ��������� ������
	        StreamResult result = new StreamResult(System.out);
	        transformer.transform(content, result);
	        System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	public static void main(String[] args) 
	{
		new SoapClientExample();
		System.exit(0);
	}
}
