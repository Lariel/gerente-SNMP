package snmp;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class SnmpGet {
	private String ip, porta, comunidade, address, oid;
	private Snmp snmp = null;
	private PDU pdu = null;
	
	public SnmpGet(String oid, SnmpManager gerente) {
		this.ip=gerente.getIp();
		this.porta=gerente.getPorta();
		this.comunidade=gerente.getComunidade();
		this.oid=oid;
	}
	
	public String get() {
		
		String saida="";

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
}
