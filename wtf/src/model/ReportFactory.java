package model;

import javafx.collections.ObservableList;
import model.interfaces.Report;

public class ReportFactory {

    public static enum REPORT_TYPE{
        REPORT_BY_CLIENT,
        REPORT_BY_CURRENT_DAY
    }

    public Report getReport(REPORT_TYPE report_type, ObservableList<FacturaInfo> facturiInfo)
    {
        if(report_type == REPORT_TYPE.REPORT_BY_CLIENT)
        {
            return new ReportByClient(facturiInfo);
        }
        else if(report_type == REPORT_TYPE.REPORT_BY_CURRENT_DAY)
        {
            return new ReportByDate(facturiInfo);
        }
        else
        {
            return null;
        }
    }

}
