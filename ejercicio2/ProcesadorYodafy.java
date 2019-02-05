//
// YodafyServidorIterativo
// Antonio Garcia Castillo
// Eugenia Castilla Fragoso
//
//
import java.io.BufferedReader;//
import java.io.IOException; //
import java.io.InputStream;//
import java.io.OutputStream;//
import java.io.PrintWriter;
import java.net.Socket;//
import java.util.Random;//
import java.io.PrintWriter;
import java.io.InputStreamReader;

//
// Nota: si esta clase extendiera la clase Thread, y el procesamiento lo hiciera el método "run()",
// ¡Podríamos realizar un procesado concurrente! 
//
public class ProcesadorYodafy {
	// Referencia a un socket para enviar/recibir las peticiones/respuestas
	private Socket socketServicio;
	// stream de lectura (por aquí se recibe lo que envía el cliente)
	private InputStream inputStream;
	// stream de escritura (por aquí se envía los datos al cliente)
	private OutputStream outputStream;
	
	// Para que la respuesta sea siempre diferente, usamos un generador de números aleatorios.
	private Random random;
	
	// Constructor que tiene como parámetro una referencia al socket abierto en por otra clase
	public ProcesadorYodafy(Socket socketServicio) {
		this.socketServicio=socketServicio;
		random=new Random();
	}
	
	
	// Aquí es donde se realiza el procesamiento realmente:
	void procesa(){
		
		// Como máximo leeremos un bloque de 1024 bytes. Esto se puede modificar.
		// cambiamos byte[] por String para adaptar
		String datosRecibidos;
		
		
		String datosEnviar=null;
		
		
		try {
			// Obtiene los flujos de escritura/lectura
			inputStream=socketServicio.getInputStream();
			outputStream=socketServicio.getOutputStream();
			
			// vamos a obtener datosRecibidos desde bufferReader, no desde InputStream
			BufferedReader inReader = new BufferedReader(new InputStreamReader(inputStream));
			datosRecibidos = inReader.readLine();
		
			String respuesta = yodaDo(datosRecibidos);

			// Yoda reinterpreta el mensaje:
			PrintWriter outPrinter = new PrintWriter(outputStream,true);
			outPrinter.println(respuesta);

			
		} catch (IOException e) {
			System.err.println("Error al obtener los flujos de entrada/salida.");
		}

	}

	// Yoda interpreta una frase y la devuelve en su "dialecto":
	private String yodaDo(String peticion) {
		// Desordenamos las palabras:
		String[] s = peticion.split(" ");
		String resultado="";
		
		for(int i=0;i<s.length;i++){
			int j=random.nextInt(s.length);
			int k=random.nextInt(s.length);
			String tmp=s[j];
			
			s[j]=s[k];
			s[k]=tmp;
		}
		
		resultado=s[0];
		for(int i=1;i<s.length;i++){
		  resultado+=" "+s[i];
		}
		
		return resultado;
	}
}
