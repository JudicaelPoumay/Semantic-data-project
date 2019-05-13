

public class Main {

	public static void main(String[] args) {
		String ior = "http://www.semanticweb.org/jxa/ontologies/2019/3/tasmania";
		OWLapi api;
		api = new OWLapi();
		api.LoadOntologyFile("/Users/alexandrelhoest/Desktop/2eme_quadri/Semantics/Project/project/australia.owl");
		//api.LoadOntologyWeb("http://www.semanticweb.org/jxa/ontologies/2019/3/tasmania");
		api.AddLogicalAxiom(ior, "#Human");
		api.PrintOntology();
		api.AddSubclassAxiom(ior, "#Woman", "#Human");
		api.AddSubclassAxiom(ior, "#Man", "#Human");
		api.AddSubclassAxiom(ior, "#Alien", "#Human");
		api.PrintOntology();
		api.RemoveSubclassAxiom(ior, "#Alien", "#Human");
		api.PrintOntology();
		
		api.AddAnnotation(ior, "#Human", "relating to or characteristic of humankind.");
		//api.RemoveAnnotation(ior, "#Human", "relating to or characteristic of humankind.");
		
		api.AddIndividual(ior, "#Michel", "#Man");
		api.AddIndividual(ior, "#Fred", "#Woman");
		api.RemoveIndividual(ior, "#Fred", "#Woman");
		
		api.AddDataPropertyDomain(ior, "#is_readhead", "#Human");
		
		api.AddObjectPropertyDomainRange(ior, "#loves", "#Human", "#Animal");
		
		api.CallReasoner();
		api.ClassHierarchy();
		api.GetInfSub("http://www.semanticweb.org/jxa/ontologies/2019/3/tasmania#Large_animal", true);
		api.MaterialiseInferences("/Users/alexandrelhoest/Desktop/2eme_quadri/Semantics/Project/project/australia-inf.owl");
		api.SaveOntology("/Users/alexandrelhoest/Desktop/2eme_quadri/Semantics/Project/project/australia.owl");
		
	}

}



