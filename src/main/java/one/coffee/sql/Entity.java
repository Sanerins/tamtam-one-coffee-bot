package one.coffee.sql;

import java.sql.SQLException;

public interface Entity {

    boolean isCreated();

    long getId();

    // Для удобного представления на SQL значений таблички.
    // Порядок выдачи значений должен соответствовать порядку сигнатурного объявления соответствующих столбцов.
    String sqlArgValues();

}
