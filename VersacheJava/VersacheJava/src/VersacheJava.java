import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Formatter;
import java.util.Scanner;

public class VersacheJava {
    // MUY IMPORTANTE: Poner las credenciales del laboratorio.
    private static String USUARIO = "alexrb";
    private static String PASSWORD = "10770849";
    // Nombre de la base de datos
    private static String NOMBRE_BASEDATOS = "versache";
    // Conexion con la base de datos
    private static String STRING_CONEXION = "jdbc:postgresql://alexrl.ddns.net:5432/" + NOMBRE_BASEDATOS;

    public static void main(String[] args) {
        try {
        	Menu m = new Menu(String.format("Ventas %s", NOMBRE_BASEDATOS), "~> ");
        	m.add("Mostrar ventas mes");
        	m.add("Mostrar ventas año");
        	if (m.runSelection() == 1) {
        		getVentasAnno();
        	} else {
        		getVentasMes();
        	}
        } catch (SQLException e) { // Tratamiento por Excepcion
            e.printStackTrace();
        } catch (IOException e) {
			e.printStackTrace();
		}
    }

    public static void writeToFile (String filename, ResultSet rs) throws FileNotFoundException, NumberFormatException, SQLException {
        double diaNoIva = 0;
        double diaIva = 0;
        
        double totalNoIva = 0;
        double totalIva = 0;
        
        double currentNoIva;
        double currentIva;
        
        int dia = 0;
        int mes = 0;
        int anno = 0;

        Formatter out = new Formatter (filename);
        write(out, "Fecha;Dia;Mes;Año;Base;IVA;Total\n");
        while (rs.next()) {
        	currentNoIva = rs.getDouble(2);
        	currentIva = rs.getDouble(3);

        	if (dia != 0 && dia != Integer.parseInt(rs.getString(1).split(" ")[0].split("-")[2])) {
        		write(out, String.format("%d-%d-%d;%d;%d;%d;%.2f;%.2f;%.2f\n", dia, mes, anno, dia, mes, anno, diaNoIva, diaIva-diaNoIva, diaIva));
        		diaNoIva = 0;
        		diaIva = 0;
        	}

        	diaNoIva+=currentNoIva;
        	diaIva+=currentIva;
        	totalNoIva+=currentNoIva;
        	totalIva+=currentIva;

        	dia = Integer.parseInt(rs.getString(1).split(" ")[0].split("-")[2]);
        	mes = Integer.parseInt(rs.getString(1).split(" ")[0].split("-")[1]);
        	anno = Integer.parseInt(rs.getString(1).split(" ")[0].split("-")[0]);
        }
        write(out, String.format("%d-%d-%d;%d;%d;%d;%.2f;%.2f;%.2f\n", dia, mes, anno, dia, mes, anno, diaNoIva, diaIva-diaNoIva, diaIva));
        diaNoIva = 0;
        diaIva = 0;
        	
		write(out, String.format("TOTAL ~>;;;;%.2f;%.2f;%.2f\n", totalNoIva, totalIva-totalNoIva, totalIva));
        if (out != null) {
        	out.close();
        }
        rs.close();
    	
    }

    public static void getVentasMes() throws SQLException, IOException {
    	Scanner in = new Scanner (System.in);
        Connection con = getConexion();
        
        // Crear la consulta SQL
        StringBuilder query = new StringBuilder();
        query.append("SELECT create_date AS date, price_subtotal AS price, price_subtotal_incl AS total ");
        query.append("FROM pos_order_line ");
        query.append("WHERE EXTRACT (YEAR ");
        query.append("FROM pos_order_line.create_date) = ? ");
        query.append("AND EXTRACT (MONTH ");
        query.append("FROM pos_order_line.create_date) = ? ");
        query.append("ORDER BY pos_order_line.create_date");

        PreparedStatement st = con.prepareStatement(query.toString());
        System.out.print("Introduce el mes: ");
        int mes = in.nextInt();
        System.out.print("Introduce el anno: ");
        int anno = in.nextInt(); 
        st.setInt(1, anno);
        st.setInt(2, mes);
        writeToFile(String.format("Ventas_Mes_%d_%d.csv", mes, anno), st.executeQuery());
        st.close();
        con.close();
        in.close();
    }

    public static void getVentasAnno() throws SQLException, IOException {
    	Scanner in = new Scanner (System.in);
        Connection con = getConexion();
        
        // Crear la consulta SQL
        StringBuilder query = new StringBuilder();
        query.append("SELECT create_date AS date, price_subtotal AS price, price_subtotal_incl AS total ");
        query.append("FROM pos_order_line ");
        query.append("WHERE EXTRACT (YEAR ");
        query.append("FROM pos_order_line.create_date) = ? ");
        query.append("ORDER BY pos_order_line.create_date");

        PreparedStatement st = con.prepareStatement(query.toString());
        System.out.print("Introduce el anno: ");
        int anno = in.nextInt();
        st.setInt(1, anno);
        writeToFile(String.format("Ventas_Año_%d.csv", anno), st.executeQuery());
        st.close();
        con.close();
        in.close();
    }

    private static void write(Formatter file, String text) {
    	System.out.print(text);
    	file.format(text);
    }

    private static Connection getConexion() throws SQLException {

        return DriverManager.getConnection(STRING_CONEXION, USUARIO, PASSWORD);
    }

    /**
     * Metodo para procesar el resultado de ResultSet
     * 
     * @throws SQLException
     */
    private static void presentaResultados(ResultSet rs) throws SQLException {
        int numeroColumnas = rs.getMetaData().getColumnCount();
        StringBuilder headers = new StringBuilder();

        for (int i = 1; i < numeroColumnas; i++)
            headers.append(rs.getMetaData().getColumnName(i) + "\t");
        headers.append(rs.getMetaData().getColumnName(numeroColumnas));

        System.out.println(headers.toString());
        StringBuilder result = null;

        while (rs.next()) {
            result = new StringBuilder();
            for (int i = 1; i < numeroColumnas; i++)
                result.append(rs.getObject(i) + "\t");
            result.append(rs.getObject(numeroColumnas));
            System.out.println(result.toString());
        }

        if (result == null)
            System.out.println("No hay datos");
    }

    @SuppressWarnings("resource")
    private static String LeerString() {
        return new Scanner(System.in).nextLine();
    }

    @SuppressWarnings("resource")
    private static int LeerInt() {
        return new Scanner(System.in).nextInt();
    }
}