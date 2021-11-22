package system;

import system.login.Login;

/**
 *
 * @author m2a
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Login login = new Login();
        login.setVisible(true);
        login.createAccess();
    }
    
}
