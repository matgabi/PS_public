package model;

import java.io.Serializable;
import java.math.BigDecimal;

public class TVA implements Serializable {
    public static enum TVA_TYPE {
        NORMAL9_TVA(9),
        NORMAL19_TVA(19),
        SP(0);
        private BigDecimal value;
        private TVA_TYPE(int value)
        {
            this.value = new BigDecimal(value);
        }

        public BigDecimal getValue() {
            return value;
        }
    };

    private TVA_TYPE type;
    public TVA(TVA_TYPE type)
    {
        this.type = type;
    }

    public BigDecimal getValue()
    {
        return type.getValue();
    }


    public static void main(String[] args)
    {
        TVA tva = new TVA(TVA_TYPE.NORMAL9_TVA);
        System.out.println(tva.getValue());
    }
}
