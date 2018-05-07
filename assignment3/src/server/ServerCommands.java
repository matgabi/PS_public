package server;

public class ServerCommands {
    public static enum COMMAND
    {
        INVALID_COMMAND,
        CLOSE,
        GET_ALL_CLIENTS,
        GET_ALL_PRODUCTS,
        GET_ALL_SELLERS,
        GET_ALL_EMITORS,
        GET_ALL_ACTIVITIES,
        ADD_NEW_PRODUCT,
        ADD_NEW_CLIENT,
        ADD_NEW_SELLER,
        ADD_NEW_EMITOR,
        ADD_NEW_ACTIVITY,
        UPDATE_PRODUCT,
        UPDATE_CLIENT,
        UPDATE_SELLER,
        UPDATE_EMITOR,
        UPDATE_ACTIVITY,
        DELETE_PRODUCT,
        DELETE_CLIENT,
        DELETE_SELLER,
        DELETE_EMITOR,
        DELETE_ACTIVITY,
        GENERATE_INVOICE,
        SET_CLIENT_NAME,
        GET_AVAILABLE_INVOICES,
        BUY_INVOICES
    }
}
