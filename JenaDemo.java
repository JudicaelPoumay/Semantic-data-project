import org.apache.commons.io.IOUtils;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.RiotException;
import org.apache.jena.util.PrintUtil;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

class JenaDemo {
    private static final String HELPLINE = "\033[1ms\033[0mearch ; \033[1ma\033[0mdd ; \033[1md\033[0melete ; \033[1mq\033[0muery ; \033[1mr\033[0meset ; sa\033[1mv\033[0me ; \033[1mh\033[0melp ; e\033[1mx\033[0mit";
    private static final String PREFIXES_SPARQL = "PREFIX     : <http://www.semanticweb.org/jxa/ontologies/2019/3/tasmania>\n" +
                                           "PREFIX  owl: <http://www.w3.org/2002/07/owl#>\n" +
                                           "PREFIX  rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                                           "PREFIX  xml: <http://www.w3.org/XML/1998/namespace>\n" +
                                           "PREFIX  xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                                           "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                                           "BASE         <http://www.semanticweb.org/jxa/ontologies/2019/3/tasmania>\n";
    private static final String PREFIXES_TTL = Arrays.stream(PREFIXES_SPARQL.replaceAll("(PREFIX|BASE)", "@$1")
            .replaceAll("PREFIX", "prefix")
            .replaceAll("BASE", "base")
            .split("\n"))
            .map(str -> str.concat(" ."))
            .collect(Collectors.joining("\n"));

    private static final Scanner sin = new Scanner(System.in);
    private static Model originModel = null;
    private static Model curModel = null;
    private static String carg;
    private static char cchar;

    public static void main(String[] args) {
        Logger.getRootLogger().setLevel(Level.OFF);

        originModel = loadOntology(args);
        curModel = ModelFactory.createDefaultModel().union(originModel);

        System.out.println(HELPLINE);

        loop();
    }

    private static void loop() {
        while (true) {
            System.out.print(">: ");
            processInput();

            switch (cchar) {
                case 's':
                    searchStatements();
                    break;

                case 'a':
                    addStatements();
                    break;

                case 'd':
                    removeStatements();
                    break;

                case 'q':
                    queryOntology();
                    break;

                case 'v':
                    saveOntology();
                    break;

                case 'r':
                    resetOntology();
                    break;

                case 'h':
                    System.out.println(HELPLINE);
                    break;

                case 'x':
                    System.exit(0);

                default:
                    System.out.println("'" + cchar + "'" + " is not a recognized command!");
            }
        }
    }

    private static void removeStatements() {
        if (!carg.matches("\\d+(\\s*,\\s*\\d+)*")) {
            System.out.println("please provide a comma-separated list of statement ids to remove!");
            return;
        }

        List<Integer> stmtids = Arrays.stream(carg.trim().split("\\s*,\\s*")).map(Integer::valueOf).collect(toList());
        List<Statement> stmtlist = curModel.listStatements().toList();

        curModel.remove(stmtids.stream().map(stmtlist::get).collect(toList()));

        System.out.println("successfully removed statements " + carg);
    }

    private static void searchStatements() {
        // first create an array of the statements
        List<Statement> stmtlist = curModel.listStatements().toList();

        for (int i = 0; i < stmtlist.size(); i++) {
            Statement stmt = stmtlist.get(i);
            String pretty = PrintUtil.print(stmt)
                    .replaceAll("<http://www\\.semanticweb\\.org/jxa/ontologies/2019/3/tasmania([^>]*)>",
                            ":$1");
            if (pretty.toLowerCase().contains(carg.toLowerCase())) {
                System.out.print(String.format("[%4d] ", i));
                System.out.println(pretty);
            }
        }
    }

    private static void processInput() {
        String cline = sin.nextLine();
        carg = "";
        cchar = ' ';

        if (cline.length() > 0) {
            cchar = cline.charAt(0);
            carg = cline.substring(1).trim();
        }
    }

    private static void addStatements() {
        String statements = PREFIXES_TTL;
        while (true) {
            System.out.print("... ");
            String line = sin.nextLine();
            if (!line.equals("")) {
                statements += "\n" + line;
            } else {
                break;
            }
        }

        try {
            curModel.read(IOUtils.toInputStream(statements, StandardCharsets.UTF_8), null, "TURTLE");
            System.out.println("statements added!");
        } catch (RiotException e) {
            e.printStackTrace();
        }
    }

    private static void resetOntology() {
        curModel = ModelFactory.createDefaultModel().union(originModel);
        System.out.println("ontology reset to original!");
    }

    private static Model loadOntology(String[] args) {
        String ontname;
        if (args.length > 0) {
            ontname = args[0];
        } else {
            ontname = sin.nextLine();
        }

        Model m = ModelFactory.createDefaultModel();
        m.read(ontname, "TURTLE");

        return m;
    }

    private static void saveOntology() {
        String filename;
        if (carg.length() > 0) {
            filename = carg;
        } else {
            System.out.print("where? ");
            filename = sin.nextLine();
        }

        try {
            FileOutputStream fout = new FileOutputStream(filename);
            curModel.write(fout, "TURTLE");
            fout.close();
            System.out.println(filename + " successfully saved!");
        } catch (IOException e) {
            System.out.println("ERROR: could not save ontology in " + filename);
        }
    }

    private static void queryOntology() {
        String queryString = PREFIXES_SPARQL;

        // append input to querySring until reaching empty line
        while (true) {
            System.out.print("... ");
            String qline = sin.nextLine();
            if (!qline.equals("")) {
                queryString += qline;
            } else {
                break;
            }
        }

        // try to create query from user input, might fail
        try {
            Query query = QueryFactory.create(queryString);
            try (QueryExecution qexec = QueryExecutionFactory.create(query, curModel)) {
                queryString = queryString.toLowerCase();
                // SELECT
                if (queryString.contains("select")) {
                    ResultSet results = qexec.execSelect();
                    ResultSetFormatter.out(System.out, results, query);
                // ASK
                } else if (queryString.contains("ask")) {
                    System.out.println(qexec.execAsk());
                // CONSTRUCT
                } else if (queryString.contains("construct")) {
                    curModel = qexec.execConstruct();
                    System.out.println("operation applied!");
                // DESCRIBE
                } else if (queryString.contains("describe")) {
                    curModel = qexec.execDescribe();
                    System.out.println("operation applied!");
                }
            }
        } catch (QueryParseException e) {
            e.printStackTrace();
        }
    }
}
