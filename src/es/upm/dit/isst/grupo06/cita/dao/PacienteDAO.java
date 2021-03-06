package es.upm.dit.isst.grupo06.cita.dao;

import java.util.Collection;

import es.upm.dit.isst.grupo06.cita.model.Paciente;

public interface PacienteDAO {
	
	public void create(Paciente paciente);

	public Paciente read(String email);

	public void update(Paciente paciente);

	public void delete(Paciente paciente);
	
	public void deleteAll();

	public Collection<Paciente> readAll();
}
