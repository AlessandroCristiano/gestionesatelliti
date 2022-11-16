package it.prova.gestionesatelliti.service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import it.prova.gestionesatelliti.model.Satellite;


public interface SatelliteService {
	
	public List<Satellite> listAllElements();

	public Satellite caricaSingoloElemento(Long id);
	
	public void aggiorna(Satellite satelliteInstance);

	public void inserisciNuovo(Satellite satelliteInstance);

	public void rimuovi(Long idSatellite);
	
	public List<Satellite> findByExample(Satellite example);
	
	public List<Satellite> cercaTuttiLanciatiPiuDiDueAnniENonDisattivati(Date data) throws ParseException;
	
	public List<Satellite> cercaDisattivatiMaNonRientrati();
	
	public List<Satellite> cercaRimastiInOrbitaperDieciAnniEFissi(Date data) throws ParseException;
	
	public List<Satellite> cercaDataRientroMinoreDiOggiAndStatoNotNull();

}
