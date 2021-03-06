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

public class SnmpWalk {
	
	private String ip, porta, comunidade, address, oid;
	private Snmp snmp = null;
	private PDU pdu = null;
	private SnmpManager gerente=null;
	
	public SnmpWalk(String oid, SnmpManager gerente) {
		this.gerente=gerente;
		this.ip=gerente.getIp();
		this.porta=gerente.getPorta();
		this.comunidade=gerente.getComunidade();
		this.oid=oid;
	}
	
	public String walk() {
		String saida="";
		String saidaTemp="";
		String oidtemp="";
		this.oid=oid;
		String[] vetoroid;
		String[] vetoroidtemp;
		boolean continua = true;
		
		vetoroid=oid.split("\\.");
		
		while(continua) {
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
						oidtemp=responsePDU.getVariableBindings().get(0).getOid().toString();
					}else{
						Alert alert = new Alert(AlertType.WARNING);
						alert.setTitle("Alerta");
						alert.setHeaderText("SnmpWalk linha 85");
						alert.setContentText("Error Status = " + errorStatus+"\nError Index = " + errorIndex+"\nError Status Text = " + errorStatusText);
						Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
						alert.showAndWait();
					}
				} else {
					Alert alert = new Alert(AlertType.WARNING);
					alert.setTitle("Alerta");
					alert.setHeaderText("SnmpWalk linha 89");
					alert.setContentText("Timeout");
					Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
					alert.showAndWait();	
				}
				snmp.close();


			} catch (Exception e) {
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Alerta");
				alert.setHeaderText("SnmpWalk linha 100");
				alert.setContentText("Erro ao enviar requisição para o OID: "+oid+"\n Causa: "+e.toString());
				Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
				alert.showAndWait();
			}
			
			vetoroidtemp=oidtemp.split("\\.");
			
			if(vetoroid[7].equals(vetoroidtemp[6])) {
				SnmpGet Get = new SnmpGet(oid, gerente);
				saida = saida+Get.get()+"\n";
				oid=oidtemp;
			}else {
				continua=false;
			}
		}
		saida=saida+"\n____________________________________________\n";
		return saida;
	}

}
