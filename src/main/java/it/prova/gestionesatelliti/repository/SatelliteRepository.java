package it.prova.gestionesatelliti.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import it.prova.gestionesatelliti.model.Satellite;


public interface SatelliteRepository extends CrudRepository<Satellite, Long>,JpaSpecificationExecutor<Satellite>{
	
	@Query("select s from Satellite s where s.dataLancio < ?1 and s.stato != 'DISATTIVATO'")
	List<Satellite> findAllLanciatiPiuDiDueAnniENonDisattivati(Date data);
	
	@Query("select s from Satellite s where s.dataRientro = null and s.stato = 'DISATTIVATO'")
	List<Satellite> findAllByStatoDisattivatoAndDataRientroIsNull();
	
	@Query("select s from Satellite s where s.dataLancio < ?1 and s.stato = 'FISSO'")
	List<Satellite> findAllByDataLancioDieciAndStatoFisso(Date data);
	
	@Query("from Satellite s where not s.stato = 'DISATTIVATO' and s.dataRientro = null")
	List<Satellite> findAllPartitiMaNonRientratiAndNonDisabilitati();
}
