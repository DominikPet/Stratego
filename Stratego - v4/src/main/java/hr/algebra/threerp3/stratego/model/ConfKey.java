package hr.algebra.threerp3.stratego.model;

public enum ConfKey {
    SERVER_PORT("server.port"), CLIENT_PORT("client.port"), HOST("host"), RANDOM_PORT_HINT("random.port.hint"), RMI_PORT("rmi.port");

    private String keyName;

    private ConfKey(String keyName) {
        this.keyName = keyName;
    }

    public String getKeyName() {
        return keyName;
    }
}
