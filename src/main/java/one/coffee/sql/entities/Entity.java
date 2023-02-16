package one.coffee.sql.entities;

public interface Entity {
    String sqlValues(); // Для удобного представления на SQL значений таблички.
                        // Порядок выдачи значений должен соответствоать порядку сигнатурного объявления соответствующих столбцов.
}
