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
import javafx.beans.value.ChangeListener;
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
	private TreeItem<Oid> mibtreeroot, folder, simple, table, editable;

	private ArrayList<Oid> listFolder, listSimple, listTable, listEditable;

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

		//Carregamento dos itens da árvore lateral
		// Root Item
		mibtreeroot= new TreeItem<Oid>(new Oid("mib-2", ".1.3.6.1.2.1"));
		mibtreeroot.setExpanded(true);
		tvMIB.setRoot(mibtreeroot);

		tree=new MibTree();

		listFolder = tree.getFolder_oids();
		listSimple = tree.getSimple_oids();
		listTable = tree.getTables_oids();
		listEditable = tree.getEditable_oids();

		String[] oidFolder, oidSimple, oidTable, oidEditable;

		for(int i=0; i<listFolder.size();i++) {  // preencher pastas na árvore
			folder = new TreeItem<Oid>(listFolder.get(i));
			mibtreeroot.getChildren().add(folder);

			oidFolder=listFolder.get(i).getOid().split("\\."); //Split não trabalha com "." 
			
			for(int j=0; j<listSimple.size();j++) { // preencher OIDs folha na árvore
				oidSimple=listSimple.get(j).getOid().split("\\.");
				
				if(oidFolder[7].equals(oidSimple[7])) {
					simple = new TreeItem<Oid>(listSimple.get(j));
					folder.getChildren().add(simple);
				}
			}
			
			for(int j=0; j<listTable.size();j++) { // preencher tabelas na árvore
				oidTable=listTable.get(j).getOid().split("\\.");
				
				if(oidFolder[7].equals(oidTable[7])) {
					table = new TreeItem<Oid>(listTable.get(j));
					folder.getChildren().add(table);
				}
			}
			
			for(int j=0; j<listEditable.size();j++) { // preencher OIDs editaveis na árvore
				oidEditable=listEditable.get(j).getOid().split("\\.");
				
				if(oidFolder[7].equals(oidEditable[7])) {
					editable = new TreeItem<Oid>(listEditable.get(j));
					folder.getChildren().add(editable);
				}
			}

		}
		
		tvMIB.getSelectionModel().selectedItemProperty().addListener(
				(observable, oldValu, newValue) -> {tfOID.setText(newValue.getValue().getOid()); // JDK 8+ lambda exp
				});


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
					cbOperacao.setDisable(false);
					tfOID.setDisable(false);
					btExecuta.setDisable(false);
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
