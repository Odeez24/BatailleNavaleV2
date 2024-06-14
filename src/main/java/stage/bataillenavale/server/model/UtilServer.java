package stage.bataillenavale.server.model;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

/**
 * Classe mettant à disposition des méthodes utilitaires pour toutes les
 *  classes du serveur
 */
public class UtilServer {
    // ATTRIBUTS CONSTANTES
    public static final String SEP_BETWEEN_SHIPS = ";";
    public static final String SEP_BETWEEN_COORDS = ",";
    public static final String SEP_BETWEEN_XY = "-";
    public static final String SEP = ":";
    public static final int TURN_TIME = 120;

    /**
     * Récupère le contenu d'un message
     */
    public static String ContentInMessage(String message) {
        StringTokenizer s1 = new StringTokenizer(message, ":");
        s1.nextToken();
        return s1.nextToken();
    }

    /**
     * Envoie des données à un client
     */
    public static void sendData(PrintWriter writer, String sendable) throws IOException {
        writer.println(sendable);
        writer.flush();
    }

    /**
     * Vérifie si le message du client est une demande pour connaître un
     * séparateur. Si oui lui renvoi le séparateur correspondant.
     */
    public static boolean checkAskSeparator(PrintWriter writer, String message) throws IOException {
        switch (message) {
            case "SEP" -> {
                sendData(writer, SEP);
                return true;
            }
            case "SEP_BETWEEN_SHIPS" -> {
                sendData(writer, SEP_BETWEEN_SHIPS);
                return true;
            }
            case "SEP_BETWEEN_COORDS" -> {
                sendData(writer, SEP_BETWEEN_COORDS);
                return true;
            }
            case "SEP_BETWEEN_XY" -> {
                sendData(writer, SEP_BETWEEN_XY);
                return true;
            }
            case  "TURN_TIME" -> {
                sendData(writer, TURN_TIME + "");
                return true;
            }
            default -> {
                return false;
            }
        }
    }
}
