package util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class Valida {
	private static Valida validador = null;
	private String ip, comunidade;
	private int porta;
	
	private Valida() {
		
	}
	
	public static Valida getInstance() {
		if(validador==null) {
			validador=new Valida();
		}
		return validador;
	}
	
	public boolean validarIp(String ip) {
        if (ip == null) { return false; }
        if (ip.trim().equals("")) { return false; }
        if (ip.indexOf("-") >= 0) { return false; }
        String[] strPartes = ip.replace('.', '-').split("-");
       // if (strPartes.length != intPartes) { return false; }
        for (int i = 0; i < strPartes.length; i++) {
            String strPedaco = strPartes[i];
            if (strPedaco == null) { return false; }
            if (strPedaco.trim().equals("")) { return false; }
            try {
                int intPedaco = Integer.parseInt(strPedaco);
                if ((intPedaco == 0) || (intPedaco >= 254)) { return false; }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }
	
	public boolean validarPorta(String porta) {
		try {
			this.porta=Integer.parseInt(porta);
		} catch (NumberFormatException e) {
			return false;
		}
		
		if(this.porta==0) {
			return false;
		}else return true;
		
	}
	
	public boolean validarComunidade(String comunidade) {
		this.comunidade=comunidade;
		if(
			comunidade.equals(null) ||
			comunidade.equals("")
			) {
			return false;
		}
		return true;
	}
	
	
	
}
