module com.sklowsky.DBFtoJSON {
    requires ini4j;
    requires com.github.albfernandez.javadbf;
    requires com.google.gson;

    opens com.sklowsky.DBFtoJSON;
    exports com.sklowsky.DBFtoJSON;
}