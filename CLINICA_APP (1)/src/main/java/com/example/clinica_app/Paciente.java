package com.example.clinica_app;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Paciente {
    private String idPaciente;
    private String nome;
    private int idade;
    private String contato;
    private List<Consulta> historicoConsultas;

    public Paciente(String idPaciente, String nome, int  idade, String contato) {
        this.idPaciente = idPaciente;
        this.nome = nome;
        this.idade = idade;
        this.contato = contato;
        this.historicoConsultas = new ArrayList<>();
    }

    public String getIdPaciente() { return idPaciente; }
    public String getNome() { return nome; }
    public List<Consulta> getHistorico() { return historicoConsultas; }
    public void adicionarConsultaAoHistorico(Consulta consulta) {
        historicoConsultas.add(consulta);
    }
}