package snmp;


import java.io.IOException;
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

			// Create Target Address object
			CommunityTarget comtarget = new CommunityTarget();
			comtarget.setCommunity(new OctetString(comunidade));
			comtarget.setVersion(SnmpConstants.version2c);
			comtarget.setAddress(new UdpAddress(ip + "/" + porta));
			comtarget.setRetries(2);
			comtarget.setTimeout(1000);

			// Create the PDU object
			pdu = new PDU();
			pdu.add(new VariableBinding(new OID(oid)));
			pdu.setType(PDU.GET);
			pdu.setRequestID(new Integer32(1));

			// Create Snmp object for sending data to Agent
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
					alert.setHeaderText("SnmpManager linha 89");
					alert.setContentText("Error Status = " + errorStatus+"\nError Index = " + errorIndex+"\nError Status Text = " + errorStatusText);
					Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
					alert.showAndWait();	
				}
			}else{
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Alerta");
				alert.setHeaderText("SnmpManager linha 93");
				alert.setContentText("Timeout");
				Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
				alert.showAndWait();	
			}
			snmp.close();

		} catch (Exception e) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Alerta");
			alert.setHeaderText("SnmpManager linha 103");
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

			// Create Target Address object
			CommunityTarget comtarget = new CommunityTarget();
			comtarget.setCommunity(new OctetString(comunidade));
			comtarget.setVersion(SnmpConstants.version2c);
			comtarget.setAddress(new UdpAddress(ip + "/" + porta));
			comtarget.setRetries(2);
			comtarget.setTimeout(1000);

			// Create the PDU object
			pdu = new PDU();
			pdu.add(new VariableBinding(new OID(oid)));
			pdu.setRequestID(new Integer32(1));
			pdu.setType(PDU.GETNEXT);			

			// Create Snmp object for sending data to Agent
			snmp = new Snmp(transport);

			ResponseEvent response = snmp.getNext(pdu, comtarget);

			if (response != null) {
				PDU responsePDU = response.getResponse();

				int errorStatus = responsePDU.getErrorStatus();
				int errorIndex = responsePDU.getErrorIndex();
				String errorStatusText = responsePDU.getErrorStatusText();

				if (errorStatus == PDU.noError)
				{
					//saida=responsePDU.getVariableBindings().get(0).getOid().toString();  - utilizar se quiser ler o OID
					saida=responsePDU.getVariableBindings().get(0).getVariable().toString();
					//saida=responsePDU.getVariableBindings().toString();

				}
				else
				{
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

	public String getbulk() {
		saida="get bulk";
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

	public String walk(String iod) {
		try {
			// Create Target Address object
			CommunityTarget comtarget = new CommunityTarget();
			comtarget.setCommunity(new OctetString(comunidade));
			comtarget.setVersion(SnmpConstants.version2c);
			comtarget.setAddress(new UdpAddress(ip + "/" + porta));
			comtarget.setRetries(2);
			comtarget.setTimeout(1000);
			
			Map<String, String> result = doWalk(".1.3.6.1.2.1.2.2", comtarget); // ifTable, mib-2 interfaces

			for (Map.Entry<String, String> entry : result.entrySet()) {
				if (entry.getKey().startsWith(".1.3.6.1.2.1.2.2.1.2.")) {
					saida=saida+"ifDescr" + entry.getKey().replace(".1.3.6.1.2.1.2.2.1.2", "") + ": " + entry.getValue();
					if(saida!=null) {
						saida=saida+"ifDescr" + entry.getKey().replace(".1.3.6.1.2.1.2.2.1.2", "") + ": " + entry.getValue()+"\n";
					}else {
						saida="ifDescr" + entry.getKey().replace(".1.3.6.1.2.1.2.2.1.2", "") + ": " + entry.getValue()+"\n";
					}				
				}
				if (entry.getKey().startsWith(".1.3.6.1.2.1.2.2.1.3.")) {
					//System.out.println("ifType" + entry.getKey().replace(".1.3.6.1.2.1.2.2.1.3", "") + ": " + entry.getValue());
					if(saida!=null) {
						saida=saida+"ifType" + entry.getKey().replace(".1.3.6.1.2.1.2.2.1.3", "") + ": " + entry.getValue()+"\n";	
					}else {
						saida="ifType" + entry.getKey().replace(".1.3.6.1.2.1.2.2.1.3", "") + ": " + entry.getValue()+"\n";
					}
				}
			}
		} catch (Exception e) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Alerta");
			alert.setHeaderText("SnmpManager linha 220");
			alert.setContentText("Erro ao enviar requisição para o OID: "+iod+"\n Causa: "+e.toString());
			Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
			alert.showAndWait();
		}
		return saida;
	}

	public Map<String, String> doWalk(String tableOid, Target target) throws IOException {
		Map<String, String> result = new TreeMap<>();
		TransportMapping<? extends Address> transport = new DefaultUdpTransportMapping();
		Snmp snmp = new Snmp(transport);
		transport.listen();

		TreeUtils treeUtils = new TreeUtils(snmp, new DefaultPDUFactory());
		List<TreeEvent> events = treeUtils.getSubtree(target, new OID(tableOid));
		if (events == null || events.size() == 0) {
			System.out.println("Não foi possível ler a tabela");
			return result;
		}

		for (TreeEvent event : events) {
			if (event == null) {
				continue;
			}
			if (event.isError()) {
				System.out.println("Erro na tabela OID [" + tableOid + "] " + event.getErrorMessage());
				continue;
			}

			VariableBinding[] varBindings = event.getVariableBindings();
			if (varBindings == null || varBindings.length == 0) {
				continue;
			}
			for (VariableBinding varBinding : varBindings) {
				if (varBinding == null) {
					continue;
				}

				result.put("." + varBinding.getOid().toString(), varBinding.getVariable().toString());
			}

		}
		snmp.close();
		return result;
	}

}