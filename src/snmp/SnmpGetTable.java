package snmp;

import java.util.ArrayList;
import java.util.List;

import org.snmp4j.PDU;
import org.snmp4j.Snmp;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class SnmpGetTable {
	private String ip, porta, comunidade, address, oid;
	private Snmp snmp = null;
	private PDU pdu = null;
	private TableView<ObservableList<String>> tabela;
	
	private static final int N_COLS = 5;
    private static final int N_ROWS = 1_000;
	
	public SnmpGetTable(String oid, SnmpManager gerente) {
		this.ip=gerente.getIp();
		this.porta=gerente.getPorta();
		this.comunidade=gerente.getComunidade();
		this.oid=oid;
		
		
		TestDataGenerator dataGenerator = new TestDataGenerator();

        tabela = new TableView<>();

        // add columns
        List<String> columnNames = dataGenerator.getNext(N_COLS);
        for (int i = 0; i < columnNames.size(); i++) {
            final int finalIdx = i;
            TableColumn<ObservableList<String>, String> column = new TableColumn<>(
                    columnNames.get(i)
            );
            column.setCellValueFactory(param ->
                    new ReadOnlyObjectWrapper<>(param.getValue().get(finalIdx))
            );
            tabela.getColumns().add(column);
        }

        // add data
        for (int i = 0; i < N_ROWS; i++) {
        	tabela.getItems().add(
                    FXCollections.observableArrayList(
                            dataGenerator.getNext(N_COLS)
                    )
            );
        }

        tabela.setPrefHeight(200);
		
	}
	
	private static class TestDataGenerator {
        private static final String[] LOREM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc tempus cursus diam ac blandit. Ut ultrices lacus et mattis laoreet. Morbi vehicula tincidunt eros lobortis varius. Nam quis tortor commodo, vehicula ante vitae, sagittis enim. Vivamus mollis placerat leo non pellentesque. Nam blandit, odio quis facilisis posuere, mauris elit tincidunt ante, ut eleifend augue neque dictum diam. Curabitur sed lacus eget dolor laoreet cursus ut cursus elit. Phasellus quis interdum lorem, eget efficitur enim. Curabitur commodo, est ut scelerisque aliquet, urna velit tincidunt massa, tristique varius mi neque et velit. In condimentum quis nisi et ultricies. Nunc posuere felis a velit dictum suscipit ac non nisl. Pellentesque eleifend, purus vel consequat facilisis, sapien lacus rutrum eros, quis finibus lacus magna eget est. Nullam eros nisl, sodales et luctus at, lobortis at sem.".split(" ");

        private int curWord = 0;

        List<String> getNext(int nWords) {
            List<String> words = new ArrayList<>();

            for (int i = 0; i < nWords; i++) {
                if (curWord == Integer.MAX_VALUE) {
                    curWord = 0;
                }

                words.add(LOREM[curWord % LOREM.length]);
                curWord++;
            }

            return words;
        }
    }
	
	public TableView<ObservableList<String>> gettable() {
		return tabela;
	}
	
	
	
}
