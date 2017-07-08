package com.cloud.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.cloud.entities.Empleado;
import com.cloud.entities.Tipoempleado;
import com.cloud.service.EmpleadoService;

@Controller
public class InicioController {
	
	@Autowired
	private EmpleadoService empleadoService;
	
	@RequestMapping(value = "/")
	public String inicio(HttpSession session) {
		Empleado empleadoCero = new Empleado();
		Tipoempleado teempleadoCero = new Tipoempleado();
		teempleadoCero.setIdtipoempleado(0);
		empleadoCero.setIdempleado(0);
		empleadoCero.setTipoempleado(teempleadoCero);
		session.setAttribute("usuariologueado", empleadoCero);
		return "inicio";
	}
	
	@RequestMapping(value = "/empleado")
	public String inicioEmpleado() {
		return "loginEmpleado";
	}
	
	@RequestMapping(value = "/empleado/admin/inicio")
	public String inicioEmpleadoAdmin() {
		return "inicioEmpleadoAdmin";
	}
	
	@RequestMapping(value = "/empleado/logistica/inicio")
	public String inicioEmpleadoLogistica() {
		return "inicioEmpleadoLogistica";
	}
	
	@RequestMapping(value = "/empleado/sucursal/inicio")
	public String inicioEmpleadoSucursal() {
		return "inicioEmpleadoSucursal";
	}
	
	@RequestMapping(value="/empleado/login", method=RequestMethod.POST)
	public String inicioEmpleadoLogin(@RequestParam String usuario, @RequestParam String password, Model model,
			                    HttpSession session)
	{
	   Empleado empleado = empleadoService.autenticarEmpleado(usuario, password);
	   if (usuario=="" || password=="") {
		   model.addAttribute("message", "ingresar los datos completos");
		   return "loginEmpleado";
	   }
	   else if (empleado==null) {
		   model.addAttribute("message", "los datos ingresados no son correctos");
		   return "loginEmpleado";
	   }
	   session.setAttribute("usuariologueado", empleado);
	   int idte = empleado.getTipoempleado().getIdtipoempleado();
	   if(idte==1)
	   {
		   return "redirect:/empleado/admin/inicio";
	   }
	   if(idte==2)
	   {
		   return "redirect:/empleado/logistica/inicio";
	   }
	   if(idte==3)
	   {
		   return "redirect:/empleado/sucursal/inicio";
	   }
	   return "redirect:/empleado/logistica/inicio";
	   
	}
	
	@RequestMapping(value = "/proveedor")
	public String loginProveedor() {
		return "loginProveedor";
	}
}
