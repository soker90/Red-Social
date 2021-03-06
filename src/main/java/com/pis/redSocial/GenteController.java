package com.pis.redSocial;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import modelo.DAOPersona;
import modelo.Persona;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class GenteController {
	private static final Logger logger = LoggerFactory.getLogger(GenteController.class);

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "gente", method = RequestMethod.GET)
	public String gente( HttpServletRequest request, HttpServletResponse response, Model model) {
	

	

		ArrayList<Persona> personas = new ArrayList<Persona>();
		DAOPersona dao = new DAOPersona();
		personas = dao.getAllPersonas();
		model.addAttribute("listPersonas", personas);
		HttpSession session = request.getSession();
		Persona user = (Persona) session.getAttribute("persona");
		try {
			user=dao.getPersona(user.getUsername());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		ArrayList<String> amigos = new ArrayList<String>();
		ArrayList<String> peticionesenviadas = new ArrayList<String>();
		ArrayList<String> amigosaux = new ArrayList<String>();
		ArrayList<String> peticionesaux = new ArrayList<String>();
		amigos = user.getAmigos();
		peticionesenviadas = user.getPeticionesenviadas();

		// Comprobramos los amigos con las personas
		try {

			for (int i = 0; i < personas.size(); i++) {
				for (int j = 0; j < amigos.size(); j++) {
					if (personas.get(i).getUsername().equals(amigos.get(j))) {
						amigosaux.add(personas.get(i).getUsername());
					}
				}
			}
		} catch (Exception e) {
		}

		// Comprobamos las peticiones con las personas
		try {
			for (int i = 0; i < personas.size(); i++) {
				for (int j = 0; j < peticionesenviadas.size(); j++) {
					if (personas.get(i).getUsername().equals(peticionesenviadas.get(j))) {
						peticionesaux.add(personas.get(i).getUsername());
					}
				}
			}
		} catch (Exception e) {
		}
		model.addAttribute("listAmigos", amigosaux);
		model.addAttribute("listPeticiones", peticionesaux);
		return "gente";
	}

	@RequestMapping(value = "enviarPeticion", method = RequestMethod.POST)
	public ModelAndView anadir(Locale locale, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		ModelAndView miMAV = new ModelAndView("gente");
		DAOPersona dao = new DAOPersona();

		ArrayList<String> peticionesRecibidas;
		ArrayList<String> peticionesEnviadas;
		
		
		String yo;
		HttpSession session = request.getSession();
		Persona user = (Persona) session.getAttribute("persona");
		yo = user.getUsername();
			
		
		
		//Persona que queremos añadir
		String username;
		username = request.getParameter("anadir");
		Persona p = dao.getPersona(username);
		
		
		if (p.getUsername().equals(yo)) {
			miMAV.addObject("mensaje", "No te puedes añadir a ti mismo!");
			return miMAV;
		} else {
			//al que se le envia
			String nombreUser=p.getUsername();
			//el que la envia
			String nombreUserPeticion=user.getUsername();
			
			peticionesRecibidas = p.getPeticiones();
			peticionesRecibidas.add(nombreUserPeticion);
			
			peticionesEnviadas = user.getPeticionesenviadas();
			peticionesEnviadas.add(nombreUser);
			
			p.setPeticiones(peticionesRecibidas);
			user.setPeticionesenviadas(peticionesEnviadas);
			System.out.println(user.getPassword());
			dao.update(user);
			dao.update(p);
			System.out.println(user.getPassword());
			/*user.decrypt();
			p.decrypt();*/
			miMAV.addObject("mensaje", "Has enviado la solicitud");
			gente(request, response, model);
			return miMAV;
		}
	}

}