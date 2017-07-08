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
import com.cloud.entities.Entradaproducto;
import com.cloud.entities.Productositio;
import com.cloud.entities.Sitio;
import com.cloud.entities.Solicitudcotizacion;
import com.cloud.service.EntradaproductoService;
import com.cloud.service.ProductoService;
import com.cloud.service.ProductositioService;
import com.cloud.service.SitioService;
import com.cloud.service.SolicitudcotizacionService;

@Controller
public class EntradaproductoController {

	@Autowired
	private EntradaproductoService entradaproductoService;
	
	@Autowired
	private ProductoService productoService;
	
	@Autowired
	private SitioService sitioService;
	
	@Autowired
	private ProductositioService productositioService;
	
	@Autowired
	private SolicitudcotizacionService solicitudcotizacionService;
	
	private List<Sitio> listSitioGeneral()
	{
		List<Sitio> listsit= new ArrayList<>();
		for (Sitio sit : sitioService.listAllSitio()) {
			if(sit.getAlmacen().getTipoalmacen().equals("Planta General"))
				listsit.add(sit);
		}
		return listsit;
	}
	
	@RequestMapping(value="/newEntradaproducto")
	public String Entradaproductos(Model model){
		model.addAttribute("entradaproducto", new Entradaproducto());
		model.addAttribute("entradaproductos",entradaproductoService.listAllEntradaproducto());
		model.addAttribute("productos",productoService.listAllProducto());
		model.addAttribute("sitios",listSitioGeneral());
		return "newEntradaproducto";
	}
	
	@RequestMapping(value="/entradaproducto/new", method=RequestMethod.POST) //HOME
	public String saveEntradaproducto(@Valid Entradaproducto ep, BindingResult result, Model model, HttpSession session){
		try 
		{
			if(result.hasErrors()){
				model.addAttribute("message",result.toString());
				model.addAttribute("entradaproductos",entradaproductoService.listAllEntradaproducto());
				model.addAttribute("productos",productoService.listAllProducto());
				model.addAttribute("sitios",listSitioGeneral());
				return "newEntradaproducto";
			}
			Solicitudcotizacion sc = new Solicitudcotizacion();
			sc.setCantidad(ep.getSolicitudcotizacion().getCantidad());
			sc.setProducto(ep.getSolicitudcotizacion().getProducto());
			sc.setSitio(ep.getSolicitudcotizacion().getSitio());
			solicitudcotizacionService.saveSolicitudcotizacion(sc);
			
			sc.setIdsolicitudcotizacion(solicitudcotizacionService.getLastIdsolicitudcotizacion().getIdsolicitudcotizacion());
			Empleado empleadologueado = (Empleado) session.getAttribute("usuariologueado");
			ep.setEmpleado(empleadologueado);
			ep.setSolicitudcotizacion(sc);
			entradaproductoService.saveEntradaproducto(ep);
			
			Iterable<Productositio> listps = productositioService.listAllProductositio();
			Productositio productositio = new Productositio();
			boolean encontrado = false;
			for (Productositio psaux : listps) {
				if(psaux.getProducto().getIdproducto()==sc.getProducto().getIdproducto() && 
						psaux.getSitio().getIdsitio()==sc.getSitio().getIdsitio())
				{
					productositio.setIdproducto_sitio(psaux.getIdproducto_sitio());
					productositio.setProducto(psaux.getProducto());
					productositio.setSitio(psaux.getSitio());
					productositio.setCantidad(psaux.getCantidad());
					encontrado = true;
				}
			}
			if(encontrado)
			{
				productositio.setCantidad(productositio.getCantidad()+sc.getCantidad());
			}
			else
			{
				productositio.setProducto(sc.getProducto());
				productositio.setSitio(sc.getSitio());
				productositio.setCantidad(sc.getCantidad());
			}
			productositioService.saveProductositio(productositio);
			model.addAttribute("message", "Entrada de Producto guardada satisfactoriamente.");
			return "redirect:/newEntradaproducto";
		} 
		catch (Exception e) 
		{
			model.addAttribute("message", "ERROR: Por favor, asegurarese que todos los campos esten llenos y correctos.");
			model.addAttribute("entradaproductos",entradaproductoService.listAllEntradaproducto());
			model.addAttribute("productos",productoService.listAllProducto());
			model.addAttribute("sitios",listSitioGeneral());
			return "newEntradaproducto";
		}
	}
	
	@RequestMapping(value="/entradaproductos")
	public String listarEntradaproductos(Model model){
		model.addAttribute("entradaproductos",entradaproductoService.listAllEntradaproducto());
		return "entradaproductos";
	}

	@RequestMapping( value="/deleteentradaproducto{id}")
	public String deleteEntradaproducto(@PathVariable int id, Model model)
	{
		try
		{
			entradaproductoService.deleteEntradaproducto(id);
			return"redirect:/newEntradaproducto";
		}
		catch (Exception e)
		{
			model.addAttribute("message", "ERROR: No se puede eliminar un ENTIDAD que está incluido dentro de alguna ENTIDAD.");
			model.addAttribute("entradaproducto", new Entradaproducto());
			model.addAttribute("entradaproductos",entradaproductoService.listAllEntradaproducto());
			model.addAttribute("productos",productoService.listAllProducto());
			model.addAttribute("sitios",listSitioGeneral());
			return "newEntradaproducto";
		}
	}
	
	@RequestMapping(value="/editarEntradaproducto{identradaproducto}",method=RequestMethod.GET)
	public String editarEntradaproducto(@PathVariable int identradaproducto,Model model)
	{
		model.addAttribute("entradaproducto", entradaproductoService.getByIdentradaproducto(identradaproducto));
		model.addAttribute("productos",productoService.listAllProducto());
		model.addAttribute("sitios",listSitioGeneral());
		return "editarEntradaproducto";
	}

}
