
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
//import java.io.Vector;
import javafx.util.Pair;
import java.io.*;



public class ProcesadorMovies extends Thread {
	// Referencia a un socket para enviar/recibir las peticiones/respuestas
	private Socket socketServicio;
	// stream de lectura (por aquí se recibe lo que envía el cliente)
	private InputStream inputStream;
	// stream de escritura (por aquí se envía los datos al cliente)
	private OutputStream outputStream;
	
	// Para que la respuesta sea siempre diferente, usamos un generador de números aleatorios.
	private Random random;

	public static ArrayList<Pair< String, String >> bdUsers = new ArrayList<Pair < String, String> >();

	public static ArrayList<Pair<Integer, List<String> >> bdmovies = new ArrayList< Pair< Integer, List<String> >>();

	
	// Constructor que tiene como parámetro una referencia al socket abierto en por otra clase
	public ProcesadorMovies(Socket socketServicio) {
		this.socketServicio=socketServicio;
		random=new Random();
	}
	
	
	public void run(){
		procesa();
	};

	void procesa(){

		//-----------CODIGOS CLIENTE SERVIDOR

		String HEY = "100";
		String OK = "200";
		String OK_REGISTER = "201";
		String ERR_AUTENTICATION = "301";
		String ERR_REGISTER = "302";
		String CIERRA_CONEXION = "400";
		String INF_SENT = "303";
		String INF_MOD = "304";
		String ERR_INF_MOD = "305";


		String datosRecibidos;	
		String datosEnviar = null;
		String respuesta;
		String mens;
		
		boolean foundPsw = false;
		boolean InicioTer = false;

		try {
			// Obtiene los flujos de escritura/lectura
			System.out.println("\n100 HEY\n");

			inputStream=socketServicio.getInputStream();
			outputStream=socketServicio.getOutputStream();
			baseUsuariosRegistrados("usuarios.txt");
			basePeliculas("timeline_movies.txt");

			while(!InicioTer)
			{

				BufferedReader inRead = new BufferedReader(new InputStreamReader(inputStream));

				datosRecibidos = inRead.readLine();
		
				String[] recibo = datosRecibidos.split(" ");
				
				// TRANSFORMAMOS NUESTRO CODIGO CLIENTE SERVIDOR A INTEGER
				Integer codigo = Integer.parseInt(recibo[0]);

				PrintWriter outPrinter = new PrintWriter(outputStream, true);


				switch (codigo){

					case 1001:
							// INICIO SESION
			
							if ( checkUser(recibo[1], recibo[2])) {
			
								System.out.println("\n200 OK\n");
								outPrinter.println(OK);
								InicioTer = true;
							}
							else{
								System.out.println("\n301 ERR_AUTENTICATION\n");
								outPrinter.println( ERR_AUTENTICATION);
							}
							
							
					break;

					case 1002:
							// REGISTRO
							if ( checkRegister(recibo[1], recibo[2])){
								System.out.println("\n302 ERR_REGISTER\n");

								outPrinter.println( ERR_REGISTER);
								
							}else {
								System.out.println("\n201 OK_REGISTER\n");
								outPrinter.println(OK_REGISTER);
								InicioTer = true;
							}
						
					break;

				}

			}
			// HEMOS SUPERADO UN INICIO DE SESION VALIDO

			datosRecibidos = null;

			boolean quieroSalir = false;

			while(!quieroSalir){

				BufferedReader inRead = new BufferedReader(new InputStreamReader(inputStream));

				PrintWriter outPrinter = new PrintWriter(outputStream, true);

				
				datosRecibidos = inRead.readLine();
				if ( datosRecibidos.equals(CIERRA_CONEXION)){
					System.out.println("\n400 CIERRA_CONEXION\n");

					quieroSalir = true;

				}else {
					System.out.println("\n303 INF_SENT\n");

					String [] ca = datosRecibidos.split(" ");

					if(ca.length == 2){	//si solo ha metido un anio
						Integer anio = Integer.parseInt(ca[1]);
					
						String peliculas = peliculasBaseDatos(anio);

						respuesta = peliculas + "\n";

						outPrinter.println(respuesta);

					}else{	//quiere introducir una puta peli nueva 
							// 1º. ver que el año existe
								//si no-> introducir un nuevo anio con la peli 
								//si ya existe
									//buscar todas las pelis de ese año para ver si ya esta
										//si ya está, nohacer nada (mensaje de error)
										//si no, se añade
						
						Integer anio = Integer.parseInt(ca[1]);
						String titulo = ca[2];
						if(ca.length >= 3){
							for(int i = 3; i < ca.length; i++){
								titulo = titulo.concat(" " + ca[i]);
							}
						}
						
						mens = introducirPeliNueva(anio, titulo);

						outPrinter.println(mens);
					}	
				}
			}
		} catch (IOException e) {
			System.err.println("Error al obtener los flujso de entrada/salida.");
		}

	}


	public static void aniadeAnioExistente(String titulo){

	}

	//FUNCION PARA INTRODUCIR UNA PELI NUEVA
	public static String introducirPeliNueva(Integer anio, String titulo)throws FileNotFoundException, IOException {

		String salida = "";
		List<String> titulos = new ArrayList<>();
		boolean anio_encontrado = false;
		boolean titulo_encontrado = false;

		for ( int i = 0 ; i < bdmovies.size() && !anio_encontrado ; i ++){
			if (bdmovies.get(i).getKey().equals(anio) ){
				anio_encontrado = true;
				titulos = bdmovies.get(i).getValue();

				for(int j = 0; j < titulos.size() ; j ++){
					if( titulos.get(j).equals(titulo) ){
						titulo_encontrado = true;
						salida = "la peli ya se encuentra en nuestra base de datos";
						System.out.println("\n305 ERR_INF_MOD\n");

						break;
						//codigo de mensaje de que ya esta la peli en la base de datos
					}
				}

				if(!titulo_encontrado){		//si no hemos encontrado un titulo coincidente
					titulos.add(titulo);
					Pair par = new Pair < Integer, List<String> > (anio, titulos );
					bdmovies.set(i, par);
					salida = "peli aniadida correctamente a la base";
					System.out.println("\n304 INF_MOD\n");
					
					BufferedReader f = new BufferedReader(new FileReader("timeline_movies.txt"));
					String line ;
					String input= "";
					String [] cadenaLinea;
					Integer anioP;
					// recorremos el fichero de texto linea a linea
					
					while ( (line = f.readLine()) != null ){
						cadenaLinea = line.split("#");
						anioP = Integer.parseInt(cadenaLinea[0]);

						if ( anioP.equals(anio)){
							input += line + "#" + titulo + "\n";
						}else {
							input += line + "\n" ;
						}
					}
					FileOutputStream fileOut = new FileOutputStream("timeline_movies.txt");
					fileOut.write(input.getBytes());
					fileOut.close();


				}	
				break;
			}
		}

		if(!anio_encontrado ){
			titulos.add(titulo);
			Pair par = new Pair < Integer, List<String> > (anio, titulos );
			bdmovies.add(par);

			// introducimos new anio 
			try {
				FileWriter fstream = new FileWriter("timeline_movies.txt", true);
				BufferedWriter out = new BufferedWriter(fstream);
				
				out.write("\n"+anio+"#"+titulo);
				
				out.close();
			} catch (IOException ex) {
				System.out.println("Error: " + ex.getMessage());
			}

			System.out.println("\n304 INF_MOD\n");
			salida = "peli aniadida correctamente a la base";

		}


		return salida;
	}




	//FUNCION QUE DEVUELVE UN STRING CON EL CONTENIDO DE PELICULAS DE UN ANIO PASADO POR PARAMETRO

	public static String  peliculasBaseDatos(Integer anio)throws FileNotFoundException, IOException {

		String pelis = "Peliculas: ";
		List<String> titulos = new ArrayList<>();


		for ( int i = 0 ; i < bdmovies.size() ; i ++){
			if (bdmovies.get(i).getKey().equals(anio) ){
				titulos = bdmovies.get(i).getValue();

				for(int j = 0; j < titulos.size(); j ++){
					pelis = pelis.concat( "-" + titulos.get(j) );
				}
				
				return pelis;
			}
		}


		return pelis = "No hay ninguna peli de ese año registrada en nuestra base de datos, sorry!";
	}

	// FUNCION PARA ALMACENAR NUESTRA BASE DE DATOS DE USUARIOS A PARTIR DE UN ARCHIVO

	 public static void baseUsuariosRegistrados(String archivo) throws FileNotFoundException, IOException 
	 {
		String cadena;
        FileReader f = new FileReader(archivo);
		BufferedReader b = new BufferedReader(f);
		
        while((cadena = b.readLine())!=null) {
			
			String [] linea = cadena.split(" ");

			bdUsers.add(new Pair < String, String > (linea[0] , linea[1]) );
		
			linea = null;
		}
        b.close();
	}

	
	// FUNCION PARA ALMACENAR NUESTRA BASE DE DATOS DE PELICULAS A PARTIR DE UN ARCHIVO

	public static void basePeliculas(String archivo) throws FileNotFoundException, IOException 
	{
	   String cadena;
	   FileReader f = new FileReader(archivo);
	   BufferedReader b = new BufferedReader(f);
	   
	   while((cadena = b.readLine())!=null) {
		   
		String[] linea_aux;
			List<String> linea = new ArrayList<>();	
			linea_aux = cadena.split("#");	  
			for(int i = 1; i < linea_aux.length; i++){
				linea.add(linea_aux[i]);
			}
			Integer anio = Integer.parseInt(linea_aux[0]);
		
		   bdmovies.add(new Pair < Integer, List<String> > (anio, linea ) );
	   
		   linea = null;
	   }
	   b.close();
   }

   // FUNCION PARA COMPROBAR SI UN USUARIO Y CONTRASEÑA SON CORRECTAS

   public static boolean checkUser(String nombre, String password){
		
		for ( int i = 0 ; i < bdUsers.size() ;  i++){

			if ( bdUsers.get(i).getKey().equals(nombre) ){
				if (bdUsers.get(i).getValue().equals(password)  ) return true;

			}
		}

		return false;
   }

   // FUNCION PARA COMPROBAR QUE UN NOMBRE DE USUARIO ESTE YA EN LA BASE DE DATOS
   // SINO LO ESTA LO REGISTRA 
   public static boolean checkRegister(String nombre, String password){


		boolean estaEnLaBase = false;

		for ( int i = 0 ; i < bdUsers.size() && !estaEnLaBase;  i++){

			if ( bdUsers.get(i).getKey().equals(nombre) ){
				estaEnLaBase = true;
			}
		}

		if ( !estaEnLaBase ){
			try {
				FileWriter fstream = new FileWriter("usuarios.txt", true);
				BufferedWriter out = new BufferedWriter(fstream);
				
				out.write("\n"+nombre + " " + password);
				
				out.close();
			} catch (IOException ex) {
				System.out.println("Error: " + ex.getMessage());
			}
		}
	
	return estaEnLaBase;


   }



	



}


    

   

