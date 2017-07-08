package com.cloud.controller;

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
import com.cloud.entities.Transferenciaproducto;
import com.cloud.service.ProductositioService;
import com.cloud.service.SitioService;
import com.cloud.service.TransferenciaproductoService;

@Controller
public class TransferenciaproductoController {

	@Autowired
	private TransferenciaproductoService transferenciaproductoService;
	
	@Autowired
	private ProductositioService productositioService;
	
	@Autowired
	private SitioService sitioService;
		
	@RequestMapping(value="/newTransferenciaproducto")
	public String Transferenciaproductos(Model model){
		model.addAttribute("transferenciaproducto", new Transferenciaproducto());
		model.addAttribute("transferenciaproductos",transferenciaproductoService.listAllTransferenciaproducto());
		model.addAttribute("productositios",productositioService.listAllProductositio());
		model.addAttribute("sitios",sitioService.listAllSitio());
		return "newTransferenciaproducto";
	}
	
	@RequestMapping(value="/transferenciaproducto/new", method=RequestMethod.POST) //HOME
	public String saveTransferenciaproducto(@Valid Transferenciaproducto tp, BindingResult result, Model model, HttpSession session){
		try 
		{
			if(result.hasErrors()){
				model.addAttribute("message",result.toString());
				model.addAttribute("transferenciaproductos",transferenciaproductoService.listAllTransferenciaproducto());
				model.addAttribute("productositios",productositioService.listAllProductositio());
				model.addAttribute("sitios",sitioService.listAllSitio());
				return "newTransferenciaproducto";
			}
			Empleado empleadologueado = (Empleado) session.getAttribute("usuariologueado");
			tp.setEmpleado(empleadologueado);
			
			//FALSO ID PRODUCTO PARA PRODUCTO SITIO
			Productositio productositiororigen = productositioService.getByIdproductositio(tp.getProducto().getIdproducto());
			if(productositiororigen.getSitio().getIdsitio()!=tp.getSitiohasta().getIdsitio())
			{
				if(tp.getCantidad()<=productositiororigen.getCantidad())
				{
					productositiororigen.setCantidad(productositiororigen.getCantidad()-tp.getCantidad());
					tp.setProducto(productositiororigen.getProducto());
					tp.setSitiodesde(productositiororigen.getSitio());
					transferenciaproductoService.saveTransferenciaproducto(tp);
					productositioService.saveProductositio(productositiororigen);
					
					Iterable<Productositio> listps = productositioService.listAllProductositio();
					Productositio productositiodestino = new Productositio();
					boolean encontrado = false;
					for (Productositio psaux : listps) {
						if(psaux.getProducto().getIdproducto()==tp.getProducto().getIdproducto() && 
								psaux.getSitio().getIdsitio()==tp.getSitiohasta().getIdsitio())
						{
							productositiodestino.setIdproducto_sitio(psaux.getIdproducto_sitio());
							productositiodestino.setProducto(psaux.getProducto());
							productositiodestino.setSitio(psaux.getSitio());
							productositiodestino.setCantidad(psaux.getCantidad());
							encontrado = true;
						}
					}
					if(encontrado)
					{
						productositiodestino.setCantidad(productositiodestino.getCantidad()+tp.getCantidad());
					}
					else
					{
						productositiodestino.setProducto(tp.getProducto());
						productositiodestino.setSitio(tp.getSitiohasta());
						productositiodestino.setCantidad(tp.getCantidad());
					}
					productositioService.saveProductositio(productositiodestino);
					
					model.addAttribute("message", "Transferencia de Producto guardada satisfactoriamente.");
					return "redirect:/newTransferenciaproducto";
				}
				else
				{
					model.addAttribute("message", "ERROR: La cantidad a transferir es mayor que la cantidad almacenada.");
					model.addAttribute("transferenciaproductos",transferenciaproductoService.listAllTransferenciaproducto());
					model.addAttribute("productositios",productositioService.listAllProductositio());
					model.addAttribute("sitios",sitioService.listAllSitio());
					return "newTransferenciaproducto";
				}
			}
			else
			{
				model.addAttribute("message", "ERROR: El sitio origen no puede ser igual al sitio destino.");
				model.addAttribute("transferenciaproductos",transferenciaproductoService.listAllTransferenciaproducto());
				model.addAttribute("productositios",productositioService.listAllProductositio());
				model.addAttribute("sitios",sitioService.listAllSitio());
				return "newTransferenciaproducto";
			}
		} 
		catch (Exception e) 
		{
			model.addAttribute("message", "ERROR: Por favor, asegurarese que todos los campos esten llenos y correctos.");
			model.addAttribute("transferenciaproductos",transferenciaproductoService.listAllTransferenciaproducto());
			model.addAttribute("productositios",productositioService.listAllProductositio());
			model.addAttribute("sitios",sitioService.listAllSitio());
			return "newTransferenciaproducto";
		}
	}
	
	@RequestMapping(value="/transferenciaproductos")
	public String listarTransferenciaproductos(Model model){
		model.addAttribute("transferenciaproductos",transferenciaproductoService.listAllTransferenciaproducto());
		return "transferenciaproductos";
	}

	@RequestMapping( value="/deletetransferenciaproducto{id}")
	public String deleteTransferenciaproducto(@PathVariable int id, Model model)
	{
		try
		{
			transferenciaproductoService.deleteTransferenciaproducto(id);
			return"redirect:/newTransferenciaproducto";
		}
		catch (Exception e)
		{
			model.addAttribute("message", "ERROR: No se puede eliminar un ENTIDAD que está incluido dentro de alguna ENTIDAD.");
			model.addAttribute("transferenciaproducto", new Transferenciaproducto());
			model.addAttribute("transferenciaproductos",transferenciaproductoService.listAllTransferenciaproducto());
			model.addAttribute("productositios",productositioService.listAllProductositio());
			model.addAttribute("sitios",sitioService.listAllSitio());
			return "newTransferenciaproducto";
		}
	}
	
	@RequestMapping(value="/editarTransferenciaproducto{idtransferenciaproducto}",method=RequestMethod.GET)
	public String editarTransferenciaproducto(@PathVariable int idtransferenciaproducto,Model model)
	{
		model.addAttribute("transferenciaproducto", transferenciaproductoService.getByIdtransferenciaproducto(idtransferenciaproducto));
		model.addAttribute("productositios",productositioService.listAllProductositio());
		model.addAttribute("sitios",sitioService.listAllSitio());
		return "editarTransferenciaproducto";
	}

}