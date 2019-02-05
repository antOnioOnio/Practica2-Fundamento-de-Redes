//
// Antonio Garcia Castillo
// Eugenia Castilla Fragoso
//

import java.net.InetAddress;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.UnknownHostException;
import java.io.IOException;

public class YodafyClienteUDP {

	public static void main(String[] args) {
		int port = 8989;

		byte []buferEnvio = new byte[256];
		byte []buferRecepcion=new byte[256];
		
		// elementos necesarios para comunicarnos mediante datagramas

		DatagramPacket paquete = null;
		DatagramSocket socketC = null;
		DatagramPacket paqueteNuevo = null;
		InetAddress direccion = null;

		String fraseYoda;
		
		// creamos el socket
		try {
			socketC = new DatagramSocket();
		} catch (IOException e) {
            System.err.println("Error de entrada/salida al abrir el socket.");
        }
		
		//Obtenemos la direccion
		try {
            direccion = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            System.out.println("Error al recuperar la direccion.");
		}
		
		// mensaje original
			buferEnvio="Al monte del volcan debes ir sin demora".getBytes();
		
		// enviamos mensaje y recibimos modificado
		try {
            paquete = new DatagramPacket(buferEnvio, buferEnvio.length, direccion, port);
            socketC.send(paquete);

            paqueteNuevo = new DatagramPacket(buferRecepcion, buferRecepcion.length);
            socketC.receive(paqueteNuevo);
        } catch (IOException e) {
            System.err.println("Error de entrada/salida al abrir el socket.");
		}
		
		fraseYoda = new String( paqueteNuevo.getData());

		System.out.println("\nRecibimos: " + fraseYoda + "\n");

		socketC.close();
	}
}
