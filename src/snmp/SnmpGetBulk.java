package snmp;

import java.util.ArrayList;
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
	private SnmpManager gerente=null;
	
	public SnmpGetBulk(String oid, SnmpManager gerente) {
		this.gerente=gerente;
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
		
		String saida="Non repeaters: "+n+"  |  Max repetitions: "+m+"\n\n";

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
			PDU pdu = new PDU();
		    pdu.setType(PDU.GETBULK);
		    pdu.setMaxRepetitions(m); 
		    pdu.setNonRepeaters(n);
		    
		    
		    ArrayList <VariableBinding> listaVB = new ArrayList<VariableBinding>();
		    
		    for(int i=0; i<=m;i++) { //realiza um getNext m vezes, e add cada OID encontrado na lista de VBs
		    	SnmpGetNext GetNext = new SnmpGetNext(oid, gerente);
				VariableBinding vb=new VariableBinding();
				vb.setOid(new OID(GetNext.getnextOID()));
				listaVB.add(vb);
				oid=GetNext.getnextOID();
		    }
		    pdu.addAll(listaVB);
 
		    ResponseEvent responseEvent = snmp.send(pdu, comtarget);
		    PDU response = responseEvent.getResponse();

		    if (response == null) {
			    System.out.println("TimeOut...");
		    } else {
			    if (response.getErrorStatus() == PDU.noError) {
	                Vector<? extends VariableBinding> vbs = response.getVariableBindings();
	                for (VariableBinding vb : vbs) {
	                	//saida=saida+vb.getVariable().toString()+"\n";
	                    //System.out.println(vb.getVariable().toString());
	                	
	                	saida=saida+"\nValor: "+vb.getVariable().toString()+
	                			"\nOID: "+vb.getOid().toString()+"\n";
	                	
			        }
			    } else {
			        saida="Error:" + response.getErrorStatusText();
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
		saida=saida+"\n____________________________________________\n";
		return saida;
	}
}
