package model;

import javafx.collections.ObservableList;
import model.interfaces.Report;
import model.interfaces.TableItem;

import javax.persistence.Table;
import java.math.BigDecimal;

public class ReportByClient implements Report {

    ObservableList<FacturaInfo> facturiInfo;

    public ReportByClient(ObservableList<FacturaInfo> facturiInfo)
    {
        this.facturiInfo = facturiInfo;
    }

    @Override
    public String getReport() {
        String client = "SC UTCN SRL";

        int size = 0;
        String report = "";

        BigDecimal total = BigDecimal.valueOf(0);
        BigDecimal totalTva = BigDecimal.valueOf(0);
        BigDecimal totalDiscount = BigDecimal.valueOf(0);
        BigDecimal totalTvaDiscount = BigDecimal.valueOf(0);

        int products = 0;

        for(FacturaInfo f : facturiInfo)
        {
            if(f.getClient().equals(client))
            {
                report += f;
                report += "\n";
                size++;
                products += f.getProducts();
                total = total.add(f.getTotal());
                totalTva = totalTva.add(f.getTotalTva());
                totalDiscount = totalDiscount.add(f.getTotalDiscount());
                totalTvaDiscount = totalTvaDiscount.add(f.getTotalDiscountTva());
            }

        }
        report += "\n\n\n\n\n";

        report += "Sunt " + size + " facturi emise pentru " + client + ", dintr-un total de " + facturiInfo.size() + " facturi.\n";
        report += "Total produse vandute: " + products + " \n";
        report += "Valoare totala: " + total.toString() + "\n";
        report += "Valoare tva: " + totalTva.toString() + "\n";
        report += "Valoare discount: " + totalDiscount.toString() + "\n";
        report += "Valoare tva discount" + totalTvaDiscount.toString() + "\n";

        return report;
    }
}
