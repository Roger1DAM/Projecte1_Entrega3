package provesMYSQL;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Scanner;

public class ProvesMYSQL {
    static final String PATHPENDENTS = "files/ENTRADES PENDENTS/";
    static final String PATHPROCESSATS = "files/ENTRADES PROCESSADES/";
    static final String PATHCOMANDES = "files/COMANDES/";
    static Connection connexioBD = null;
    static Scanner teclat = new Scanner(System.in);

    public static void main(String[] args) throws SQLException, IOException {

        boolean sortir = false;

        connexioBD();

        do {
            System.out.println("\n-----GESTOR D'INVENTARI-----");
            System.out.println("1. Gestió productes");
            System.out.println("2. Actualitzar stock");
            System.out.println("3. Preparar comandes");
            System.out.println("4. Analitzar les comandes");
            System.out.println("5. Sortir");
            System.out.print("\nTRIA UNA OPCIÓ: ");

            int opcio = teclat.nextInt();

            switch (opcio) {
            case 1:
                gestioProductes();
                break;
            case 2:
                actualitzarStock();
                break;
            case 3:
                prepararComandes();
                break;
            case 4:
                analisiComandes();
                break;
            case 5:
                sortir = true;
                break;
            default:
                System.out.println("VALOR NO VÀLID");
                break;
            }
        } while (!sortir);
        desconnexioBD();
    }

    static void gestioProductes() throws SQLException {

        Scanner teclat = new Scanner(System.in);
        boolean sortir = false;

        connexioBD();

        do {
            System.out.println("\n-----GESTOR PRODUCTES-----");
            System.out.println("1. LLISTA tots els productes");
            System.out.println("2. Consulta un producte");
            System.out.println("3. ALTA producte");
            System.out.println("4. MODIFICA producte");
            System.out.println("5. ESBORRA producte");
            System.out.println("6. Sortir");
            System.out.print("\nTRIA UNA OPCIÓ: ");

            int opcio = teclat.nextInt();

            switch (opcio) {
            case 1:
                llistarTotsProductes();
                break;
            case 2:
                consultaProducte();
                break;
            case 3:
                altaProductes();
                break;
            case 4:
                modificarProducte();
                break;
            case 5:
                esborraProductes();
                break;
            case 6:
                sortir = true;
                break;
            default:
                System.out.println("VALOR NO VÀLID");
                break;
            }
        } while (!sortir);
    }

    static void consultaProducte() throws SQLException {
        System.out.println("Introdueix l'ID del producte que vols consultar: ");
        int id = teclat.nextInt();

        String consulta = "SELECT * FROM productes WHERE id = " + id;

        PreparedStatement sentencia = connexioBD.prepareStatement(consulta);

        sentencia.executeQuery();

        ResultSet rs = sentencia.executeQuery();

        while (rs.next()) {
            System.out.println("ID: " + rs.getInt("id") + " | Nom: " + rs.getString("nom") + " | Estoc: "
                    + rs.getInt("estoc") + " | Imatge: " + rs.getString("imatge") + " | Codi categoria: "
                    + rs.getInt("codi_categoria"));
        }
    }

    static void altaProductes() throws SQLException {
        System.out.println("Introdueix el nom del producte: ");
        teclat.nextLine();
        String nom = teclat.nextLine();
        System.out.println("Introdueix l'estoc del producte: ");
        int estoc = teclat.nextInt();
        System.out.println("Introdueix la url de la imatge");
        teclat.nextLine();
        String url = teclat.nextLine();
        System.out.println("Introdueix el codi de la categoria: ");
        int codiCat = teclat.nextInt();

        String consulta = "INSERT INTO productes (nom, estoc, imatge, codi_categoria) VALUES (?,?,?,?)";
        PreparedStatement sentencia = connexioBD.prepareStatement(consulta);

        sentencia.setString(1, nom);
        sentencia.setInt(2, estoc);
        sentencia.setString(3, url);
        sentencia.setInt(4, codiCat);

        if (sentencia.executeUpdate() != 0) {
            System.out.println("Producte donat d'alta: " + nom + " | " + estoc + " | " + url + " | " + codiCat);
        } else {
            System.out.println("No s'ha donat d'alta cap producte");
        }
    }

    static void llistarTotsProductes() throws SQLException {
        System.out.println("LLISTAT DE TOTS ELS PRODUCTES");

        // Creem un String, el valor serà la consulta que volem fer.
        String consulta = "SELECT * FROM productes ORDER BY id";

        PreparedStatement ps = connexioBD.prepareStatement(consulta);

        // Per fer SELECT fem executeQuery, per fer INSERT, UPDATE, DELETE
        // executeUpdate()
        ps.executeQuery();

        ResultSet rs = ps.executeQuery();

        // rs.next retorna un valor booleà. TRUE si hi ha més registres de la consulta
        // que s'ha fet, FALSE si no n'hi ha.
        while (rs.next()) {
            System.out.println("ID: " + rs.getInt("id") + " | Nom: " + rs.getString("nom") + " | Estoc: "
                    + rs.getInt("estoc") + " | Imatge: " + rs.getString("imatge") + " | Codi categoria: "
                    + rs.getInt("codi_categoria"));
        }
    }

    static void modificarProducte() throws SQLException {
        System.out.println("Escriu la ID producte que vol modificar: ");
        int id = teclat.nextInt();
        teclat.nextLine();

        String productes = " SELECT * FROM productes WHERE id =" + id;
        PreparedStatement dades = connexioBD.prepareStatement(productes);
        dades.executeQuery();

        ResultSet producte = dades.executeQuery();

        if (!producte.next()) {

        } else {
            // Declaro les variables on es guardaran els valors dels atributs del producte.
            String nom;
            int estoc;
            String imatge;
            int codi_categoria;

            // Aquesta serà la consulta que fa per actualitzar els atributs.
            String actualitzar = "UPDATE productes SET nom = ?, estoc= ?, imatge= ?, codi_categoria= ? where id = "
                    + id;
            PreparedStatement sentencia = connexioBD.prepareStatement(actualitzar);

            System.out.println("Escriu 'S' si vols editar el nom");
            String resposta = teclat.nextLine();

            // Faig un IF, si l'usuari indica que vol editar el nom es posarà el valor que
            // escrigui a la variable nom, si no escriu S la variable nom mantindrà el valor
            // que ja té.
            // Es repeteix això per a tots els atributs del producte.
            if (resposta.equals("S")) {
                System.out.println("Escriu el NOU nom del producte: ");
                nom = teclat.nextLine();
                sentencia.setString(1, nom);
            } else {
                nom = producte.getString("nom");
                sentencia.setString(2, nom);
            }
            System.out.println("Escriu 'S' si vols editar l'estoc");
            String resposta1 = teclat.nextLine();
            if (resposta1.equals("S")) {
                System.out.println("Escriu l'estoc del producte: ");
                estoc = teclat.nextInt();
                sentencia.setInt(2, estoc);
                teclat.nextLine();
            } else {
                estoc = producte.getInt("estoc");
                sentencia.setInt(2, estoc);
            }
            System.out.println("Escriu 'S' si vols editar l'enllaç de la imatge");
            String resposta2 = teclat.nextLine();
            if (resposta2.equals("S")) {
                System.out.println("Escriu l'enllaç de la imatge del producte: ");
                imatge = teclat.nextLine();
                sentencia.setString(3, imatge);
            } else {
                imatge = producte.getString("imatge");
                sentencia.setString(3, imatge);
            }
            System.out.println("Escriu 'S' si vols editar la categoria");
            String resposta3 = teclat.nextLine();
            if (resposta3.equals("S")) {
                System.out.println("Escriu el codi de la categoria del producte: ");
                codi_categoria = teclat.nextInt();
                sentencia.setInt(4, codi_categoria);
                teclat.nextLine();
            } else {
                codi_categoria = producte.getInt("codi_categoria");
                sentencia.setInt(4, codi_categoria);
            }

            // S'executa i imprimeix un missatge indicant si s'ha fet correctament.
            sentencia.executeUpdate();

            if (sentencia.executeUpdate() != 0) {
                System.out.println("El producte " + id + " s'ha actualitzat correctament");
            } else {
                System.out.println("No s'ha actualitzat cap producte");
            }
        }
    }

    static void esborraProductes() throws SQLException {
        System.out.println("Introdueix la ID del producte que vols eliminar: ");
        int id = teclat.nextInt();

        String consulta = "DELETE FROM productes where id = " + id;
        PreparedStatement delete = connexioBD.prepareStatement(consulta);

        
        if (delete.executeUpdate() != 0) {
            System.out.println("S'ha eliminat el producte " + id);
        } else {
            System.out.println("No s'ha eliminat cap producte");
        }

    }

    static void actualitzarStock() throws IOException, SQLException {
        //He declarat el directori com a CONSTANT al principi del programa.
        File fitxer = new File (PATHPENDENTS);

        //Aquest mètode crea els directoris que hem indicat.
        fitxer.mkdirs();

        if (fitxer.isDirectory()) {
            //El mètode .listFiles retorna un array d'objectes de tipus FILE.
            File[] fitxers = fitxer.listFiles();
            
            //Aquest bucle recorre els fitxers fins que els ha recorregut tots
            for (int i=0; i< fitxers.length; i++) {
                System.out.println(fitxers[i].getName());
                actualitzarFitxerBD(fitxers[i]);
                moureFitxerAProcessat(fitxers[i]);
            }
        }

        File fitxer3 = new File (PATHPROCESSATS);
        fitxer3.mkdirs();

    }

    static void actualitzarFitxerBD(File fitxer) throws IOException, SQLException {
        FileReader reader = new FileReader(fitxer);
        
        //BufferedReader permet fer un .readLine per a llegir un fitxer línea a línea en lloc de caràcter a caràcter.
        BufferedReader buffer = new BufferedReader(reader);

        String linea;

        //Aquest bucle recorre totes les línies d'un fitxer
        while((linea=buffer.readLine()) != null) {
            System.out.println(linea);
            int posSep = linea.indexOf(":");

            int id = Integer.parseInt(linea.substring(0,posSep));
            int unitats = Integer.parseInt(linea.substring(posSep+1));

            String update = "UPDATE productes SET estoc=estoc+? WHERE id=?";
            PreparedStatement actualitzar = connexioBD.prepareStatement(update);
            actualitzar.setInt(1, unitats);
            actualitzar.setInt(2, id);

            actualitzar.executeUpdate();
            
        }
        //Hem de tancar el FileReader i BufferedReader per a poder moure els fitxers després.
        reader.close();
        buffer.close();
    }

    static void moureFitxerAProcessat(File fitxers) throws IOException {
        FileSystem sistemaFitxers = FileSystems.getDefault();
        Path origen = sistemaFitxers.getPath(PATHPENDENTS + fitxers.getName());
        Path desti = sistemaFitxers.getPath(PATHPROCESSATS + fitxers.getName());

        //Indiquem quin fitxer volem moure i on el volem moure.
        Files.move(origen, desti, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("S'ha mogut a processats el fitxer: " + fitxers.getName());
    }

    static void prepararComandes() throws SQLException, IOException {
        String consulta = "SELECT A.id, A.nom, A.estoc, B.nom FROM productes A, proveïdors B, proveeix C WHERE A.estoc < 20  AND  C.id = A.id AND C.NIF = B.NIF ORDER BY B.nom;";
        PreparedStatement menor20 = connexioBD.prepareStatement(consulta);

        ResultSet rs = menor20.executeQuery();

        String proveidorAnterior = null;
        
        while (rs.next()) {

            FileWriter fw = null;
            BufferedWriter bf = null;
            PrintWriter pw = null;
            String format = "|%-10s |%-50s |%-35s|\n";
            String proveidorActual = rs.getString("B.nom");
            if (!proveidorActual.equals(proveidorAnterior)) {

                fw = new FileWriter(PATHCOMANDES + rs.getString("B.nom") + LocalDate.now() + ".txt", true);
                bf = new BufferedWriter(fw);
                pw = new PrintWriter(bf);
                
                String infoF = "|%-10s %-87s|\n";

                //Escriu la capçalera cada cop que canvia el proveidor
                pw.println("|---------------------------------------------COMANDA-----------------------------------------------|\n|												    |");
                pw.println("|___________________________________________________________________________________________________|");
                pw.printf(infoF, "PROVEIDOR: ", rs.getString("B.nom"), "\n|\n");
                pw.printf(format, "ID", "PRODUCTE", "QUANTITAT");
                proveidorAnterior = proveidorActual;
                pw.close();
            }

            
            int estoc = rs.getInt("A.estoc");
            
            //Es demanaran productes per a que n'hi hagi 200 de cada.
            int quant = 200 - estoc;
            
            fw = new FileWriter(PATHCOMANDES + rs.getString("B.nom") + LocalDate.now() + ".txt", true);
            bf = new BufferedWriter(fw);
            pw = new PrintWriter(bf);
            
            //Escriu els productes que es volen demanar.
            pw.printf(format, rs.getInt("A.id"), rs.getString("A.nom"), quant);
            pw.close();
        }
        
    }

    static void analisiComandes() {

    }

    static void connexioBD() {

        String servidor = "jdbc:mysql://localhost:3306/";
        String bbdd = "empresa";
        String user = "root";
        String password = "root";

        try { // El try intenta fer una connexió amb la base de dades.
            connexioBD = DriverManager.getConnection(servidor + bbdd, user, password);
            System.out.println("Connexió amb èxit");
        } catch (SQLException e) { // Si la connexió no funciona executarà el codi de dins del catch.
            e.printStackTrace();
        }

    }

    static void desconnexioBD() throws SQLException {
        connexioBD.close();
    }

}
