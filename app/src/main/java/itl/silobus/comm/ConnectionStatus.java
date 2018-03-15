package itl.silobus.comm;

public enum ConnectionStatus {
    CONNECTED,
    DISCONNECTED,
    LOST,
    CANNOT_CONNECT,
    IP_PORT_ERROR,
    INPUT_ERROR,
    INTERNET_DISCONNECTED,
    NO_ROUTE_TO_HOST
}