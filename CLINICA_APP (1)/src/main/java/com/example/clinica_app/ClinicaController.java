package com.example.clinica_app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.stream.Collectors;

public class ClinicaController {

    // Campos do Menu Inicial
    @FXML private TextField campoNomeUsuario;

    // Campos de Cadastro de Paciente
    @FXML private TextField campoIdPaciente;
    @FXML private TextField campoNomePaciente;
    @FXML private TextField campoIdadePaciente;
    @FXML private TextField campoContatoPaciente;

    // Campos de Cadastro de Médico
    @FXML private TextField campoIdMedico;
    @FXML private TextField campoNomeMedico;
    @FXML private TextField campoEspecialidadeMedico;

    // Campos de Cadastro de Disponibilidade
    @FXML private DatePicker dataInicioPicker;
    @FXML private TextField horaInicioField;
    @FXML private DatePicker dataFimPicker;
    @FXML private TextField horaFimField;
    @FXML private TableView<Consulta> tabelaDisponibilidades;
    @FXML private TableColumn<Consulta, LocalDateTime> colInicio;
    @FXML private TableColumn<Consulta, LocalDateTime> colFim;
    @FXML private TableColumn<Consulta, String> colStatus;

    // Variável para armazenar o ID do médico logado
    private String idMedicoLogado;

    // Sistema compartilhado
    private final SistemaAgendamento sistema = AppContext.sistema;
    //test


    // MÉTODO 1: Verifica ID digitado e direciona para tela correspondente
    @FXML
    private void verificarUsuarioEAvancar(ActionEvent event) {
        String id = campoNomeUsuario.getText().trim();

        if (id.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Digite um ID.");
            return;
        }

        if (sistema.isPaciente(id)) {
            abrirTela("/view/tela-paciente.fxml", "Área do Paciente");
        } else if (sistema.isMedico(id)) {
            AppContext.usuarioLogadoId=id; // ← salva o ID do médico logado
            abrirTela("/view/tela-medico.fxml", "Área do Médico");
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "ID não encontrado.");
        }

        // Fecha a janela atual
        Stage stageAtual = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stageAtual.close();
    }
    @FXML
    private void onCadastrarPacienteButton() {
        abrirTela("/view/cadastro-paciente.fxml", "Cadastro de Paciente");
    }
    @FXML
    private void onCadastrarMedicoButton() {
        abrirTela("/view/cadastro-medico.fxml", "Cadastro de Médico");
    }
    @FXML
    private void irparaCadastro(){
        abrirTela("/view/menu-cadastro.fxml", "Cadastro de Paciente ou Médico");
    }

    // MÉTODO 2: Cadastrar Paciente
    @FXML
    protected void onCadastrarPaciente() {
        String id = campoIdPaciente.getText().trim();
        String nome = campoNomePaciente.getText().trim();
        String idadeStr = campoIdadePaciente.getText().trim();
        String contato = campoContatoPaciente.getText().trim();

        if (id.isEmpty() || nome.isEmpty() || idadeStr.isEmpty() || contato.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Preencha todos os campos.");
            return;
        }

        int idade;
        try {
            idade = Integer.parseInt(idadeStr);
            if (idade <= 0) {
                mostrarAlerta(Alert.AlertType.WARNING, "Idade deve ser maior que zero.");
                return;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Idade inválida.");
            return;
        }

        Paciente paciente = new Paciente(id, nome, idade, contato);
        sistema.registrarPaciente(paciente);

        mostrarAlerta(Alert.AlertType.INFORMATION, "Paciente cadastrado com sucesso!");

        campoIdPaciente.clear();
        campoNomePaciente.clear();
        campoIdadePaciente.clear();
        campoContatoPaciente.clear();

        Stage stage = (Stage) campoIdPaciente.getScene().getWindow();
        stage.close();
    }

    // MÉTODO 3: Cadastrar Médico
    @FXML
    protected void onCadastrarMedico() {
        String id = campoIdMedico.getText().trim();
        String nome = campoNomeMedico.getText().trim();
        String especialidade = campoEspecialidadeMedico.getText().trim();

        if (id.isEmpty() || nome.isEmpty() || especialidade.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Preencha todos os campos.");
            return;
        }

        Medico medico = new Medico(id, nome, especialidade);
        sistema.registrarMedico(medico);

        mostrarAlerta(Alert.AlertType.INFORMATION, "Médico cadastrado com sucesso!");

        campoIdMedico.clear();
        campoNomeMedico.clear();
        campoEspecialidadeMedico.clear();

        Stage stage = (Stage) campoIdMedico.getScene().getWindow();
        stage.close();
    }

    // MÉTODO AUXILIAR: abrir qualquer tela
    private void abrirTela(String caminhoFXML, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(caminhoFXML));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Erro ao abrir a tela: " + e.getMessage());
        }
    }

    // MÉTODO AUXILIAR: mostrar alertas
    private void mostrarAlerta(Alert.AlertType tipo, String msg) {
        Alert alert = new Alert(tipo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    //MÉTODO Auxiliar: Voltar para a tela inicial
    public void onVoltarTelaInicial(ActionEvent actionEvent) {
        abrirTela("/view/menu-inicial.fxml", "Menu Inicial");
    }
    //Método Auxiliar
    private void atualizarTabelaDisponibilidades(String idMedico) {
        colInicio.setCellValueFactory(new PropertyValueFactory<>("dataHoraInicio"));
        colFim.setCellValueFactory(new PropertyValueFactory<>("dataHoraFim"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        tabelaDisponibilidades.getItems().setAll(
                sistema.getConsultas(idMedico).stream()
                        .filter(c -> "DISPONIVEL".equals(c.getStatus()))
                        .collect(Collectors.toList())
        );
    }

    public void onMarcarConsultaMedico(ActionEvent actionEvent) {
        abrirTela("/view/tela-consulta-medico.fxml", "Marcar Consulta");
    }

    public void onCancelarConsulta(ActionEvent actionEvent) {
    }

    public void onVerAgenda(ActionEvent actionEvent) {
    }

    public void onAgendarConsultaPaciente(ActionEvent actionEvent) {
    }

    public void onCancelarConsultaPaciente(ActionEvent actionEvent) {
    }

    public void onVerConsultasPaciente(ActionEvent actionEvent) {
    }

    public void onCadastrarDisponibilidade(ActionEvent actionEvent) {
        try {
            String idMedico = AppContext.usuarioLogadoId;
            if (idMedico == null || idMedico.isEmpty()) {
                mostrarAlerta(Alert.AlertType.ERROR, "Nenhum médico está logado.");
                return;
            }

            // Validar campos
            if (dataInicioPicker.getValue() == null || horaInicioField.getText().isEmpty() ||
                    dataFimPicker.getValue() == null || horaFimField.getText().isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Preencha todos os campos");
                return;
            }

            // Converter para LocalDateTime
            LocalDateTime inicio = LocalDateTime.of(
                    dataInicioPicker.getValue(),
                    LocalTime.parse(horaInicioField.getText())
            );

            LocalDateTime fim = LocalDateTime.of(
                    dataFimPicker.getValue(),
                    LocalTime.parse(horaFimField.getText())
            );

            // Cadastrar no sistema
            sistema.cadastrarDisponibilidade(idMedico, inicio, fim);
            // Atualizar tabela
            atualizarTabelaDisponibilidades(idMedico);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Disponibilidade cadastrada!");

        } catch (DateTimeParseException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Formato de hora inválido (use HH:mm)");
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro: " + e.getMessage());
        }
    }


    public static class TelaPaciente {
    }
}

