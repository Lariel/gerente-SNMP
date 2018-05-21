package snmp;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
			CommunityTarget comtarget = new CommunityTarget();
			comtarget.setCommunity(new OctetString(comunidade));
			comtarget.setVersion(SnmpConstants.version2c);
			comtarget.setAddress(new UdpAddress(ip + "/" + porta));
			comtarget.setRetries(2);
			comtarget.setTimeout(1000);

			// Criado PDU
			pdu = new PDU();
			pdu.add(new VariableBinding(new OID(oid)));
			pdu.setType(PDU.GET);
			pdu.setRequestID(new Integer32(1));

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
					alert.setHeaderText("SnmpManager linha 86");
					alert.setContentText("Error Status = " + errorStatus+"\nError Index = " + errorIndex+"\nError Status Text = " + errorStatusText);
					Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
					alert.showAndWait();	
				}
			}else{
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Alerta");
				alert.setHeaderText("SnmpManager linha 94");
				alert.setContentText("Timeout");
				Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
				alert.showAndWait();	
			}
			snmp.close();

		} catch (Exception e) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Alerta");
			alert.setHeaderText("SnmpManager linha 104");
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
					alert.setHeaderText("SnmpManager linha 155");
					alert.setContentText("Error Status = " + errorStatus+"\nError Index = " + errorIndex+"\nError Status Text = " + errorStatusText);
					Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
					alert.showAndWait();
				}
			} else {
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Alerta");
				alert.setHeaderText("SnmpManager linha 163");
				alert.setContentText("Timeout");
				Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
				alert.showAndWait();	
			}
			snmp.close();


		} catch (Exception e) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Alerta");
			alert.setHeaderText("SnmpManager linha 172");
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
			pdu.add(new VariableBinding(new OID(oid)));
			pdu.setType(PDU.GETBULK);
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

				int errorStatus = responsePDU.getErrorStatus();
				int errorIndex = responsePDU.getErrorIndex();
				String errorStatusText = responsePDU.getErrorStatusText();

				if (errorStatus == PDU.noError){
					
					for(VariableBinding vb : responsePDU.getVariableBindings()) {
						System.out.println("232");
	                    result.put("." + vb.getOid().toString(), vb.getVariable().toString());
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
			alert.setHeaderText("SnmpManager linha 256");
			alert.setContentText("Erro ao enviar requisição para o OID: "+oid+"\n Causa: "+e.toString());
			Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
			alert.showAndWait();
		} 
System.out.println("260");		
		for(int i=0;i<result.size();i++) {
			System.out.println(result.get(i).toString());
			saida=result.get(i).toString()+"\n";
		}
		System.out.println("265");
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