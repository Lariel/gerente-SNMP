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
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;
import snmp.SnmpGet;
import snmp.SnmpWalk;

public class TelaController implements Initializable{
	ObservableList<String> itenscbOperacao = FXCollections.observableArrayList("Get","GetNext","Set","GetBulk","Walk","GetTable","GetDelta");

	// Itens de Menu

	@FXML // fx:id="miClose"
	private MenuItem miClose; // Value injected by FXMLLoader

	@FXML // fx:id="miSobre"
	private MenuItem miSobre; // Value injected by FXMLLoader    

	
	// Ações na tela

	@FXML // fx:id="cbOperacao"
	private ComboBox<String> cbOperacao; // Value injected by FXMLLoader

	@FXML // fx:id="taResult"
	private TextArea taResult; // Value injected by FXMLLoader

	@FXML // fx:id="btExecuta"
	private Button btExecuta; // Value injected by FXMLLoader

	@FXML // fx:id="tvMIB"
	private TreeView<String> tvMIB; // Value injected by FXMLLoader




	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		cbOperacao.setItems(itenscbOperacao);
		cbOperacao.setTooltip(new Tooltip("Selecione a operação desejada"));
		TreeItem<String> t1= new TreeItem<>("MIB II");
		
		tvMIB.setRoot(t1);

	}

	// Ações tela
	@FXML
	void executar(ActionEvent event) throws Exception {
		String op = cbOperacao.getSelectionModel().getSelectedItem();

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
