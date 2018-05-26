package snmp;

import java.io.IOException;
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

public class snmpGetBulkEX {


	public static void main(String[] args) throws IOException, InterruptedException {
	    Snmp snmp = new Snmp(new DefaultUdpTransportMapping());
	    snmp.listen();

	    CommunityTarget target = new CommunityTarget();
	    target.setCommunity(new OctetString("public"));
	    target.setVersion(SnmpConstants.version2c);
	    target.setAddress(new UdpAddress("127.0.0.1/161"));
	    target.setTimeout(3000);    //3s
	    target.setRetries(1);

	    PDU pdu = new PDU();
	    pdu.setType(PDU.GETBULK);
	    pdu.setMaxRepetitions(3); 
	    pdu.setNonRepeaters(2);
	    VariableBinding[] array = {new VariableBinding(new OID(".1.3.6.1.2.1.1.1.0")),  //.1.3.6.1.2.1
                				   new VariableBinding(new OID(".1.3.6.1.2.1.1.2.0")),
	                               new VariableBinding(new OID(".1.3.6.1.2.1.1.3.0")),
	                               new VariableBinding(new OID(".1.3.6.1.2.1.1.4.0")),
	                               new VariableBinding(new OID(".1.3.6.1.2.1.1.5.0")),
	                               new VariableBinding(new OID(".1.3.6.1.2.1.1.6.0"))
	                               };
	    pdu.addAll(array);

	    //pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.2000.1.2.5.1.3"))); 

	    ResponseEvent responseEvent = snmp.send(pdu, target);
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
	}
}

