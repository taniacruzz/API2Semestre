package org.openjfx.api2semestre.database.query;

public enum TableProperty {

    // universal property - All tables have this!
    Id("id"),

    // Appointments table
    Requester("usr_id"),
    Type("tipo"),
    StartDate("hora_inicio"),
    EndDate("hora_fim"),
    ResultCenter("cr_id"),
    Client("cliente"),
    Project("projeto"),
    Justification("justificativa"),
    Status("aprovacao"),
    Feedback("feedback"),

    // Users table
    Nome("nome"),
    Email("email"),
    Senha("senha"),
    Matricula("matricula"),

    // ResultCenter table
    Sigla("sigla"),
    Codigo("codigo"),
    User("usr_id"),
    NomeGestor("gestor_nome");

    // TODO: Clients table



    // TODO: IntervalFees table




    // TableProperty variable and constructor
    private String stringValue;
    private TableProperty (String stringValue) { this.stringValue = stringValue; }

    // Getter: raw string value
    public String getStringValue() { return stringValue; }

    // Getter: string value formatted for "SELECT where _ = ?"
    public String getStringValueWhere() { return stringValue + " = ?"; }

}
