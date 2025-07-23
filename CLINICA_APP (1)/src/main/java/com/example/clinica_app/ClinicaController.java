package com.example.clinica_app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.beans.property.SimpleStringProperty;
import javafx.stage.Window;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ArrayList;
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
    @FXML private DatePicker dataDisponibilidade;
    @FXML private TextField horaInicio;
    @FXML private TextField horaFim;
    @FXML private TableView<Consulta> tabelaDisponibilidades;
    @FXML private TableColumn<Consulta, LocalDateTime> colInicio;
    @FXML private TableColumn<Consulta, LocalDateTime> colFim;
    @FXML private TableColumn<Consulta, String> colStatus;

    // Campos para a tabela de disponibilidades (agenda)
    @FXML private TableView<Consulta> tabelaAgenda;
    @FXML private TableColumn<Consulta, String> colAgendaData;
    @FXML private TableColumn<Consulta, String> colAgendaHora;

    // Campos para a tabela de consultas agendadas
    @FXML private TableView<Consulta> tabelaConsultas;
    @FXML private TableColumn<Consulta, String> colConsultaData;
    @FXML private TableColumn<Consulta, String> colConsultaHora;
    @FXML private TableColumn<Consulta, String> colConsultaPaciente;
    @FXML private TableColumn<Consulta, String> colConsultaNumero;
    @FXML private TableColumn<Consulta, String> colConsultaStatus;
    @FXML private TextArea motivoCancelamento;

    @FXML private TableView<Consulta> tabelaMinhasConsultas;
    @FXML private TableColumn<Consulta, String> colMinhasConsultaData;
    @FXML private TableColumn<Consulta, String> colMinhasConsultaHora;
    @FXML private TableColumn<Consulta, String> colMinhasConsultaStatus;
    @FXML private TableColumn<Consulta, String> colMinhasConsultaMotivo;

    // --- CAMPOS FXML DA TELA "AGENDAR CONSULTAS" ---
    @FXML private ComboBox<String> comboEspecialidades; // MUDOU: Agora é de String

    // --- CAMPOS FXML DA TELA "MEDICO" ---
    @FXML private ListView<Consulta> listaDisponibilidades;
    @FXML private ListView<Consulta> listaConsultas;

    // --- CAMPOS FXML DA TELA "MINHAS CONSULTAS" ---
    @FXML private ListView<Consulta> listaMinhasConsultas;
    @FXML private ListView<Consulta> listaAgenda;

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
            AppContext.usuarioLogadoId = id;
            abrirTela("/view/tela-paciente.fxml", "Área do Paciente");
        } else if (sistema.isMedico(id)) {
            AppContext.usuarioLogadoId=id; // ← salva o ID do médico logado
            abrirTela("/view/tela-medico.fxml", "Área do Médico");
            atualizarTabelaAgenda(id); // Atualiza a tabela de agenda do médico logado
            atualizarListaAgenda(id);
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "ID não encontrado.");
        }
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
    private void irparaCadastro() {
        abrirTela("/view/menu-cadastro.fxml", "Cadastro de Paciente ou Médico");
    }

    @FXML
    public void abrirTelaAgendarConsulta(ActionEvent event) {
        abrirTela("/view/tela-agendar-consulta.fxml", "Agendar Nova Consulta");
    }

    @FXML
    public void abrirTelaMinhasConsultas(ActionEvent event) {
        abrirTela("/view/tela-minhas-consultas.fxml", "Minhas Consultas");
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
        ArquivoUtils.salvarPacientes(new ArrayList<>(sistema.getTodosPacientes()));

        mostrarAlerta(Alert.AlertType.INFORMATION, "Paciente cadastrado com sucesso!");

        campoIdPaciente.clear();
        campoNomePaciente.clear();
        campoIdadePaciente.clear();
        campoContatoPaciente.clear();

        // Abre a tela inicial
        abrirTela("/view/menu-inicial.fxml", "Menu Inicial");
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
        ArquivoUtils.salvarMedicos(new ArrayList<>(sistema.getTodosMedicos()));

        mostrarAlerta(Alert.AlertType.INFORMATION, "Médico cadastrado com sucesso!");

        campoIdMedico.clear();
        campoNomeMedico.clear();
        campoEspecialidadeMedico.clear();

        // Abre a tela inicial
        abrirTela("/view/menu-inicial.fxml", "Menu Inicial");
    }

    // MÉTODO AUXILIAR: abrir qualquer tela
    private void abrirTela(String caminhoFXML, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(caminhoFXML));
            Parent root = loader.load();
            Stage stageAtual = (Stage) Stage.getWindows().stream()
                    .filter(Window::isShowing)
                    .findFirst()
                    .orElse(null);
            if (stageAtual != null) {
                stageAtual.setTitle(titulo);
                stageAtual.setScene(new Scene(root));
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Erro ao abrir a tela: " + e.getMessage());
        }
    }

    @FXML
    public void initialize() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter horaFormatter = DateTimeFormatter.ofPattern("HH:mm");

        // --- LÓGICA PARA A TELA "AGENDAR CONSULTAS" ---
        if (comboEspecialidades != null && listaDisponibilidades != null) {
            List<String> especialidadesUnicas = sistema.getTodosMedicos().stream()
                    .map(Medico::getEspecialidade)
                    .distinct()
                    .collect(Collectors.toList());
            comboEspecialidades.getItems().setAll(especialidadesUnicas);

            comboEspecialidades.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    atualizarListaDisponibilidadesPorEspecialidade(newVal);
                }
            });
        }

        if (tabelaMinhasConsultas != null) {
            colMinhasConsultaData.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getDataHoraInicio().toLocalDate().format(formatter))
            );
            colMinhasConsultaHora.setCellValueFactory(cellData -> {
                String inicio = cellData.getValue().getDataHoraInicio().toLocalTime().format(horaFormatter);
                String fim = cellData.getValue().getDataHoraFim().toLocalTime().format(horaFormatter);
                return new SimpleStringProperty(inicio + " - " + fim);
            });
            colMinhasConsultaStatus.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getStatus())
            );
            colMinhasConsultaMotivo.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getMotivoCancelamento() != null ? cellData.getValue().getMotivoCancelamento() : "")
            );
            tabelaMinhasConsultas.getItems().setAll(
                    sistema.getConsultasPaciente(AppContext.usuarioLogadoId).stream()
                            .filter(c -> !"DISPONIVEL".equals(c.getStatus()))
                            .collect(Collectors.toList())
            );
        }

        if (tabelaAgenda != null) {
            colAgendaData.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getDataHoraInicio().toLocalDate().format(formatter))
            );
            colAgendaHora.setCellValueFactory(cellData -> {
                String inicio = cellData.getValue().getDataHoraInicio().toLocalTime().format(horaFormatter);
                String fim = cellData.getValue().getDataHoraFim().toLocalTime().format(horaFormatter);
                return new SimpleStringProperty(inicio + " - " + fim);
            });
            atualizarTabelaAgenda(AppContext.usuarioLogadoId);
        }

        if (tabelaConsultas != null) {
            colConsultaData.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getDataHoraInicio().toLocalDate().format(formatter))
            );
            colConsultaHora.setCellValueFactory(cellData -> {
                String inicio = cellData.getValue().getDataHoraInicio().toLocalTime().format(horaFormatter);
                String fim = cellData.getValue().getDataHoraFim().toLocalTime().format(horaFormatter);
                return new SimpleStringProperty(inicio + " - " + fim);
            });
            colConsultaPaciente.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getPaciente() != null ? cellData.getValue().getPaciente().getNome() : ""));
            colConsultaNumero.setCellValueFactory(cellData ->
                    new SimpleStringProperty(
                            cellData.getValue().getPaciente() != null
                                    ? cellData.getValue().getPaciente().getContato()
                                    : ""
                    )
            );
            atualizarTabelaConsultasMedico(AppContext.usuarioLogadoId);
        }

        // --- LÓGICA PARA A TELA "MINHAS CONSULTAS" ---
        if (listaMinhasConsultas != null) {
            atualizarListaMinhasConsultas();
        }

        // --- LÓGICA PARA A TELA DO MÉDICO (TableView) ---
        if (tabelaDisponibilidades != null) {
            colInicio.setCellValueFactory(new PropertyValueFactory<>("dataHoraInicio"));
            colFim.setCellValueFactory(new PropertyValueFactory<>("dataHoraFim"));
            colStatus.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getStatus())
            );

            // Formatação das datas
            colInicio.setCellFactory(column -> new TableCell<Consulta, LocalDateTime>() {
                @Override
                protected void updateItem(LocalDateTime item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "" : item.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                }
            });
            colFim.setCellFactory(column -> new TableCell<Consulta, LocalDateTime>() {
                @Override
                protected void updateItem(LocalDateTime item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "" : item.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                }
            });
            atualizarTabelaDisponibilidades(AppContext.usuarioLogadoId);
        }

        // --- LÓGICA PARA A TELA DO MÉDICO (ListView Agenda) ---
        if (listaAgenda != null) {
            atualizarListaAgenda(AppContext.usuarioLogadoId);
        }
    }

    public List<Consulta> getConsultasPaciente(String idPaciente) {
        return sistema.getConsultasPaciente(idPaciente).stream()
                .filter(c -> c.getPaciente() != null && c.getPaciente().getIdPaciente().equals(idPaciente))
                .collect(Collectors.toList());
    }

    private void carregarEspecialidade() {
        List<Medico> medicos = AppContext.sistema.getTodosMedicos();
        for (Medico medico : medicos) {
            comboEspecialidades.getItems().add(medico.getIdMedico());
        }
    }

    private void carregarConsultas() {
        listaMinhasConsultas.getItems().clear();
        List<Consulta> consultas = AppContext.sistema.getConsultasPaciente(AppContext.usuarioLogadoId);
        for (Consulta consulta : consultas) {
            listaMinhasConsultas.getItems().add(consulta);
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
    //MÉTODO Auxiliar: Voltar para a tela de Cadastro
    @FXML
    public void onVoltarTelaCadastro(ActionEvent event) {
        abrirTela("/view/menu-cadastro.fxml", "Cadastro de Paciente ou Médico");
    }
    //MÉTODO Auxiliar: Voltar para a tela paciente
    public void onVoltarTelaPaciente(ActionEvent actionEvent) {
        abrirTela("/view/tela-paciente.fxml", "Tela Paciente");
    }
    //MÉTODO Auxiliar: Voltar para a tela paciente
    public void onVoltarTelaMinhasConsultas(ActionEvent actionEvent) {
        abrirTela("/view/tela-minhas-consultas.fxml", "Minhas Consultas");
    }
    //MÉTODO Auxiliar: Voltar para a tela medico
    public void onVoltarTelaMedico(ActionEvent actionEvent) {
        abrirTela("/view/tela-medico.fxml", "Área do Médico");
    }
    //Método Auxiliar
    private void atualizarTabelaDisponibilidades(String idMedico) {
        tabelaDisponibilidades.getItems().setAll(
                sistema.getConsultas(idMedico).stream()
                        .filter(c -> "DISPONIVEL".equals(c.getStatus()))
                        .collect(Collectors.toList())
        );
    }

    @FXML
    public void onCancelarConsultaMinhasConsultas(ActionEvent event) {
        Consulta consultaSelecionada = tabelaMinhasConsultas.getSelectionModel().getSelectedItem();
        if (consultaSelecionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selecione uma consulta para cancelar.");
            return;
        }
        if (!"AGENDADA".equals(consultaSelecionada.getStatus())) {
            mostrarAlerta(Alert.AlertType.WARNING, "Só é possível cancelar consultas agendadas.");
            return;
        }
        if ("AGENDADA".equals(consultaSelecionada.getStatus())) {
            sistema.cancelarConsultaPorPaciente(consultaSelecionada);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Consulta cancelada com sucesso!");
            ArquivoUtils.salvarConsultas(
                    AppContext.sistema.getTodosMedicos().stream()
                            .flatMap(medico -> AppContext.sistema.getConsultas(medico.getIdMedico()).stream())
                            .collect(Collectors.toList())
            );
            tabelaMinhasConsultas.getItems().setAll(sistema.getConsultasPaciente(AppContext.usuarioLogadoId));
            return;
        }
        mostrarAlerta(Alert.AlertType.ERROR, "Erro");
    }

    @FXML
    public void onConfirmarConsulta(ActionEvent event) {
        Consulta consultaSelecionada = tabelaConsultas.getSelectionModel().getSelectedItem();
        if (consultaSelecionada == null || !"SOLICITADO".equals(consultaSelecionada.getStatus())) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selecione uma consulta solicitada para confirmar.");
            return;
        }
        consultaSelecionada.setStatus("AGENDADA");
        mostrarAlerta(Alert.AlertType.INFORMATION, "Consulta confirmada!");
        atualizarTabelaConsultasMedico(AppContext.usuarioLogadoId);
    }

    @FXML
    public void onConcluirConsulta(ActionEvent event) {
        Consulta consultaSelecionada = tabelaConsultas.getSelectionModel().getSelectedItem();
        if (consultaSelecionada == null || !"AGENDADA".equals(consultaSelecionada.getStatus())) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selecione uma consulta agendada para confirmar.");
            return;
        }
        consultaSelecionada.setStatus("CONCLUIDA");
        mostrarAlerta(Alert.AlertType.INFORMATION, "Consulta finalizada!");
        ArquivoUtils.salvarConsultas(
                AppContext.sistema.getTodosMedicos().stream()
                        .flatMap(medico -> AppContext.sistema.getConsultas(medico.getIdMedico()).stream())
                        .collect(Collectors.toList())
        );
        atualizarTabelaConsultasMedico(AppContext.usuarioLogadoId);
    }

    @FXML
    public void onRemoverConsultaMinhasConsultas(ActionEvent event) {
        Consulta consultaSelecionada = tabelaMinhasConsultas.getSelectionModel().getSelectedItem();
        if (consultaSelecionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selecione uma consulta para remover.");
            return;
        }
        if (!"CANCELADA".equals(consultaSelecionada.getStatus()) && !"CONCLUIDA".equals(consultaSelecionada.getStatus())) {
            mostrarAlerta(Alert.AlertType.WARNING, "Só é possível remover consultas canceladas ou concluídas.");
            return;
        }
        // Remove do histórico do paciente
        if (consultaSelecionada.getPaciente() != null) {
            consultaSelecionada.getPaciente().removerConsultaDoHistorico(consultaSelecionada.getIdConsulta());
        }
        // Remove da agenda do médico
        sistema.getAgendaMedico(consultaSelecionada.getMedico().getIdMedico())
                .remove(consultaSelecionada.getDataHoraInicio());

        // Atualiza o arquivo
        ArquivoUtils.salvarConsultas(
                AppContext.sistema.getTodosMedicos().stream()
                        .flatMap(medico -> AppContext.sistema.getConsultas(medico.getIdMedico()).stream())
                        .collect(Collectors.toList())
        );

        // Atualiza a tabela
        tabelaMinhasConsultas.getItems().setAll(
                sistema.getConsultasPaciente(AppContext.usuarioLogadoId)
        );
        mostrarAlerta(Alert.AlertType.INFORMATION, "Consulta removida com sucesso!");
    }

    @FXML
    public void onCancelarConsulta(ActionEvent event) {
        Consulta consultaSelecionada = tabelaConsultas.getSelectionModel().getSelectedItem();
        String motivo = motivoCancelamento.getText().trim();

        if (consultaSelecionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selecione uma consulta para cancelar.");
            return;
        }
        if (motivo.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Informe o motivo do cancelamento.");
            return;
        }

        consultaSelecionada.setMotivoCancelamento(motivo);
        if ("AGENDADA".equals(consultaSelecionada.getStatus())) {
            sistema.cancelarConsultaPorMedico(AppContext.usuarioLogadoId, consultaSelecionada.getIdConsulta());
            mostrarAlerta(Alert.AlertType.INFORMATION, "Consulta cancelada com sucesso!");
            atualizarTabelaConsultasMedico(AppContext.usuarioLogadoId);
            motivoCancelamento.clear();
            ArquivoUtils.salvarConsultas(
                    AppContext.sistema.getTodosMedicos().stream()
                            .flatMap(medico -> AppContext.sistema.getConsultas(medico.getIdMedico()).stream())
                            .collect(Collectors.toList())
            );
            return;
        }
        mostrarAlerta(Alert.AlertType.ERROR, "Selecione uma consulta agendada para cancelar.");
        atualizarTabelaConsultasMedico(AppContext.usuarioLogadoId);
        motivoCancelamento.clear();
    }

    public void onVerAgenda(ActionEvent actionEvent) {
    }

    // O método onAgendarConsultaPaciente não precisa mudar, pois a lógica dele já era pegar uma Consulta da lista.
    @FXML
    public void onAgendarConsultaPaciente(ActionEvent event) {
        // Verifica se está na tela de reagendamento
        String id = AppContext.usuarioLogadoId;
        if (dataDisponibilidade != null && horaInicio != null && horaFim != null
                && dataDisponibilidade.getValue() != null
                && !horaInicio.getText().isEmpty()
                && !horaFim.getText().isEmpty()) {
            List<Consulta> consultas = sistema.getConsultasPaciente(AppContext.usuarioLogadoId);
            Consulta consultaCancelada = consultas.stream()
                    .filter(c -> "CANCELADA".equals(c.getStatus()))
                    .findFirst()
                    .orElse(null);
            Consulta consultaAgendada = consultas.stream()
                    .filter(c -> "AGENDADA".equals(c.getStatus()))
                    .findFirst()
                    .orElse(null);
            // Verifica se o usuário é paciente ou médico
            if (sistema.isPaciente(id)) {
                Consulta consultaCancelada2 = consultas.stream()
                        .filter(c -> "CANCELADA".equals(c.getStatus()))
                        .findFirst()
                        .orElse(null);
                consultaCancelada = consultaCancelada2;
                if (consultaCancelada == null) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Nenhuma consulta cancelada para reagendar.");
                    return;
                }
                } else if (sistema.isMedico(id)) {
                    Consulta consultaCancelada2 = consultas.stream()
                            .filter(c -> "CANCELADA".equals(c.getStatus()))
                            .findFirst()
                            .orElse(null);
                    consultaCancelada = consultaCancelada2;
                    Consulta consultaAgendada2 = consultas.stream()
                            .filter(c -> "AGENDADA".equals(c.getStatus()))
                            .findFirst()
                            .orElse(null);
                    consultaAgendada = consultaAgendada2;
                    if (consultaCancelada == null && consultaAgendada == null) {
                        mostrarAlerta(Alert.AlertType.ERROR, "Porra consulta para reagendar.");
                        return;
                    }
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Faça login como paciente ou médico.");
                    return;
                }

            LocalDate data = dataDisponibilidade.getValue();
            LocalTime inicio = LocalTime.parse(horaInicio.getText());
            LocalTime fim = LocalTime.parse(horaFim.getText());
            LocalDateTime inicioDT = LocalDateTime.of(data, inicio);
            LocalDateTime fimDT = LocalDateTime.of(data, fim);

            if (inicioDT.isAfter(fimDT) || inicioDT.isEqual(fimDT)) {
                mostrarAlerta(Alert.AlertType.ERROR, "Horário de início deve ser antes do horário de fim.");
                return;
            }
            if (sistema.isPaciente(id)) {
                Consulta consultaSolicitada = new Consulta(
                        java.util.UUID.randomUUID().toString(),
                        consultaCancelada.getPaciente(),
                        consultaCancelada.getMedico(),
                        inicioDT,
                        fimDT
                );
                consultaSolicitada.setStatus(Consulta.STATUS_SOLICITADO);

                sistema.solicitarReagendamento(consultaSolicitada);

                mostrarAlerta(Alert.AlertType.INFORMATION, "Reagendamento solicitado! Aguarde confirmação do médico.");
                ArquivoUtils.salvarConsultas(
                        AppContext.sistema.getTodosMedicos().stream()
                                .flatMap(medico -> AppContext.sistema.getConsultas(medico.getIdMedico()).stream())
                                .collect(Collectors.toList())
                );
                abrirTela("/view/tela-minhas-consultas.fxml", "Minhas Consultas");
                return;
            } else if (sistema.isMedico(id)) {
                Consulta consultaReagendada = new Consulta(
                        java.util.UUID.randomUUID().toString(),
                        consultaCancelada.getPaciente(),
                        consultaCancelada.getMedico(),
                        inicioDT,
                        fimDT
                );
                consultaReagendada.setStatus(Consulta.STATUS_REAGENDADA);

                sistema.solicitarReagendamento(consultaReagendada);

                mostrarAlerta(Alert.AlertType.INFORMATION, "Reagendamento feito!");
                ArquivoUtils.salvarConsultas(
                        AppContext.sistema.getTodosMedicos().stream()
                                .flatMap(medico -> AppContext.sistema.getConsultas(medico.getIdMedico()).stream())
                                .collect(Collectors.toList())
                );
                abrirTela("/view/tela-minhas-consultas.fxml", "Minhas Consultas");
                return;
            }

        }
        Consulta consultaDisponivel = listaDisponibilidades.getSelectionModel().getSelectedItem();
        if (consultaDisponivel == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selecione um horário disponível para agendar.");
            return;
        }

        try {
            sistema.agendarConsulta(
                    AppContext.usuarioLogadoId,
                    consultaDisponivel.getMedico().getIdMedico(),
                    consultaDisponivel.getDataHoraInicio(),
                    consultaDisponivel.getDataHoraFim()
            );
            mostrarAlerta(Alert.AlertType.INFORMATION, "Consulta agendada com sucesso!");
            atualizarTabelaConsultasMedico(AppContext.usuarioLogadoId);
            atualizarListaDisponibilidadesPorEspecialidade(comboEspecialidades.getValue());
        } catch (IllegalStateException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro ao agendar: " + e.getMessage());
        }
        ArquivoUtils.salvarConsultas(
                AppContext.sistema.getTodosMedicos().stream()
                        .flatMap(medico -> AppContext.sistema.getConsultas(medico.getIdMedico()).stream())
                        .collect(Collectors.toList())
        );
    }

    @FXML
    public void onReagendarConsultaPaciente(ActionEvent actionEvent) {
        Consulta consultaSelecionada = tabelaConsultas.getSelectionModel().getSelectedItem();

        if (consultaSelecionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selecione uma consulta para reagendar.");
            return;
        }

        if ("AGENDADA".equals(consultaSelecionada.getStatus())) {
            abrirTela("/view/tela-reagendamento-paciente.fxml", "Reagendar Consulta");
            return;
        }

        mostrarAlerta(Alert.AlertType.ERROR, "Selecione uma consulta agendada, ou que foi cancelada para reagendar.");
        atualizarTabelaConsultasMedico(AppContext.usuarioLogadoId);
        motivoCancelamento.clear();
    }

    @FXML
    public void onReagendarConsultaMedico(ActionEvent actionEvent) {
        Consulta consultaSelecionada = tabelaConsultas.getSelectionModel().getSelectedItem();
        String motivo = motivoCancelamento.getText().trim();
        if (consultaSelecionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selecione uma consulta para reagendar.");
            return;
        }
        if (motivo.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Informe o motivo do reagendamento.");
            return;
        }

        consultaSelecionada.setMotivoCancelamento(motivo);
        if ("AGENDADA".equals(consultaSelecionada.getStatus())) {
            abrirTela("/view/tela-reagendamento-medico.fxml", "Reagendar Consulta");
            return;
        }

        mostrarAlerta(Alert.AlertType.ERROR, "Selecione uma consulta agendada para reagendar.");
        atualizarTabelaConsultasMedico(AppContext.usuarioLogadoId);
        motivoCancelamento.clear();
    }

    // O método onCancelarConsultaPaciente também não precisa mudar.
    @FXML
    public void onCancelarConsultaPaciente(ActionEvent actionEvent) {
        Consulta minhaConsulta = listaMinhasConsultas.getSelectionModel().getSelectedItem();
        if (minhaConsulta == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selecione uma de suas consultas para cancelar.");
            return;
        }

        if (minhaConsulta.getPaciente() == null || !minhaConsulta.getPaciente().getIdPaciente().equals(AppContext.usuarioLogadoId)) {
            mostrarAlerta(Alert.AlertType.ERROR, "Esta não é uma consulta sua.");
            return;
        }

        sistema.cancelarConsultaPorPaciente(minhaConsulta);
        mostrarAlerta(Alert.AlertType.INFORMATION, "Consulta cancelada com sucesso!");
        atualizarListaMinhasConsultas(); // Atualiza a lista para remover a consulta cancelada

        mostrarAlerta(Alert.AlertType.INFORMATION, "Consulta cancelada com sucesso!");
    }

/**
 * Método auxiliar para carregar/atualizar ata de consultas do paciente na tela.
 * Ele deve ser chamado no início e após qualquer agendamento/cancelamento.
 */
private void atualizarTabelaAgenda(String idMedico) {
    if (tabelaAgenda != null) {
        tabelaAgenda.getItems().setAll(
                sistema.getConsultas(idMedico).stream()
                        .filter(c -> "DISPONIVEL".equals(c.getStatus()))
                        .collect(Collectors.toList())
        );
    }
}

    private void atualizarTabelaConsultasMedico(String idMedico) {
        if (tabelaConsultas != null) {
            tabelaConsultas.getItems().setAll(
                    sistema.getConsultas(idMedico).stream()
                            .filter(c -> !"DISPONIVEL".equals(c.getStatus()))
                            .collect(Collectors.toList())
            );
        }
    }

/** NOVO MÉTODO: Atualiza a lista de horários disponíveis filtrando por especialidade. */
private void atualizarListaDisponibilidadesPorEspecialidade(String especialidade) {
    // Pega TODAS as consultas disponíveis de TODOS os médicos
    List<Consulta> disponibilidadesTotais = sistema.getTodasConsultasDisponiveis();

    // Filtra essa lista para mostrar apenas as da especialidade selecionada
    List<Consulta> disponibilidadesFiltradas = disponibilidadesTotais.stream()
            .filter(c -> c.getMedico().getEspecialidade().equals(especialidade))
            .collect(Collectors.toList());

    listaDisponibilidades.getItems().setAll(disponibilidadesFiltradas);
}

    /** Atualiza a lista de consultas que o paciente logado já agendou. (Sem alteração) */
    private void atualizarListaMinhasConsultas() {
        if (AppContext.usuarioLogadoId != null) {
            listaMinhasConsultas.getItems().setAll(sistema.getConsultasPaciente(AppContext.usuarioLogadoId));
        }
    }

    public void onVerConsultasPaciente(ActionEvent actionEvent) {
    }

    @FXML
    public void onCancelarDisponibilidade(ActionEvent event) {
        Consulta disponibilidadeSelecionada = tabelaAgenda.getSelectionModel().getSelectedItem();
        if (disponibilidadeSelecionada == null || !"DISPONIVEL".equals(disponibilidadeSelecionada.getStatus())) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selecione uma disponibilidade para cancelar.");
            return;
        }
        boolean removido = sistema.cancelarConsultaPorMedico(
                AppContext.usuarioLogadoId,
                disponibilidadeSelecionada.getIdConsulta()
        );
        if (removido) {
            ArquivoUtils.salvarConsultas(
                    AppContext.sistema.getTodosMedicos().stream()
                            .flatMap(medico -> AppContext.sistema.getConsultas(medico.getIdMedico()).stream())
                            .collect(Collectors.toList())
            );
            atualizarTabelaAgenda(AppContext.usuarioLogadoId);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Disponibilidade cancelada");
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro ao cancelar disponibilidade.");
        }
    }

    public void onCadastrarDisponibilidade(ActionEvent actionEvent) {
        try {
            String idMedico = AppContext.usuarioLogadoId;
            if (idMedico == null || idMedico.isEmpty()) {
                mostrarAlerta(Alert.AlertType.ERROR, "Nenhum médico está logado.");
                return;
            }

            // Validar campos
            if (dataDisponibilidade.getValue() == null ||
                    horaInicio.getText().isEmpty() ||
                    horaFim.getText().isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Preencha todos os campos.");
                return;
            }

            // Converter data e hora
            LocalDate data = dataDisponibilidade.getValue();
            LocalTime horaInicioParsed = LocalTime.parse(horaInicio.getText());
            LocalTime horaFimParsed = LocalTime.parse(horaFim.getText());

            LocalDateTime inicio = LocalDateTime.of(data, horaInicioParsed);
            LocalDateTime fim = LocalDateTime.of(data, horaFimParsed);

            if (!fim.isAfter(inicio)) {
                mostrarAlerta(Alert.AlertType.WARNING, "A hora de fim deve ser após a hora de início.");
                return;
            }

            // Cadastrar no sistema
            sistema.cadastrarDisponibilidade(idMedico, inicio, fim);

            // Atualizar todas as tabelas/listas relevantes
            if (tabelaDisponibilidades != null) {
                atualizarTabelaDisponibilidades(idMedico);
            }
            if (tabelaAgenda != null) {
                atualizarTabelaAgenda(idMedico);
            }
            if (listaAgenda != null) {
                atualizarListaAgenda(idMedico);
            }

            mostrarAlerta(Alert.AlertType.INFORMATION, "Disponibilidade cadastrada com sucesso!");

        } catch (DateTimeParseException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Formato de hora inválido (use HH:mm).");
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro ao cadastrar: " + e.getMessage());
        }
        ArquivoUtils.salvarConsultas(
                AppContext.sistema.getTodosMedicos().stream()
                        .flatMap(medico -> AppContext.sistema.getConsultas(medico.getIdMedico()).stream())
                        .collect(Collectors.toList())
        );
    }

    // Adicione este método auxiliar ao seu controller:
    private void atualizarListaAgenda(String idMedico) {
        if (listaAgenda != null) {
            listaAgenda.getItems().setAll(
                    sistema.getConsultas(idMedico).stream()
                            .filter(c -> "DISPONIVEL".equals(c.getStatus()))
                            .collect(Collectors.toList())
            );
        }
    }
}