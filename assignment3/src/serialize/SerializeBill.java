package serialize;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Factura;
import model.interfaces.TableItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;


public class SerializeBill {

    private static ObservableList<Factura> facturi = FXCollections.observableArrayList();

    public static void serialize(){
        try {

            File file = new File("facturi");
            if (!file.exists()) {
                if (file.mkdir()) {
                    System.out.println("Directory is created!");
                } else {
                    System.out.println("Failed to create directory!");
                }
            }

            FileOutputStream fout = new FileOutputStream("facturi/facturi.ser");
            ObjectOutputStream out = new ObjectOutputStream(fout);
            out.writeObject(new ArrayList<Factura>(facturi));
            out.close();
            fout.close();
            System.out.println("Object serialized");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deserialize(){

        FileInputStream fileIn = null;
        ObjectInputStream in = null;
        ArrayList<Factura> facturi_l = new ArrayList<Factura>();
        try {
            fileIn = new FileInputStream("facturi/facturi.ser");
            in = new ObjectInputStream(fileIn);
            facturi_l = (ArrayList)in.readObject();
            facturi = FXCollections.observableArrayList(facturi_l);
            System.out.println("Facturi deserialized!");
			for(Factura fact : facturi){
			    System.out.println(fact.getName());
				for(TableItem p : fact.getProducts())
					System.out.println(p);
			}
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("Facturi class not found");
            c.printStackTrace();
        } finally {
            try{
                in.close();
                fileIn.close();

            }catch(Exception e){
                //e.printStackTrace();
            }
        }

    }

    public static int getCount(){
        return facturi.size();
    }

    public static ObservableList<Factura> getFacturi(){
        return facturi;
    }

}
