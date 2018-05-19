package gui;

import java.net.URL;
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
import snmp.SnmpGet;
import snmp.SnmpWalk;
import util.Valida;

public class TelaController implements Initializable{
	ObservableList<String> itenscbOperacao = FXCollections.observableArrayList("Get","GetNext","Set","GetBulk","Walk","GetTable","GetDelta");
	Valida validador = Valida.getInstance();
	int porta;
	// Itens de Menu

	@FXML // fx:id="miClose"
	private MenuItem miClose; // Value injected by FXMLLoader

	@FXML // fx:id="miSobre"
	private MenuItem miSobre; // Value injected by FXMLLoader    


	// Itens e ações na tela

	@FXML // fx:id="cbOperacao"
	private ComboBox<String> cbOperacao; // Value injected by FXMLLoader

	@FXML // fx:id="taResult"
	private TextArea taResult; // Value injected by FXMLLoader

	@FXML // fx:id="btExecuta"
	private Button btExecuta; // Value injected by FXMLLoader

	@FXML // fx:id="tvMIB"
	private TreeView<String> tvMIB; // Value injected by FXMLLoader

	@FXML // fx:id="tfIp"
	private TextField tfIp; // Value injected by FXMLLoader

	@FXML // fx:id="tfPorta"
	private TextField tfPorta; // Value injected by FXMLLoader

	@FXML // fx:id="tfComunidade"
	private TextField tfComunidade; // Value injected by FXMLLoader


	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		cbOperacao.setItems(itenscbOperacao);
		cbOperacao.setTooltip(new Tooltip("Selecione a operação desejada"));

		// Root Item
		TreeItem<String> treeroot= new TreeItem<>("MIB II");
		treeroot.setExpanded(true);
		tvMIB.setRoot(treeroot);

		// Itens
		TreeItem<String> item1 = new TreeItem<String>("teste1");
		TreeItem<String> item2 = new TreeItem<String>("teste2");

		// Add to Root
		treeroot.getChildren().addAll(item1, item2);




	}

	// Ações tela
	@FXML
	void executar(ActionEvent event) throws Exception {
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
					String op = cbOperacao.getSelectionModel().getSelectedItem();
					
					try {
						switch (op){
						case "Get":
							SnmpGet snmpGet=new SnmpGet();
							taResult.setText(snmpGet.snmpGet());
							break;

						case "GetNext":
							taResult.setText("GetNext");
							break;

						case "Set":
							taResult.setText("Set");
							break;

						case "GetBulk":
							taResult.setText("GetBulk");
							break;

						case "Walk":
							SnmpWalk snmpWalk=new SnmpWalk();

							taResult.setText(snmpWalk.snmpWalk());


							break;

						case "GetTable":
							taResult.setText("GetTable");
							break;

						case "GetDelta":
							taResult.setText("GetDelta");
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
				}
			}
		}
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
	void fechar(ActionEvent event) {
		Platform.exit();
	}


}
