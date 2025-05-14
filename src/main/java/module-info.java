module com.example.projectjava {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires jdk.httpserver;
    requires org.json;

    // Vision API is an automatic module from the JAR file
    requires google.cloud.vision;
    requires com.google.gson;
    requires jakarta.mail;
    requires java.net.http;
    requires javafx.web;
    requires jdk.jsobject;
    requires stripe.java;
    requires itext;
    requires jbcrypt;


    opens com.example.projectjava to javafx.fxml;
    exports com.example.projectjava;
    exports controllers;
    opens controllers to javafx.fxml;
    opens entities to javafx.base;
}
