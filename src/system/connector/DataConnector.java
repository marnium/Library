package system.connector;

import java.io.Serializable;

public class DataConnector implements Serializable {
    private static final long serialVersionUID = -6060232507346604331L;
    private String host;
    private String user;
    private String password;

    public DataConnector(String host, String user, String password) {
        this.host = host;
        this.user = user;
        this.password = password;
    }

    public String get_host() {
        return host;
    }

    public String get_user() {
        return user;
    }

    public String get_pass() {
        return password;
    }
}