package gui;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.ResourceBundle;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;

import snmp.SnmpManager;
import util.Valida;

public class TelaController implements Initializable{
	private ObservableList<String> itenscbOperacao = FXCollections.observableArrayList("Get","GetNext","Set","GetBulk","Walk","GetTable","GetDelta");
	private Valida validador = Valida.getInstance();
	private HashMap<String, String> tables_oids;
	private HashMap<String, String> simple_oids;
	private HashMap<String, String> editable_oids;
	private int porta;
	private boolean gerenteIniciado=false;
	private SnmpManager gerente;
	// Itens de Menu

	@FXML // fx:id="miClose"
	private MenuItem miClose; // Value injected by FXMLLoader
	
	@FXML // fx:id="miLimpaParams"
    private MenuItem miLimpaParams; // Value injected by FXMLLoader

	@FXML // fx:id="miTeste"
	private MenuItem miTeste; // Value injected by FXMLLoader

	@FXML // fx:id="miSobre"
	private MenuItem miSobre; // Value injected by FXMLLoader    


	// Itens e ações na tela

	@FXML // fx:id="cbOperacao"
	private ComboBox<String> cbOperacao; // Value injected by FXMLLoader

	@FXML // fx:id="taResult"
	private TextArea taResult; // Value injected by FXMLLoader

	@FXML // fx:id="btExecuta"
	private Button btExecuta; // Value injected by FXMLLoader

    @FXML // fx:id="btGerenciar"
    private Button btGerenciar; // Value injected by FXMLLoader
    
	@FXML // fx:id="btLimpaResults"
	private Button btLimpaResults; // Value injected by FXMLLoader

	@FXML // fx:id="tvMIB"
	private TreeView<String> tvMIB; // Value injected by FXMLLoader

	@FXML // fx:id="tfIp"
	private TextField tfIp; // Value injected by FXMLLoader

	@FXML // fx:id="tfPorta"
	private TextField tfPorta; // Value injected by FXMLLoader

	@FXML // fx:id="tfComunidade"
	private TextField tfComunidade; // Value injected by FXMLLoader
    
	@FXML // fx:id="tfOID"
    private TextField tfOID; // Value injected by FXMLLoader


	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		cbOperacao.setItems(itenscbOperacao);
		cbOperacao.setTooltip(new Tooltip("Selecione a operação desejada"));
		tfIp.setTooltip(new Tooltip("IP do host"));
		tfPorta.setTooltip(new Tooltip("porta 161"));
		tfComunidade.setTooltip(new Tooltip("public"));
		taResult.setText("");
		
		
		tables_oids = new HashMap<String, String>();
		simple_oids = new HashMap<String, String>();
		editable_oids = new HashMap<String, String>();
		addTableOids();
		addSimpleOids();
		addEditableOids();
		

		// Root Item
		TreeItem<String> treeroot= new TreeItem<>("MIB II");
		treeroot.setExpanded(true);
		tvMIB.setRoot(treeroot);

		// Itens
		TreeItem<String> system = new TreeItem<String>("system");
		TreeItem<String> interfaces = new TreeItem<String>("interfaces");
		TreeItem<String> at = new TreeItem<String>("at");
		TreeItem<String> ip = new TreeItem<String>("ip");
		TreeItem<String> icmp = new TreeItem<String>("icmp");
		TreeItem<String> tcp = new TreeItem<String>("tcp");
		TreeItem<String> udp = new TreeItem<String>("udp");
		TreeItem<String> egp = new TreeItem<String>("egp");
		TreeItem<String> transmission = new TreeItem<String>("transmission");
		TreeItem<String> snmp = new TreeItem<String>("snmp");
		TreeItem<String> host = new TreeItem<String>("host");


		TreeItem<String> sysDescr = new TreeItem<String>("sysDescr");
		TreeItem<String> sysObjectID = new TreeItem<String>("sysObjectID");


		// Add Root
		treeroot.getChildren().addAll(system, interfaces, at, ip, icmp, tcp, udp, egp, transmission, snmp, host);
		system.getChildren().addAll(sysDescr,sysObjectID);

	}

	// Ações tela

    @FXML
    void gerenciar(ActionEvent event) throws Exception {
    	//definir a criação do gerente aqui
    	
    	if(validador.validarIp(tfIp.getText())==false) { //se for um IP inválido mostra o alerta
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Alerta");
			alert.setHeaderText("Atenção");
			alert.setContentText("Informe um IP válido");
			Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
			alert.showAndWait();
		}else { // senão, se for válido avança
			if(validador.validarPorta(tfPorta.getText())==false) { //se for uma porta inválida mostra o alerta
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Alerta");
				alert.setHeaderText("Atenção");
				alert.setContentText("Campo porta deve conter um valor de porta válido!");
				Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
				alert.showAndWait();
			}else { // senão, se for válido avança
				if(validador.validarComunidade(tfComunidade.getText())==false) { //se for uma comunidade inválida mostra o alerta
					Alert alert = new Alert(AlertType.WARNING);
					alert.setTitle("Alerta");
					alert.setHeaderText("Atenção");
					alert.setContentText("Preencha corretamente a comunidade!");
					Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
					alert.showAndWait();
				}else { // senão, se for válido avança
					gerente = new SnmpManager(tfIp.getText(), tfPorta.getText(), tfComunidade.getText());
					
					gerenteIniciado=true;
					btGerenciar.setText("Gerenciando");
				}
			}
		}
    }
	
	
	@FXML
	void executar(ActionEvent event) throws Exception {
		if(gerenteIniciado) { //gerente snmp foi criado
			if(validador.validarOID(tfOID.getText())) { //OID do objeto foi inforado
				String op = cbOperacao.getSelectionModel().getSelectedItem();
				try {
					switch (op){
					case "Get":
						//taResult.setText(taResult.getText()+"\n"+gerente.get(".1.3.6.1.2.1.1.3.0"));
						//taResult.clear();
						taResult.setText(taResult.getText()+"\n"+gerente.get(tfOID.getText()));
						break;

					case "GetNext":
						taResult.setText(taResult.getText()+"\n"+gerente.getnext(tfOID.getText()));
						break;

					case "Set":
						taResult.setText(taResult.getText()+"\n"+gerente.set());
						break;

					case "GetBulk":
						taResult.setText(taResult.getText()+"\n"+gerente.getbulk());
						break;

					case "Walk":
						taResult.clear();
						//taResult.setText(taResult.getText()+"\n"+gerente.walk(".1.3.6.1.2.1.2.2"));
						taResult.setText(gerente.walk(tfOID.getText()));
						
						break;

					case "GetTable":
						taResult.setText(taResult.getText()+"\n"+gerente.gettable());
						
						break;

					case "GetDelta":
						taResult.setText(taResult.getText()+"\n"+gerente.getdelta());
						
						break;

					}
				} catch (NullPointerException e) {  //exibe alerta caso nenhuma opção seja selecionada para as operações
					Alert alert = new Alert(AlertType.WARNING);
					alert.setTitle("Alerta");
					alert.setHeaderText("Atenção");
					alert.setContentText("Selecione a operação desejada!");
					Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
					alert.showAndWait();
				}
			}else {
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Alerta");
				alert.setHeaderText("Atenção");
				alert.setContentText("Informe o OID do objeto para gerenciar");
				Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
				alert.showAndWait();
			}
			
		}else {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Alerta");
			alert.setHeaderText("Atenção");
			alert.setContentText("Informe os dados do host");
			Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
			alert.showAndWait();
		}
	}

	@FXML
	void limpaResultados(ActionEvent event) {
		taResult.clear();
		
	}

	@FXML
	void limpaParametros(ActionEvent event) {
		tfIp.clear();
		tfPorta.clear();
		tfComunidade.clear();
		gerenteIniciado=false;
		btGerenciar.setText("Gerenciar");
	}

	// Itens de menu

	@FXML
	void sobre(ActionEvent event) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Sobre");
		alert.setHeaderText("T1 - Gerencia de Redes");
		alert.setContentText("Desenvolvido por: \n Guilherme Dohms\n Lariel Negreiros \n \nProfessora: \n Cristina Nunes");
		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		alert.showAndWait();
	}

	@FXML
	void teste(ActionEvent event) {
		tfIp.setText("127.0.0.1");
		tfPorta.setText("161");
		tfComunidade.setText("public");
		tfOID.setText(".1.3.6.1.2.1.1.1.0");
	}

	@FXML
	void fechar(ActionEvent event) {
		Platform.exit();
	}

	// Preenchimento da árvore MIB2

	private void addTableOids() {
		this.tables_oids.put("ifTable", ".1.3.6.1.2.1.2.2");
		this.tables_oids.put("atTable", ".1.3.6.1.2.1.3.1");
		this.tables_oids.put("ipAddrTable", ".1.3.6.1.2.1.4.20");
		this.tables_oids.put("ipRouteTable", ".1.3.6.1.2.1.4.21");
		this.tables_oids.put("ipNetToMediaTable", ".1.3.6.1.2.1.4.22");
		this.tables_oids.put("tcpConnTable", ".1.3.6.1.2.1.6.13");
		this.tables_oids.put("udpTable", ".1.3.6.1.2.1.7.5");
		this.tables_oids.put("egpNeighTable", ".1.3.6.1.2.1.8.5");
		this.tables_oids.put("hrStorageTable", ".1.3.6.1.2.1.25.2.3");
		this.tables_oids.put("hrDeviceTable", ".1.3.6.1.2.1.25.3.2");
		this.tables_oids.put("hrProcessorTable", ".1.3.6.1.2.1.25.3.3");
		this.tables_oids.put("hrNetworkTable", ".1.3.6.1.2.1.25.3.4");
		this.tables_oids.put("hrPrinterTable", ".1.3.6.1.2.1.25.3.5");
		this.tables_oids.put("hrDiskStorageTable", ".1.3.6.1.2.1.25.3.6");
		this.tables_oids.put("hrPartitionTable", ".1.3.6.1.2.1.25.3.7");
		this.tables_oids.put("hrFSTable", ".1.3.6.1.2.1.25.3.8");
		this.tables_oids.put("hrSWRunTable", ".1.3.6.1.2.1.25.4.2");
		this.tables_oids.put("hrSWRunPerfTable", ".1.3.6.1.2.1.25.5.1");
		this.tables_oids.put("hrSWInstalledTable", ".1.3.6.1.2.1.25.6.3");
	}

	private void addSimpleOids() {
		this.simple_oids.put("mib-2", ".1.3.6.1.4.1.77.1.4.1.0");
		this.simple_oids.put("system", ".1.3.6.1.2.1.1");
		this.simple_oids.put("interfaces", ".1.3.6.1.2.1.2");
		this.simple_oids.put("at", ".1.3.6.1.2.1.3");
		this.simple_oids.put("ip", ".1.3.6.1.2.1.4");
		this.simple_oids.put("icmp", ".1.3.6.1.2.1.5");
		this.simple_oids.put("tcp", ".1.3.6.1.2.1.6");
		this.simple_oids.put("udp", ".1.3.6.1.2.1.7");
		this.simple_oids.put("egp", ".1.3.6.1.2.1.8");
		this.simple_oids.put("snmp", ".1.3.6.1.2.1.11");
		this.simple_oids.put("host", ".1.3.6.1.2.1.25");

		this.simple_oids.put("sysDescr", ".1.3.6.1.2.1.1.1.0");
		this.simple_oids.put("sysObjectID", ".1.3.6.1.2.1.1.2.0");
		this.simple_oids.put("sysUpTime", ".1.3.6.1.2.1.1.3.0");
		this.simple_oids.put("sysContact", ".1.3.6.1.2.1.1.4.0");
		this.simple_oids.put("sysName", ".1.3.6.1.2.1.1.5.0");
		this.simple_oids.put("sysLocation", ".1.3.6.1.2.1.1.6.0");
		this.simple_oids.put("sysServices", ".1.3.6.1.2.1.1.7.0");

		this.simple_oids.put("ifNumber", ".1.3.6.1.2.1.2.1.0");

		this.simple_oids.put("ipForwarding", ".1.3.6.1.2.1.4.1.0");
		this.simple_oids.put("ipDefaultTTL", ".1.3.6.1.2.1.4.2.0");
		this.simple_oids.put("ipInReceives", ".1.3.6.1.2.1.4.3.0");
		this.simple_oids.put("ipInHdrErrors", ".1.3.6.1.2.1.4.4.0");
		this.simple_oids.put("ipInAddrErrors", ".1.3.6.1.2.1.4.5.0");
		this.simple_oids.put("ipForwDatagrams", ".1.3.6.1.2.1.4.6.0");
		this.simple_oids.put("ipInUnknownProtos", ".1.3.6.1.2.1.4.7.0");
		this.simple_oids.put("ipInDiscards", ".1.3.6.1.2.1.4.8.0");
		this.simple_oids.put("ipInDelivers", ".1.3.6.1.2.1.4.9.0");
		this.simple_oids.put("ipOutRequests", ".1.3.6.1.2.1.4.10.0");
		this.simple_oids.put("ipOutDiscards", ".1.3.6.1.2.1.4.11.0");
		this.simple_oids.put("ipOutNoRoutes", ".1.3.6.1.2.1.4.12.0");
		this.simple_oids.put("ipReasmTimeout", ".1.3.6.1.2.1.4.13.0");
		this.simple_oids.put("ipReasmReqds", ".1.3.6.1.2.1.4.14.0");
		this.simple_oids.put("ipReasmOKs", ".1.3.6.1.2.1.4.15.0");
		this.simple_oids.put("ipReasmFails", ".1.3.6.1.2.1.4.16.0");
		this.simple_oids.put("ipFragOKs", ".1.3.6.1.2.1.4.17.0");
		this.simple_oids.put("ipFragFails", ".1.3.6.1.2.1.4.18.0");
		this.simple_oids.put("ipFragCreates", ".1.3.6.1.2.1.4.19.0");
		this.simple_oids.put("ipRoutingDiscards", ".1.3.6.1.2.1.4.23.0");

		this.simple_oids.put("icmpInMsgs", ".1.3.6.1.2.1.5.1.0");
		this.simple_oids.put("icmpInMsgs", ".1.3.6.1.2.1.5.1.0");
		this.simple_oids.put("icmpInErrors", ".1.3.6.1.2.1.5.2.0");
		this.simple_oids.put("icmpInDestUnreachs", ".1.3.6.1.2.1.5.3.0");
		this.simple_oids.put("icmpInTimeExcds", ".1.3.6.1.2.1.5.4.0");
		this.simple_oids.put("icmpInParmProbs", ".1.3.6.1.2.1.5.5.0");
		this.simple_oids.put("icmpInSrcQuenchs", ".1.3.6.1.2.1.5.6.0");
		this.simple_oids.put("icmpInRedirects", ".1.3.6.1.2.1.5.7.0");
		this.simple_oids.put("icmpInEchos", ".1.3.6.1.2.1.5.8.0");
		this.simple_oids.put("icmpInEchoReps", ".1.3.6.1.2.1.5.9.0");
		this.simple_oids.put("icmpInTimestamps", ".1.3.6.1.2.1.5.10.0");
		this.simple_oids.put("icmpInTimestampReps", ".1.3.6.1.2.1.5.11.0");
		this.simple_oids.put("icmpInAddrMasks", ".1.3.6.1.2.1.5.12.0");
		this.simple_oids.put("icmpInAddrMaskReps", ".1.3.6.1.2.1.5.13.0");
		this.simple_oids.put("icmpOutMsgs", ".1.3.6.1.2.1.5.14.0");
		this.simple_oids.put("icmpOutErrors", ".1.3.6.1.2.1.5.15.0");
		this.simple_oids.put("icmpOutDestUnreachs", ".1.3.6.1.2.1.5.16.0");
		this.simple_oids.put("icmpOutTimeExcds", ".1.3.6.1.2.1.5.17.0");
		this.simple_oids.put("icmpOutParmProbs", ".1.3.6.1.2.1.5.18.0");
		this.simple_oids.put("icmpOutSrcQuenchs", ".1.3.6.1.2.1.5.19.0");
		this.simple_oids.put("icmpOutRedirects", ".1.3.6.1.2.1.5.20.0");
		this.simple_oids.put("icmpOutEchos", ".1.3.6.1.2.1.5.21.0");
		this.simple_oids.put("icmpOutEchoReps", ".1.3.6.1.2.1.5.22.0");
		this.simple_oids.put("icmpOutTimestamps", ".1.3.6.1.2.1.5.23.0");
		this.simple_oids.put("icmpOutTimestampReps", ".1.3.6.1.2.1.5.24.0");
		this.simple_oids.put("icmpOutAddrMaskReps", ".1.3.6.1.2.1.5.25.0");

		this.simple_oids.put("tcpRtoAlgorithm", ".1.3.6.1.2.1.6.1.0");
		this.simple_oids.put("tcpRtoMin", ".1.3.6.1.2.1.6.2.0");
		this.simple_oids.put("tcpRtoMax", ".1.3.6.1.2.1.6.3.0");
		this.simple_oids.put("tcpRtoMaxConn", ".1.3.6.1.2.1.6.4.0");
		this.simple_oids.put("tcpActiveOpens", ".1.3.6.1.2.1.6.5.0");
		this.simple_oids.put("tcpPassiveOpens", ".1.3.6.1.2.1.6.6.0");
		this.simple_oids.put("tcpAttemptFails", ".1.3.6.1.2.1.6.7.0");
		this.simple_oids.put("tcpEstabResets", ".1.3.6.1.2.1.6.8.0");
		this.simple_oids.put("tcpCurrEstab", ".1.3.6.1.2.1.6.9.0");
		this.simple_oids.put("tcpInSegs", ".1.3.6.1.2.1.6.10.0");
		this.simple_oids.put("tcpOutSegs", ".1.3.6.1.2.1.6.11.0");
		this.simple_oids.put("tcpRetransSegs", ".1.3.6.1.2.1.6.12.0");
		this.simple_oids.put("tcpInErrs", ".1.3.6.1.2.1.6.14.0");
		this.simple_oids.put("tcpOutRsts", ".1.3.6.1.2.1.6.15.0");

		this.simple_oids.put("udpInDatagrams", ".1.3.6.1.2.1.7.1.0");
		this.simple_oids.put("udpNoPorts", ".1.3.6.1.2.1.7.2.0");
		this.simple_oids.put("udpInErrors", ".1.3.6.1.2.1.7.3.0");
		this.simple_oids.put("udpOutDatagrams", ".1.3.6.1.2.1.7.2.0");
		this.simple_oids.put("udpNoPorts", ".1.3.6.1.2.1.7.4.0");
		this.simple_oids.put("udpInErrors", ".1.3.6.1.2.1.7.4.0");
		this.simple_oids.put("udpOutDatagrams", ".1.3.6.1.2.1.7.4.0");

		this.simple_oids.put("egpInMsgs", ".1.3.6.1.2.1.8.1.0");
		this.simple_oids.put("egpInErrors", ".1.3.6.1.2.1.8.2.0");
		this.simple_oids.put("egpOutMsgs", ".1.3.6.1.2.1.8.3.0");
		this.simple_oids.put("egpOutErrors", ".1.3.6.1.2.1.8.4.0");
		this.simple_oids.put("egpAs", ".1.3.6.1.2.1.8.6.0");

		this.simple_oids.put("transmission", ".1.3.6.1.2.1.10");

		this.simple_oids.put("snmpInPkts", ".1.3.6.1.2.1.11.1.0");
		this.simple_oids.put("snmpOutPkts", ".1.3.6.1.2.1.11.2.0");
		this.simple_oids.put("snmpInBadVersions", ".1.3.6.1.2.1.11.3.0");
		this.simple_oids.put("snmpInBadComunityNames", ".1.3.6.1.2.1.11.4.0");
		this.simple_oids.put("snmpInBadComunityUses", ".1.3.6.1.2.1.11.5.0");
		this.simple_oids.put("snmpInASNParseErrs", ".1.3.6.1.2.1.11.6.0");
		this.simple_oids.put("snmpInTooBigs", ".1.3.6.1.2.1.11.8.0");
		this.simple_oids.put("snmpInNoSuchNames", ".1.3.6.1.2.1.11.9.0");
		this.simple_oids.put("snmpInBadValues", ".1.3.6.1.2.1.11.10.0");
		this.simple_oids.put("snmpInReadOnlys", ".1.3.6.1.2.1.11.11.0");
		this.simple_oids.put("snmpInGenErrs", ".1.3.6.1.2.1.11.12.0");
		this.simple_oids.put("snmpInTotalReqVars", ".1.3.6.1.2.1.11.13.0");
		this.simple_oids.put("snmpInTotalSetVars", ".1.3.6.1.2.1.11.14.0");
		this.simple_oids.put("snmpInGetRequests", ".1.3.6.1.2.1.11.15.0");
		this.simple_oids.put("snmpInGetNexts", ".1.3.6.1.2.1.11.16.0");
		this.simple_oids.put("snmpInSetRequests", ".1.3.6.1.2.1.11.17.0");
		this.simple_oids.put("snmpInGetResponses", ".1.3.6.1.2.1.11.18.0");
		this.simple_oids.put("snmpInTraps", ".1.3.6.1.2.1.11.19.0");
		this.simple_oids.put("snmpEnableAuthenTraps", ".1.3.6.1.2.1.11.30.0");
		this.simple_oids.put("snmpOutTooBigs", ".1.3.6.1.2.1.11.20.0");
		this.simple_oids.put("snmpOutNoSuchNames", ".1.3.6.1.2.1.11.21.0");
		this.simple_oids.put("snmpOutBadValues", ".1.3.6.1.2.1.11.22.0");
		this.simple_oids.put("snmpOutGenErrs", ".1.3.6.1.2.1.11.24.0");
		this.simple_oids.put("snmpOutGetRequests", ".1.3.6.1.2.1.11.25.0");
		this.simple_oids.put("snmpOutGetNexts", ".1.3.6.1.2.1.11.26.0");
		this.simple_oids.put("snmpOutSetRequests", ".1.3.6.1.2.1.11.27.0");
		this.simple_oids.put("snmpOutGetResponses", ".1.3.6.1.2.1.11.28.0");
		this.simple_oids.put("snmpOutTraps", ".1.3.6.1.2.1.11.29.0");

		this.simple_oids.put("hrSystemUpTime", ".1.3.6.1.2.1.25.1.1.0");
		this.simple_oids.put("hrSystemDate", ".1.3.6.1.2.1.25.1.2.0");
		this.simple_oids.put("hrSystemInitialLoadDevice", ".1.3.6.1.2.1.25.1.3.0");
		this.simple_oids.put("hrSystemInitialLoadParameters", ".1.3.6.1.2.1.25.1.4.0");
		this.simple_oids.put("hrSystemNumUsers", ".1.3.6.1.2.1.25.1.5.0");
		this.simple_oids.put("hrSystemProcesses", ".1.3.6.1.2.1.25.1.6.0");
		this.simple_oids.put("hrSystemMaxProcesses", ".1.3.6.1.2.1.25.1.7.0");
		this.simple_oids.put("hrStorageTypes", ".1.3.6.1.2.1.25.2.1");
		this.simple_oids.put("hrMemorySize", ".1.3.6.1.2.1.25.1.7.0");
		this.simple_oids.put("hrDeviceTypes", ".1.3.6.1.2.1.25.3.1");
		this.simple_oids.put("hrFSTypes", ".1.3.6.1.2.1.25.3.9");
		this.simple_oids.put("hrSWOSIndex", ".1.3.6.1.2.1.25.4.1.0");
		this.simple_oids.put("hrSWInstalledLastChange", ".1.3.6.1.2.1.25.6.1.0");
		this.simple_oids.put("hrSWInstalledLastUpdateTime", ".1.3.6.1.2.1.25.6.2.0");

		this.simple_oids.put("hostResourcesMibModule", ".1.3.6.1.2.1.25.7.1");

		this.simple_oids.put("hrMIBCompliance", ".1.3.6.1.2.1.25.7.2.1");
		this.simple_oids.put("hrSystemGroup", ".1.3.6.1.2.1.25.7.3.1");
		this.simple_oids.put("hrStorageGroup", ".1.3.6.1.2.1.25.7.3.2");
		this.simple_oids.put("hrDeviceGroup", ".1.3.6.1.2.1.25.7.3.3");
		this.simple_oids.put("hrSWRunGroup", ".1.3.6.1.2.1.25.7.3.4");
		this.simple_oids.put("hrSWRunPerfGroup", ".1.3.6.1.2.1.25.7.3.5");
		this.simple_oids.put("hrSWInstalledGroup", ".1.3.6.1.2.1.25.7.3.6");

	}

	private void addEditableOids() {
		this.editable_oids.put("sysContact", ".1.3.6.1.2.1.1.4.0");
		this.editable_oids.put("sysName", ".1.3.6.1.2.1.1.5.0");
		this.editable_oids.put("sysLocation", ".1.3.6.1.2.1.1.6.0");
		this.editable_oids.put("ipForwarding", ".1.3.6.1.2.1.4.1.0");
		this.editable_oids.put("ipDefaultTTL", ".1.3.6.1.2.1.4.2.0");
		this.editable_oids.put("snmpEnableAuthenTraps", ".1.3.6.1.2.1.11.30.0");
		this.editable_oids.put("hrSystemDate", ".1.3.6.1.2.1.25.1.2.0");
		this.editable_oids.put("hrSystemInitialLoadDevice", ".1.3.6.1.2.1.25.1.3.0");
		this.editable_oids.put("hrSystemInitialLoadParameters", ".1.3.6.1.2.1.25.1.4.0");
	}


}
