
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.Console;;

public class ClienteMovies {

	public static void main(String[] args) {
		
		String buferEnvio;
		String buferRecepcion;

		//-----------CODIGOS CLIENTE SERVIDOR
		
		String LOGIN = "1001";
		String REGISTER = "1002";
		String OK_REGISTER = "201";
		String OK = "200";
		String PETICION_INFORMACION = "202";
		String PETICION_MODIFICACION = "203";
		String ERROR_REGISTER = "302";
		String CIERRA_CONEXION = "400";
		
		// Nombre del host donde se ejecuta el servidor:
		String host="localhost";
		// Puerto en el que espera el servidor:
		int port=8989;
		
		// Socket para la conexión TCP
		Socket socketServicio=null;
		
		try {
			Console con = System.console();
			socketServicio = new Socket(host, port);
			//////////////////////////////////////////////////////			
			
			InputStream inputStream = socketServicio.getInputStream();
			OutputStream outputStream = socketServicio.getOutputStream();
			String nombre;
			String passwd;
			boolean found = false;
			boolean userNoValido = false;
			Integer opcion= -1;
			String 	opc = null;

			
			while ( opcion != 0 && opcion != 1){

				opc = con.readLine("Elige \n0 para iniciar sesion \n1 para registrarte: \n  "); 
				opcion = Integer.parseInt(opc);
			}

			

			switch (opcion){
				case 0:
						//-----------------------------nombre usuario y contraseña----------
						while ( !found)
						{
							nombre = null;
							nombre = con.readLine("Enter your username: "); 
					

							passwd = con.readLine("Enter your password: "); 


							buferEnvio = LOGIN + " " + nombre + " " + passwd;

							
							PrintWriter outPrinter = new PrintWriter(outputStream, true);
							outPrinter.println(buferEnvio);

							outputStream.flush();
							
							BufferedReader inReader = new BufferedReader(new InputStreamReader(inputStream));
							buferRecepcion = inReader.readLine();
							
							System.out.println("\nRecibido: " + buferRecepcion + "\n");		
							
							if( buferRecepcion.equals(OK)){
								
								found = true;
							}else {
								System.out.println("\nUsuario o contraseña invalida, intentalo de nuevo\n");
								buferEnvio = null;
								buferRecepcion = null;

							}
						}

				break;

				case 1:
						//-----------------------------registro----------
						while ( !userNoValido)
						{
							nombre = null;
							nombre = con.readLine("Enter your username: "); 
							System.out.println("\nNombre: " + nombre + "\n");	

							passwd = con.readLine("Choose your password: "); 

							
							buferEnvio = REGISTER + " " + nombre + " " + passwd;

							System.out.println(buferEnvio);

							
							PrintWriter outPrinter = new PrintWriter(outputStream, true);
							outPrinter.println(buferEnvio);

							outputStream.flush();
							
							BufferedReader inReader = new BufferedReader(new InputStreamReader(inputStream));
							buferRecepcion = inReader.readLine();
							
							System.out.println("\nRecibido: " + buferRecepcion + "\n");		
							
							if( buferRecepcion.equals(ERROR_REGISTER)){
								
								System.out.println("\nUsuario ya existente");
									buferEnvio = null;
								buferRecepcion = null;
					
							}else {
								System.out.println("\nBIENVENIDO");
								userNoValido = true;
							}
						}

				break;
			}

			buferEnvio = null;
			buferRecepcion = null;
			boolean quieroSalir= false;
			Integer salir = -1;
			String quit;
			String anio;
			String year;
			String titulo;

			while(!quieroSalir){
				quit = con.readLine("\nElige \n0 para elegir un anio \n1 Para insertar una peli dado un anio y un titulo \n2 para salir de la app \n  "); 
				salir = Integer.parseInt(quit);

				switch(salir){
					case 0 :

						anio = con.readLine("\nDime un anio: \n  "); 

						buferEnvio = PETICION_INFORMACION + " " + anio;

						PrintWriter outPrinter = new PrintWriter(outputStream, true);
						outPrinter.println(buferEnvio);

						outputStream.flush();

						BufferedReader inReader = new BufferedReader(new InputStreamReader(inputStream));
						buferRecepcion = inReader.readLine();

						String [] cadena = buferRecepcion.split("-");

						for(int i = 0; i < cadena.length; i++){
							System.out.println("\n\t" + cadena[i] );	

						}
						quieroSalir = false;
					break;

					case 1 :
				
						year = con.readLine("\nDime un anio: \n  "); 

						titulo = con.readLine("\nDime el titulo: \n  "); 
						buferEnvio = PETICION_MODIFICACION + " " + year + " " + titulo;							
						outPrinter = new PrintWriter(outputStream, true);
						outPrinter.println(buferEnvio);
						outputStream.flush();
						BufferedReader inReader3 = new BufferedReader(new InputStreamReader(inputStream));
						buferRecepcion = inReader3.readLine();
						System.out.println(buferRecepcion );	

						
						
					break;

					case 2:
						//salir
						buferEnvio = CIERRA_CONEXION ;
						outPrinter = new PrintWriter(outputStream, true);
						outPrinter.println(buferEnvio);

						outputStream.flush();
						quieroSalir = true;


					break;
				}
			}


			socketServicio.close();
		
			
		} catch (UnknownHostException e) {
			System.err.println("Error: Nombre de host no encontrado.");
		} catch (IOException e) {
			System.err.println("Error de entrada/salida al abrir el socket.");
		}
	}
}


