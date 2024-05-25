module hr.algebra.threerp3.stratego {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;
    requires java.naming;
    requires java.xml;


    opens hr.algebra.threerp3.stratego to javafx.fxml;
    exports hr.algebra.threerp3.stratego;
    exports hr.algebra.threerp3.stratego.rmi to java.rmi;
    exports hr.algebra.threerp3.stratego.chat to java.rmi;
}