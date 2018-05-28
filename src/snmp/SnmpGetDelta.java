package snmp;

import java.util.Timer;
import java.util.TimerTask;

import org.snmp4j.PDU;
import org.snmp4j.Snmp;

public class SnmpGetDelta implements Runnable{
	private SnmpManager gerente=null;
	private String ip, porta, comunidade, address, oid, saida;
	private Snmp snmp = null;
	private PDU pdu = null;
	private int n,m, cont;
	

	public SnmpGetDelta(String oid, SnmpManager gerente, int n, int m){
		this.gerente=gerente;
		this.ip=gerente.getIp();
		this.porta=gerente.getPorta();
		this.comunidade=gerente.getComunidade();
		this.oid=oid;
		this.n = n;
        this.m=m;
	}

	/**
	 * 
	 * @param int n numero amostras
	 * @param int m intervalo tempo
	 * @param OID
	 */
	public String getdelta() {
		saida="";
		SnmpGet Get = new SnmpGet(oid, gerente);
		saida=saida+Get.get();
		return saida;
	}

	@Override
	public void run() {
		for(int i=1;i<=n;i++) {
			System.out.println(getdelta());
			getdelta();
    		try {
				Thread.sleep(m*1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
		
	}

}
