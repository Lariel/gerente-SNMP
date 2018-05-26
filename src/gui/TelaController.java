package gui;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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

import snmp.MibTree;
import snmp.Oid;
import snmp.SnmpManager;

import util.Valida;

public class TelaController implements Initializable{
	private ObservableList<String> itenscbOperacao = FXCollections.observableArrayList("Get","GetNext","Set","GetBulk","Walk","GetTable","GetDelta");
	private Valida validador = Valida.getInstance();
	private int porta;
	private boolean gerenteIniciado=false;
	private SnmpManager gerente;
	private MibTree tree;
	private ArrayList<Oid> treeFolders;
	// Itens de Menu

	@FXML // fx:id="miClose"
	private MenuItem miClose; // Value injected by FXMLLoader

	@FXML // fx:id="miLimpaParams"
	private MenuItem miLimpaParams; // Value injected by FXMLLoader

	@FXML // fx:id="miTeste"
	private MenuItem miTeste; // Value injected by FXMLLoader

	@FXML // fx:id="miSobre"
	private MenuItem miSobre; // Value injected by FXMLLoader    

	@FXML // fx:id="miLoadMIB"
	private MenuItem miLoadMIB; // Value injected by FXMLLoader	

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
	private TreeView<Oid> tvMIB; // Value injected by FXMLLoader

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

		// Root Item
		TreeItem<Oid> mibtreeroot= new TreeItem<>(new Oid("mib-2", ".1.3.6.1.2.1"));
		mibtreeroot.setExpanded(true);
		tvMIB.setRoot(mibtreeroot);

		tree=new MibTree();
		treeFolders = tree.getFolder_oids();
		
		for(int i=0; i<treeFolders.size();i++) {
			TreeItem<Oid> folders = new TreeItem<Oid>(treeFolders.get(i));
		    mibtreeroot.getChildren().addAll(folders);
		}
		
		

		/*
		 * 



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
		mibtree.getChildren().addAll(system, interfaces, at, ip, icmp, tcp, udp, egp, transmission, snmp, host);
		system.getChildren().addAll(sysDescr,sysObjectID);



		 * 
		 */


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
						taResult.setText(taResult.getText()+"\n"+gerente.getbulk(1,3,tfOID.getText())); //n,m
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
	void loadMIB(ActionEvent event) {



		//URL url = getClass().getResource("RFC1213-MIB");
		//URL url = getClass().getResource("D:/teste.txt");

		//File f = new File(url.toURI());

		//File f = new File("D:\\teste.txt");  //path local destino
		//File f = new File("D:\\RFC1213-MIB");


		//System.out.println(f.getAbsolutePath());
		//System.out.println(f.getName());
		//System.out.println(f.getName().length());


		//URL url = getClass().getResource("/mibs.ietf/RFC1213-MIB");
		//File f = new File(url.getPath());
		//File f=new File("/mibs.ietf/RFC1213-MIB");
		/*
		 * 	try {
			gerente.loadMib(f);
		} catch (NullPointerException | MibLoaderException | IOException e) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Alerta");
			alert.setHeaderText("Arquivo não encontrado");
			alert.setContentText(e.getMessage());
			Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
			alert.showAndWait();
		}
		 * 
		 * 
		 */

	}

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
	void teste(ActionEvent event) throws URISyntaxException {
		tfIp.setText("127.0.0.1");
		tfPorta.setText("161");
		tfComunidade.setText("public");
		tfOID.setText(".1.3.6.1.2.1.1.1.0");

	}

	@FXML
	void fechar(ActionEvent event) {
		Platform.exit();
	}

}
