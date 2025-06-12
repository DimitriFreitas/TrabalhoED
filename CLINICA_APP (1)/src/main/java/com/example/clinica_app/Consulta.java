package com.example.clinica_app;

import java.time.LocalDateTime;

public class Consulta {
    private String idConsulta;
    private Paciente paciente;
    private Medico medico;
    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim;
    private String status;
    private String notasMedico;

    public Consulta(String idConsulta, Paciente paciente, Medico medico,
                    LocalDateTime dataHoraInicio, LocalDateTime dataHoraFim) {
        this.idConsulta = idConsulta;
        this.paciente = paciente;
        this.medico = medico;
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
        this.status = "AGENDADA";
    }
    public Consulta(String idConsulta, Medico medico,
                    LocalDateTime dataHoraInicio, LocalDateTime dataHoraFim) {
        this.idConsulta = idConsulta;
        this.medico = medico;
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
        this.status = "DISPONIVEL";  // paciente será null
    }

    public String getIdConsulta() { return idConsulta; }
    public Paciente getPaciente() { return paciente; }
    public Medico getMedico() { return medico; }
    public LocalDateTime getDataHoraInicio() { return dataHoraInicio; }
    public LocalDateTime getDataHoraFim() {return dataHoraFim;}
    public String getStatus() { return status; }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void cancelar() {
        if (status.equals("REALIZADA")) throw new IllegalStateException("Consulta já realizada");
        status = "CANCELADA";
    }

    private String motivoCancelamento;

    // Getter
    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }

    // Setter
    public void setMotivoCancelamento(String motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
    }

    @Override
    public String toString() {
        return String.format("%s - %s",
            dataHoraInicio.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
            dataHoraFim.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"))
        );
    }
}