package snmp;

import org.snmp4j.PDU;
import org.snmp4j.Snmp;

public class SnmpManager {
	private String ip, porta, comunidade, address, oid;
	private Snmp snmp = null;
	private PDU pdu = null;

	public SnmpManager(String ip, String porta, String comunidade) throws Exception {
		this.ip=ip;
		this.porta=porta;
		this.comunidade=comunidade;
	}
	
	public String getIp() {
		return ip;
	}
	
	public String getPorta() {
		return porta;
	}
	
	public String getComunidade() {
		return comunidade;
	}
	
	public String get(String oid) {
		this.oid=oid;
		String saida="";
		SnmpGet Get = new SnmpGet(oid, this);
		saida = Get.get();
		return saida;
	}

	public String getnext(String oid) {
		this.oid=oid;
		String saida="";
		SnmpGetNext GetNext = new SnmpGetNext(oid, this);
		saida = GetNext.getnext();
		return saida;
	}
	
	public String getnextOid(String oid) {
		this.oid=oid;
		String saida="";
		SnmpGetNext GetNext = new SnmpGetNext(oid, this);
		saida = GetNext.getnextOID();
		return saida;
	}

	/**
	 * 
	 * @param int n non-repeaters
	 * @param int m max-repetitions
	 * @param OID
	 */
	public String getbulk(int n, int m, String oid) {
		String saida="";
		this.oid=oid;
		SnmpGetBulk GetBulk = new SnmpGetBulk(oid, this);
		saida = GetBulk.getbulk(n, m);
		return saida;
	}

	public String getdelta() {
		String saida="";
		saida="get detla";

		return saida;
	}

	public String gettable() {
		String saida="";
		saida="get table";

		return saida;
	}

	public String set() {
		String saida="";
		saida="set";

		return saida;
	}

	public String walk(String oid) {
		String saida="";
		this.oid=oid;
		SnmpWalk Walk = new SnmpWalk(oid, this);
		saida = Walk.walk();
		return saida;
	}

}