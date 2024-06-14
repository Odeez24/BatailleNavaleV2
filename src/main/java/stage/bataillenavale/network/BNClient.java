package stage.bataillenavale.network;

import stage.bataillenavale.utils.Contract;

import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;

/**
 * Gestion d'une connexions avec un serveur pouvant envoyer et recevoir des
 * données au/du serveur
 *
 * @inv <pre>
 * 		socket != null
 * 		reader != null
 * 		writer != null
 * @cons <pre>
 * 		Crée un socket sur le serveur de domaine host et sur le port port
 * 		$ARGS$ String host, int port
 * 		$PRE$
 *     		host != null && port > 1024  && port < 43150
 * 		$POST$
 *     		isLinked()</pre>
 * @cons <pre>
 * 		Crée un socket sur le serveur de domaine host et sur le port
 * 			DEFAULT_PORT
 *     	$ARGS$ String host
 *     	$PRE$
 *     		host != null
 *    	$POST$
 *    		isLinked()</pre>
 * @cons <pre>
 * 		Crée un socket sur le serveur de domaine DEFAULT_HOST et sur le
 * 			port DEFAULT_PORT
 *     	$ARGS$
 *     	$PRE$
 *    	$POST$
 *    		isLinked()</pre>
 */
public class BNClient {

    // ATTRIBUTS STATIQUES

    public static final int DEFAULT_PORT = 6969;
    public static final String DEFAULT_HOST = "srv-dpi-proj-bataille-navale.univ-rouen.fr";
    public static final String PASSWORD = "password";
    // ATTRIBUTS

    private final SSLSocket socket;
    private final BufferedReader reader;
    private final PrintWriter writer;

    // CONSTRUCTEURS
    public BNClient(String host, int port) throws IOException {
        Contract.checkCondition(host != null && port > 1024
                        && port < 43150,
                "ERR - host is not exist or port not valid");
        // Création de la socket.
        SSLContext sslContext = createSSLContext();
        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        socket = (SSLSocket) sslSocketFactory.createSocket(DEFAULT_HOST,
                6969);
        socket.setEnabledCipherSuites(socket.getSupportedCipherSuites());
        socket.setEnabledProtocols(socket.getSupportedProtocols());
        //socket.startHandshake();
        // Création de l'écrivain et du lecteur.
        reader = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream());
    }

    public BNClient(String host) throws IOException {
        this(host, DEFAULT_PORT);
    }

    public BNClient() throws IOException {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }

    /**
     * Retourne si le client est connecté au serveur
     */
    public boolean isLinked() {
        return socket != null;
    }

    /**
     * Envoie au serveur le contenu du paramètre
     *
     * @pre <pre>
     * 		sendable != null
     * 		&& isLinked()</pre>
     */
    public void sendData(String sendable) throws IOException {
        Contract.checkCondition(sendable != null,
                "ERR - sendable object is null");
        writer.println(sendable);
        writer.flush();
    }

    /**
     * Récupérer sur le serveur la dernière ligne envoyée par celui-ci
     *
     * @pre <pre>
     * 		isLinked()
     * @throws IOException si la lecture n'a pas pu se faire
     */
    public String getData() throws IOException {
        Contract.checkCondition(isLinked(), "ERR - no server to get data from");
        return reader.readLine();
    }

    /**
     * Ferme la connexion au serveur
     */
    public void terminateConnection() throws IOException {
        Contract.checkCondition(!isLinked(),
                "ERR - no server to terminate connection with");
        socket.close();
    }

    // OUTILS

    /**
     * Crée un contexte SSL pour la connexion
     */
    private SSLContext createSSLContext() {
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream("keystore.jks"),
                    PASSWORD.toCharArray());

            // Creation du keyManager
            KeyManagerFactory keyManagerFactory =
                    KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, PASSWORD.toCharArray());
            KeyManager[] km = keyManagerFactory.getKeyManagers();

            // Création du TrustManager
            TrustManagerFactory trustManagerFactory =
                    TrustManagerFactory.getInstance("SunX509");
            trustManagerFactory.init(keyStore);
            TrustManager[] tm = trustManagerFactory.getTrustManagers();

            // Initialisation du SSLContext
            SSLContext sslContext = SSLContext.getInstance("TLSv1");
            sslContext.init(km, tm, new java.security.SecureRandom());

            return sslContext;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

}
