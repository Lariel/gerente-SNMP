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
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;

public class TelaController implements Initializable{
	ObservableList<String> itenscbOperacao = FXCollections.observableArrayList("Get","GetNext","Set","GetBulk","Walk","GetTable","GetDelta");


	@FXML // fx:id="lvMIB"
	private ListView<?> lvMIB; // Value injected by FXMLLoader

	@FXML // fx:id="cbOperacao"
	private ComboBox<String> cbOperacao; // Value injected by FXMLLoader

	@FXML // fx:id="btExecuta"
	private Button btExecuta; // Value injected by FXMLLoader

	@FXML // fx:id="miClose"
	private MenuItem miClose; // Value injected by FXMLLoader

	@FXML // fx:id="miSobre"
	private MenuItem miSobre; // Value injected by FXMLLoader    


	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		cbOperacao.setItems(itenscbOperacao);
		cbOperacao.setTooltip(new Tooltip("Selecione a operação desejada"));

	}
	
	// Ações tela
	@FXML
	void executar(ActionEvent event) {
		String op = cbOperacao.getSelectionModel().getSelectedItem();
		
		switch (op){
			case "Get":
				 System.out.println("Get");
			break;
			
			case "GetNext":
				System.out.println("GetNext");
			break;
			
			case "Set":
				System.out.println("Set");
			break;
			
			case "GetBulk":
				System.out.println("GetBulk");
			break;
			
			case "Walk":
				System.out.println("Walk");
			break;
			
			case "GetTable":
				System.out.println("GetTable");
			break;
			
			case "GetDelta":
				System.out.println("GetDelta");
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
