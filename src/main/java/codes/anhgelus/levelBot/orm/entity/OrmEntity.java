package codes.anhgelus.levelBot.orm.entity;

import codes.anhgelus.levelBot.orm.OrmException;
import codes.anhgelus.levelBot.orm.database.Database;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class OrmEntity {

    private final Database database;

    public OrmEntity(Database database) {
        this.database = database;
    }

    public void update() throws OrmException, SQLException, IllegalAccessException {
        database.update(getFields(), getName(), this);
    }

    protected void update(Field field) throws OrmException, SQLException, IllegalAccessException {
        database.update(field, getName(), this);
    }

    public void create() throws OrmException, SQLException, IllegalAccessException {
        database.insert(getFields(), getName(), this);
    }

    protected List<Field> getFields() throws OrmException {
        final var fields = new ArrayList<Field>();
        for (Field field : this.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(Update.class)) {
                continue;
            }
            if (!field.isAnnotationPresent(Column.class)) {
                throw new OrmException("Field " + field.getName() + " is not annotated with @Column");
            }
            fields.add(field);
        }
        return fields;
    }

    protected String getName() throws OrmException {
        if (!this.getClass().isAnnotationPresent(Entity.class)){
            throw new OrmException("Annotation @Entity is not present on class " + this.getClass().getName());
        }
        return this.getClass().getAnnotation(Entity.class).name();
    }

    protected void onUpdateField(String field) {
        try {
            update(getClass().getDeclaredField(field));
        } catch (NoSuchFieldException | OrmException | SQLException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void generateTable() throws OrmException, SQLException {
        database.createTable(getFields(), getName());
    }
}
