package snmp;

import java.util.ArrayList;

import org.snmp4j.PDU;
import org.snmp4j.Snmp;

import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

public class SnmpManager {
	private String ip, porta, comunidade;
	private MibTree tree;

	public SnmpManager(String ip, String porta, String comunidade) throws Exception {
		this.ip=ip;
		this.porta=porta;
		this.comunidade=comunidade;
		tree=new MibTree();
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
		String saida="";
		SnmpGet Get = new SnmpGet(oid, this);
		saida = Get.get();
		return saida;
	}

	public String getnext(String oid) {
		String saida="";
		SnmpGetNext GetNext = new SnmpGetNext(oid, this);
		saida = GetNext.getnext();
		return saida;
	}

	public String getnextOid(String oid) {
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
		SnmpGetBulk GetBulk = new SnmpGetBulk(oid, this);
		saida = GetBulk.getbulk(n, m);
		return saida;
	}
	
	/**
	 * @param int n numero amostras
	 * @param int m intervalo tempo
	 * @param OID
	 * @throws InterruptedException 
	 */
	public String getdelta(int n, int m, String oid) throws InterruptedException {
		String saida="";
		//SnmpGetDelta GetDelta = new SnmpGetDelta(oid, this);
		//saida=GetDelta.getdelta(n, m, oid);
		
		Thread tGetDelta = new Thread(new SnmpGetDelta(oid, this, n, m));
		tGetDelta.start();
		
		return saida;
	}

	public TableView<ObservableList<String>> gettable(String oid) {
		TableView<ObservableList<String>> tabela = new TableView<>();
		String saida="";
		SnmpGetTable GetTable = new SnmpGetTable(oid, this);
		tabela=GetTable.gettable();
		return tabela;
	}

	public String set(String valor, String oid) {
		String saida="";
		SnmpSet Set = new SnmpSet(oid,this);
		saida=Set.set(valor);
		return saida;
	}

	public String walk(String oid) {
		String saida="";
		SnmpWalk Walk = new SnmpWalk(oid, this);
		saida = Walk.walk();
		return saida;
	}

}