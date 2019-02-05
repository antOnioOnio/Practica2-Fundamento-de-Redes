import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class ServidorMovies {

	public static void main(String[] args) {
	
		// Puerto de escucha
		int port=8989;

		
		try {

			ServerSocket socketServidor = new ServerSocket(port);

			do {
				
				Socket socketServicio = socketServidor.accept();

				ProcesadorMovies procesador=new ProcesadorMovies(socketServicio);
				procesador.start();
				
			} while (true);
			
		} catch (IOException e) {
			System.err.println("Error al escuchar en el puerto "+port);
		}

	}

}
