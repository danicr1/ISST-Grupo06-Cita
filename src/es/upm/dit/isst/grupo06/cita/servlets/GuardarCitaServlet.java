package es.upm.dit.isst.grupo06.cita.servlets;

import java.io.IOException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import es.upm.dit.isst.grupo06.cita.dao.CitaDAO;
import es.upm.dit.isst.grupo06.cita.dao.CitaDAOImplementation;

import es.upm.dit.isst.grupo06.cita.model.Cita;
import es.upm.dit.isst.grupo06.cita.model.Medico;
import es.upm.dit.isst.grupo06.cita.model.Paciente;

@WebServlet("/GuardarCitaServlet")
public class GuardarCitaServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String horaStr = req.getParameter("hora");

		// Recuperamos los objetos de la sesión
		Paciente paciente = (Paciente) req.getSession().getAttribute("paciente");
		Medico medico = (Medico) req.getSession().getAttribute("medico");

		Date fecha = (Date) req.getSession().getAttribute("fecha");
		// Creamos un objeto sql.Date (para que concuerde con el tipo del modelo)
		java.sql.Date fechaSql = new java.sql.Date(fecha.getTime());

		// Creamos una fecha a partir del string recibido como parámetro
		Date hora = null;
		try {
			hora = new SimpleDateFormat("HH:mm").parse(horaStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		// Creamos un objeto sql.Time (para que concuerde con el tipo del modelo) a
		// partir del util.Date parseado anteriormente
		java.sql.Time horaSql = new Time(hora.getTime());
		
		
		CitaDAO citdao = CitaDAOImplementation.getInstance();

		// Comprobamos si se trata de una modificación o una creación
		boolean modificando = (boolean) req.getSession().getAttribute("modificando");
		if (modificando) {
			Cita citaAModificar = (Cita) req.getSession().getAttribute("citaAModificar");
			citaAModificar.setPaciente(paciente);
			citaAModificar.setMedico(medico);
			citaAModificar.setFecha(fechaSql); 
			citaAModificar.setHora(horaSql);
			
			citdao.update(citaAModificar);
			
			// Para que no se quede permanentemente modificando
			req.getSession().setAttribute("modificando", false);
			
		} else { // Si es una cita nueva
			Cita cita = new Cita(); // Creamos un objeto cita

			cita.setPaciente(paciente);
			cita.setMedico(medico);
			cita.setFecha(fechaSql); 
			cita.setHora(horaSql);

			// Creamos la cita en la base de datos
			citdao.create(cita);

		}

		//Comprobamos si venimos de pedir cita por parte de un medico para redirigir su pantalla de inicio
		boolean citamedico = (boolean) req.getSession().getAttribute("citamedico");
		if (citamedico) {
			req.getSession().setAttribute("citamedico", false);
			resp.sendRedirect(req.getContextPath() + "/MedicoServlet?id=" + medico.getEmail());
		} else {
			// Redirigimos al paciente a la pantalla inicial donde puede ver las citas
			resp.sendRedirect(req.getContextPath() + "/PacienteServlet?id=" + paciente.getEmail());
		}
	}

}
