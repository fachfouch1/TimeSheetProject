package tn.esprit.spring.services;

import java.util.ArrayList; 
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tn.esprit.spring.entities.Contrat;
import tn.esprit.spring.entities.Departement;
import tn.esprit.spring.entities.Employe;
import tn.esprit.spring.entities.Entreprise;
import tn.esprit.spring.entities.Mission;
import tn.esprit.spring.entities.Timesheet;
import tn.esprit.spring.repository.ContratRepository;
import tn.esprit.spring.repository.DepartementRepository;
import tn.esprit.spring.repository.EmployeRepository;
import tn.esprit.spring.repository.TimesheetRepository;

@Service
public class EmployeServiceImpl implements IEmployeService {
	private static final Logger l = LogManager.getLogger(EmployeServiceImpl.class);
	@Autowired
	EmployeRepository employeRepository;
	@Autowired
	DepartementRepository deptRepoistory;
	@Autowired
	ContratRepository contratRepoistory;
	@Autowired
	TimesheetRepository timesheetRepository;

	public int ajouterEmploye(Employe employe) {
		try {
		employeRepository.save(employe);
		if(l.isDebugEnabled())
		{
			l.debug(String.format("Saved new employe : %s", employe));
			}
		} catch (Exception e) {
			l.error(e.getMessage());
		}
		return employe.getId();
	}

	public void mettreAjourEmailByEmployeId(String email, int employeId) {
		try {
		Employe employe = employeRepository.findById(employeId).get();
		if(l.isDebugEnabled())
		{
			l.debug(String.format("Old mail: %s", employe.getEmail()));					
		}
		employe.setEmail(email);
		if(l.isDebugEnabled())
		{
			l.debug(String.format("New mail: %s", employe.getEmail()));					
		}
		employeRepository.save(employe);
		l.debug("New mail saved");
		} catch (Exception e) {
			l.error(e.getMessage());
		}

	}

	@Transactional	
	public void affecterEmployeADepartement(int employeId, int depId) {
		try {
			if (deptRepoistory.findById(depId).isPresent() && employeRepository.findById(employeId).isPresent()) {
				Departement depManagedEntity =  getdeptById(depId);
				Employe employeManagedEntity = getEmployeById(employeId);
				if (depManagedEntity.getEmployes() == null) {
					List<Employe> employes = new ArrayList<>();
					employes.add(employeManagedEntity);
					depManagedEntity.setEmployes(employes);
					if(l.isDebugEnabled())
					{
						l.debug(String.format("The first employe %s has been affected to the department %s",
								employeManagedEntity.getNom(),depManagedEntity.getName()));						
					}
				} else {
					depManagedEntity.getEmployes().add(employeManagedEntity);
					if(l.isDebugEnabled())
					{
						l.debug(String.format("Employe %s has been affected to the department %s",
								employeManagedEntity.getNom(),depManagedEntity.getName()));						
					}
				}
			}
		} catch (Exception e) {
			l.error(e.getMessage());
		}

	}
	@Transactional
	public void desaffecterEmployeDuDepartement(int employeId, int depId)
	{
		try {
			Departement dep = getdeptById(depId);
			int employeNb = dep.getEmployes().size();
			for (int index = 0; index < employeNb; index++) {
				if (dep.getEmployes().get(index).getId() == employeId) {
					if(l.isDebugEnabled())
					{
						l.debug(String.format("Employe %s kicked from the department %s",
								dep.getEmployes().get(index).getNom(), dep.getName()));							
					}
					dep.getEmployes().remove(index);
					break;
				}
			}
	} catch (Exception e) {
		l.error(e.getMessage());
	}

}

	public int ajouterContrat(Contrat contrat) {
		try{
				contratRepoistory.save(contrat);
				if(l.isDebugEnabled())
				{
					l.debug(String.format("Saved new contract = %s" ,contrat.toString()));					
				}
			}catch (Exception e) {
				l.error(e.getMessage());
			}
	return contrat.getReference();
	}

	public void affecterContratAEmploye(int contratId, int employeId) {
		try {
				Contrat contratManagedEntity = getContratById(contratId);
				Employe employeManagedEntity = getEmployeById(employeId);
				contratManagedEntity.setEmploye(employeManagedEntity);
				contratRepoistory.save(contratManagedEntity);
				if(l.isDebugEnabled())
				{
					l.debug(String.format("Contract %d affected to employe %s", contratManagedEntity.getReference(),
							employeManagedEntity.getNom()));
				}
		} catch (Exception e) {
			l.error(e.getMessage());
		}
	}
	public String getEmployePrenomById(int employeId) {
		String testValidator = "empty";
		try {
			Employe employeManagedEntity = getEmployeById(employeId);
			testValidator = employeManagedEntity.getPrenom();
		} catch (Exception e) {
			l.error(e.getMessage());
		}
		return testValidator;
	}
	public void deleteEmployeById(int employeId)
	{
		try {
		Employe employe = getEmployeById(employeId);
		//Desaffecter l'employe de tous les departements
		//c'est le bout master qui permet de mettre a jour
		//la table d'association
		for (Departement dep : employe.getDepartements()) {
			dep.getEmployes().remove(employe);
			if(l.isDebugEnabled())
			{
				l.debug(String.format("Employe %s deleted from the department %s ",employe.getNom() ,dep.getName()));						
			}
		}
		employeRepository.delete(employe);

} catch (Exception e) {
	l.error(e.getMessage());
}

}

	public void deleteContratById(int contratId) {
		try {
			Contrat contratManagedEntity = getContratById(contratId);
			contratRepoistory.delete(contratManagedEntity);
			if(l.isDebugEnabled())
			{
				l.debug(String.format("Contract %d deleted",contratManagedEntity.getReference()));
			}
		} catch (Exception e) {
			l.error(e.getMessage());
		}
	}

	public int getNombreEmployeJPQL() {
		return employeRepository.countemp();
	}
	
	public List<String> getAllEmployeNamesJPQL() {
		return employeRepository.employeNames();

	}
	
	public List<Employe> getAllEmployeByEntreprise(Entreprise entreprise) {
		return employeRepository.getAllEmployeByEntreprisec(entreprise);
	}

	public void mettreAjourEmailByEmployeIdJPQL(String email, int employeId) {
		employeRepository.mettreAjourEmailByEmployeIdJPQL(email, employeId);

	}
	public void deleteAllContratJPQL() {
         employeRepository.deleteAllContratJPQL();
	}
	
	public float getSalaireByEmployeIdJPQL(int employeId) {
		return employeRepository.getSalaireByEmployeIdJPQL(employeId);
	}

	public Double getSalaireMoyenByDepartementId(int departementId) {
		return employeRepository.getSalaireMoyenByDepartementId(departementId);
	}
	
	public List<Timesheet> getTimesheetsByMissionAndDate(Employe employe, Mission mission, Date dateDebut,
			Date dateFin) {
		return timesheetRepository.getTimesheetsByMissionAndDate(employe, mission, dateDebut, dateFin);
	}

	public List<Employe> getAllEmployes() {
				return (List<Employe>) employeRepository.findAll();
	}

	@Override
	public Employe getEmployeById(int id) {
		Optional<Employe> employe = employeRepository.findById(id);
        if (employe.isPresent()) {
        	if(l.isDebugEnabled())
        	{
        		l.debug(String.format("Entreprise exitse: %d", employe.get().getId()));        		
        	}
            return employe.get();
        }
        return null;
	}
	
	public Contrat getContratById(int id)
	{
		Optional<Contrat> contrat = contratRepoistory.findById(id);
        if (contrat.isPresent()) {
        	if(l.isDebugEnabled())
        	{
        		l.debug(String.format("contrat exitse: %d", contrat.get().getReference()));        		
        	}
            return contrat.get();
        }
        return null;
	}
	
	public Departement getdeptById(int id)
	{
		Optional<Departement> dept = deptRepoistory.findById(id);
        if (dept.isPresent()) {
        	if(l.isDebugEnabled())
        	{
        		l.debug(String.format("dept exitse: %d", dept.get().getId()));        		
        	}
            return dept.get();
        }
        return null;
	}

}
