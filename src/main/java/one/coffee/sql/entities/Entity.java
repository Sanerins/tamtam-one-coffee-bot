package one.coffee.sql.entities;

public interface Entity {

    boolean isCreated();
    long getId();

    // Для удобного представления на SQL значений таблички.
    // Порядок выдачи значений должен соответствовать порядку сигнатурного объявления соответствующих столбцов.
    String sqlArgValues();

    // Сохраняет состояние сущности в базе
    void commit();
}
