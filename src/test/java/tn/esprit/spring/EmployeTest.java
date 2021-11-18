package tn.esprit.spring;




import java.util.Date;

import javax.transaction.Transactional;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.junit4.SpringRunner;

import tn.esprit.spring.entities.Contrat;
import tn.esprit.spring.entities.Departement;
import tn.esprit.spring.entities.Employe;
import tn.esprit.spring.entities.Role;
import tn.esprit.spring.repository.DepartementRepository;
import tn.esprit.spring.services.EmployeServiceImpl;
import tn.esprit.spring.services.EntrepriseServiceImpl;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class EmployeTest {
	
	@Autowired
	EmployeServiceImpl employeService;
	
	@Autowired
	EntrepriseServiceImpl entrepriseService;
	
	@Autowired
	DepartementRepository departementRepository;
	
	
	@Test
	@Order(1)
	public void testAjoutEmploye() {
		Employe employe = new Employe("Saidi","ahmed","Ahmed.Saidi@esprit.tn",true,Role.INGENIEUR);
		int id = employeService.ajouterEmploye(employe);
		Assert.assertNotNull(employeService.getEmployeById(id));
	}
	
	@Test
	@Order(2)
	public void testMettreAjourEmailByEmployeId()
	{
		Employe employe = new Employe("Slama","ahmed khalil","Ahmedkhalil.slama@esprit.tn",true,Role.INGENIEUR);
		int id = employeService.ajouterEmploye(employe);
		employeService.mettreAjourEmailByEmployeId("olay@esprit.tn", id);
		Assert.assertTrue("not equal", employeService.getEmployeById(id).getEmail().equals("olay@esprit.tn"));
	}
	
	@Transactional
	@Test
	@Order(3)
	public void testaffecterEmployeADepartement()
	{
		Employe employe = new Employe("Mneri","Malek","Malek.Mneri@esprit.tn",true,Role.INGENIEUR);
		int idEmploye = employeService.ajouterEmploye(employe);
		Departement departement = new Departement("Khalil's Departement");
		int idDepartement = entrepriseService.ajouterDepartement(departement);
		employeService.affecterEmployeADepartement(idEmploye, idDepartement);
		Assert.assertTrue(employeService.getdeptById(idDepartement).getEmployes().indexOf(employe)!= -1);
	}
	
	@Transactional
	@Test
	@Order(4)
	public void testdesaffecterEmployeDuDepartemen()
	{
		Employe employe = new Employe("aziz","sahnoun","sahnoun.aziz@esprit.tn",true,Role.INGENIEUR);
		int idEmploye = employeService.ajouterEmploye(employe);
		Departement departement = new Departement("Khalil's Departement");
		int idDepartement = entrepriseService.ajouterDepartement(departement);
		employeService.affecterEmployeADepartement(idEmploye, idDepartement);
		employeService.desaffecterEmployeDuDepartement(idEmploye, idDepartement);
		Assert.assertTrue(employeService.getdeptById(idDepartement).getEmployes().indexOf(employe) == -1);
	}
	
	@Test
	@Order(5)
	public void testAjouterContrat()
	{
		Date date = new Date(System.currentTimeMillis());
		Contrat contrat = new Contrat(date,"CDI",2000);
		int referenceContrat = employeService.ajouterContrat(contrat);
		Assert.assertNotNull(employeService.getContratById(referenceContrat));
	}
	
	@Test
	@Order(6)
	public void testAffecterContratAEmploye()
	{
		Date date = new Date(System.currentTimeMillis());
		Contrat contrat = new Contrat(date,"CDI",2000);
		int referenceContrat = employeService.ajouterContrat(contrat);
		Employe employe = new Employe("Oneil","Shaqil","Shaq.OG@esprit.tn",true,Role.INGENIEUR);
		int idEmploye = employeService.ajouterEmploye(employe);
		employeService.affecterContratAEmploye(referenceContrat, idEmploye);
		Assert.assertNotNull(employeService.getContratById(referenceContrat).getEmploye());
	}
	
	@Test
	@Order(7)
	public void testDeleteEmployeById()
	{
		Employe employe = new Employe("Jordan","Michael","BlackJesus@esprit.tn",true,Role.INGENIEUR);
		int idEmploye = employeService.ajouterEmploye(employe);
		employeService.deleteEmployeById(idEmploye);
		Assert.assertNull(employeService.getEmployeById(idEmploye));
	}
	
}
