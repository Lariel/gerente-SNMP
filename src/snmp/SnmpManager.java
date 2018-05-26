package snmp;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TreeEvent;
import org.snmp4j.util.TreeUtils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class SnmpManager {
	private String saida, ip, porta, comunidade, address, oid;
	private Snmp snmp = null;
	private PDU pdu = null;

	public SnmpManager(String ip, String porta, String comunidade) throws Exception {
		this.ip=ip;
		this.porta=porta;
		this.comunidade=comunidade;
	}

	public String get(String oid) {
		this.oid=oid;

		try {
			TransportMapping transport = new DefaultUdpTransportMapping();
			transport.listen();

			// criação do objeto alvo da conexão
			//A CommunityTarget represents SNMP target properties for community based message processing models (SNMPv1 and SNMPv2c).
			CommunityTarget comtarget = new CommunityTarget();
			comtarget.setCommunity(new OctetString(comunidade));
			comtarget.setVersion(SnmpConstants.version2c);
			comtarget.setAddress(new UdpAddress(ip + "/" + porta));
			comtarget.setRetries(2);
			comtarget.setTimeout(1000);

			// Criado PDU
			//The PDU class represents a SNMP protocol data unit.
			pdu = new PDU();
			pdu.add(new VariableBinding(new OID(oid)));
			pdu.setType(PDU.GET);
			pdu.setRequestID(new Integer32(1));

			//The Snmp class is the core of SNMP4J.
			// Estabelecida conexão snmp com o objeto
			snmp = new Snmp(transport);


			//enviando request...
			ResponseEvent response = snmp.get(pdu, comtarget);

			if (response != null) {
				//recebida a Response
				PDU responsePDU = response.getResponse();

				int errorStatus = responsePDU.getErrorStatus();
				int errorIndex = responsePDU.getErrorIndex();
				String errorStatusText = responsePDU.getErrorStatusText();

				if (errorStatus == PDU.noError){
					//saida=responsePDU.getVariableBindings().get(0).getOid().toString();  - utilizar se quiser ler o OID
					saida=responsePDU.getVariableBindings().get(0).getVariable().toString();
				} else{
					Alert alert = new Alert(AlertType.WARNING);
					alert.setTitle("Alerta");
					alert.setHeaderText("SnmpManager linha 90");
					alert.setContentText("Error Status = " + errorStatus+"\nError Index = " + errorIndex+"\nError Status Text = " + errorStatusText);
					Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
					alert.showAndWait();	
				}
			}else{
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Alerta");
				alert.setHeaderText("SnmpManager linha 98");
				alert.setContentText("Timeout");
				Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
				alert.showAndWait();	
			}
			snmp.close();

		} catch (Exception e) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Alerta");
			alert.setHeaderText("SnmpManager linha 108");
			alert.setContentText("Erro ao enviar requisição para o OID: "+oid+"\n Causa: "+e.toString());
			Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
			alert.showAndWait();
		} 
		return saida;
	}

	public String getnext(String oid) {
		this.oid=oid;

		try {
			TransportMapping transport = new DefaultUdpTransportMapping();
			transport.listen();

			// criação do objeto alvo da conexão
			CommunityTarget comtarget = new CommunityTarget();
			comtarget.setCommunity(new OctetString(comunidade));
			comtarget.setVersion(SnmpConstants.version2c);
			comtarget.setAddress(new UdpAddress(ip + "/" + porta));
			comtarget.setRetries(2);
			comtarget.setTimeout(1000);

			// Criado PDU
			pdu = new PDU();
			pdu.add(new VariableBinding(new OID(oid)));
			pdu.setRequestID(new Integer32(1));
			pdu.setType(PDU.GETNEXT);			

			// Estabelecida conexão snmp com o objeto
			snmp = new Snmp(transport);

			ResponseEvent response = snmp.getNext(pdu, comtarget);

			if (response != null) {
				PDU responsePDU = response.getResponse();

				int errorStatus = responsePDU.getErrorStatus();
				int errorIndex = responsePDU.getErrorIndex();
				String errorStatusText = responsePDU.getErrorStatusText();

				if (errorStatus == PDU.noError)	{
					//oid=responsePDU.getVariableBindings().get(0).getOid().toString();
					saida="Valor: "+responsePDU.getVariableBindings().get(0).getVariable().toString()+
							"\nOID: "+responsePDU.getVariableBindings().get(0).getOid().toString();
					//saida=responsePDU.getVariableBindings().toString();

				}else{
					Alert alert = new Alert(AlertType.WARNING);
					alert.setTitle("Alerta");
					alert.setHeaderText("SnmpManager linha 158");
					alert.setContentText("Error Status = " + errorStatus+"\nError Index = " + errorIndex+"\nError Status Text = " + errorStatusText);
					Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
					alert.showAndWait();
				}
			} else {
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Alerta");
				alert.setHeaderText("SnmpManager linha 166");
				alert.setContentText("Timeout");
				Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
				alert.showAndWait();	
			}
			snmp.close();


		} catch (Exception e) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Alerta");
			alert.setHeaderText("SnmpManager linha 177");
			alert.setContentText("Erro ao enviar requisição para o OID: "+oid+"\n Causa: "+e.toString());
			Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
			alert.showAndWait();
		}
		return saida;
	}
	
	/**
     * 
     * @param int n non-repeaters
     * @param int m max-repetitions
     * @param OID
     */
	public String getbulk(int n, int m, String oid) {
		Map<String, String> result = new HashMap<>();
		
		this.oid=oid;

		try {
			TransportMapping transport = new DefaultUdpTransportMapping();
			transport.listen();

			// criação do objeto alvo da conexão
			CommunityTarget comtarget = new CommunityTarget();
			comtarget.setCommunity(new OctetString(comunidade));
			comtarget.setVersion(SnmpConstants.version2c);
			comtarget.setAddress(new UdpAddress(ip + "/" + porta));
			comtarget.setRetries(2);
			comtarget.setTimeout(1000);

			// Criado PDU
			pdu = new PDU();
			pdu.setType(PDU.GETBULK);
			pdu.add(new VariableBinding(new OID(oid)));
			pdu.add(new VariableBinding(new OID(".1.3.6.1.2.1.1.1.0")));
			//pdu.add(new VariableBinding(new OID(".1.3.6.1.2.1.1.3.0")));
			
			System.out.println("PDU size"+pdu.size());
			
			pdu.setNonRepeaters(n);
			pdu.setMaxRepetitions(m);
			pdu.setRequestID(new Integer32(1));

			// Estabelecida conexão snmp com o objeto
			snmp = new Snmp(transport);


			//enviando request...
			ResponseEvent response = snmp.send(pdu, comtarget);

			if (response != null) {
				//recebida a Response
				PDU responsePDU = response.getResponse();
				
				System.out.println("responsePDU size"+responsePDU.size());
				
				int errorStatus = responsePDU.getErrorStatus();
				int errorIndex = responsePDU.getErrorIndex();
				String errorStatusText = responsePDU.getErrorStatusText();

				if (errorStatus == PDU.noError){
					
					System.out.println("responsePDU.getVariableBindings().size()"+responsePDU.getVariableBindings().size());
					
					for(int i = 0; i<responsePDU.getVariableBindings().size();i++) {
						result.put("Valor: "+responsePDU.getVariableBindings().get(i).getVariable().toString(),
								"\nOID: "+responsePDU.getVariableBindings().get(i).getOid().toString());
						
						System.out.println("Valor: "+responsePDU.getVariableBindings().get(i).getVariable().toString()+
								"\nOID: "+responsePDU.getVariableBindings().get(i).getOid().toString());
					}
					
				} else{
					Alert alert = new Alert(AlertType.WARNING);
					alert.setTitle("Alerta");
					alert.setHeaderText("SnmpManager linha 238");
					alert.setContentText("Error Status = " + errorStatus+"\nError Index = " + errorIndex+"\nError Status Text = " + errorStatusText);
					Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
					alert.showAndWait();	
				}
			}else{
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Alerta");
				alert.setHeaderText("SnmpManager linha 246");
				alert.setContentText("Timeout");
				Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
				alert.showAndWait();	
			}
			snmp.close();

		} catch (Exception e) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Alerta");
			alert.setHeaderText("SnmpManager linha 259");
			alert.setContentText("Erro ao enviar requisição para o OID: "+oid+"\n Causa: "+e.toString());
			Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
			alert.showAndWait();
		} 
		
		
		Set<String> chaves = result.keySet();
		for (String chave : chaves){
			if(chave != null)
				saida=chave + result.get(chave)+"\n";
		}
		return saida;

	}

	public String getdelta() {
		saida="get detla";

		return saida;
	}

	public String gettable() {
		saida="get table";

		return saida;
	}

	public String set() {
		saida="set";

		return saida;
	}

	public String walk(String oid) {
		saida="walk";

		return saida;	
	}
}