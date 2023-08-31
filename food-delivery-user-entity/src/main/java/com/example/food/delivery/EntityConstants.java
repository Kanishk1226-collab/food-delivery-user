package com.example.food.delivery;

public class EntityConstants {
    //ADMIN TABLE AND COLUMNS
    public static final String ADMIN_TABLE_NAME = "admin";
    public static final String ADMIN_ID = "admin_id";
    public static final String ADMIN_NAME = "admin_name";
    public static final String ADMIN_EMAIL = "admin_email";
    public static final String ADMIN_ROLE = "admin_role";
    public static final String ADMIN_PASSWORD = "admin_password";

    //RESTAURANT AGENT TABLE AND COLUMNS
    public static final String REST_AGENT_TABLE_NAME = "restaurant_agent";
    public static final String REST_AGENT_ID = "rest_agent_id";
    public static final String REST_ID = "rest_id";
    public static final String REST_AGENT_NAME = "rest_agent_name";
    public static final String REST_AGENT_EMAIL = "rest_agent_email";
    public static final String REST_AGENT_PASSWORD = "rest_agent_password";
    public static final String REST_AGENT_PHONE = "rest_agent_phone";

    //DELIVERY AGENT TABLE AND COLUMNS
    public static final String DELIVERY_AGENT_TABLE_NAME = "delivery_agent";
    public static final String DELIVERY_AGENT_ID = "del_agent_id";
    public static final String DELIVERY_AGENT_NAME = "del_agent_name";
    public static final String DELIVERY_AGENT_EMAIL = "del_agent_email";
    public static final String DELIVERY_AGENT_PASSWORD = "del_agent_password";
    public static final String DELIVERY_AGENT_PHONE = "del_agent_phone";
    public static final String DELIVERY_AGENT_IS_VERIFIED = "is_verified";
    public static final String DELIVERY_AGENT_IS_AVAILABLE = "is_available";


    //CUSTOMER TABLE AND COLUMNS
    public static final String CUSTOMER_TABLE_NAME = "customer";
    public static final String CUSTOMER_ID = "customer_id";
    public static final String CUSTOMER_NAME = "customer_name";
    public static final String CUSTOMER_EMAIL = "customer_email";
    public static final String CUSTOMER_PASSWORD = "customer_password";
    public static final String CUSTOMER_PHONE = "customer_phone";


    //CUSTOMER ADDRESS TABLE AND COLUMNS
    public static final String ADDRESS_TABLE_NAME = "address";
    public static final String ADDRESS_ID = "address_id";
    public static final String ADDRESS_CUSTOMER_ID = "customer_id";
    public static final String DOOR_NO = "door_no";
    public static final String LOCALITY = "locality";
    public static final String CITY = "city";
    public static final String PINCODE = "pincode";

    //LOGGED IN FIELD
    public static final String IS_LOGGED_IN = "is_logged_in";


}
