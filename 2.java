// StringApp.idl
module StringApp {
  interface StringOperations {
    string toUpper(in string str);
    string toLower(in string str);
    string reverse(in string str);
  };
};
// idlj -fall StringApp.idl

// StringImpl.java
import StringApp.StringOperationsPOA;
public class StringImpl extends StringOperationsPOA {
    public String toUpper(String str) {
        return str.toUpperCase();
    }
    public String toLower(String str) {
        return str.toLowerCase();
    }
    public String reverse(String str) {
        return new StringBuilder(str).reverse().toString();
    }
}


// Server.java
import org.omg.CORBA.*;
import org.omg.CosNaming.*;
import org.omg.PortableServer.*;
import StringApp.StringOperations;
import StringApp.StringOperationsHelper;

public class Server {
    public static void main(String args[]) {
        try {
            ORB orb = ORB.init(args, null);

            POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();

            StringImpl stringImpl = new StringImpl();
            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(stringImpl);

            StringOperations href = StringOperationsHelper.narrow(ref);

            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            NameComponent path[] = ncRef.to_name("StringOperations");
            ncRef.rebind(path, href);

            System.out.println("Server ready and waiting...");
            orb.run();
        } catch (Exception e) {
            System.err.println("Server Error: " + e);
            e.printStackTrace();
        }
    }
}

// Client.java
import StringApp.StringOperations;
import StringApp.StringOperationsHelper;
import org.omg.CORBA.*;
import org.omg.CosNaming.*;

public class Client {
    public static void main(String args[]) {
        try {
            ORB orb = ORB.init(args, null);

            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            StringOperations strObj = StringOperationsHelper.narrow(ncRef.resolve_str("StringOperations"));

            System.out.println("Calling toUpper(\"hello\") → " + strObj.toUpper("hello"));
            System.out.println("Calling toLower(\"WORLD\") → " + strObj.toLower("WORLD"));
            System.out.println("Calling reverse(\"CORBA\") → " + strObj.reverse("CORBA"));

        } catch (Exception e) {
            System.err.println("ERROR : " + e);
            e.printStackTrace(System.out);
        }
    }
}

// javac *.java StringApp/*.java

// Start-Process orbd -ArgumentList "-ORBInitialPort 1050 -ORBInitialHost localhost" 


// java Server -ORBInitialPort 1050 -ORBInitialHost localhost


// java Client -ORBInitialPort 1050 -ORBInitialHost localhost
