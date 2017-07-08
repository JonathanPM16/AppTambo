package com.cloud.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cloud.entities.Empleado;
import com.cloud.entities.Productositio;
import com.cloud.entities.Salidaproducto;
import com.cloud.service.ProductositioService;
import com.cloud.service.SalidaproductoService;

@Controller
public class SalidaproductoController {

	@Autowired
	private SalidaproductoService salidaproductoService;
	
	@Autowired
	private ProductositioService productositioService;
	
	private List<Productositio> listProductoSitioSucursal()
	{
		List<Productositio> listprosit= new ArrayList<>();
		for (Productositio prosit : productositioService.listAllProductositio()) {
			if(prosit.getSitio().getAlmacen().getTipoalmacen().equals("Sucursal"))
				listprosit.add(prosit);
		}
		return listprosit;
	}
	
	@RequestMapping(value="/newSalidaproducto")
	public String Salidaproductos(Model model){
		model.addAttribute("salidaproducto", new Salidaproducto());
		model.addAttribute("salidaproductos",salidaproductoService.listAllSalidaproducto());
		model.addAttribute("productositios",listProductoSitioSucursal());
		return "newSalidaproducto";
	}
	
	@RequestMapping(value="/salidaproducto/new", method=RequestMethod.POST) //HOME
	public String saveSalidaproducto(@Valid Salidaproducto sp, BindingResult result, Model model, HttpSession session){
		try 
		{
			if(result.hasErrors()){
				model.addAttribute("message",result.toString());
				model.addAttribute("salidaproductos",salidaproductoService.listAllSalidaproducto());
				model.addAttribute("productositios",listProductoSitioSucursal());
				return "newSalidaproducto";
			}
			Empleado empleadologueado = (Empleado) session.getAttribute("usuariologueado");
			sp.setEmpleado(empleadologueado);
			
			//FALSO ID PRODUCTO PARA PRODUCTO SITIO
			Productositio productositio = productositioService.getByIdproductositio(sp.getProducto().getIdproducto());
			if(sp.getCantidad()<=productositio.getCantidad())
			{
				productositio.setCantidad(productositio.getCantidad()-sp.getCantidad());
				sp.setProducto(productositio.getProducto());
				sp.setSitio(productositio.getSitio());
				salidaproductoService.saveSalidaproducto(sp);
				productositioService.saveProductositio(productositio);
				model.addAttribute("message", "Salida de Producto guardada satisfactoriamente.");
				return "redirect:/newSalidaproducto";
			}
			else
			{
				model.addAttribute("message", "ERROR: La cantidad a retirar es mayor que la cantidad almacenada.");
				model.addAttribute("salidaproductos",salidaproductoService.listAllSalidaproducto());
				model.addAttribute("productositios",listProductoSitioSucursal());
				return "newSalidaproducto";
			}
		} 
		catch (Exception e) 
		{
			model.addAttribute("message", "ERROR: Por favor, asegurarese que todos los campos esten llenos y correctos.");
			model.addAttribute("salidaproductos",salidaproductoService.listAllSalidaproducto());
			model.addAttribute("productositios",listProductoSitioSucursal());
			return "newSalidaproducto";
		}
	}
	
	@RequestMapping(value="/salidaproductos")
	public String listarSalidaproductos(Model model){
		model.addAttribute("salidaproductos",salidaproductoService.listAllSalidaproducto());
		return "salidaproductos";
	}

	@RequestMapping( value="/deletesalidaproducto{id}")
	public String deleteSalidaproducto(@PathVariable int id, Model model)
	{
		try
		{
			salidaproductoService.deleteSalidaproducto(id);
			return"redirect:/newSalidaproducto";
		}
		catch (Exception e)
		{
			model.addAttribute("message", "ERROR: No se puede eliminar un ENTIDAD que está incluido dentro de alguna ENTIDAD.");
			model.addAttribute("salidaproducto", new Salidaproducto());
			model.addAttribute("salidaproductos",salidaproductoService.listAllSalidaproducto());
			return "newSalidaproducto";
		}
	}
	
	@RequestMapping(value="/editarSalidaproducto{idsalidaproducto}",method=RequestMethod.GET)
	public String editarSalidaproducto(@PathVariable int idsalidaproducto,Model model)
	{
		model.addAttribute("salidaproducto", salidaproductoService.getByIdsalidaproducto(idsalidaproducto));
		model.addAttribute("productositios",listProductoSitioSucursal());
		return "editarSalidaproducto";
	}

}
