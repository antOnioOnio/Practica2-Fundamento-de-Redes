import java.io.IOException;
import java.util.Random;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

//
// Antonio Garcia Castillo
// Eugenia Castilla Fragoso
//
public class YodafyServidorIterativo {

	public static void main(String[] args) {
	
		// Puerto de escucha
		int port=8989;
		// elementos necesarios
		byte [] bufferRecepcion=new byte[256];
		byte [] bufferEnvio = new byte[256];
		InetAddress direccion;
		int puerto;
		DatagramPacket paquete = null;
		DatagramPacket paqueteNuevo = null;
		DatagramSocket socketS = null;
		String mensaje;
		// creacion del socket
		try {
            socketS = new DatagramSocket(port);
        } catch (IOException e) {
            System.err.println("Error de entrada/salida al abrir el socket.");
		}
		

		do{
			paquete =  new DatagramPacket(bufferRecepcion, bufferRecepcion.length);

			// recibimos paquete
			try{
				socketS.receive(paquete);
			} catch (IOException e){
				System.err.println("Error de entrada/salida al abrir el socket.");
			}
			
			direccion = paquete.getAddress();
			mensaje = new String(paquete.getData());
			puerto = paquete.getPort();

			// yodificamos

            String[] s = mensaje.split(" ");
            String resultado = "";

            Random random = new Random();

            for (int i = 0; i < s.length; i++) {
                int j = random.nextInt(s.length);
                int k = random.nextInt(s.length);
                String tmp = s[j];

                s[j] = s[k];
                s[k] = tmp;
            }

            resultado = s[0];

            for (int i = 1; i < s.length; i++) {
                resultado += " " + s[i];
			}
			
			// enviamos

			bufferEnvio = resultado.getBytes();

			paqueteNuevo = new DatagramPacket(bufferEnvio, bufferEnvio.length, direccion, puerto);

			try {
                socketS.send(paqueteNuevo);
            } catch (IOException e) {
                System.err.println("Error de entrada/salida al abrir el socket.");
            }



		}while ( true ) ; 

	}

}
