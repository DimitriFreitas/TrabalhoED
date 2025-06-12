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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
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
    private void irparaCadastro() {
        abrirTela("/view/menu-cadastro.fxml", "Cadastro de Paciente ou Médico");
        Stage stageAtual = (Stage) campoNomeUsuario.getScene().getWindow();
        stageAtual.close();
    }

    @FXML
    public void abrirTelaAgendarConsulta(ActionEvent event) {
        abrirTela("/view/tela-agendar-consulta.fxml", "Agendar Nova Consulta");
        Stage stageAtual = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stageAtual.close();
    }

    @FXML
    public void abrirTelaMinhasConsultas(ActionEvent event) {
        abrirTela("/view/tela-minhas-consultas.fxml", "Minhas Consultas");
        Stage stageAtual = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stageAtual.close();
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

        // Fecha a tela de cadastro de paciente
        Stage stageCadastro = (Stage) campoIdPaciente.getScene().getWindow();
        stageCadastro.close();

        // Fecha o menu-cadastro, se estiver aberto
        for (Stage stage : Stage.getWindows().stream().filter(w -> w instanceof Stage).map(w -> (Stage) w).toList()) {
            if (stage.getTitle() != null && stage.getTitle().contains("Cadastro de Paciente ou Médico")) {
                stage.close();
            }
        }

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

        mostrarAlerta(Alert.AlertType.INFORMATION, "Médico cadastrado com sucesso!");

        campoIdMedico.clear();
        campoNomeMedico.clear();
        campoEspecialidadeMedico.clear();

        // Fecha a tela de cadastro de médico
        Stage stageCadastro = (Stage) campoIdMedico.getScene().getWindow();
        stageCadastro.close();

        // Fecha o menu-cadastro, se estiver aberto
        for (Stage stage : Stage.getWindows().stream().filter(w -> w instanceof Stage).map(w -> (Stage) w).toList()) {
            if (stage.getTitle() != null && stage.getTitle().contains("Cadastro de Paciente ou Médico")) {
                stage.close();
            }
        }

        // Abre a tela inicial
        abrirTela("/view/menu-inicial.fxml", "Menu Inicial");
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

    @FXML
    public void initialize() {
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

        if (listaAgenda != null) {
            atualizarListaAgenda(AppContext.usuarioLogadoId);
        }
        if (listaConsultas != null) {
            atualizarListaConsultasMedico(AppContext.usuarioLogadoId);
        }

        // --- LÓGICA PARA A TELA "MINHAS CONSULTAS" ---
        if (listaMinhasConsultas != null) {
            atualizarListaMinhasConsultas();
        }

        // --- LÓGICA PARA A TELA DO MÉDICO (TableView) ---
        if (tabelaDisponibilidades != null) {
            colInicio.setCellValueFactory(new PropertyValueFactory<>("dataHoraInicio"));
            colFim.setCellValueFactory(new PropertyValueFactory<>("dataHoraFim"));
            colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

            // Formatação das datas
            colInicio.setCellFactory(column -> new TableCell<Consulta, LocalDateTime>() {
                @Override
                protected void updateItem(LocalDateTime item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "" : item.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                }
            });
            colFim.setCellFactory(column -> new TableCell<Consulta, LocalDateTime>() {
                @Override
                protected void updateItem(LocalDateTime item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "" : item.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                }
            });

            atualizarTabelaDisponibilidades(AppContext.usuarioLogadoId);
        }

        // --- LÓGICA PARA A TELA DO MÉDICO (ListView Agenda) ---
        if (listaAgenda != null) {
            atualizarListaAgenda(AppContext.usuarioLogadoId);
        }
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
        Stage stageAtual = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stageAtual.close();
    }
    //MÉTODO Auxiliar: Voltar para a tela paciente
    public void onVoltarTelaPaciente(ActionEvent actionEvent) {
        abrirTela("/view/tela-paciente.fxml", "Tela Paciente");
        Stage stageAtual = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stageAtual.close();
    }
    //Método Auxiliar
    private void atualizarTabelaDisponibilidades(String idMedico) {
        colInicio.setCellValueFactory(new PropertyValueFactory<>("dataHoraInicio"));
        colFim.setCellValueFactory(new PropertyValueFactory<>("dataHoraFim"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        tabelaDisponibilidades.getItems().setAll(
                sistema.getConsultas(idMedico).stream()
                        .filter(c -> "DISPONIVEL".equals(c.getStatus()))
                        .map(c -> new Consulta(
                                c.getIdConsulta(),
                                c.getMedico(),
                                c.getDataHoraInicio(),
                                c.getDataHoraFim()
                        ))
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

    // O método onAgendarConsultaPaciente não precisa mudar, pois a lógica dele já era pegar uma Consulta da lista.
    @FXML
    public void onAgendarConsultaPaciente(ActionEvent actionEvent) {
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
            // Atualiza a lista de disponibilidades para a especialidade que estava selecionada
            atualizarListaDisponibilidadesPorEspecialidade(comboEspecialidades.getValue());
        } catch (IllegalStateException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro ao agendar: " + e.getMessage());
        }
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

        // Atualiza a lista para remover a consulta cancelada
        atualizarListaConsultasPaciente();
    }

/**
 * Método auxiliar para carregar/atualizar a lista de consultas do paciente na tela.
 * Ele deve ser chamado no início e após qualquer agendamento/cancelamento.
 */
private void atualizarListaConsultasPaciente() {
    if (AppContext.usuarioLogadoId != null) {
        listaMinhasConsultas.getItems().clear();
        List<Consulta> consultas = sistema.getConsultasPaciente(AppContext.usuarioLogadoId);
        listaMinhasConsultas.getItems().setAll(consultas);
    }
}

private void atualizarListaConsultasMedico(String idMedico) {
    if (listaConsultas != null) {
        listaConsultas.getItems().setAll(
                sistema.getConsultas(idMedico).stream()
                        .filter(c -> "AGENDADA".equals(c.getStatus()))
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
public void onCancelarDisponibilidade(ActionEvent actionEvent) {
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

            // Atualizar tabela ou lista, dependendo da tela
            if (tabelaDisponibilidades != null) {
                atualizarTabelaDisponibilidades(idMedico);
            } else if (listaAgenda != null) {
                atualizarListaAgenda(idMedico);
            }

            mostrarAlerta(Alert.AlertType.INFORMATION, "Disponibilidade cadastrada com sucesso!");

        } catch (DateTimeParseException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Formato de hora inválido (use HH:mm).");
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro ao cadastrar: " + e.getMessage());
        }
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