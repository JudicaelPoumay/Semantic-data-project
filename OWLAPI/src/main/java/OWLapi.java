import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.*;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.*;
import org.semanticweb.owlapi.reasoner.*;

import org.semanticweb.HermiT.*;


public class OWLapi {
	
	private OWLOntologyManager man = OWLManager.createOWLOntologyManager();
	private OWLOntology ont;
	private OWLReasoner reas;
	OWLDataFactory df;
	
	public OWLapi() {
		
	}

	public void LoadOntologyFile(String file_path){
		try {
			File file1 = new File(file_path);
			OWLOntology o = this.man.loadOntologyFromOntologyDocument(file1);
			System.out.println(o);
			IRI i = this.man.getOntologyDocumentIRI(o);
			System.out.println(i);
			this.ont = o;
			this.df = this.ont.getOWLOntologyManager().getOWLDataFactory();
		} catch(OWLOntologyCreationException e){
			e.printStackTrace();
		}
	}
	
	public void LoadOntologyWeb(String path){
		try {
			
		IRI iri = IRI.create(path);
		OWLOntology o = this.man.loadOntology(iri);
		System.out.println(o);
		this.ont = o;
		this.df = this.ont.getOWLOntologyManager().getOWLDataFactory();
		
		} catch(OWLOntologyCreationException e){
			e.printStackTrace();
		}
	}

	public void SaveOntology(String file_path){
		try{
			File save_file = new File(file_path);
			man.saveOntology(this.ont, new FunctionalSyntaxDocumentFormat(), new FileOutputStream(save_file));
		}
		catch (FileNotFoundException ex){
			ex.printStackTrace();

		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();

		}
	}
	
	public void AddLogicalAxiom(String ior, String axiom){

		OWLClass new_class = this.df.getOWLClass(ior + axiom);
		OWLDeclarationAxiom da = this.df.getOWLDeclarationAxiom(new_class);
		this.ont.add(da);
	}
	
	public void RemoveLogicalAxiom(String ior, String axiom){
		OWLClass class_remove = this.df.getOWLClass(ior + axiom);
		OWLDeclarationAxiom da = this.df.getOWLDeclarationAxiom(class_remove);
		this.ont.remove(da);
		
	}
	
	public void AddIndividual(String ior, String ind, String class_ind){
		OWLIndividual new_ind = this.df.getOWLNamedIndividual(ior + ind);
		OWLClass cls = this.df.getOWLClass(ior + class_ind);
		OWLAxiom axiom = this.df.getOWLClassAssertionAxiom(cls, new_ind);
		this.ont.add(axiom);
	}
	
	public void RemoveIndividual(String ior, String ind, String class_ind){
		OWLIndividual ind_remove = this.df.getOWLNamedIndividual(ior + ind);
		OWLClass cls = this.df.getOWLClass(ior + class_ind);
		OWLAxiom axiom = this.df.getOWLClassAssertionAxiom(cls, ind_remove);
		this.ont.remove(axiom);
		
	}
	public void AddSubclassAxiom(String ior, String axiom_a, String axiom_b){
		OWLClass class_a = this.df.getOWLClass(ior + axiom_a);
		OWLClass class_b = this.df.getOWLClass(ior + axiom_b);
		OWLSubClassOfAxiom b_sub_a = this.df.getOWLSubClassOfAxiom(class_a, class_b);
		this.ont.add(b_sub_a);
	}
	
	public void RemoveSubclassAxiom(String ior, String axiom_a, String axiom_b){
		OWLClass class_remove_a = this.df.getOWLClass(ior + axiom_a);
		OWLClass class_remove_b = this.df.getOWLClass(ior + axiom_b);
		OWLSubClassOfAxiom b_sub_a = this.df.getOWLSubClassOfAxiom(class_remove_a, class_remove_b);
		this.ont.remove(b_sub_a);
	}
	
	public void AddObjectPropertyDomain(String ior, String object, String class_obj){
		OWLObjectProperty obj = this.df.getOWLObjectProperty(ior + object);
		OWLClass cls = this.df.getOWLClass(ior + class_obj);
		OWLObjectPropertyDomainAxiom axiom = df.getOWLObjectPropertyDomainAxiom(obj, cls);
		this.ont.add(axiom);
		
	}
	
	public void RemoveObjectPropertyDomain(String ior, String object, String domain){
		OWLObjectProperty obj = this.df.getOWLObjectProperty(ior + object);
		OWLClass cls = this.df.getOWLClass(ior + domain);
		OWLObjectPropertyDomainAxiom axiom = df.getOWLObjectPropertyDomainAxiom(obj, cls);
		this.ont.remove(axiom);
	}
	
	public void AddObjectPropertyDomainRange(String ior, String object, String domain, String range){
		OWLObjectProperty obj = df.getOWLObjectProperty(ior+object);
		OWLClass dom = this.df.getOWLClass(ior+domain);
		OWLClass ran = this.df.getOWLClass(ior+range);
		Set<OWLAxiom> dom_ran = new HashSet<OWLAxiom>();
		dom_ran.add(df.getOWLObjectPropertyDomainAxiom(obj, dom));
		dom_ran.add(df.getOWLObjectPropertyRangeAxiom(obj, ran));
		this.man.addAxioms(this.ont, dom_ran);
	}
	
	public void RemoveObjectPropertyDomainRange(String ior, String object, String domain, String range){
		OWLObjectProperty obj = df.getOWLObjectProperty(ior+object);
		OWLClass dom = this.df.getOWLClass(ior+domain);
		OWLClass ran = this.df.getOWLClass(ior+range);
		Set<OWLAxiom> dom_ran = new HashSet<OWLAxiom>();
		dom_ran.add(df.getOWLObjectPropertyDomainAxiom(obj, dom));
		dom_ran.add(df.getOWLObjectPropertyRangeAxiom(obj, ran));
		this.man.removeAxioms(this.ont, dom_ran);
	}
	
	public void AddDataPropertyDomain(String ior, String data, String domain){
		OWLDataProperty dt = this.df.getOWLDataProperty(ior + data);
		OWLClass cls = this.df.getOWLClass(ior + domain);
		OWLDataPropertyDomainAxiom axiom = df.getOWLDataPropertyDomainAxiom(dt, cls);
		this.ont.add(axiom);
	}

	public void RemoveDataPropertyDomain(String ior, String data, String domain){
		OWLDataProperty dt = this.df.getOWLDataProperty(ior + data);
		OWLClass cls = this.df.getOWLClass(ior + domain);
		OWLDataPropertyDomainAxiom axiom = df.getOWLDataPropertyDomainAxiom(dt, cls);
		this.ont.remove(axiom);
	}
					
				
	public void AddAnnotation(String ior, String axiom, String annotation){
		OWLClass class_a = this.df.getOWLClass(IRI.create(ior + axiom));
		OWLAnnotation comment = this.df.getOWLAnnotation(this.df.getRDFSComment(), this.df.getOWLLiteral(annotation, "en"));
		OWLAxiom ax = df.getOWLAnnotationAssertionAxiom(class_a.getIRI(), comment);
		this.ont.add(ax);
		
	}
	
	public void RemoveAnnotation(String ior, String axiom, String annotation){
		OWLClass class_a = this.df.getOWLClass(IRI.create(ior + axiom));
		OWLAnnotation comment = this.df.getOWLAnnotation(this.df.getRDFSComment(), this.df.getOWLLiteral(annotation, "en"));
		OWLAxiom ax = df.getOWLAnnotationAssertionAxiom(class_a.getIRI(), comment);
		this.ont.remove(ax);
	}
	
	public void CallReasoner(){
		OWLReasonerFactory rf = new ReasonerFactory();
		OWLReasoner r = rf.createReasoner(this.ont);
		this.reas = r;
	}
	
	public void ClassHierarchy(){
		this.reas.precomputeInferences(InferenceType.CLASS_HIERARCHY);
	}
	
	// Print the Subclasses 
	public void GetInfSub(String iri, boolean direct){
		this.reas.precomputeInferences(InferenceType.CLASS_HIERARCHY);
		this.reas.getSubClasses(this.df.getOWLClass(iri), direct).forEach(System.out::println);
	}

	
	// This function create an ontology by computing implicit subclass relationships and class assertion axioms.
	public void MaterialiseInferences(String file_name){
		try {
			List<InferredAxiomGenerator<? extends OWLAxiom>> gens=new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>();
			gens.add(new InferredSubClassAxiomGenerator());
			gens.add(new InferredClassAssertionAxiomGenerator());

			gens.add(new InferredDisjointClassesAxiomGenerator() {
				boolean precomputed=false;
				protected void addAxioms(OWLClass entity, OWLReasoner r, OWLDataFactory dataFact, Set<OWLDisjointClassesAxiom> set) {
					if (!precomputed) {
						r.precomputeInferences(InferenceType.DISJOINT_CLASSES);
						precomputed=true;
					}
					for (OWLClass cls : r.getDisjointClasses(entity).getFlattened()) {
						set.add(dataFact.getOWLDisjointClassesAxiom(entity, cls));
					}
				}
			});
 
			InferredOntologyGenerator inf_ont_gen=new InferredOntologyGenerator(this.reas,gens);
 
			OWLOntology inf_ax_ont=this.man.createOntology();
  
			inf_ont_gen.fillOntology(this.man.getOWLDataFactory(), inf_ax_ont);
 
			File inf_ont_file=new File(file_name);
			if (!inf_ont_file.exists())
				inf_ont_file.createNewFile();
			inf_ont_file=inf_ont_file.getAbsoluteFile();
 
			OutputStream outputStream=new FileOutputStream(inf_ont_file);

			this.man.saveOntology(inf_ax_ont, this.man.getOntologyFormat(this.ont), outputStream);

		} catch (OWLOntologyCreationException e){
			e.printStackTrace();
		} catch (IOException ex){
			ex.printStackTrace();
		} catch (OWLOntologyStorageException e){
			e.printStackTrace();
		}
	}

	public void PrintOntology(){
		System.out.println(this.ont);
	}
}
