package it.prova.gestionesatelliti.web.controller;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.prova.gestionesatelliti.model.Satellite;
import it.prova.gestionesatelliti.model.StatoSatellite;
import it.prova.gestionesatelliti.service.SatelliteService;

@Controller
@RequestMapping(value = "/satellite")
public class SatelliteController {
	
	@Autowired
	private SatelliteService satelliteService;
	
	@GetMapping
	public ModelAndView listAll() {
		ModelAndView mv = new ModelAndView();
		List<Satellite> results = satelliteService.listAllElements();
		mv.addObject("satellite_list_attribute", results);
		mv.setViewName("satellite/list");
		return mv;
	}
	
	@GetMapping("/search")
	public String search() {
		return "satellite/search";
	}
	
	@PostMapping("/list")
	public String listByExample(Satellite example, ModelMap model) {
		List<Satellite> results = satelliteService.findByExample(example);
		model.addAttribute("satellite_list_attribute", results);
		return "satellite/list";
	}
	
	@GetMapping("/insert")
	public String create(Model model) {
		model.addAttribute("insert_satellite_attr", new Satellite());
		return "satellite/insert";
	}
	
	@PostMapping("/save")
	public String save(@Valid @ModelAttribute("insert_satellite_attr") Satellite satellite, BindingResult result,
			RedirectAttributes redirectAttrs, ModelMap model) {
		
		if(satellite.getDataLancio() != null && satellite.getDataRientro() != null && satellite.getDataLancio().after(satellite.getDataRientro())) {
			model.addAttribute("errorMessage",
					"Errore non puoi inserire una data di lancio minore di data rientro");
			return "satellite/insert";
		}
		if(satellite.getDataRientro() != null && satellite.getDataLancio()==null) {
			model.addAttribute("errorMessage",
					"Errore non puoi inserire una data di rientro senza una data di rientro");
			return "satellite/insert";
		}
		if (satellite.getDataLancio() != null && satellite.getDataRientro() != null && satellite.getDataLancio().before(new Date())
				&& satellite.getDataRientro().before(new Date()) && (satellite.getStato() == null || satellite.getStato() != StatoSatellite.DISATTIVATO)) {
			model.addAttribute("errorMessage",
					"Errore non si possono inserire entrambe le date passate con stato DISATTIVATO");
			return "satellite/insert";
		}
		if(satellite.getDataLancio() == null && satellite.getDataRientro() == null && satellite.getStato() != null) {
			model.addAttribute("errorMessage",
					"Errore non si puo inserire uno stato se non si ha almeno una data di lancio");
			return "satellite/insert";
		}
		if(satellite.getDataLancio() != null && satellite.getDataLancio().before(new Date()) && satellite.getStato() == null) {
			model.addAttribute("errorMessage",
					"Errore bisogna valorizzare uno stato se il satellite è gia stato lanciato");
			return "satellite/insert";
		}

		if (result.hasErrors())
			return "satellite/insert";

		satelliteService.inserisciNuovo(satellite);

		redirectAttrs.addFlashAttribute("successMessage", "Operazione eseguita correttamente");
		return "redirect:/satellite";
	}
	
	@GetMapping("/show/{idSatellite}")
	public String show(@PathVariable(required = true) Long idSatellite, Model model) {
		model.addAttribute("show_satellite_attr", satelliteService.caricaSingoloElemento(idSatellite));
		return "satellite/show";
	}
	
	@GetMapping("/preDelete/{idSatellite}")
	public String preDelete(@PathVariable(required = true) Long idSatellite, Model model) {
		model.addAttribute("del_satellite_attr", satelliteService.caricaSingoloElemento(idSatellite));
		return "satellite/delete";
	}
	
	@PostMapping("/delete")
	public String delete(@RequestParam Long idDaEliminare, RedirectAttributes redirectAttrs) {

		satelliteService.rimuovi(idDaEliminare);

		redirectAttrs.addFlashAttribute("successMessage", "Operazione eseguita correttamente");
		return "redirect:/satellite";
	}
	
	@GetMapping("/preUpdate/{idSatellite}")
	public String preUpdate(@PathVariable(required = true) Long idSatellite, Model model) {
		model.addAttribute("update_satellite_attr", satelliteService.caricaSingoloElemento(idSatellite));
		return "satellite/update";
	}
	
	@PostMapping("/update")
	public String update(@Valid @ModelAttribute("update_satellite_attr") Satellite satellite, BindingResult result,
			RedirectAttributes redirectAttrs) {
		
		if (result.hasErrors())
			return "satellite/update";

		satelliteService.aggiorna(satellite);

		redirectAttrs.addFlashAttribute("successMessage", "Operazione eseguita correttamente");
		return "redirect:/satellite";
	}
	
	@GetMapping("/query1")
	public String query1(Model model) throws ParseException {
		Date dataOggi = new Date();
		Calendar calendario = Calendar.getInstance();
		calendario.setTime(dataOggi);
		calendario.add(Calendar.YEAR, -2);
		Date dataFinale = calendario.getTime();
		
		List<Satellite> result= satelliteService.cercaTuttiLanciatiPiuDiDueAnniENonDisattivati(dataFinale);
		model.addAttribute("satellite_list_attribute", result);
		return "satellite/list";
	}
	
	@GetMapping("/query2")
	public String query2(Model model) {
		List<Satellite> result= satelliteService.cercaDisattivatiMaNonRientrati();
		model.addAttribute("satellite_list_attribute", result);
		return "satellite/list";
	}
	
	@GetMapping("/query3")
	public String query3(Model model) throws ParseException {
		Date dataOggi = new Date();
		Calendar calendario = Calendar.getInstance();
		calendario.setTime(dataOggi);
		calendario.add(Calendar.YEAR, -10);
		Date dataFinale = calendario.getTime();
		List<Satellite> result= satelliteService.cercaRimastiInOrbitaperDieciAnniEFissi(dataFinale);
		model.addAttribute("satellite_list_attribute", result);
		return "satellite/list";
	}
	
	@PostMapping("/launch")
	public String lounch(@RequestParam(name = "idSatellite") Long idSatellite, ModelMap model) {
		Satellite satellite = satelliteService.caricaSingoloElemento(idSatellite);
		satellite.setDataLancio(new Date());
		satellite.setStato(StatoSatellite.IN_MOVIMENTO);
		satelliteService.aggiorna(satellite);
		model.addAttribute("satellite_list_attribute", satelliteService.listAllElements());
		return "satellite/list";
	}
	
	@PostMapping("/rientro")
	public String rientro(@RequestParam(name = "idSatellite") Long idSatellite, ModelMap model) {
		Satellite satellite = satelliteService.caricaSingoloElemento(idSatellite);

		satellite.setDataRientro(new Date());
		satellite.setStato(StatoSatellite.DISATTIVATO);
		satelliteService.aggiorna(satellite);

		model.addAttribute("satellite_list_attribute", satelliteService.listAllElements());
		return "satellite/list";
	}

}
