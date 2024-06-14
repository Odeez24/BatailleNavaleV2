package stage.bataillenavale.utils;

import java.io.IOException;

public class utils {
    /**
     * Teste les différentes erreurs pouvant être renvoyé par le serveur en
     * réponse à un envoi par le client.
     * Renvoie une IOException indiquant d'un problème avec le serveur.
     *
     * @pre <pre>
     * 		error != null <\pre>
     */
    public static IOException checkServerError(String error) {
        Contract.checkCondition(error != null,
                "ERR: Error can't be null");
        switch (error) {
            case "BADARGS":
                System.err.println("ERR: Nombre d'argument incorrect." +
                        " Le serveur en attendais 2");
                break;
            case "BADCODE":
                System.err.println("ERR: Le format du code est " +
                        "incorrect il doit comporté 6 chiffres.");
                break;
            case "ALRDYCONNECTED":
                System.err.println("ERR: Le client est déjà connectée" +
                        " à une autre partie.");
                break;
            case "ALRDYPENDING":
                System.err.println("ERR: Le client est déjà en " +
                        "attente que l'autre client se connecte.");
                break;
            case "DISCONNECTED":
                System.err.println("ERR: Une deconnection à eu lieu.");
                break;
        }
        return new IOException();
    }
}
