package stage.bataillenavale.server;

import stage.bataillenavale.server.model.UtilServer;

import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @inv <pre>
 *     serverSocket != null
 *     waitingClients != null
 *     game != null </pre>
 * @cons <pre>
 *     $ARG$
 *     $POST$
 *      serverSocket == new SSLServerSocket(PORT, MAX_CLIENTS)
 *      waitingClients == new HashMap<>()
 *      game == new HashMap<>() </pre>
 */
public class server {
    public static final String PASSWORD = "password";
    // ATTRIBUTS STATIQUES
    private static final int PORT = 6969;
    private static final int MAX_CLIENTS = 10;
    // ATTRIBUTS CONSTANT
    private final String PATTERN = "[0-9]{6}";

    // ATTRIBUTS
    private final SSLServerSocket serverSocket;
    private final HashMap<String, SSLSocket> waitingClients;
    private final Map<SSLSocket, SSLSocket> game;

    // CONSTRUCTEUR
    public server() throws IOException {
        waitingClients = new HashMap<>();
        game = new HashMap<>();
        try {
            SSLContext sslContext = createSSLContext();
            SSLServerSocketFactory sslServerSocketFactory =
                    sslContext.getServerSocketFactory();
            serverSocket = (SSLServerSocket)
                    sslServerSocketFactory.createServerSocket(PORT, MAX_CLIENTS);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException(e);
        }
    }

    // COMMANDES

    public static void main(String[] args) {
        try {
            server s = new server();
            s.star();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // OUTILS

    /**
     * Gérer les connexions des clients au serveur et les associer en
     * fonction de leur clé de partie.
     *
     * @throws IOException si le serveur ne peut pas accepter de connexion
     */
    public void star() throws IOException {
        System.out.println("Server Start");
        SSLSocket clientSocket;
        while (true) {
            clientSocket = (SSLSocket) serverSocket.accept();
            //clientSocket.startHandshake();
            System.out.println("New client connected");
            BufferedReader input = new BufferedReader(new InputStreamReader(
                    clientSocket.getInputStream()));
            PrintWriter output = new PrintWriter(new OutputStreamWriter(
                    clientSocket.getOutputStream()));
            String s = input.readLine();
            System.out.println(s);
            while (UtilServer.checkAskSeparator(output, s)) {
                s = input.readLine();
                System.out.println(s);
            }
            StringTokenizer st = new StringTokenizer(s, ":");
            if (!st.nextToken().equals("INIT")) {
                UtilServer.sendData(output, "S:ERR:BADARGS");
            }
            String key = st.nextToken();
            Matcher m = Pattern.compile(PATTERN).matcher(key);
            if (!m.matches()) {
                UtilServer.sendData(output, "S:ERR:BADCODE");
            }
            System.out.println("Game " + key + " asked");
            if (game.containsKey(clientSocket)) {
                UtilServer.sendData(output, "S:ERR:ALRDYCONNECTED");
            }
            if (!waitingClients.containsKey(key)) {
                waitingClients.put(key, clientSocket);
                UtilServer.sendData(output, "S:PENDING");
                System.out.println("Game " + key + " pending");
            } else {
                /*if (!waitingClients.get(key).equals(clientSocket)) {
                    UtilServer.sendData(output, "S:ERR:ALRDYPENDING");
                }*/
                SSLSocket client2 = waitingClients.get(key);
                game.put(clientSocket, client2);
                waitingClients.remove(key);
                Thread t = getThread(clientSocket, client2);
                t.start();
                UtilServer.sendData(new PrintWriter(new OutputStreamWriter(
                        client2.getOutputStream())), "S:CONNECTED");
                UtilServer.sendData(output, "S:CONNECTED");
                System.out.println("Game " + key + " started");
            }
        }
    }

    /**
     * Créer un thread pour gérer une partie entre deux clients.
     *
     * @param clientSocket  le premier client
     * @param clientSocket2 le deuxième client
     */
    private Thread getThread(SSLSocket clientSocket, SSLSocket clientSocket2) {
        return new Thread(() -> {
            try {
                ServerGame game1 = new ServerGame(clientSocket, clientSocket2);
                game1.startGame();
                endGame(clientSocket, clientSocket2);
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    UtilServer.sendData(new PrintWriter(new OutputStreamWriter(
                                    clientSocket.getOutputStream())),
                            "S:ERR:DISCONNECTED");
                    UtilServer.sendData(new PrintWriter(new OutputStreamWriter(
                                    clientSocket2.getOutputStream())),
                            "S:ERR:DISCONNECTED");
                } catch (IOException ex) {
                    ex.printStackTrace();
                    endGame(clientSocket, clientSocket2);
                    throw new RuntimeException(ex);
                }
                endGame(clientSocket, clientSocket2);
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Supprimer les clients de la liste des parties en cours.
     *
     * @param clientSocket1 le premier client
     * @param clientSocket2 le deuxième client
     */
    public void endGame(SSLSocket clientSocket1, SSLSocket clientSocket2) {
        game.remove(clientSocket1);
        game.remove(clientSocket2);
    }

    /**
     * Créer un contexte SSL pour le serveur.
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
