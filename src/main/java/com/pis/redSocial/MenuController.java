package com.pis.redSocial;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import modelo.DAOPersona;
import modelo.DAOPublicacion;
import modelo.Persona;
import modelo.Publicacion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MenuController {
	private static final Logger logger = LoggerFactory.getLogger(MenuController.class);
	@RequestMapping("modificarUsuario")
	public ModelAndView modificar(HttpServletRequest request, HttpServletResponse response)throws Exception{
		//logger.info("Register page! The client locale is {}.", locale);
		boolean flag=false;
		String nombre, apellidos, username, email, password, direccion, telefono, foto;
		ArrayList<String>amigos=new ArrayList<String>();
		ArrayList<String>peticiones=new ArrayList<String>();
		ArrayList<String>peticionesenviadas=new ArrayList<String>();
		nombre = request.getParameter("inputNombre");
		apellidos = request.getParameter("inputApellidos");
		password = request.getParameter("inputPassword");
		direccion = request.getParameter("inputDireccion");
		telefono = request.getParameter("inputTelefono");
		username= request.getParameter("aUser");
		email=request.getParameter("aEmail");
		DAOPersona dao = new DAOPersona();
		Persona p= new Persona(nombre,apellidos, username,email, password, direccion, telefono, "", dao.getPersona(username).isEsAdmin(), amigos, peticiones,peticionesenviadas);
		dao.update(p);
		return new ModelAndView("menu");
	}
	
	@RequestMapping("passtresmeses")
	public ModelAndView passtresmeses(HttpServletRequest request, HttpServletResponse response)throws Exception{
		DAOPersona dao = new DAOPersona();
		String password = request.getParameter("inputPasswordRegistro");
		String repitePassword = request.getParameter("inputRePasswordRegistro");
		ModelAndView miMAV = new ModelAndView("menu");
		String nombre = (String) request.getSession().getAttribute("usernombre");
		Persona p= (Persona) dao.getPersona(nombre);
		String oldpassword = p.getPassword();
		p.setPassword(password);
		
		
		if (!(password.equalsIgnoreCase(repitePassword))) {
			miMAV.addObject("mensaje",
					"No se puede registrar. Las contraseñas no coinciden.");
			miMAV.setViewName("passtresmeses");
			return miMAV;
		}if (password.equalsIgnoreCase(oldpassword)) {
			miMAV.addObject("mensaje",
					"Escoja una contraseña diferente a la antigua.");
			miMAV.setViewName("passtresmeses");
			return miMAV;
		} else {
			if (!p.requisitosPassword()) {
				miMAV.addObject("mensaje",
						"No se puede registrar. No se cumple los requisitos de la contraseña.");
				miMAV.setViewName("passtresmeses");
				return miMAV;
			}else{
				p.setFecha(new Date());
				dao.update(p);				
				miMAV.addObject("mensaje",
						"Contraseña cambiada.");	
			}
		}
		return miMAV;		
		
	}

	/**
	 * Simply selects the home view to render by returning its name.
	 * @throws Exception 
	 */

	@RequestMapping(value = "menu", method = RequestMethod.GET)
	public String menu(HttpServletRequest request, Locale locale, Model model) throws Exception {
	    Date date1;
	    Date date2 = new Date();
	    DAOPersona dao = new DAOPersona();
	    Persona e = dao.getPersona((String) request.getSession().getAttribute("usernombre"));
		logger.info("Register page! The client locale is {}.", locale);
		
		try {
			date1 = e.getFecha();
		    long diff = date2.getTime() - date1.getTime();
		    int dias = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
		    System.out.println ("Days: " + dias);
		    if (dias > 1) 
				return "passtresmeses";
		} catch (Exception x) {
		    x.printStackTrace();
		}
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate );
		DAOPublicacion daoPublicacion = new DAOPublicacion();
		HttpSession session=request.getSession();
		Persona a=(Persona) session.getAttribute("persona");
		List<Publicacion> publicaciones = daoPublicacion.leerTodasPublicacionesUser(a);
		model.addAttribute("listPublicacionesPersona", publicaciones );
		System.out.println("ewf");
		
		return "menu";
	}

	@RequestMapping(value = "eliminarPublicacionPersonal", method = RequestMethod.POST)
	public ModelAndView eliminar(HttpServletRequest request, HttpServletResponse response, Model model)throws Exception{
		// NOS TRAEMOS LA LISTA DE USUARIOS EXISTENTES
		ModelAndView miMAV = new ModelAndView("menu");
		List<Persona> usuarios =new ArrayList<Persona>();
		DAOPersona dao = new DAOPersona();
		usuarios=dao.getAllPersonas();
		model.addAttribute("listUsuarios", usuarios );
		DAOPublicacion daoPublicacion = new DAOPublicacion();
		
		// TRAIGO LA INFORMACION QUE NECESITO
		String username;
		username = request.getParameter("eliminarNombre");
		String mensaje;
		mensaje = request.getParameter("eliminarMensaje");
		String fecha;
		fecha = request.getParameter("eliminarFecha");
		
		// CREAMOS LA PUBLICACION QUE QUEREMOS BORRAR
		daoPublicacion.borrarPublicacionExacta(username, fecha);
		HttpSession session=request.getSession();
		Persona a=(Persona) session.getAttribute("persona");
		List<Publicacion> publicaciones = daoPublicacion.leerPublicaciones(a.getUsername());
		model.addAttribute("listPublicacionesPersona", publicaciones );

		return miMAV;
	}
	@RequestMapping(value = "editarPublicacionPersonal", method = RequestMethod.POST)
	public ModelAndView editar(HttpServletRequest request, HttpServletResponse response, Model model)throws Exception{
		// NOS TRAEMOS LA LISTA DE USUARIOS EXISTENTES
		ModelAndView miMAV = new ModelAndView("menu");
		List<Persona> usuarios =new ArrayList<Persona>();
		DAOPersona dao = new DAOPersona();
		usuarios=dao.getAllPersonas();
		model.addAttribute("listUsuarios", usuarios );
		DAOPublicacion daoPublicacion = new DAOPublicacion();
		
		// TRAIGO LA INFORMACION QUE NECESITO
		String username;
		username = request.getParameter("eliminarNombre");
		String mensaje;
		mensaje = request.getParameter("eliminarMensaje");
		String fecha;
		fecha = request.getParameter("eliminarFecha");
		Publicacion pubAnt = daoPublicacion.leerPublicacion(username,fecha);
		
		// CREAMOS LA PUBLICACION QUE QUEREMOS BORRAR
		String newUsername = request.getParameter("eliminarNombre");
		String newMessage = request.getParameter("nuevoMensaje");
		//String newMessage = "Prueba de editar el mensaje";
		LinkedList<String> listaVacia = new LinkedList<String> ();
		Publicacion pubNew = new Publicacion(newUsername,newMessage,"publico",listaVacia);
		daoPublicacion.actualizaPublicacion(pubAnt, pubNew);
		HttpSession session=request.getSession();
		Persona a=(Persona) session.getAttribute("persona");
		List<Publicacion> publicaciones = daoPublicacion.leerPublicaciones(a.getUsername());
		model.addAttribute("listPublicacionesPersona", publicaciones );

		return miMAV;
	}
	
	@RequestMapping(value = "publicarMensaje", method = RequestMethod.POST)
	public ModelAndView publicar(HttpServletRequest request, HttpServletResponse response,Model model)throws Exception{
		String username, texto,privacidad;
		LinkedList<String> adjuntos= new LinkedList<String>();
		texto = request.getParameter("message");
		HttpSession session=request.getSession();
		Persona user=(Persona) session.getAttribute("persona");
		//username = request.getParameter("obtenerUsuario");
		username= user.getUsername();
		privacidad=request.getParameter("privacidad");
		
		DAOPublicacion dao = new DAOPublicacion();
		Publicacion p,a;
		p = new Publicacion(username,texto,privacidad,adjuntos);
		if(dao.crearPublicacion(p)) {
			DAOPublicacion daoPublicacion = new DAOPublicacion();
			List<Publicacion> publicaciones = daoPublicacion.leerPublicaciones(username);
			model.addAttribute("listPublicacionesPersona", publicaciones );
			return new ModelAndView("menu", "publicacion", p);
		}else {
			return new ModelAndView("menu", "aviso", "Ha habido algún problema");
		}
		
	}
	
	

}
