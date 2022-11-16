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
			RedirectAttributes redirectAttrs) {
		
		//se data lancio è minore di data rientro
		if(satellite.getDataLancio() != null && satellite.getDataRientro() != null && satellite.getDataLancio().after(satellite.getDataRientro())) {
			result.rejectValue("dataLancio", "satellite.data.minoreDiRientro");			
			return "satellite/insert";
		}
		//se data rientro è valorizzato ma data lancio no
		if(satellite.getDataRientro() != null && satellite.getDataLancio()==null) {
			result.rejectValue("dataLancio", "satellite.data.rientroValorizzatoMaLancioNo");
			return "satellite/insert";
		}
		//se data Di lancio e rientro è dopo la data odierna lo stato deve essere diverso da disattivato
		if (satellite.getDataLancio() != null && satellite.getDataRientro() != null && satellite.getDataLancio().before(new Date())
				&& satellite.getDataRientro().before(new Date()) && (satellite.getStato() == null || satellite.getStato() != StatoSatellite.DISATTIVATO)) {
			result.rejectValue("stato", "satellite.data.rientroValorizzatoMaLancioNo");
			return "satellite/insert";
		}
		//se data Lancio e rientro sono null ma stato è valorizzato
		if(satellite.getDataLancio() == null && satellite.getDataRientro() == null && satellite.getStato() != null) {
			result.rejectValue("dataLancio", "satellite.data.datenullMaStatoValorizzato");
			return "satellite/insert";
		}
		//se data lancio ha valore ma non si ha uno stato
		if(satellite.getDataLancio() != null && satellite.getDataLancio().before(new Date()) && satellite.getStato() == null) {
			result.rejectValue("dataLancio", "satellite.data.dataLancioMaSenzaStato");
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
	public String preDelete(@PathVariable(required = true) Long idSatellite, Model model, RedirectAttributes redirectAttrs) {
		
		model.addAttribute("del_satellite_attr", satelliteService.caricaSingoloElemento(idSatellite));
		return "satellite/delete";
	}
	
	@PostMapping("/delete")
	public String delete(@RequestParam Long idDaEliminare, RedirectAttributes redirectAttrs) {
		
		Satellite satellite= satelliteService.caricaSingoloElemento(idDaEliminare);
		
		if(satellite.getDataRientro()!=null && satellite.getDataLancio()!=null && satellite.getDataRientro().after(new Date())) {
			redirectAttrs.addFlashAttribute("errorMessage", "Non si puo eseguire l'operazione");
			return "redirect:/satellite";
		}
		
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
		
		//se stato è disattivato data Rientro deve essere valorizzata
		if(satellite.getStato()==StatoSatellite.DISATTIVATO && satellite.getDataRientro()==null) {
			result.rejectValue("dataRientro", "satellite.data.statoDisattivatoMaDataRientroNull");			
			return "satellite/update";
		}
		
		//se data Rientro valorizzata deve esserlo anche data Lancio
		if(satellite.getDataLancio()==null && satellite.getDataRientro()!=null) {
			result.rejectValue("dataLancio", "satellite.data.dataRientroValorizzataMaLancioNo");
		}
				
		//controlliamo se le date sono valorizzate altrimenti after e before restituiscono null
		if(satellite.getDataLancio()!=null && satellite.getDataRientro()!=null) {
			//se la data di lancio è maggiore di data rientro
			if(satellite.getDataLancio().after(satellite.getDataRientro())) {
				result.rejectValue("dataLancio", "satellite.data.minoreDiRientro");			
				return "satellite/update";
			}
			//se data Lancio e data Rientro è precedente alla data di oggi lo stato satellite deve essere DISATTIVATO
			if(satellite.getDataLancio().before(new Date()) && satellite.getDataRientro().before(new Date()) && satellite.getStato() != StatoSatellite.DISATTIVATO) {
				result.rejectValue("stato", "satellite.stato.datePrecedentiAOggiStatoNonDisattivato");			
				return "satellite/update";
			}
			//se data rientro è valorizzata deve esserlo anche stato
			if(satellite.getDataRientro()!=null && satellite.getStato()==null) {
				result.rejectValue("stato", "satellite.stato.dataRientroValorizzataMaStatoNo");			
				return "satellite/update";
			}
			
		}
		
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
