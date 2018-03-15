
    /*
	
    public static final String ROUTES_TABLE_NAME = "routes";
    public static final String ROUTE_NAME = "route_name";
    public static final String ROUTE_COORDINATE = "route_coordinate";
    public static final String ROUTE_ID = "route_id";
    public static final String ROUTE_TYPE = "route_type";
    private static final String STOPS_TABLE_NAME = "stops";
    private static final String STOPS_ID = "stops_id";
    private static final String STOPS_LAT = "stops_lat";
    private static final String STOPS_LNG = "stops_lng";
    private static final String ROUTES_BY_STOPS_TABLE_NAME = "routes_by_stops";
    private static final String ROUTES_BY_STOP_ID = "routes_by_stops_id";
        public static final String[] ROUTES_COLUMNS = {
                ROUTE_ID, ROUTE_NAME, ROUTE_COORDINATE, ROUTE_TYPE
        };
    
        private static final String CREATE_DATABASE_SCRIPT =
                "create table " +
                        ROUTES_TABLE_NAME +
                        "(" +
                        ROUTE_ID + " INTEGER PRIMARY KEY NOT NULL, " +
                        ROUTE_NAME + " TEXT, " +
                        ROUTE_COORDINATE + " TEXT, " +
                        ROUTE_TYPE + "  TEXT);" +
                        "create table " +
                        STOPS_TABLE_NAME +
                        "(" +
                        STOPS_ID + " INTEGER PRIMARY KEY NOT NULL, " +
                        STOPS_LAT + " FLOAT, " +
                        STOPS_LNG + " FLOAT);" +
                        "create table " +
                        ROUTES_BY_STOPS_TABLE_NAME +
                        "(" +
                        ROUTES_BY_STOP_ID + " INTEGER PRIMARY KEY NOT NULL, " +
                        ROUTE_ID + " INTEGER, " +
                        STOPS_ID + " INTEGER, " +
                        "FOREIGN KEY(" + ROUTE_ID + ") REFERENCES " + ROUTES_TABLE_NAME + "(" + ROUTE_ID + "))," +
                        "FOREIGN KEY(" + STOPS_ID + ") REFERENCES " + STOPS_TABLE_NAME + "(" + STOPS_ID + "));";
    
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DATABASE_SCRIPT);
        }
    
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    
        }*/