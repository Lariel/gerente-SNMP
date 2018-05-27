package snmp;

import java.util.Vector;

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

public class SnmpGetBulk {
	private String ip, porta, comunidade, address, oid;
	private Snmp snmp = null;
	private PDU pdu = null;
	
	public SnmpGetBulk(String oid, SnmpManager gerente) {
		this.ip=gerente.getIp();
		this.porta=gerente.getPorta();
		this.comunidade=gerente.getComunidade();
		this.oid=oid;
	}
	
	/**
	 * 
	 * @param int n non-repeaters
	 * @param int m max-repetitions
	 * @param OID
	 */
	public String getbulk(int n, int m) {
		
		String saida="";

		try {
			snmp = new Snmp(new DefaultUdpTransportMapping());
			snmp.listen();

			// criação do objeto alvo da conexão
			CommunityTarget comtarget = new CommunityTarget();
			comtarget.setCommunity(new OctetString(comunidade));
			comtarget.setVersion(SnmpConstants.version2c);
			comtarget.setAddress(new UdpAddress(ip + "/" + porta));
			comtarget.setRetries(2);
			comtarget.setTimeout(3000);    //3s

			// Criado PDU
			pdu = new PDU();
			pdu.setType(PDU.GETBULK);
			pdu.setMaxRepetitions(m); 
			pdu.setNonRepeaters(n);
			pdu.add(new VariableBinding(new OID(oid)));
			pdu.setRequestID(new Integer32(1));
			System.out.println("PDU size "+pdu.size());
			/*

			VariableBinding[] array = {new VariableBinding(new OID(".1.3.6.1.2.1.1.1.0")),  //.1.3.6.1.2.1
					new VariableBinding(new OID(".1.3.6.1.2.1.1.2.0")),
					new VariableBinding(new OID(".1.3.6.1.2.1.1.3.0")),
					new VariableBinding(new OID(".1.3.6.1.2.1.1.4.0")),
					new VariableBinding(new OID(".1.3.6.1.2.1.1.5.0")),
					new VariableBinding(new OID(".1.3.6.1.2.1.1.6.0"))
			};
			pdu.addAll(array);


			 */ 

			ResponseEvent responseEvent = snmp.send(pdu, comtarget);
			PDU response = responseEvent.getResponse();

			if (response == null) {
				System.out.println("TimeOut...");
			} else {
				if (response.getErrorStatus() == PDU.noError) {
					Vector<? extends VariableBinding> vbs = response.getVariableBindings();
					for (VariableBinding vb : vbs) {
						System.out.println(vb.getVariable().toString());
					}
				} else {
					System.out.println("Error:" + response.getErrorStatusText());
				}
			}
		} catch (Exception e){
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Alerta");
			alert.setHeaderText("deu merda");
			alert.setContentText("Erro ao enviar requisição para o OID: "+oid+"\n Causa: "+e.toString());
			Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
			alert.showAndWait();
		}
		
		return saida;
	}
}

/*

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

*/