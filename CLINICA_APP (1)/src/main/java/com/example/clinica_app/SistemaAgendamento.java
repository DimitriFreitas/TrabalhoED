package com.example.clinica_app;

import java.time.LocalDateTime;
import java.util.*;

public class SistemaAgendamento {
    private Map<String, Paciente> pacientes = new HashMap<>();
    private Map<String, Medico> medicos = new HashMap<>();
    private Map<String, TreeMap<LocalDateTime, Consulta>> agendas = new HashMap<>();

    public boolean isPaciente(String id) {
        return pacientes.containsKey(id);
    }

    public boolean isMedico(String id) {
        return medicos.containsKey(id);
    }

    public void registrarPaciente(Paciente paciente) {
        pacientes.put(paciente.getIdPaciente(), paciente);
    }

    public void registrarMedico(Medico medico) {
        medicos.put(medico.getIdMedico(), medico);
        agendas.put(medico.getIdMedico(), new TreeMap<>());
    }

    public void agendarConsulta(String idPaciente, String idMedico, LocalDateTime inicio, LocalDateTime fim) {
        Paciente paciente = pacientes.get(idPaciente);
        Medico medico = medicos.get(idMedico);
        TreeMap<LocalDateTime, Consulta> agenda = agendas.get(idMedico);

        if (agenda.containsKey(inicio)) throw new IllegalStateException("Horário já ocupado");

        Consulta consulta = new Consulta(UUID.randomUUID().toString(), paciente, medico, inicio, fim);
        agenda.put(inicio, consulta);
        paciente.adicionarConsultaAoHistorico(consulta);
    }

    public boolean cancelarConsultaPorPaciente(String idPaciente, String idConsulta) {
        for (TreeMap<LocalDateTime, Consulta> agenda : agendas.values()) {
            for (Map.Entry<LocalDateTime, Consulta> entry : agenda.entrySet()) {
                Consulta consulta = entry.getValue();
                if (consulta.getIdConsulta().equals(idConsulta) &&
                    consulta.getPaciente().getIdPaciente().equals(idPaciente)) {

                    agenda.remove(entry.getKey());
                    
                    consulta.getPaciente().removerConsultaDoHistorico(idConsulta);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean cancelarConsultaPorMedico(String idMedico, String idConsulta) {
        TreeMap<LocalDateTime, Consulta> agenda = agendas.get(idMedico);
        if (agenda == null) return false;

        for (Map.Entry<LocalDateTime, Consulta> entry : agenda.entrySet()) {
            Consulta consulta = entry.getValue();
            if (consulta.getIdConsulta().equals(idConsulta) &&
                consulta.getMedico().getIdMedico().equals(idMedico)) {

                agenda.remove(entry.getKey());
                consulta.getPaciente().removerConsultaDoHistorico(idConsulta);
                return true;
            }
        }
        return false;
    }

    public List<Consulta> getConsultas(String idMedico) {
        return new ArrayList<>(agendas.get(idMedico).values());
    }
}
